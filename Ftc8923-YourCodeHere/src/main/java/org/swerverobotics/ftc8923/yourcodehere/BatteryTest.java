package org.swerverobotics.ftc8923.yourcodehere;

import android.os.Environment;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Tests batteries by slowly draining them via
 * a motor (a resistor wired to a motor port)
 * and periodically measuring the voltage
 */
@TeleOp(name = "Battery Test")
public class BatteryTest extends SynchronousOpMode
{
    DigitalChannel relay;

    int minimumSafeVoltage = 11;
    ElapsedTime eTime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);

    // How often logs measurements should be taken, in seconds
    int period = 5;

    // The number of log readings taken thus far
    int readings = 1;

    // The total time to run the test, in minutes
    int testTime = 1;

    // Substring is used to only take the date and time from the Date object
    String FILENAME = "BatteryTest " + String.format("%tc", new Date(System.currentTimeMillis())).substring(4, 19) + ".txt";
    PrintWriter outputFile;

    boolean keepRunning = true;

    @Override
    public void main() throws InterruptedException
    {
        relay = hardwareMap.digitalChannel.get("relay");
        relay.setMode(DigitalChannelController.Mode.OUTPUT);

        composeDashboard();
        openPublicFileForWriting(FILENAME);

        waitForStart();

        eTime.reset();

        startTest();

        while (this.opModeIsActive() && keepRunning)
        {
            /*
            // Break if elapsed time has exceeded test time
            if(eTime.time() >= testTime * 60)
            {
                telemetry.log.add("[STOPPED] Test time exceeded");
                break;
            }*/

            if (updateGamepads())
            {
                if (gamepad1.a)
                {
                    relay.setState(true);
                }

                if (gamepad1.b)
                {
                    relay.setState(false);
                }

            }

            // Break if battery voltage drops below minimum safe value
            // TODO Replace the stuff below with the new sensor
            /*
            if(voltageSensor.getVoltage() < minimumSafeVoltage)
            {
                telemetry.log.add("[STOPPED] Battery voltage below minimum safe value");

                stopTest();
            }

            // Checks to see if another period has passed
            if(eTime.time() - (readings * period) >= 0)
            {
                //telemetry.log.add("Voltage at " + readings * period + " seconds: " + formatNumber(voltageSensor.getVoltage()));
                writeDataToPublicFile(voltageSensor.getVoltage());
                readings += 1;
            }
            */

            telemetry.update();
            idle();
        }

        // TODO Replace with new sensor
        //telemetry.log.add("End Voltage: " + formatNumber(voltageSensor.getVoltage()));
        closePublicFile();
    }

    void startTest()
    {
        keepRunning = true;

        // TODO Replace with new sensor
        //telemetry.log.add("Start Voltage: " + formatNumber(voltageSensor.getVoltage()));

        //write initial unloaded voltage to the file
        // TODO Replace with new sensor
        //writeDataToPublicFile(voltageSensor.getVoltage());

        //turn on the relay so we can start the test
        relay.setState(true);
    }

    void stopTest()
    {
        keepRunning = false;

        relay.setState(false);
    }

    void composeDashboard()
    {
        telemetry.addLine
                (
                        this.telemetry.item("Relay: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return relay.getState();
                            }
                        })
                );
        telemetry.addLine
                (
                        this.telemetry.item("Current Voltage: ", new IFunc<Object>() {
                            @Override
                            public Object value() {// TODO Replace with new sensor
                                //return formatNumber(voltageSensor.getVoltage());
                                return "Fix me";
                            }
                        })
                );
        telemetry.addLine
                (
                        this.telemetry.item("Readings: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(readings);
                            }
                        })
                );

        telemetry.addLine
                (
                        this.telemetry.item("Elapsed Time: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(eTime.time());
                            }
                        })
                );

        /*
         * 3 extra lines are added, because there are messages
         * getting logged other than the periodical voltage reading
         */
        //telemetry.log.setCapacity(testTime * 60 / period + 3);
    }

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