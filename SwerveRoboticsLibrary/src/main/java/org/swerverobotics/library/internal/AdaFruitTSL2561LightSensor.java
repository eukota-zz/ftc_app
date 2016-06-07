package org.swerverobotics.library.internal;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.exceptions.UnexpectedI2CDeviceException;
import org.swerverobotics.library.interfaces.TCA9548A;
import org.swerverobotics.library.interfaces.TSL2561LightSensor;

import java.nio.ByteBuffer;

import static org.swerverobotics.library.internal.Util.handleCapturedInterrupt;

/**
 * This class implements the TSL2561LightSensor interface (i.e., an AdaFruit light sensor module over i2c)
 * https://www.adafruit.com/products/439
 * https://github.com/adafruit/Adafruit_TSL2561
 */
public class AdaFruitTSL2561LightSensor implements TSL2561LightSensor, IOpModeStateTransitionEvents
{
    private final OpMode opmodeContext;
    private final I2cMultiplexedDeviceSync deviceClient;

    private Parameters parameters;

    private static final I2cDeviceSynch.ReadMode readMode = I2cDeviceSynch.ReadMode.REPEAT;


    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    public AdaFruitTSL2561LightSensor(OpMode opmodeContext, I2cDevice i2cDevice, Parameters params) {
        this.opmodeContext = opmodeContext;

        this.deviceClient = ClassFactory.createI2cDeviceSynch(i2cDevice, params.i2cAddress.bVal * 2);

        this.engage();

        this.deviceClient.enableWriteCoalescing(false);

        this.deviceClient.setLogging(params.loggingEnabled);
        this.deviceClient.setLoggingTag(params.loggingTag);

        this.parameters = params;
    }

    public static TSL2561LightSensor create(OpMode opmodeContext, I2cDevice i2cDevice, Parameters parameters)
    {
        // Create a sensor which is a client of i2cDevice
        TSL2561LightSensor result = new AdaFruitTSL2561LightSensor(opmodeContext, i2cDevice, parameters);

        // Initialize it with the indicated parameters
        result.initialize(parameters);
        return result;
    }

    public void initialize(Parameters parameters)
    {
        // Remember the parameters for future use
        this.parameters = parameters;

        boolean armed = this.deviceClient.isArmed();

        byte id = this.getDeviceID();

        if ( (id != ADAFRUIT_TSL2561_ID) )
        {
            throw new UnexpectedI2CDeviceException(id);
        }

        // Set the integration time and gain
        setIntegrationTimeAndGain(parameters.integrationTime, parameters.gain);

        //wait x milliseconds for first integration to complete
        waitForIntegrationToComplete();

        // Enable the device
        // todo: in Adafruit's implelmentation they enable/disable the device for each read,
        // presumably to save power. Should we? If we do, that would delay the read time
        // while we wait for integration to complete. I'll skip that for now.
        enable();
    }

    private synchronized void enable()
    {
        write8(REGISTER.CONTROL, TSL2561_CONTROL_POWERON);
    }

    private synchronized void disable()
    {
        /* Turn the device off to save power */
        write8(REGISTER.CONTROL, TSL2561_CONTROL_POWEROFF);
    }

    private synchronized void setIntegrationTimeAndGain(INTEGRATION_TIME time, GAIN gain)
    {
        write8(REGISTER.TIMING, time.byteVal | gain.byteVal);
    }

    private synchronized void waitForIntegrationToComplete()
    {
        //wait x milliseconds for integration to complete
        if (parameters.integrationTime == INTEGRATION_TIME.MS_13)  delay(TSL2561_INTEGRATION_DELAY_13MS);
        else if (parameters.integrationTime == INTEGRATION_TIME.MS_101) delay(TSL2561_INTEGRATION_DELAY_101MS);
        else /*if (parameters.integrationTime == INTEGRATION_TIME.MS_402)*/ delay(TSL2561_INTEGRATION_DELAY_402MS);
    }

    public synchronized byte getState()
    {
        byte b = this.read8(REGISTER.CONTROL);
        return b;
    }

    public synchronized byte getDeviceID()
    {
        byte b = this.read8(REGISTER.ID);
        return b;
    }


    //----------------------------------------------------------------------------------------------
    // LightSensor methods
    //----------------------------------------------------------------------------------------------

    /*
     * Get the amount of light detected by the sensor. 1.0 is max possible light, 0.0 is least possible light.
     * Returns amount of light, on a scale of 0 to 1
     */
    @Override
    public double getLightDetected()
    {
        double raw = getLightDetectedRaw(); //get this as a double so the division below will use double math

        return (raw/TSL2561_MAX_RAW_VALUE);
    }


    //return the raw value of the sensor, considering which light detection mode the user has asked for.
    @Override
    public int getLightDetectedRaw()
    {
        if (parameters.detectionMode == LIGHT_DETECTION_MODE.BROADBAND)
        {
            return getRawBroadbandLight();
        }
        else if (parameters.detectionMode == LIGHT_DETECTION_MODE.INFRARED)
        {
            return getRawIRSpectrumLight();
        }
        else /* if (parameters.detectionMode == LIGHT_DETECTION_MODE.VISIBLE) */
        {
            return ( getRawBroadbandLight() - getRawIRSpectrumLight());
        }
    }

    /**
     *  This method is intended to return the value in standard SI lux units.
     *  The code in this method has been ported from AdaFruit's sample implementation,
     *  which itself appears to be adapted from the datasheet's implementation.
     *  This code returns an error value of -1 if the sensor is saturated such that the value is not reliable.
     *
     *  ToDo: the output values from this method need validation and checking with a light meter.
     *  ToDo: THIS METHOD NEEDS DEBUGGING: IT'S PROBABLY WRONG!
     */
    @Override
    public int getLightLux()
    {
        int broadband = 0;
        int ir = 0;

        /* Set saturation thresholds */
        int clipThreshold = 0;
        if (parameters.integrationTime == INTEGRATION_TIME.MS_13) clipThreshold = TSL2561_CLIPPING_13MS;
        else if (parameters.integrationTime == INTEGRATION_TIME.MS_101) clipThreshold = TSL2561_CLIPPING_101MS;
        else /*if (parameters.integrationTime == INTEGRATION_TIME.MS_402)*/ clipThreshold = TSL2561_CLIPPING_402MS;

        if (parameters.detectionMode == LIGHT_DETECTION_MODE.BROADBAND)
        {
            broadband = getRawBroadbandLight();
        }
        else if (parameters.detectionMode == LIGHT_DETECTION_MODE.INFRARED)
        {
            ir = getRawIRSpectrumLight();
        }
        else /*if (parameters.detectionMode == LIGHT_DETECTION_MODE.VISIBLE) */
        {
            broadband = getRawBroadbandLight();
            ir = getRawIRSpectrumLight();
        }


        /* Make sure sensor isn't saturated! */
        /* Return a specific error value for lux if the sensor is saturated */
        if ((broadband > clipThreshold) || (ir > clipThreshold))
        {
            return -1; //todo use 0 or Adafruit's '65536' or ... ?
        }

        long chScale = 0;
        long channel1;
        long channel0;

        /* Get the correct scale depending on the integration time. 402ms doesn't need to be scaled */
        if (parameters.integrationTime == INTEGRATION_TIME.MS_13) chScale = TSL2561_LUX_CHSCALE_TINT0;
        else if (parameters.integrationTime == INTEGRATION_TIME.MS_101) chScale = TSL2561_LUX_CHSCALE_TINT1;
        else /*if (parameters.integrationTime == INTEGRATION_TIME.MS_402)*/ /* no scaling if 402ms*/ chScale = (1 << TSL2561_LUX_CHSCALE);

        /* Scale for gain (1x or 16x). 16x does not need to be scaled. */
        if (parameters.gain == GAIN.GAIN_1) chScale = chScale << 4;

        /* Scale the channel values */
        channel0 = (broadband * chScale) >> TSL2561_LUX_CHSCALE;
        channel1 = (ir * chScale) >> TSL2561_LUX_CHSCALE;

        /* Find the ratio of the channel values (Channel1/Channel0) */
        long ratio1 = 0;
        if (channel0 != 0) ratio1 = (channel1 << (TSL2561_LUX_RATIOSCALE+1)) / channel0;

        /* round the ratio value */
        long ratio = (ratio1 + 1) >> 1;

        int b=0, m=0;

        //lux calculation for TSL2561_PACKAGE_CS. This is not the AdaFruit default so I'm skipping it.
        /*
        if ((ratio >= 0) && (ratio <= TSL2561_LUX_K1C))
        {b=TSL2561_LUX_B1C; m=TSL2561_LUX_M1C;}
        else if (ratio <= TSL2561_LUX_K2C)
        {b=TSL2561_LUX_B2C; m=TSL2561_LUX_M2C;}
        else if (ratio <= TSL2561_LUX_K3C)
        {b=TSL2561_LUX_B3C; m=TSL2561_LUX_M3C;}
        else if (ratio <= TSL2561_LUX_K4C)
        {b=TSL2561_LUX_B4C; m=TSL2561_LUX_M4C;}
        else if (ratio <= TSL2561_LUX_K5C)
        {b=TSL2561_LUX_B5C; m=TSL2561_LUX_M5C;}
        else if (ratio <= TSL2561_LUX_K6C)
        {b=TSL2561_LUX_B6C; m=TSL2561_LUX_M6C;}
        else if (ratio <= TSL2561_LUX_K7C)
        {b=TSL2561_LUX_B7C; m=TSL2561_LUX_M7C;}
        else if (ratio > TSL2561_LUX_K8C)
        {b=TSL2561_LUX_B8C; m=TSL2561_LUX_M8C;}
        */

        //lux calculation for the TSL2561_PACKAGE_T_FN_CL
        if ((ratio >= 0) && (ratio <= TSL2561_LUX_K1T))
        {b=TSL2561_LUX_B1T; m=TSL2561_LUX_M1T;}
        else if (ratio <= TSL2561_LUX_K2T)
        {b=TSL2561_LUX_B2T; m=TSL2561_LUX_M2T;}
        else if (ratio <= TSL2561_LUX_K3T)
        {b=TSL2561_LUX_B3T; m=TSL2561_LUX_M3T;}
        else if (ratio <= TSL2561_LUX_K4T)
        {b=TSL2561_LUX_B4T; m=TSL2561_LUX_M4T;}
        else if (ratio <= TSL2561_LUX_K5T)
        {b=TSL2561_LUX_B5T; m=TSL2561_LUX_M5T;}
        else if (ratio <= TSL2561_LUX_K6T)
        {b=TSL2561_LUX_B6T; m=TSL2561_LUX_M6T;}
        else if (ratio <= TSL2561_LUX_K7T)
        {b=TSL2561_LUX_B7T; m=TSL2561_LUX_M7T;}
        else if (ratio > TSL2561_LUX_K8T)
        {b=TSL2561_LUX_B8T; m=TSL2561_LUX_M8T;}

        long temp;
        temp = ((channel0 * b) - (channel1 * m));

        /* Do not allow negative lux value */
        if (temp < 0) temp = 0;

        /* Round lsb (2^(LUX_SCALE-1)) */
        temp += (1 << (TSL2561_LUX_LUXSCALE-1));

        /* Strip off fractional portion */
        int lux = (int) (temp >> TSL2561_LUX_LUXSCALE);

        /* Signal I2C had no errors */
        return lux;
    }

    @Override
    public synchronized void enableLed(boolean enable)
    {
        throw new IllegalArgumentException("The AdaFruit light sensor does not support controlling an LED.");
    }

    @Override
    public String status()
    {
        return "";
    }

    //----------------------------------------------------------------------------------------------

/* //not needed for LightSensor ?
    @Override
    public synchronized int getI2cAddress() {
        return this.deviceClient.getI2cAddr();
    }

    @Override
    public synchronized void setI2cAddress(int i2cAddr8Bit) {
        this.deviceClient.setI2cAddr(i2cAddr8Bit);
    }
*/

    private void engage() {
        this.deviceClient.engage();
    }

    private void disengage() {
        this.deviceClient.disengage();
    }

    //Implement Multiplexable
    public void attachToMultiplexer(TCA9548A mux, TCA9548A.MULTIPLEXER_CHANNEL channel)
    {
        deviceClient.attachToMultiplexer(mux, channel);
    }

    //----------------------------------------------------------------------------------------------
    // IOpModeStateTransitionEvents
    //----------------------------------------------------------------------------------------------

    @Override
    synchronized public boolean onUserOpModeStop() {
        this.disable(); //turn the device off to save power
        this.disengage();
        return true;    // unregister us
    }

    @Override
    synchronized public boolean onRobotShutdown() {
        // We actually shouldn't be here by now, having received a onUserOpModeStop()
        // after which we should have been unregistered. But we close down anyway.
        this.close();
        return true;    // unregister us
    }

    //----------------------------------------------------------------------------------------------
    // HardwareDevice
    //----------------------------------------------------------------------------------------------

    @Override
    public void close() {
        this.deviceClient.close();
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public String getConnectionInfo() {
        return this.deviceClient.getConnectionInfo();
    }

    @Override
    public String getDeviceName() {
        return "Swerve AdaFruit I2C Light Sensor";
    }

    //----------------------------------------------------------------------------------------------
    // Sensor
    //----------------------------------------------------------------------------------------------

    private int getRawBroadbandLight()
    {
        //todo In AdaFruit's implementation, they always enable, read, disable. Should we?
        //enable();
        //waitForIntegrationToComplete();
        //int returnValue = readTwoByteUnsignedRegister(REGISTER.CHAN0_LOW);
        //disable();
        //return returnValue;
        return readTwoByteUnsignedRegister(REGISTER.CHAN0_LOW);
    }

    private int getRawIRSpectrumLight()
    {
        //todo In AdaFruit's implementation, they always enable, read, disable. Should we?
        // No for now.
        //enable();
        //waitForIntegrationToComplete();
        //int returnValue = readTwoByteUnsignedRegister(REGISTER.CHAN0_LOW);
        //disable();
        //return returnValue;
        return readTwoByteUnsignedRegister(REGISTER.CHAN1_LOW);
    }

    @Override public synchronized byte read8(final REGISTER reg) {
        //this device likes the COMMAND bit to be set when specifying registers
        return deviceClient.read8(reg.byteVal | TSL2561_COMMAND_BIT);
    }

    @Override public synchronized byte[] read(final REGISTER reg, final int cb) {
        //this device likes the COMMAND bit to be set when specifying registers,
        //todo the device also has a WORD bit to be set when reading or writing a word instead of a byte; should we use it? I think not.
        return deviceClient.read(reg.byteVal | TSL2561_COMMAND_BIT /*| TSL2561_WORD_BIT */, cb);
    }

    @Override public void write8(REGISTER reg, int data) {
        //this device likes the COMMAND bit to be set when specifying registers
        this.deviceClient.write8(reg.byteVal | TSL2561_COMMAND_BIT, data);
        this.deviceClient.waitForWriteCompletions();
    }

    @Override public void write(REGISTER reg, byte[] data) {
        //this device likes the COMMAND bit to be set when specifying registers,
        //todo the device also has a WORD bit to be set when reading or writing a word instead of a byte; should we use it? I think not.
        this.deviceClient.write(reg.byteVal | TSL2561_COMMAND_BIT /*| TSL2561_WORD_BIT*/, data);
        this.deviceClient.waitForWriteCompletions();
    }

    public int readTwoByteUnsignedRegister(REGISTER reg) {
        byte[] bytes = this.read(reg, 2);
        int result = 0;

        if (bytes.length==2)
        {
            //colors are two bytes, unsigned
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            //make unsigned by and'ing with 0x0000FFFF
            //this will cause Java to treat the resulting value as an unsigned int, rather than as a signed short
            //because the most significant bit won't be 1 (which denotes "is negative" in 2's complement numbers)
            result = buffer.getShort() & 0xFFFF;
        }

        return result;
    }


    public int readTwoByteSignedRegister(REGISTER reg) {
        byte[] bytes = this.read(reg, 2);
        int result = 0;

        if (bytes.length==2)
        {
            //read a two-byte signed number
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            result = buffer.getShort();
        }

        return result;
    }

    @Override public void writeTwoByteRegister(REGISTER ireg, int value)
    {
        byte[] b = new byte[2];
        b[0] = (byte) ((value & 0x0000FF00) >> 8); //most significant byte
        b[1] = (byte)  (value & 0x000000FF); //least significant byte
        this.write(ireg, b);
    }


    void delayExtra(int ms) {
        delay(ms + 10);
    }

    /**
     * delayLore() implements a delay that is specified in the device datasheet and therefore should be correct
     *
     * @see #delay(int)
     */
    void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            handleCapturedInterrupt(e);
        }
    }

    /**
     * delayLore() implements a delay that only known by lore and mythology to be necessary.
     *
     * @see #delay(int)
     */
    private void delayLore(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            handleCapturedInterrupt(e);
        }
    }


}

/*
  some code borrowed from Adafruit's sample implementation at:
  https://github.com/adafruit/Adafruit_TSL2561
 */

