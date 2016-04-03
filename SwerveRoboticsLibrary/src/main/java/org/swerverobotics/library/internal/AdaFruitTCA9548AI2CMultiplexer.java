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
 * This device has no registers. You switch to the I2C device 0..7
 * attached to the multiplexer by writing the value 1 << (0..7)
 * to this device's I2C address.
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

        //Unfortunately there's no register we can read to confirm this is the device we think it is.
    }


    //----------------------------------------------------------------------------------------------
    // Control the multiplexer
    //----------------------------------------------------------------------------------------------

    //Note: the multiplexer actually allows you to have multiple channels on at a time.
    //I'm not currently making use of that capability.
    @Override
    public void switchToChannel(int channel) throws IllegalArgumentException
    {
        if (channel > 7)
        {
            throw new IllegalArgumentException("Multiplexer port must be 0..7 but was " + channel);
        }
        write8((byte)(1 << channel));
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
    // Data retrieval
    //------------------------------------------------------------------------------------------


    public void write8(byte data) {
        //This device expects us to write a single byte, not a register address + a byte.
        //But, deviceClient insists that we give it both a register address and a value.
        //So, we'll send the same data twice. It's a tiny bit wasteful but doesn't hurt anything.
        this.deviceClient.write8(0, data);
        this.deviceClient.waitForWriteCompletions();
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
     * delay() implements delays which are known to be necessary according to the specification
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
