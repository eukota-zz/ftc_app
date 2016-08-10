package org.swerverobotics.library.interfaces;

import com.qualcomm.robotcore.hardware.LightSensor;

/**
 * https://www.adafruit.com/products/439
 *
 * Some code borrowed from AdaFruit's sample implementation at
 * https://github.com/adafruit/Adafruit_TSL2561
 */
public interface TSL2561LightSensor extends LightSensor, Multiplexable
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
     * Instances of Parameters contain data indicating how the
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

        //default integration time for our implementation
        public INTEGRATION_TIME integrationTime = INTEGRATION_TIME.MS_13;

        //default gain for our implementation
        public GAIN gain = GAIN.GAIN_1;

        public LIGHT_DETECTION_MODE detectionMode = LIGHT_DETECTION_MODE.BROADBAND;

        /**
         * debugging aid: enable logging for this device?
         */
        public boolean loggingEnabled = false;
        /**
         * debugging aid: the logging tag to use when logging
         */
        public String loggingTag = "TSL2561LightSensor";
    }

    /**
     * Shut down the sensor. This doesn't do anything in the hardware device itself, but rather
     * shuts down any resources (threads, etc) that we use to communicate with it.
     */
    /*void close();*/

    //----------------------------------------------------------------------------------------------
    // Reading sensor output
    //----------------------------------------------------------------------------------------------

    //implementers should override the LightSensor methods

    /*
        double getLightDetected();
        int getLightDetectedRaw();
        void enableLed(boolean var1);
        String status();
    */


    //----------------------------------------------------------------------------------------------
    // Status inquiry
    //----------------------------------------------------------------------------------------------

    byte getDeviceID();
    byte getState();


    //----------------------------------------------------------------------------------------------
    // Low level reading and writing
    //----------------------------------------------------------------------------------------------

    /**
     * Low level: read the byte starting at the indicated register
     *
     * @param register the location from which to read the data
     * @return the data that was read
     */
    byte read8(TSL2561LightSensor.REGISTER register);

    /**
     * Low level: read data starting at the indicated register
     *
     * @param register the location from which to read the data
     * @param cb       the number of bytes to read
     * @return the data that was read
     */
    byte[] read(TSL2561LightSensor.REGISTER register, int cb);


    /**
     * Low level: read two bytes of data starting at the indicated register
     * and return the results as an unsigned integer
     *
     * @param reg the location from which to read the data; should be an integer register.
     * @return the data that was read
     */
    int readTwoByteUnsignedRegister(TSL2561LightSensor.REGISTER reg);


    /**
     * Low level: read two bytes of data starting at the indicated register
     * and return the results as a signed integer
     *
     * @param reg the location from which to read the data; should be an integer register.
     * @return the data that was read
     */
    int readTwoByteSignedRegister(TSL2561LightSensor.REGISTER reg);


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
     * @param value the integer to
     */
    void writeTwoByteRegister(REGISTER ireg, int value);

    //------------------------------------------------------------------------------------------
    // Constants
    //------------------------------------------------------------------------------------------

    //This board/chip allows 3 possible i2c addresses.
    // Need to mult that by 2 when passing it to create the I2C device context.
    enum I2CADDR {
        UNSPECIFIED(-1), DEFAULT(0x39), ADDR_29(0x29), ADDR_49(0x49);
        public final byte bVal;

        I2CADDR(int i) {
            bVal = (byte) i;
        }
    }

    /**
     * REGISTER provides symbolic names for interesting device registers
     */

    enum REGISTER {
        CONTROL(0x00),
        TIMING(0x01),
        THRESHHOLDL_LOW(0x02),
        THRESHHOLDL_HIGH(0x03),
        THRESHHOLDH_LOW(0x04),
        THRESHHOLDH_HIGH(0x05),
        INTERRUPT(0x06),
        CRC(0x08),
        ID(0x0A),
        CHAN0_LOW(0x0C),
        CHAN0_HIGH(0x0D),
        CHAN1_LOW(0x0E),
        CHAN1_HIGH(0x0F);

        //------------------------------------------------------------------------------------------
        public final byte byteVal;

        REGISTER(int i) {
            this.byteVal = (byte) i;
        }
    }

    enum GAIN {
        GAIN_1(0x00),  //no gain
        GAIN_16(0x10); //16x gain

        public final byte byteVal;

        GAIN(int i) {
            this.byteVal = (byte) i;
        }
    }

    enum INTEGRATION_TIME {
        MS_13(0x00), //fast but low resolution
        MS_101(0x01), //medium speed and resolution
        MS_402(0x02); //16-bit data but slowest conversions

        public final byte byteVal;

        INTEGRATION_TIME(int i) {
            this.byteVal = (byte) i;
        }
    }


    enum LIGHT_DETECTION_MODE {
        BROADBAND(0x00), //detect both visible and IR light
        INFRARED(0x01),  //detect only IR light
        VISIBLE(0x02);   //detect only visible light

        public final byte byteVal;

        LIGHT_DETECTION_MODE(int i) {
            this.byteVal = (byte) i;
        }
    }


    //------------------------------------------------------------------------------------------
    // Register descriptions
    //------------------------------------------------------------------------------------------
    /*
     ADDRESS     REGISTER NAME     REGISTER FUNCTION
     −−         COMMAND           Specifies register address
     0h          CONTROL           Control of basic functions
     1h          TIMING            Integration time/gain control
     2h          THRESHLOWLOW      Low byte of low interrupt threshold
     3h          THRESHLOWHIGH     High byte of low interrupt threshold
     4h          THRESHHIGHLOW     Low byte of high interrupt threshold
     5h          THRESHHIGHHIGH    High byte of high interrupt threshold
     6h          INTERRUPT         Interrupt control
     7h          −−               Reserved
     8h          CRC               Factory test — not a user register
     9h          −−               Reserved
     Ah          ID                Part number/ Rev ID
     Bh          −−               Reserved
     Ch          DATA0LOW          Low byte of ADC channel 0
     Dh          DATA0HIGH         High byte of ADC channel 0
     Eh          DATA1LOW          Low byte of ADC channel 1
     Fh          DATA1HIGH         High byte of ADC channel 1

    */
    //----------------------------------------------------------------------------------------------

    byte TSL2561_VISIBLE = 2;                   // channel 0 - channel 1
    byte TSL2561_INFRARED = 1;                  // channel 1
    byte TSL2561_FULLSPECTRUM = 0;              // channel 0

    // I2C address options
    //int TSL2561_ADDR_LOW         = (0x29);
    //int TSL2561_ADDR_FLOAT       = (0x39);    // Default address (pin left floating)
    //int TSL2561_ADDR_HIGH        = (0x49);

    byte ADAFRUIT_TSL2561_ID     = (0x50);  //this doesn't match the datasheet I read, but matches actual device?!

    int TSL2561_MAX_RAW_VALUE_13MS                = (0x000013B7);
    int TSL2561_MAX_RAW_VALUE_101MS                = (0x00009139);
    int TSL2561_MAX_RAW_VALUE_402MS                = (0x0000FFFF);

    int TSL2561_INTEGRATION_DELAY_13MS       =  (15);    // These values come from AdaFruit's implementation
    int TSL2561_INTEGRATION_DELAY_101MS      = (120);    // These values come from AdaFruit's implementation
    int TSL2561_INTEGRATION_DELAY_402MS      = (450);    // These values come from AdaFruit's implementation

    // Lux calculations differ slightly for CS package
    //Package options are:
        //TSL2561_PACKAGE_CS
        //TSL2561_PACKAGE_T_FN_CL //this is the default package in the adafruit library

    int TSL2561_COMMAND_BIT      = (0x80);    // Must be 1
    int TSL2561_CLEAR_BIT        = (0x40);    // Clears any pending interrupt (write 1 to clear)
    int TSL2561_WORD_BIT         = (0x20);    // 1 = read/write word (rather than byte)
    int TSL2561_BLOCK_BIT        = (0x10);    // 1 = using block read/write

    int TSL2561_CONTROL_POWERON  = (0x03);
    int TSL2561_CONTROL_POWEROFF = (0x00);

    int TSL2561_LUX_LUXSCALE     = (14);      // Scale by 2^14
    int TSL2561_LUX_RATIOSCALE   = (9);       // Scale ratio by 2^9
    int TSL2561_LUX_CHSCALE      = (10);      // Scale channel values by 2^10
    int TSL2561_LUX_CHSCALE_TINT0 = (0x7517);  // 322/11 * 2^TSL2561_LUX_CHSCALE
    int TSL2561_LUX_CHSCALE_TINT1 = (0x0FE7);  // 322/81 * 2^TSL2561_LUX_CHSCALE

    // T, FN and CL package values
    int TSL2561_LUX_K1T          = (0x0040);  // 0.125 * 2^RATIO_SCALE
    int TSL2561_LUX_B1T          = (0x01f2);  // 0.0304 * 2^LUX_SCALE
    int TSL2561_LUX_M1T          = (0x01be);  // 0.0272 * 2^LUX_SCALE
    int TSL2561_LUX_K2T          = (0x0080);  // 0.250 * 2^RATIO_SCALE
    int TSL2561_LUX_B2T          = (0x0214);  // 0.0325 * 2^LUX_SCALE
    int TSL2561_LUX_M2T          = (0x02d1);  // 0.0440 * 2^LUX_SCALE
    int TSL2561_LUX_K3T          = (0x00c0);  // 0.375 * 2^RATIO_SCALE
    int TSL2561_LUX_B3T          = (0x023f);  // 0.0351 * 2^LUX_SCALE
    int TSL2561_LUX_M3T          = (0x037b);  // 0.0544 * 2^LUX_SCALE
    int TSL2561_LUX_K4T          = (0x0100);  // 0.50 * 2^RATIO_SCALE
    int TSL2561_LUX_B4T          = (0x0270);  // 0.0381 * 2^LUX_SCALE
    int TSL2561_LUX_M4T          = (0x03fe);  // 0.0624 * 2^LUX_SCALE
    int TSL2561_LUX_K5T          = (0x0138);  // 0.61 * 2^RATIO_SCALE
    int TSL2561_LUX_B5T          = (0x016f);  // 0.0224 * 2^LUX_SCALE
    int TSL2561_LUX_M5T          = (0x01fc);  // 0.0310 * 2^LUX_SCALE
    int TSL2561_LUX_K6T          = (0x019a);  // 0.80 * 2^RATIO_SCALE
    int TSL2561_LUX_B6T          = (0x00d2);  // 0.0128 * 2^LUX_SCALE
    int TSL2561_LUX_M6T          = (0x00fb);  // 0.0153 * 2^LUX_SCALE
    int TSL2561_LUX_K7T          = (0x029a);  // 1.3 * 2^RATIO_SCALE
    int TSL2561_LUX_B7T          = (0x0018);  // 0.00146 * 2^LUX_SCALE
    int TSL2561_LUX_M7T          = (0x0012);  // 0.00112 * 2^LUX_SCALE
    int TSL2561_LUX_K8T          = (0x029a);  // 1.3 * 2^RATIO_SCALE
    int TSL2561_LUX_B8T          = (0x0000);  // 0.000 * 2^LUX_SCALE
    int TSL2561_LUX_M8T          = (0x0000);  // 0.000 * 2^LUX_SCALE

    // CS package values
    int TSL2561_LUX_K1C          = (0x0043);  // 0.130 * 2^RATIO_SCALE
    int TSL2561_LUX_B1C          = (0x0204);  // 0.0315 * 2^LUX_SCALE
    int TSL2561_LUX_M1C          = (0x01ad);  // 0.0262 * 2^LUX_SCALE
    int TSL2561_LUX_K2C          = (0x0085);  // 0.260 * 2^RATIO_SCALE
    int TSL2561_LUX_B2C          = (0x0228);  // 0.0337 * 2^LUX_SCALE
    int TSL2561_LUX_M2C          = (0x02c1);  // 0.0430 * 2^LUX_SCALE
    int TSL2561_LUX_K3C          = (0x00c8);  // 0.390 * 2^RATIO_SCALE
    int TSL2561_LUX_B3C          = (0x0253);  // 0.0363 * 2^LUX_SCALE
    int TSL2561_LUX_M3C          = (0x0363);  // 0.0529 * 2^LUX_SCALE
    int TSL2561_LUX_K4C          = (0x010a);  // 0.520 * 2^RATIO_SCALE
    int TSL2561_LUX_B4C          = (0x0282);  // 0.0392 * 2^LUX_SCALE
    int TSL2561_LUX_M4C          = (0x03df);  // 0.0605 * 2^LUX_SCALE
    int TSL2561_LUX_K5C          = (0x014d);  // 0.65 * 2^RATIO_SCALE
    int TSL2561_LUX_B5C          = (0x0177);  // 0.0229 * 2^LUX_SCALE
    int TSL2561_LUX_M5C          = (0x01dd);  // 0.0291 * 2^LUX_SCALE
    int TSL2561_LUX_K6C          = (0x019a);  // 0.80 * 2^RATIO_SCALE
    int TSL2561_LUX_B6C          = (0x0101);  // 0.0157 * 2^LUX_SCALE
    int TSL2561_LUX_M6C          = (0x0127);  // 0.0180 * 2^LUX_SCALE
    int TSL2561_LUX_K7C          = (0x029a);  // 1.3 * 2^RATIO_SCALE
    int TSL2561_LUX_B7C          = (0x0037);  // 0.00338 * 2^LUX_SCALE
    int TSL2561_LUX_M7C          = (0x002b);  // 0.00260 * 2^LUX_SCALE
    int TSL2561_LUX_K8C          = (0x029a);  // 1.3 * 2^RATIO_SCALE
    int TSL2561_LUX_B8C          = (0x0000);  // 0.000 * 2^LUX_SCALE
    int TSL2561_LUX_M8C          = (0x0000);  // 0.000 * 2^LUX_SCALE

    // Auto-gain thresholds
    int TSL2561_AGC_THI_13MS     = (4850);    // Max value at Ti 13ms = 5047
    int TSL2561_AGC_TLO_13MS     = (100);
    int TSL2561_AGC_THI_101MS    = (36000);   // Max value at Ti 101ms = 37177
    int TSL2561_AGC_TLO_101MS    = (200);
    int TSL2561_AGC_THI_402MS    = (63000);   // Max value at Ti 402ms = 65535
    int TSL2561_AGC_TLO_402MS    = (500);

    // Clipping thresholds
    int TSL2561_CLIPPING_13MS    = (4900);
    int TSL2561_CLIPPING_101MS   = (37000);
    int TSL2561_CLIPPING_402MS   = (65000);

}


