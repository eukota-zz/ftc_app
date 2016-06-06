package org.swerverobotics.library.examples;

import android.os.Environment;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Disabled;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.INA219;
import org.swerverobotics.library.interfaces.TeleOp;
import org.swerverobotics.library.internal.AdaFruitINA219CurrentSensor;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Tests batteries by slowly draining them and periodically measuring the voltage.
 * Connect phone and battery to battery testing station, designed by Sig Johnson.
 * Basic wiring consists of two relays wired in parallel to each other, and
 * a resistor in series with each relay. A single INA219 current sensor is in
 * series with the battery and parallel relays.
 */
@TeleOp(name = "Battery Internal Resistance Test")
@Disabled
public class BatteryInternalResistanceTest extends SynchronousOpMode
{
    DigitalChannel relay200Ohm = null;
    DigitalChannel relay3Ohm = null;

    INA219 currentSensor = null;
    AdaFruitINA219CurrentSensor.Parameters parameters = new AdaFruitINA219CurrentSensor.Parameters();

    double minimumSafeVoltage = 11.5;
    ElapsedTime eTime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);

    // How often logs measurements should be taken, in seconds
    int period = 5; //IGNORED

    // The number of log readings taken thus far
    int readings = 1;

    // The total time to run the test, in minutes
    int testTime = 1;

    double resistance = 0.0;
    double resistor0 = 200.0;
    double resistor1 = 3.2;

    // Substring is used to only take the date and time from the Date object
    String FILENAME = "BatteryTest " + String.format("%tc", new Date(System.currentTimeMillis())).substring(4, 19) + ".txt";
    PrintWriter outputFile;

    boolean keepRunning = true;
    boolean initialized = false;

    @Override
    public void main() throws InterruptedException
    {
        relay200Ohm = hardwareMap.digitalChannel.get("relay0");
        relay3Ohm = hardwareMap.digitalChannel.get("relay1");
        relay200Ohm.setMode(DigitalChannelController.Mode.OUTPUT);
        relay3Ohm.setMode(DigitalChannelController.Mode.OUTPUT);

        parameters.loggingEnabled = false;
        parameters.shuntResistorInOhms = 0.03;

        currentSensor = ClassFactory.createAdaFruitINA219(hardwareMap.i2cDevice.get("currentSensor"), parameters);

        openPublicFileForWriting(FILENAME);

        waitForStart();

        eTime.reset();

        startTest();

        // Calculate current resistance of the circuit
        // both resistors are in parallel, and in series with respective relays
        /*if(!relay200Ohm.getState() && !relay3Ohm.getState())
            resistance = 0.0;
        else if(relay200Ohm.getState() && !relay3Ohm.getState())
            resistance = resistor0;
        else if(!relay200Ohm.getState() && relay3Ohm.getState())
            resistance = resistor1;
        else if(relay200Ohm.getState() && relay3Ohm.getState())
            resistance = (resistor0 * resistor1) / (resistor0 + resistor1);*/
        resistance = 0.0;

        // Break if battery voltage drops below minimum safe value
        /*if(Math.abs(currentSensor.getBusVoltage_V()) < minimumSafeVoltage)
        {
            telemetry.log.add("[STOPPED] Battery voltage below minimum safe value");
            stopTest();
        }*/

        // Checks to see if another period has passed
        //if(eTime.time() - (readings * period) >= 0)

        //first reading
        //telemetry.log.add("Voltage at " + readings * period + " seconds: " + formatNumber(voltageSensor.getVoltage()));
        double voltage1 = currentSensor.getBusVoltage_V();
        double calculatedCurrent1 = calcCurrentUsingOhmsLaw(voltage1, resistance);

        String s = readings + "," + formatNumber(voltage1) +
                "," + formatNumber(calculatedCurrent1) +
            "," + formatNumber(resistance) + "\r\n";
        writeDataToPublicFile(s);
        readings += 1;

        //engage first relay
        relay3Ohm.setState(true);
        resistance = resistor1;

        pause(250);

        double voltage2 = currentSensor.getBusVoltage_V();
        double calculatedCurrent2 = calcCurrentUsingOhmsLaw(voltage2, resistance);

        s = readings + "," + formatNumber(voltage2) +
                "," + formatNumber(calculatedCurrent2) +
                "," + formatNumber(resistance) + "\r\n";
        writeDataToPublicFile(s);
        readings += 1;

        //calculate internal resistance by calculating the slope of the line
        double internalResistance = (-1.0) * ( (voltage2 - voltage1) / (calculatedCurrent2 - calculatedCurrent1) );

        writeDataToPublicFile("internal resistance: " + formatNumber(internalResistance) + " Ohms\r\n");

        telemetry.log.add("V1, V2: " + formatNumber(voltage1) + "," + formatNumber(voltage2));
        telemetry.log.add("C1, C2: " + formatNumber(calculatedCurrent1) + "," + formatNumber(calculatedCurrent2));
        telemetry.log.add("Internal Resistance: "+ formatNumber(internalResistance));
        telemetry.update();

        stopTest();

        closePublicFile();

        while (true)
        {
            //do nothing. loop anyway because we want telemetry to stay on the screen during the demo
            idle();
        }
    }

    double calcCurrentUsingOhmsLaw(double voltage, double resistance)
    {
        if (resistance == 0) return 0; //special case, would normally be infinity
        else return (voltage/resistance);
    }

    void startTest()
    {
        keepRunning = true;

        //telemetry.log.add("Start Voltage: " + formatNumber(currentSensor.getBusVoltage_V()));

        // Write initial unloaded voltage to the file
        String s = Math.round(eTime.time()) + "," + formatNumber(currentSensor.getBusVoltage_V()) +
                "," + formatNumber(resistance) + "\r\n";
        writeDataToPublicFile(s);

        // Ensure relays are off, per Sig's request
        relay200Ohm.setState(false);
        relay3Ohm.setState(false);
    }

    void stopTest()
    {
        keepRunning = false;

        relay200Ohm.setState(false);
        relay3Ohm.setState(false);
    }

    //pause a number of milliseconds
    public void pause(int t) throws InterruptedException
    {
        //we don't use System.currentTimeMillis() because it can be inconsistent
        long initialTime = System.nanoTime();
        while((System.nanoTime() - initialTime)/1000/1000 < t)
        {
            idle();
        }
    }


    String formatConfig (int config) { return String.format("0x%04X", config); }

    public String formatNumber(double number)
    {
        return String.format("%.2f", number);
    }

    public void openPublicFileForWriting(String filename)
    {
        String fullpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;

        try
        {
            File file = new File(fullpath);
            if (!file.exists())
            {
                file.createNewFile();
            }
            outputFile = new PrintWriter(file);
        }
        catch (Exception e)
        {
            telemetry.log.add("Exception opening file: " + e.toString());
        }
    }

    public void writeDataToPublicFile(String message)
    {
        if (outputFile!=null)
        {
            try
            {
                outputFile.println(message);
                outputFile.flush();
            }
            catch (Exception e)
            {
                telemetry.log.add("Exception writing to file: " + e.toString());
            }
        }
    }

    public void writeDataToPublicFile(double voltage)
    {
        String s = formatNumber(eTime.time()) + "," + formatNumber(voltage) + "\r\n";
        writeDataToPublicFile(s);
    }

    public void closePublicFile()
    {
        if (outputFile != null)
        {
            try
            {
                outputFile.flush();
                outputFile.close();
            }
            catch (Exception e)
            {
                telemetry.log.add("Exception closing file: " + e.toString());
            }
        }
    }
}