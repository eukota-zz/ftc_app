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

        double result = 0;

        if (parameters.integrationTime == INTEGRATION_TIME.MS_13) result = (raw/TSL2561_MAX_RAW_VALUE_13MS);
        else if (parameters.integrationTime == INTEGRATION_TIME.MS_101) result = (raw/TSL2561_MAX_RAW_VALUE_101MS);
        else if (parameters.integrationTime == INTEGRATION_TIME.MS_402) result = (raw/TSL2561_MAX_RAW_VALUE_402MS);

        return result;
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

        byte temp = bytes[0];
        bytes[0] = bytes[1];
        bytes[1] = temp;

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

