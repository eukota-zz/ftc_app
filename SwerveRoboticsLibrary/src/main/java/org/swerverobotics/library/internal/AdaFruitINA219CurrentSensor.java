package org.swerverobotics.library.internal;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.exceptions.UnexpectedI2CDeviceException;
import org.swerverobotics.library.interfaces.II2cDeviceClient;
import org.swerverobotics.library.interfaces.II2cDeviceClientUser;
import org.swerverobotics.library.interfaces.INA219;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

    // We always read as much as we can when we have nothing else to do
    private static final II2cDeviceClient.READ_MODE readMode = II2cDeviceClient.READ_MODE.REPEAT;

    // store the calibration value so we can use it when reading
    private int ina219_calValue;

    // The following multipliers are used to convert raw current and power
    // values to mA and mW, taking into account the current config settings
    private double ina219_currentMultiplier_mA;
    private double ina219_powerMultiplier_mW;

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    /**
     * Instantiate an AdaFruitINA219CurrentSensor on the indicated device whose I2C address is the one indicated.
     */
    public AdaFruitINA219CurrentSensor(OpMode opmodeContext, I2cDevice i2cDevice, INA219.Parameters params) {
        this.opmodeContext = opmodeContext;

        // Allow the device to auto-close since we don't have special shutdown logic
        this.deviceClient = ClassFactory.createI2cDeviceClient(opmodeContext, ClassFactory.createI2cDevice(i2cDevice), params.i2cAddress.bVal * 2, true);
        this.deviceClient.engage();

        this.deviceClient.enableWriteCoalescing(false);

        this.deviceClient.setLogging(params.loggingEnabled);
        this.deviceClient.setLoggingTag(params.loggingTag);

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

        resetINA219();

        int config = this.getConfiguration();

        if (config != 0x399F)
        {
            throw new UnexpectedI2CDeviceException(config);
        }

        setCalibration(parameters);
    }


    //TODO replace the range and sensitivities with enums and use those values in the initialization
    public void setCalibration(INA219.Parameters parameters)
    {
        int calibrationValue = 0;

        // Adafruit sample implementation code used heavily throughout this function.
        // I am focusing on default settings that are likely to be useful to FTC.
        // All of the calculations are shown below if you want to change the settings.
        // You will also need to change any relevant register settings to be consistent,
        // such as setting the VBUS_MAX to 32V instead of 16V, etc.

        // VBUS_MAX = 16V             (Assumes 32V, can also be set to 16V)
        // VSHUNT_MAX = 0.32          (Assumes Gain 8, 320mV, can also be 0.16, 0.08, 0.04)
        // RSHUNT = 0.1               (Resistor value in kOhms)

        int vbus_max = 16; //default value for our framework

        //todo allow configuration of other values
        //if (parameters.rangeInVolts == VOLTAGE_RANGE.VOLTS_32)       vbus_max = 32;
        //else if (parameters.rangeInVolts == VOLTAGE_RANGE.VOLTS_16)  vbus_max = 16;

        double vshunt_max = 0.32; //default value for our framework

        //todo allow configuration of other values
        //if (parameters.sensitivityInMilliamps == GAIN.GAIN_8_320MV)  vshunt_max = 0.32; //320mV
        //else if (parameters.sensitivityInMilliamps == GAIN.GAIN_4_160MV)  vshunt_max = 0.16; //160mV
        //else if (parameters.sensitivityInMilliamps == GAIN.GAIN_2_80MV)  vshunt_max = 0.08; //80mV
        //else if (parameters.sensitivityInMilliamps == GAIN.GAIN_1_40MV)  vshunt_max = 0.04; //40mV

        // 1. Determine max possible current
        // MaxPossible_I = VSHUNT_MAX / RSHUNT
        // MaxPossible_I = 3.2A for default resistor.

        double maxPossibleCurrent = vshunt_max / parameters.shuntResistorInOhms;

        // 2. Determine max expected current
        // MaxExpected_I = 20A    //In our implementation this value is provided in parameters


        // 3. Calculate possible range of LSBs (Min = 15-bit, Max = 12-bit)
        // MinimumLSB = MaxExpected_I/32767
        // MinimumLSB = 0.00061              (61uA per bit)
        // MaximumLSB = MaxExpected_I/4096
        // MaximumLSB = 0.00488              (488uA per bit)

        double minimumLSB = parameters.maxExpectedCurrentInAmps / 32767; //15 bit
        double maximumLSB = parameters.maxExpectedCurrentInAmps / 4096; //12 bit

        // 4. Choose an LSB between the min and max values
        //    (Preferrably a roundish number close to MinLSB)
        // CurrentLSB = 0.001 (1mA per bit)

        //TODO replace this with a general calculation
        //What does "roundish" need to be in this context?
        //for now this is calculated based on the ftc defaults we're using.
        double currentLSB = 0.001;

        // 5. Compute the calibration register
        // Cal = trunc (0.04096 / (Current_LSB * RSHUNT))
        // Cal = 4096 (0x1000)

        //0.0496 comes from the ina219 datasheet page 17 equation 5
        calibrationValue = (int) Math.floor(0.0496 / (currentLSB * parameters.shuntResistorInOhms));

        ina219_calValue = calibrationValue; //calculates to 496 for our defaults

        // 6. Calculate the power LSB
        // PowerLSB = 20 * CurrentLSB  see datasheet page 18 equation 6
        // PowerLSB = 0.02 (20mW per bit)
        double powerLSB = 20 * currentLSB;

        // 7. Compute the maximum current and shunt voltage values before overflow
        //
        // Max_Current = Current_LSB * 32767
        // Max_Current = 32.767A before overflow
        double maxCurrent = currentLSB * 32767;

        //
        // If Max_Current > Max_Possible_I then
        //    Max_Current_Before_Overflow = MaxPossible_I
        // Else
        //    Max_Current_Before_Overflow = Max_Current
        // End If
        //this will be 3.2A with default resistor of 0.100
        double maxCurrentBeforeOverflow = 0;
        if (maxCurrent > maxPossibleCurrent) maxCurrentBeforeOverflow = maxPossibleCurrent;
        else maxCurrentBeforeOverflow = maxCurrent;

        //
        // Max_ShuntVoltage = Max_Current_Before_Overflow * RSHUNT
        // Max_ShuntVoltage = 0.32V
        double maxShuntVoltage = maxCurrentBeforeOverflow * parameters.shuntResistorInOhms;
        //
        // If Max_ShuntVoltage >= VSHUNT_MAX
        //    Max_ShuntVoltage_Before_Overflow = VSHUNT_MAX
        // Else
        //    Max_ShuntVoltage_Before_Overflow = Max_ShuntVoltage
        // End If
        double maxShuntVoltageBeforeOverflow = 0;
        if (maxShuntVoltage >= vshunt_max) maxShuntVoltageBeforeOverflow = vshunt_max;
        else maxShuntVoltageBeforeOverflow = maxShuntVoltage;

        // 8. Compute the Maximum Power
        // MaximumPower = Max_Current_Before_Overflow * VBUS_MAX
        // MaximumPower = 3.2 * 16V
        // MaximumPower = 51.2W
        double maxPower = maxCurrentBeforeOverflow * vbus_max;

        // Set multipliers to convert raw current/power values
        ina219_currentMultiplier_mA = currentLSB;  // Current LSB = 0.001A per bit
        ina219_powerMultiplier_mW = powerLSB;      // PowerLSB = 0.02mW per bit

        // Set Calibration register to 'Cal' calculated above
        writeTwoByteINARegister(REGISTER.CALIBRATION, ina219_calValue);

        // Set Config register to take into account the settings above
        int config = INA219_CONFIG_BVOLTAGERANGE_16V +
                INA219_CONFIG_GAIN_8_320MV +
                INA219_CONFIG_BADCRES_12BIT +
                INA219_CONFIG_SADCRES_12BIT_1S_532US +
                INA219_CONFIG_MODE_SANDBVOLT_CONTINUOUS;

        writeTwoByteINARegister(REGISTER.CONFIGURATION, config);

    }

    public int getCalibration()
    {
        return this.readTwoByteINARegister(REGISTER.CALIBRATION);
    }

    public double getBusVoltage_V()
    {
        double value = getBusVoltage_raw();
        return value * 0.001;
    }

    public double getShuntVoltage_mV()
    {
        double value = getShuntVoltage_raw();
        return value * 0.01;
    }

    public double getCurrent_mA()
    {
        double valueDec = getCurrent_raw();
        valueDec *= ina219_currentMultiplier_mA;
        return valueDec;
    }

    public double getPower_mW()
    {
        double valueDec = getPower_raw();
        valueDec *= ina219_powerMultiplier_mW;
        return valueDec;
    }

    public int getConfiguration()
    {
       return this.readTwoByteINARegister(INA219.REGISTER.CONFIGURATION);
    }

    public void resetINA219()
    {
        this.writeTwoByteINARegister(REGISTER.CONFIGURATION, INA219_CONFIG_RESET);
        delayLore(40);//I don't know if this is needed; just playing it safe
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
    private synchronized int getBusVoltage_raw()
    {
        int value = this.readTwoByteINARegister(REGISTER.BUS_VOLTAGE);

        // Shift to the right 3 to drop CNVR and OVF and multiply by LSB
        return (int) ((value >> 3) * 4);
    }


    /**
     * Get the raw shunt voltage (16-bit signed integer, so +-32767)
     */
    private synchronized int getShuntVoltage_raw()
    {
        int value = this.readTwoByteINARegister(REGISTER.SHUNT_VOLTAGE);

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
        this.writeTwoByteINARegister(REGISTER.CALIBRATION, ina219_calValue);

        //this.delayLore(20);//not sure if this is needed, just playing it safe

        // Now we can safely read the CURRENT register!
        int value = this.readTwoByteINARegister(REGISTER.CURRENT);

        return value;
    }

    /**
     * Get the raw current value (16-bit signed integer, so +-32767)
     */
    private synchronized int getPower_raw() {
        //Lore from Adafruit:
        // Sometimes a sharp load will reset the INA219, which will
        // reset the cal register, meaning CURRENT and POWER will
        // not be available ... avoid this by always setting a cal
        // value even if it's an unfortunate extra step
        writeTwoByteINARegister(REGISTER.CALIBRATION, ina219_calValue);

        //this.delayLore(20);//not sure if this is needed, just playing it safe

        // Now we can safely read the CURRENT register!
        int value = this.readTwoByteINARegister(REGISTER.POWER);

        return value;
    }


    //------------------------------------------------------------------------------------------
    // Calibration
    //------------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------------
    // INA219 data retrieval
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

    @Override public int readTwoByteINARegister(REGISTER ireg) {
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

    @Override public void writeTwoByteINARegister(REGISTER ireg, int value)
    {
        //INA219 data sheet says that register values are sent most-significant-byte first
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
