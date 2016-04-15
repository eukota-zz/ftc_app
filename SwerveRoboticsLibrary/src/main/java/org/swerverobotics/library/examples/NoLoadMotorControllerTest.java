package org.swerverobotics.library.examples;

import android.os.Environment;

import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;

/*
 * Monitors the voltage of a battery
 * when plugged into a motor controller
 * with no load per Sig's request
 */
@TeleOp(name = "No Load Motor Controller Test")
public class NoLoadMotorControllerTest extends SynchronousOpMode
{
    VoltageSensor voltageSensor = null;

    ElapsedTime eTime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);

    // How often logs measurements should be taken, in seconds
    int period = 5;

    // The number of log readings taken thus far
    int readings = 1;

    // The total time to run the test, in minutes
    int testTime = 30;

    // Substring is used to only take the date and time from the Date object
    String FILENAME = "NoLoadMCTest " + String.format("%tc", new Date(System.currentTimeMillis())).substring(4, 19) + ".txt";
    PrintWriter outputFile;

    boolean keepRunning = true;

    @Override
    public void main() throws InterruptedException
    {
        voltageSensor = hardwareMap.voltageSensor.iterator().next();

        composeDashboard();
        openPublicFileForWriting(FILENAME);

        waitForStart();

        eTime.reset();

        startTest();

        while (this.opModeIsActive() && keepRunning)
        {
            // Break if elapsed time has exceeded test time
            if(eTime.time() >= testTime * 60)
            {
                telemetry.log.add("[STOPPED] Test time exceeded");
                keepRunning = false;
            }

            // Checks to see if another period has passed
            if(eTime.time() - (readings * period) >= 0)
            {
                //telemetry.log.add("Voltage at " + readings * period + " seconds: " + formatNumber(voltageSensor.getVoltage()));
                String s = Math.round(eTime.time()) + "," + formatNumber(voltageSensor.getVoltage()) + "\r\n";
                writeDataToPublicFile(s);
                readings += 1;
            }

            telemetry.update();
            idle();
        }

        stopTest();
        telemetry.log.add("End Voltage: " + formatNumber(voltageSensor.getVoltage()));
        closePublicFile();
    }

    void startTest()
    {
        keepRunning = true;

        telemetry.log.add("Start Voltage: " + formatNumber(voltageSensor.getVoltage()));

        // Write initial unloaded voltage to the file
        String s = Math.round(eTime.time()) + "," + formatNumber(voltageSensor.getVoltage()) + "\r\n";
        writeDataToPublicFile(s);
    }

    void stopTest()
    {
        keepRunning = false;
    }

    void composeDashboard()
    {
        telemetry.addLine
                (
                        this.telemetry.item("Elapsed Time: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return Math.round(eTime.time());
                            }
                        })
                );

        telemetry.addLine
                (
                        this.telemetry.item("Voltage: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(voltageSensor.getVoltage());
                            }
                        })
                );

    }

    public String formatNumber(double number)
    {
        return String.format("%.3f", number);
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