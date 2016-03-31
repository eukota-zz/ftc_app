package org.swerverobotics.library.internal;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.exceptions.UnexpectedI2CDeviceException;
import org.swerverobotics.library.interfaces.I2cDeviceSynchUser;
import org.swerverobotics.library.interfaces.TCA9548A;

import java.nio.ByteBuffer;

import static org.swerverobotics.library.internal.Util.handleCapturedInterrupt;

/**
 * Instances of AdaFruitTCA9548AI2CMultiplexer provide API access to an
 * <a href="https://www.adafruit.com/products/2717">AdaFruit I2C Multiplexer</a> that
 * is attached to a Modern Robotics Core Device Interface module.
 *
 * Register Set
 *
 *  HEX      Register Name      Value at Power On      Register Type     Notes
 *  00       Control                   0                 R/W             Allows you to switch to I2C device 0..7 attached to the multiplexer
 */
public final class AdaFruitTCA9548AI2CMultiplexer implements I2cDeviceSynchUser, TCA9548A, IOpModeStateTransitionEvents
{

    //------------------------------------------------------------------------------------------
    // State
    //------------------------------------------------------------------------------------------

    private final OpMode opmodeContext;
    private final I2cDeviceSynch deviceClient;

    private Parameters parameters;

    // We always read as much as we can when we have nothing else to do
    private static final I2cDeviceSynch.ReadMode readMode = I2cDeviceSynch.ReadMode.REPEAT;


    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    /**
     * Instantiate an AdaFruitTCA9548AI2CMultiplexer on the indicated device whose I2C address is the one indicated.
     */
    public AdaFruitTCA9548AI2CMultiplexer(OpMode opmodeContext, I2cDevice i2cDevice, Parameters params) {
        this.opmodeContext = opmodeContext;

        this.deviceClient = ClassFactory.createI2cDeviceSynch(i2cDevice, params.i2cAddress.bVal * 2);

        this.deviceClient.engage();

        this.deviceClient.enableWriteCoalescing(false);

        this.deviceClient.setLogging(params.loggingEnabled);
        this.deviceClient.setLoggingTag(params.loggingTag);

        this.parameters = params;
    }

    /**
     * Instantiate an AdaFruitTCA9548AI2CMultiplexer and then initialize it with the indicated set of parameters.
     */
    public static TCA9548A create(OpMode opmodeContext, I2cDevice i2cDevice, Parameters parameters)
    {
        // Create a sensor which is a client of i2cDevice
        TCA9548A result = new AdaFruitTCA9548AI2CMultiplexer(opmodeContext, i2cDevice, parameters);

        // Initialize it with the indicated parameters
        result.initialize(parameters);
        return result;
    }

    @Override synchronized public boolean onUserOpModeStop()
    {
        this.deviceClient.close();
        return true;
    }

    @Override synchronized public boolean onRobotShutdown()
    {
        this.deviceClient.close();
        return true;
    }

    public void initialize(Parameters parameters)
    {
        // Remember the parameters for future use
        this.parameters = parameters;

        //@todo is there any register we can read to be sure it's the device we think it is?
    }




    //------------------------------------------------------------------------------------------
    // I2cDeviceSynchUser
    //------------------------------------------------------------------------------------------

    @Override
    public I2cDeviceSynch getI2cDeviceSynch()
    {
        return this.deviceClient;
    }


    //------------------------------------------------------------------------------------------
    // Calibration
    //------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------
    // Data retrieval
    //------------------------------------------------------------------------------------------


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

    @Override public int readTwoByteSignedRegister(REGISTER ireg) {
        byte[] bytes = this.read(ireg, 2);
        int result = 0;

        if (bytes.length==2)
        {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            result = buffer.getShort(); //signed integer
        }

        return result;
    }

    @Override public int readTwoByteUnsignedRegister(REGISTER ireg) {
        byte[] bytes = this.read(ireg, 2);
        int result = 0;

        if (bytes.length==2)
        {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            //make unsigned by and'ing with 0x0000FFFF
            //this will cause Java to treat the resulting value as an unsigned int, rather than as a signed short
            //because the most significant bit won't be 1 (which denotes "is negative" in 2's complement numbers)
            result = buffer.getShort() & 0xFFFF;
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

    //------------------------------------------------------------------------------------------
    // Internal utility
    //------------------------------------------------------------------------------------------

    private String getLoggingTag() {
        return parameters.loggingTag + ":"; // add suffix so we can filter out our I2C logging if we wish
    }

    private void log_v(String format, Object... args) {
        if (this.parameters.loggingEnabled) {
            String message = String.format(format, args);
            Log.v(getLoggingTag(), message);
        }
    }

    private void log_d(String format, Object... args) {
        if (this.parameters.loggingEnabled) {
            String message = String.format(format, args);
            Log.d(getLoggingTag(), message);
        }
    }

    private void log_w(String format, Object... args) {
        if (this.parameters.loggingEnabled) {
            String message = String.format(format, args);
            Log.w(getLoggingTag(), message);
        }
    }


    // Our write logic doesn't actually know when the I2C writes are issued. All it knows is
    // when the write has made it to the USB Core Device Interface Module. It's a pretty
    // deterministic interval after that that the I2C write occurs, we guess, but we don't
    // really know what that is. To account for this, we slop in some extra time to the
    // delays so that we're not cutting things too close to the edge. And given that this is
    // initialization logic and so not time critical, we err on being generous: the current
    // setting of this extra can undoubtedly be reduced.

    private final static int msExtra = 50;

    private void delayExtra(int ms) {
        delay(ms + msExtra);
    }

    private void delayLoreExtra(int ms) {
        delayLore(ms + msExtra);
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

    /**
     * delay() implements delays which are known to be necessary according to the BNO055 specification
     *
     * @see #delayLore(int)
     */
    private void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            handleCapturedInterrupt(e);
        }
    }
}
