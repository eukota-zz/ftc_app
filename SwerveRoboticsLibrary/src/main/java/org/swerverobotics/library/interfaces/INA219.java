package org.swerverobotics.library.interfaces;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.util.TypeConversion;

import java.nio.ByteOrder;

/**
 * Interface API to the Adafruit INA219 current sensor.
 * You can create an implementation of this interface for a given sensor using
 * {@link org.swerverobotics.library.ClassFactory#createAdaFruitINA219(OpMode, I2cDevice) ClassFactory.createAdaFruitINA219()}.
 *
 * @see org.swerverobotics.library.ClassFactory#createAdaFruitINA219(OpMode, I2cDevice)
 * @see <a href="http://www.adafruit.com/products/904">http://www.adafruit.com/products/904</a>
 */
public interface INA219
{
    //----------------------------------------------------------------------------------------------
    // Construction 
    //----------------------------------------------------------------------------------------------

    /**
     * Initialize the sensor using the indicated set of parameters.
     *
     * @param parameters the parameters with which to initialize the INA219
     */
    void initialize(Parameters parameters);

    /**
     * Instances of Parameters contain data indicating how a BNO055 absolute orientation
     * sensor is to be initialized.
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
         *  the value of the shunt resistor
         */
        public int shuntResistorInOhms = 100; //default resistor

        /**
         *  the range of the sensor in volts
         */
        public int rangeInVolts = 32; //default range

        /**
         *  the sensitivity of the sensor
         */
        public int sensitivityInMilliamps = 100; //default sensitivity



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
    // Reading sensor output
    //----------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    // Status inquiry
    //----------------------------------------------------------------------------------------------

    public void setCalibration(int rangeInVolts, int sensitivityInMilliamps, int shuntResistorInOhms);

    public double getBusVoltage_V();

    public double getShuntVoltage_mV();

    public double getCurrent_mA();

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
     * Low level: read two byts of data starting at the indicated register
     * and return the results as an integer
     *
     * @param ireg the location from which to read the data; should be an integer register.
     * @return the data that was read
     */
    int readIntegerRegister(REGISTER ireg);


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


    //------------------------------------------------------------------------------------------
    // Constants
    //------------------------------------------------------------------------------------------

    //This board/chip uses I2C 7-bit addresses 0x40, 0x41, 0x44, 0x45, selectable with jumpers
    enum I2CADDR {
        UNSPECIFIED(-1), DEFAULT(0x40), ADDR_41(0x41), ADDR_44(0x44), ADDR_45(0x45);
        public final byte bVal;

        I2CADDR(int i) {
            bVal = (byte) i;
        }
    }

    /**
     * REGISTER provides symbolic names for each of the INA219 device registers
     */
    enum REGISTER {
        CONFIGURATION(0x00),
        SHUNT_VOLTAGE(0x01),
        BUS_VOLTAGE(0x02),
        POWER(0x03),
        CURRENT(0x04),
        CALIBRATION(0x05);

        //------------------------------------------------------------------------------------------
        public final byte bVal;

        private REGISTER(int i) {
            this.bVal = (byte) i;
        }
    }

    /*=========================================================================
    I2C ADDRESS/BITS
    -----------------------------------------------------------------------*/
    public static final int INA219_ADDRESS = (0x40);    // 1000000 (A0+A1=GND)
    public static final int INA219_READ = (0x01);
/*=========================================================================*/

    /*=========================================================================
        CONFIG REGISTER (R/W)
        -----------------------------------------------------------------------*/
    public static final int INA219_REG_CONFIG = (0x00);
    /*---------------------------------------------------------------------*/
    public static final int INA219_CONFIG_RESET = (0x8000);  // Reset Bit

    public static final int INA219_CONFIG_BVOLTAGERANGE_MASK = (0x2000);  // Bus Voltage Range Mask
    public static final int INA219_CONFIG_BVOLTAGERANGE_16V = (0x0000); // 0-16V Range
    public static final int INA219_CONFIG_BVOLTAGERANGE_32V = (0x2000);// 0-32V Range

    public static final int INA219_CONFIG_GAIN_MASK = (0x1800);  // Gain Mask
    public static final int INA219_CONFIG_GAIN_1_40MV = (0x0000);  // Gain 1, 40mV Range
    public static final int INA219_CONFIG_GAIN_2_80MV = (0x0800);  // Gain 2, 80mV Range
    public static final int INA219_CONFIG_GAIN_4_160MV = (0x1000);  // Gain 4, 160mV Range
    public static final int INA219_CONFIG_GAIN_8_320MV = (0x1800);  // Gain 8, 320mV Range

    public static final int INA219_CONFIG_BADCRES_MASK = (0x0780);  // Bus ADC Resolution Mask
    public static final int INA219_CONFIG_BADCRES_9BIT = (0x0080);  // 9-bit bus res = 0..511
    public static final int INA219_CONFIG_BADCRES_10BIT = (0x0100);  // 10-bit bus res = 0..1023
    public static final int INA219_CONFIG_BADCRES_11BIT = (0x0200);  // 11-bit bus res = 0..2047
    public static final int INA219_CONFIG_BADCRES_12BIT = (0x0400);  // 12-bit bus res = 0..4097

    public static final int INA219_CONFIG_SADCRES_MASK = (0x0078);  // Shunt ADC Resolution and Averaging Mask
    public static final int INA219_CONFIG_SADCRES_9BIT_1S_84US = (0x0000);  // 1 x 9-bit shunt sample
    public static final int INA219_CONFIG_SADCRES_10BIT_1S_148US = (0x0008);  // 1 x 10-bit shunt sample
    public static final int INA219_CONFIG_SADCRES_11BIT_1S_276US = (0x0010);  // 1 x 11-bit shunt sample
    public static final int INA219_CONFIG_SADCRES_12BIT_1S_532US = (0x0018);  // 1 x 12-bit shunt sample
    public static final int INA219_CONFIG_SADCRES_12BIT_2S_1060US = (0x0048);     // 2 x 12-bit shunt samples averaged together
    public static final int INA219_CONFIG_SADCRES_12BIT_4S_2130US = (0x0050);  // 4 x 12-bit shunt samples averaged together
    public static final int INA219_CONFIG_SADCRES_12BIT_8S_4260US = (0x0058);  // 8 x 12-bit shunt samples averaged together
    public static final int INA219_CONFIG_SADCRES_12BIT_16S_8510US = (0x0060);  // 16 x 12-bit shunt samples averaged together
    public static final int INA219_CONFIG_SADCRES_12BIT_32S_17MS = (0x0068);  // 32 x 12-bit shunt samples averaged together
    public static final int INA219_CONFIG_SADCRES_12BIT_64S_34MS = (0x0070);  // 64 x 12-bit shunt samples averaged together
    public static final int INA219_CONFIG_SADCRES_12BIT_128S_69MS = (0x0078);  // 128 x 12-bit shunt samples averaged together

    public static final int INA219_CONFIG_MODE_MASK = (0x0007);  // Operating Mode Mask
    public static final int INA219_CONFIG_MODE_POWERDOWN = (0x0000);
    public static final int INA219_CONFIG_MODE_SVOLT_TRIGGERED = (0x0001);
    public static final int INA219_CONFIG_MODE_BVOLT_TRIGGERED = (0x0002);
    public static final int INA219_CONFIG_MODE_SANDBVOLT_TRIGGERED = (0x0003);
    public static final int INA219_CONFIG_MODE_ADCOFF = (0x0004);
    public static final int INA219_CONFIG_MODE_SVOLT_CONTINUOUS = (0x0005);
    public static final int INA219_CONFIG_MODE_BVOLT_CONTINUOUS = (0x0006);
    public static final int INA219_CONFIG_MODE_SANDBVOLT_CONTINUOUS = (0x0007);
/*=========================================================================*/

    /*=========================================================================
        SHUNT VOLTAGE REGISTER (R)
        -----------------------------------------------------------------------*/
    public static final int INA219_REG_SHUNTVOLTAGE = (0x01);
/*=========================================================================*/

    /*=========================================================================
        BUS VOLTAGE REGISTER (R)
        -----------------------------------------------------------------------*/
    public static final int INA219_REG_BUSVOLTAGE = (0x02);
/*=========================================================================*/

    /*=========================================================================
        POWER REGISTER (R)
        -----------------------------------------------------------------------*/
    public static final int INA219_REG_POWER = (0x03);
/*=========================================================================*/

    /*=========================================================================
        CURRENT REGISTER (R)
        -----------------------------------------------------------------------*/
    public static final int INA219_REG_CURRENT = (0x04);
/*=========================================================================*/

    /*=========================================================================
        CALIBRATION REGISTER (R/W)
        -----------------------------------------------------------------------*/
    public static final int INA219_REG_CALIBRATION = (0x05);
/*=========================================================================*/


}


