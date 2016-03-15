package org.swerverobotics.library.internal;

import android.graphics.Color;

import com.qualcomm.hardware.adafruit.*;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.*;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.exceptions.UnexpectedI2CDeviceException;
import org.swerverobotics.library.interfaces.INA219;
import org.swerverobotics.library.interfaces.TCS34725;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.swerverobotics.library.internal.Util.handleCapturedInterrupt;

/**
 * http://adafru.it/1334
 * https://www.adafruit.com/products/1334?&main_page=product_info&products_id=1334
 * https://github.com/adafruit/Adafruit_TCS34725
 */
public class AdaFruitTCS34725ColorSensor implements TCS34725, IOpModeStateTransitionEvents
{
    private final OpMode opmodeContext;
    private final I2cDeviceSynch deviceClient;
    private TCS34725.Parameters parameters;

    //private final I2cDeviceSynch i2CDeviceSynch;

    //@todo remove led support since it's not controllable via the i2c port. Interested students can use a digital channel to control it independently.
    //boolean ledIsEnabled;
    //boolean ledStateIsKnown;
    //I2cDeviceReplacementHelper<ColorSensor> helper;

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    public AdaFruitTCS34725ColorSensor(OpMode opmodeContext, I2cDevice i2cDevice, TCS34725.Parameters params) {
        this.opmodeContext = opmodeContext;

        this.deviceClient = ClassFactory.createI2cDeviceSynch(i2cDevice, params.i2cAddress.bVal * 2);

        this.deviceClient.enableWriteCoalescing(false);

        this.deviceClient.setLogging(params.loggingEnabled);
        this.deviceClient.setLoggingTag(params.loggingTag);
        this.engage();

        this.parameters = params;
    }

    public static TCS34725 create(OpMode opmodeContext, I2cDevice i2cDevice, TCS34725.Parameters parameters)
    {
        // Create a sensor which is a client of i2cDevice
        TCS34725 result = new AdaFruitTCS34725ColorSensor(opmodeContext, i2cDevice, parameters);

        // Initialize it with the indicated parameters
        result.initialize(parameters);
        return result;
    }

    public void initialize(Parameters parameters)
    {
        // Remember the parameters for future use
        this.parameters = parameters;

        //resetINA219();

        boolean armed = this.deviceClient.isArmed();

        byte[] buffer = this.read(REGISTER.ENABLE, 2);

        byte aa = REGISTER.DEVICE_ID.bVal;
        byte bb = REGISTER.ATIME.bVal;
        byte cc = REGISTER.ENABLE.bVal;

        byte id = this.getDeviceID();

        byte atime = this.read8(REGISTER.ATIME);

       /* if (id != ADAFRUIT_TCS34725_PARTNUM)
        {
            throw new UnexpectedI2CDeviceException(id);
        }
        */

        // Set the gain and integration time
        //write8(TCS34725_ATIME, parameters.integrationTime.byteVal);
        //write8(TCS34725_CONTROL, parameters.gain.byteVal);

        // Enable the device
        write8(REGISTER.ENABLE, TCS34725_ENABLE_PON | TCS34725_ENABLE_AEN );

        delayLore(10);

        byte enableReg = read8(REGISTER.ENABLE);

        int c = enableReg | 0x00;



        //write8(TCS34725_ENABLE, TCS34725_ENABLE_PON);
       // delayExtra(3);
        //this.i2CDeviceSynch.write8(TCS34725_ENABLE, TCS34725_ENABLE_PON | TCS34725_ENABLE_AEN);

        //setCalibration(parameters);
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

    private void engage() {
        /*
        if (!this.helper.isEngaged()) {
            this.helper.engage();
            this.i2CDeviceSynch.engage();
        }
        */
        this.deviceClient.engage();
    }

    private void disengage() {
        /*
        if (this.helper.isEngaged()) {
            this.i2CDeviceSynch.disengage();
            this.helper.disengage();
        }
        */
        this.deviceClient.disengage();
    }

    //----------------------------------------------------------------------------------------------
    // IOpModeStateTransitionEvents
    //----------------------------------------------------------------------------------------------

    @Override
    synchronized public boolean onUserOpModeStop() {
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

    //@Override
    public void close() {
        this.deviceClient.close();
    }

    //@Override
    public int getVersion() {
        return 1;
    }

    //@Override
    public String getConnectionInfo() {
        return this.deviceClient.getConnectionInfo();
    }

    //@Override
    public String getDeviceName() {
        return "Swerve AdaFruit I2C Color Sensor";
    }

    //----------------------------------------------------------------------------------------------
    // ColorSensor
    //----------------------------------------------------------------------------------------------

    @Override public synchronized byte read8(final REGISTER reg) {
        return deviceClient.read8(reg.bVal);
    }

    @Override public synchronized byte[] read(final REGISTER reg, final int cb) {
        return deviceClient.read(reg.bVal, cb);
    }

    @Override public void write8(REGISTER reg, int data) {
        this.deviceClient.write8(reg.bVal, data);
        this.deviceClient.waitForWriteCompletions();
    }

    @Override public void write(REGISTER reg, byte[] data) {
        this.deviceClient.write(reg.bVal, data);
        this.deviceClient.waitForWriteCompletions();
    }


    @Override
    public int readTwoByteRegister(TCS34725.REGISTER ireg) {
        byte[] bytes = this.read(ireg, 2);
        int result = 0;

        if (bytes.length==2)
        {
            //INA219 data sheet says that register values are sent most-significant-byte first
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            result = buffer.getShort();
        }

        return result;
    }

    @Override public void writeTwoByteRegister(TCS34725.REGISTER ireg, int value)
    {
        //INA219 data sheet says that register values are sent most-significant-byte first
        byte[] b = new byte[2];
        b[0] = (byte) ((value & 0x0000FF00) >> 8); //most significant byte
        b[1] = (byte)  (value & 0x000000FF); //least significant byte
        this.write(ireg, b);
    }

    private synchronized byte getDeviceID()
    {
        byte b = this.read8(REGISTER.DEVICE_ID);
        return b;
    }

    //@Override
    public synchronized int red() {
        return this.readTwoByteRegister(REGISTER.RED);
    }

    //@Override
    public synchronized int green() {
        return this.readTwoByteRegister(REGISTER.GREEN);
    }

    //@Override
    public synchronized int blue() {
        return this.readTwoByteRegister(REGISTER.BLUE);
    }

    //@Override
    public synchronized int alpha() {
        return this.readTwoByteRegister(REGISTER.CLEAR);
    }

    //@Override
    public synchronized int argb() {
        return Color.argb(this.alpha(), this.red(), this.green(), this.blue());
    }

    //@Override
    public synchronized void enableLed(boolean enable)
    // We can't directly control the LED with I2C; it's always on
    {
        /*
        if (!this.ledStateIsKnown || this.ledIsEnabled != enable) {
            if (enable) {
                this.ledIsEnabled = enable;
                this.ledStateIsKnown = true;
            } else
                throw new IllegalArgumentException("disabling LED is not supported");
        }
        */
        //throw new IllegalArgumentException("controlling LED is not supported on the Adafruit color sensor; use a digital channel for that.");
    }

    //@Override
    public synchronized int getI2cAddress() {
        return this.deviceClient.getI2cAddr();
    }

    //@Override
    public synchronized void setI2cAddress(int i2cAddr8Bit) {
        this.deviceClient.setI2cAddr(i2cAddr8Bit);
    }




}

