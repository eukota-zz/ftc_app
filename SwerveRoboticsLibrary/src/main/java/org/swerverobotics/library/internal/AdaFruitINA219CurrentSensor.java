package org.swerverobotics.library.internal;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.interfaces.II2cDeviceClient;
import org.swerverobotics.library.interfaces.II2cDeviceClientUser;
import org.swerverobotics.library.interfaces.INA219;

import static junit.framework.Assert.assertTrue;
import static org.swerverobotics.library.interfaces.NavUtil.meanIntegrate;
import static org.swerverobotics.library.interfaces.NavUtil.plus;
import static org.swerverobotics.library.internal.Util.handleCapturedInterrupt;

/**
 * Instances of AdaFruitINA219CurrentSensor provide API access to an
 * <a href="https://www.adafruit.com/products/904">AdaFruit INA219 High Side DC Current Sensor</a> that
 * is attached to a Modern Robotics Core Device Interface module.
 *
 * INA219 Register Set
 *
 *  HEX      Register Name      Value at Power On      Register Type     Notes
 *  00       Configuration            399F                 R/W           All-register reset, settings for bus voltage range, PGA Gain, ADC resolution/averaging.
 *  01       Shunt voltage        Shunt voltage             R            Shunt voltage measurement data.
 *  02       Bus voltage            Bus voltage             R            Bus voltage measurement data.
 *  03       Power measurement        0000                  R            Power measurement data
 *  04       Current measurement      0000                  R            Contains the value of the current flowing through the shunt resistor
 *  05       Calibration              0000                 R/W           Sets full-scale range and LSB of current and power measurements. Overall system calibration
 *
 *
 *
 *      THIS CODE IS CURRENTLY UNTESTED. (pun may have been intended, so sue me.)
 *
 */
public class AdaFruitINA219CurrentSensor implements II2cDeviceClientUser, INA219
{

    //------------------------------------------------------------------------------------------
    // State
    //------------------------------------------------------------------------------------------

    private final OpMode opmodeContext;
    private final II2cDeviceClient deviceClient;

    private Parameters parameters;

    //TODO delete these objects left over from IMU acceleration integration algorithm, we don't use them
    //private final Object dataLock = new Object();
    //private final Object startStopLock = new Object();

    // We always read as much as we can when we have nothing else to do
    private static final II2cDeviceClient.READ_MODE readMode = II2cDeviceClient.READ_MODE.REPEAT;


    // store the calibration value so we can use it when reading
    private int ina219_calValue;

    // The following multipliers are used to convert raw current and power
    // values to mA and mW, taking into account the current config settings
    private int ina219_currentDivider_mA;
    private int ina219_powerDivider_mW;

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    /**
     * Instantiate an AdaFruitINA219CurrentSensor on the indicated device whose I2C address is the one indicated.
     */
    public AdaFruitINA219CurrentSensor(OpMode opmodeContext, I2cDevice i2cDevice, INA219.Parameters params) {
        this.opmodeContext = opmodeContext;

        // Allow the device to auto-close since we don't have special shutdown logic
        this.deviceClient = ClassFactory.createI2cDeviceClient(opmodeContext, ClassFactory.createI2cDevice(i2cDevice), params.i2cAddress.bVal, true);
        this.deviceClient.engage();

        this.parameters = params;
    }

    /**
     * Instantiate an AdaFruitINA219CurrrentSensor and then initialize it with the indicated set of parameters.
     */
    public static INA219 create(OpMode opmodeContext, I2cDevice i2cDevice, INA219.Parameters parameters)
    {
        // Create a sensor which is a client of i2cDevice
        INA219 result = new AdaFruitINA219CurrentSensor(opmodeContext, i2cDevice, parameters);

        // Initialize it with the indicated parameters
        result.initialize(parameters);
        return result;
    }

    public void initialize(Parameters parameters)
    {
        // Remember the parameters for future use
        this.parameters = parameters;

        setCalibration(parameters.rangeInVolts, parameters.sensitivityInMilliamps,  parameters.shuntResistorInOhms);
    }

    //TODO replace the range and sensitivities with enums and use those values in the initialization
    public void setCalibration(int rangeInVolts, int sensitivityInMilliamps, int shuntResistorInOhms) {
        // By default we use a large range for the input voltage,
        // which probably isn't the most appropriate choice for system
        // that don't use a lot of power.  But all of the calculations
        // are shown below if you want to change the settings.  You will
        // also need to change any relevant register settings, such as
        // setting the VBUS_MAX to 16V instead of 32V, etc.

        // VBUS_MAX = 32V             (Assumes 32V, can also be set to 16V)
        // VSHUNT_MAX = 0.32          (Assumes Gain 8, 320mV, can also be 0.16, 0.08, 0.04)
        // RSHUNT = 0.1               (Resistor value in ohms)

        // 1. Determine max possible current
        // MaxPossible_I = VSHUNT_MAX / RSHUNT
        // MaxPossible_I = 3.2A

        // 2. Determine max expected current
        // MaxExpected_I = 2.0A

        // 3. Calculate possible range of LSBs (Min = 15-bit, Max = 12-bit)
        // MinimumLSB = MaxExpected_I/32767
        // MinimumLSB = 0.000061              (61uA per bit)
        // MaximumLSB = MaxExpected_I/4096
        // MaximumLSB = 0,000488              (488uA per bit)

        // 4. Choose an LSB between the min and max values
        //    (Preferrably a roundish number close to MinLSB)
        // CurrentLSB = 0.0001 (100uA per bit)

        // 5. Compute the calibration register
        // Cal = trunc (0.04096 / (Current_LSB * RSHUNT))
        // Cal = 4096 (0x1000)

        ina219_calValue = 4096;

        // 6. Calculate the power LSB
        // PowerLSB = 20 * CurrentLSB
        // PowerLSB = 0.002 (2mW per bit)

        // 7. Compute the maximum current and shunt voltage values before overflow
        //
        // Max_Current = Current_LSB * 32767
        // Max_Current = 3.2767A before overflow
        //
        // If Max_Current > Max_Possible_I then
        //    Max_Current_Before_Overflow = MaxPossible_I
        // Else
        //    Max_Current_Before_Overflow = Max_Current
        // End If
        //
        // Max_ShuntVoltage = Max_Current_Before_Overflow * RSHUNT
        // Max_ShuntVoltage = 0.32V
        //
        // If Max_ShuntVoltage >= VSHUNT_MAX
        //    Max_ShuntVoltage_Before_Overflow = VSHUNT_MAX
        // Else
        //    Max_ShuntVoltage_Before_Overflow = Max_ShuntVoltage
        // End If

        // 8. Compute the Maximum Power
        // MaximumPower = Max_Current_Before_Overflow * VBUS_MAX
        // MaximumPower = 3.2 * 32V
        // MaximumPower = 102.4W

        // Set multipliers to convert raw current/power values
        ina219_currentDivider_mA = 10;  // Current LSB = 100uA per bit (1000/100 = 10)
        ina219_powerDivider_mW = 2;     // Power LSB = 1mW per bit (2/1)

        // Set Calibration register to 'Cal' calculated above
        this.write8(REGISTER.CALIBRATION, ina219_calValue);

        // Set Config register to take into account the settings above
        int config = INA219_CONFIG_BVOLTAGERANGE_32V |
                INA219_CONFIG_GAIN_8_320MV |
                INA219_CONFIG_BADCRES_12BIT |
                INA219_CONFIG_SADCRES_12BIT_1S_532US |
                INA219_CONFIG_MODE_SANDBVOLT_CONTINUOUS;
        this.write8(REGISTER.CONFIGURATION, config);

    }

    public double getBusVoltage_V() {
        double value = getBusVoltage_raw();
        return value * 0.001;
    }

    public double getShuntVoltage_mV() {
        double value = getShuntVoltage_raw();
        return value * 0.01;
    }

    public double getCurrent_mA() {
        double valueDec = getCurrent_raw();
        valueDec /= ina219_currentDivider_mA;
        return valueDec;
    }



    //------------------------------------------------------------------------------------------
    // II2cDeviceClientUser
    //------------------------------------------------------------------------------------------

    @Override
    public II2cDeviceClient getI2cDeviceClient() {
        return this.deviceClient;
    }


    /**
     * Get the raw bus voltage (16-bit signed integer, so +-32767)
     */
    private synchronized int getBusVoltage_raw() {
        int value = this.readIntegerRegister(REGISTER.BUS_VOLTAGE);

        // Shift to the right 3 to drop CNVR and OVF and multiply by LSB
        return (int) ((value >> 3) * 4);
    }


    /**
     * Get the raw shunt voltage (16-bit signed integer, so +-32767)
     */
    private synchronized int getShuntVoltage_raw() {
        int value = this.readIntegerRegister(REGISTER.SHUNT_VOLTAGE);

        return value;
    }


    /**
     * Get the raw current value (16-bit signed integer, so +-32767)
     */
    private synchronized int getCurrent_raw() {
        //Lore from Adafruit:
        // Sometimes a sharp load will reset the INA219, which will
        // reset the cal register, meaning CURRENT and POWER will
        // not be available ... avoid this by always setting a cal
        // value even if it's an unfortunate extra step
        this.write8(REGISTER.CALIBRATION, ina219_calValue);

        // Now we can safely read the CURRENT register!
        int value = this.readIntegerRegister(REGISTER.CURRENT);

        return value;
    }


    //------------------------------------------------------------------------------------------
    // Calibration
    //------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------
    // INA219 data retrieval
    //------------------------------------------------------------------------------------------

    /**
     * We need one register window for reading from the INA219
     *
     */
    /*
    private static final II2cDeviceClient.ReadWindow window = newWindow(INA219.REGISTER.CONFIGURATION, INA219.REGISTER.CALIBRATION);

    private static II2cDeviceClient.ReadWindow newWindow(INA219.REGISTER regFirst, INA219.REGISTER regMax)
    {
        return new II2cDeviceClient.ReadWindow(regFirst.bVal, regMax.bVal-regFirst.bVal, readMode);
    }

    private void ensureReadWindow(II2cDeviceClient.ReadWindow needed)
    {
        II2cDeviceClient.ReadWindow windowToSet = window;
        this.deviceClient.ensureReadWindow(needed, windowToSet);
    }
   */

    @Override public synchronized byte read8(final REGISTER reg) {
        //ensureReadWindow(new II2cDeviceClient.ReadWindow(reg.bVal, 1, readMode));
        return deviceClient.read8(reg.bVal);
    }

    @Override public synchronized byte[] read(final REGISTER reg, final int cb) {
        //ensureReadWindow(new II2cDeviceClient.ReadWindow(reg.bVal, cb, readMode));
        return deviceClient.read(reg.bVal, cb);
    }

    @Override public void write8(REGISTER reg, int data) {
        this.deviceClient.write8(reg.bVal, data);
    }

    @Override public void write(REGISTER reg, byte[] data) {
        this.deviceClient.write(reg.bVal, data);
    }

    public int readIntegerRegister(REGISTER ireg) {
        byte[] bytes = this.read(ireg, 2);
        //return TypeConversion.byteArrayToInt(bytes, ByteOrder.LITTLE_ENDIAN);
        if (bytes.length==2)
        {
            int temp1 = bytes[1];
            int temp2 = bytes[0];

            //handle case in which no current is applied to sensor
            if ( (temp1==0xFF) && (temp2==0xFF)) return 0;

            return (temp1 << 8) + temp2;
        }
        else return 0;
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



// This code is in part modelled after https://github.com/adafruit/Adafruit_INA219

/**************************************************************************/
/*!
    @file     Adafruit_INA219.h
    @author   K. Townsend (Adafruit Industries)
	@license  BSD (see license.txt)

	This is a library for the Adafruit INA219 breakout board
	----> https://www.adafruit.com/products/???

	Adafruit invests time and resources providing this open source code,
	please support Adafruit and open-source hardware by purchasing
	products from Adafruit!
*/
