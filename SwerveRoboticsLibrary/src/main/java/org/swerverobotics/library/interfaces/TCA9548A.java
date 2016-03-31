package org.swerverobotics.library.interfaces;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;

/**
 * Interface API to the Adafruit TCA9548A i2c multiplexer.
 * You can create an implementation of this interface for a given sensor using
 * {@link org.swerverobotics.library.ClassFactory#createAdaFruitTCA954A(OpMode, I2cDevice) ClassFactory.createAdaFruitTCA954A()}.
 *
 * @see org.swerverobotics.library.ClassFactory#createAdaFruitTCA954A(OpMode, I2cDevice)
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
        public String loggingTag = "AdaFruitINA219";
    }

    /**
     * Shut down the sensor. This doesn't do anything in the hardware device itself, but rather
     * shuts down any resources (threads, etc) that we use to communicate with it.
     */
    /*void close();*/



    //----------------------------------------------------------------------------------------------
    // Low level reading and writing 
    //----------------------------------------------------------------------------------------------

    /**
     * Low level: read the byte starting at the indicated register
     *
     * @param register the location from which to read the data
     * @return the data that was read
     */
    byte read8(REGISTER register);

    /**
     * Low level: read data starting at the indicated register
     *
     * @param register the location from which to read the data
     * @param cb       the number of bytes to read
     * @return the data that was read
     */
    byte[] read(REGISTER register, int cb);


    /**
     * Low level: read two bytes of data starting at the indicated register
     * and return the results as an unsigned integer
     *
     * @param ireg the location from which to read the data; should be an integer register.
     * @return the data that was read
     */
    int readTwoByteUnsignedRegister(REGISTER ireg);


    /**
     * Low level: read two bytes of data starting at the indicated register
     * and return the results as a signed integer
     *
     * @param ireg the location from which to read the data; should be an integer register.
     * @return the data that was read
     */
    int readTwoByteSignedRegister(REGISTER ireg);

    /**
     * Low level: write a byte to the indicated register
     *
     * @param register the location at which to write the data
     * @param bVal     the data to write
     */
    void write8(REGISTER register, int bVal);

    /**
     * Low level: write data starting at the indicated register
     *
     * @param register the location at which to write the data
     * @param data     the data to write
     */
    void write(REGISTER register, byte[] data);

    /**
     * Low level: write two bytes of data starting at the indicated register
     *
     * @param ireg the location into which to write the data; should be an integer register.
     * @param value the integer to write
     */
    void writeTwoByteRegister(REGISTER ireg, int value);

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
    enum REGISTER {
        CONTROL(0x00); //this device has a single register that lets us pick the i2c device 0..7

        //------------------------------------------------------------------------------------------
        public final byte bVal;

        private REGISTER(int i) {
            this.bVal = (byte) i;
        }
    }


}


