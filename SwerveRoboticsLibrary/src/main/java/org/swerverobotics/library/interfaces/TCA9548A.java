package org.swerverobotics.library.interfaces;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;

/**
 * Interface API to the Adafruit TCA9548A i2c multiplexer.
 * You can create an implementation of this interface for a given multiplexer using
 * {@link org.swerverobotics.library.ClassFactory#createAdaFruitTCA9548A(OpMode, I2cDevice) ClassFactory.createAdaFruitTCA954A()}.
 *
 * Then, add
 *
 * @see org.swerverobotics.library.ClassFactory#createAdaFruitTCA9548A(OpMode, I2cDevice)
 * @see <a href="http://www.adafruit.com/products/2717">http://www.adafruit.com/products/2717</a>
 */
public interface TCA9548A
{
    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    /**
     * Initialize the sensor using the indicated set of parameters.
     *
     * @param parameters the parameters with which to initialize the device
     */
    void initialize(Parameters parameters);

    /**
     * Instances of Parameters contain data indicating how the device
     * is to be initialized.
     *
     * @see #initialize(Parameters)
     */
    public static class Parameters
    {
        /**
         * the address at which the sensor resides on the I2C bus.
         */
        public I2CADDR i2cAddress = I2CADDR.DEFAULT;

        /**
         * debugging aid: enable logging for this device?
         */
        public boolean loggingEnabled = false;
        /**
         * debugging aid: the logging tag to use when logging
         */
        public String loggingTag = "AdaFruitTCA9548AMultiplexer";
    }

    /**
     * Shut down the sensor. This doesn't do anything in the hardware device itself, but rather
     * shuts down any resources (threads, etc) that we use to communicate with it.
     */
    /*void close();*/

    //----------------------------------------------------------------------------------------------
    // Control the multiplexer
    //----------------------------------------------------------------------------------------------

    void addMultiplexableDevice(Multiplexable multiplexableDevice, MULTIPLEXER_CHANNEL channel);

    void switchToChannel(MULTIPLEXER_CHANNEL channel) throws IllegalArgumentException;

    int NUM_CHANNELS = 8; //the multiplexer has 8 channels
    enum MULTIPLEXER_CHANNEL
    {
        CHANNEL0(0x00),
        CHANNEL1(0x01),
        CHANNEL2(0x02),
        CHANNEL3(0x03),
        CHANNEL4(0x04),
        CHANNEL5(0x05),
        CHANNEL6(0x06),
        CHANNEL7(0x07);

        //------------------------------------------------------------------------------------------
        public final int iVal;

        MULTIPLEXER_CHANNEL(int i) {
            this.iVal = i;
        }
    }


    //------------------------------------------------------------------------------------------
    // Constants
    //------------------------------------------------------------------------------------------

    //This board/chip uses I2C 7-bit addresses 0x70 - 0x70, selectable with jumpers
    enum I2CADDR {
        UNSPECIFIED(-1), DEFAULT(0x70), ADDR_71(0x71), ADDR_72(0x72), ADDR_73(0x73),
        ADDR_74(0x74), ADDR_75(0x75), ADDR_76(0x76), ADDR_77(0x77);
        public final byte bVal;

        I2CADDR(int i) {
            bVal = (byte) i;
        }
    }

    /**
     * REGISTER provides symbolic names for each of the device registers
     */
    //This device does not use registers.
    //To control the device, we write a single byte (0..7) to the device's i2c address to switch to that port number.


}


