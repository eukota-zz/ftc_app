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
 * Tests batteries by slowly draining them
 * and periodically measuring the voltage
 */
@TeleOp(name = "Battery Test")
@Disabled
public class BatteryTest extends SynchronousOpMode
{
    DigitalChannel relay0 = null;
    DigitalChannel relay1 = null;

    INA219 currentSensor = null;
    AdaFruitINA219CurrentSensor.Parameters parameters = new AdaFruitINA219CurrentSensor.Parameters();

    double minimumSafeVoltage = 11.5;
    ElapsedTime eTime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);

    // How often logs measurements should be taken, in seconds
    int period = 5;

    // The number of log readings taken thus far
    int readings = 1;

    // The total time to run the test, in minutes
    int testTime = 1;

    double resistance = 0.0;
    double resistor0 = 200.0;
    double resistor1 = 3.0;

    // Substring is used to only take the date and time from the Date object
    String FILENAME = "BatteryTest " + String.format("%tc", new Date(System.currentTimeMillis())).substring(4, 19) + ".txt";
    PrintWriter outputFile;

    boolean keepRunning = true;
    boolean initialized = false;

    @Override
    public void main() throws InterruptedException
    {
        relay0 = hardwareMap.digitalChannel.get("relay0");
        relay1 = hardwareMap.digitalChannel.get("relay1");
        relay0.setMode(DigitalChannelController.Mode.OUTPUT);
        relay1.setMode(DigitalChannelController.Mode.OUTPUT);

        parameters.loggingEnabled = false;
        parameters.shuntResistorInOhms = 0.03;

        currentSensor = ClassFactory.createAdaFruitINA219(hardwareMap.i2cDevice.get("currentSensor"), parameters);

        openPublicFileForWriting(FILENAME);

        waitForStart();

        telemetry.addLine
                (
                        this.telemetry.item("Choose a period, then press start: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return period;
                            }
                        })
                );

        while(this.opModeIsActive() && !initialized)
        {
            if(updateGamepads())
            {
                if(gamepad1.dpad_up)
                    period += 1;
                if(gamepad1.dpad_down)
                    if(period > 1)
                        period -= 1;
                if(gamepad1.start)
                    initialized = true;
            }

            telemetry.update();
            idle();
        }

        telemetry.clearDashboard();

        eTime.reset();
        composeDashboard();

        startTest();

        while (this.opModeIsActive() && keepRunning)
        {
            // TODO Is this necessary?
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
                    relay0.setState(true);
                if (gamepad1.b)
                    relay0.setState(false);
                if (gamepad1.x)
                    relay1.setState(true);
                if (gamepad1.y)
                    relay1.setState(false);
            }

            // Calculate current resistance of the circuit
            // both resistors are in parallel, and in series with respective relays
            if(!relay0.getState() && !relay1.getState())
                resistance = 0.0;
            else if(relay0.getState() && !relay1.getState())
                resistance = resistor0;
            else if(!relay0.getState() && relay1.getState())
                resistance = resistor1;
            else if(relay0.getState() && relay1.getState())
                resistance = (resistor0 * resistor1) / (resistor0 + resistor1);

            // Break if battery voltage drops below minimum safe value
            if(Math.abs(currentSensor.getBusVoltage_V()) < minimumSafeVoltage)
            {
                telemetry.log.add("[STOPPED] Battery voltage below minimum safe value");
                stopTest();
            }

            // Checks to see if another period has passed
            if(eTime.time() - (readings * period) >= 0)
            {
                //telemetry.log.add("Voltage at " + readings * period + " seconds: " + formatNumber(voltageSensor.getVoltage()));
                String s = Math.round(eTime.time()) + "," + formatNumber(currentSensor.getBusVoltage_V()) +
                        "," + formatNumber(currentSensor.getCurrent_mA()) +
                    "," + formatNumber(resistance) + "\r\n";
                writeDataToPublicFile(s);
                readings += 1;
            }

            telemetry.update();
            idle();
        }

        stopTest();
        telemetry.log.add("End Voltage: " + formatNumber(currentSensor.getBusVoltage_V()));
        closePublicFile();
    }

    void startTest()
    {
        keepRunning = true;

        telemetry.log.add("Start Voltage: " + formatNumber(currentSensor.getBusVoltage_V()));

        // Write initial unloaded voltage to the file
        String s = Math.round(eTime.time()) + "," + formatNumber(currentSensor.getBusVoltage_V()) +
                "," + formatNumber(resistance) + "\r\n";
        writeDataToPublicFile(s);

        // Ensure relays are off, per Sig's request
        relay0.setState(false);
        relay1.setState(false);
    }

    void stopTest()
    {
        keepRunning = false;

        relay0.setState(false);
        relay1.setState(false);
    }

    void composeDashboard()
    {
        telemetry.addLine
                (
                        this.telemetry.item("Relay1: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return relay0.getState();
                            }
                        }),
                        this.telemetry.item("Relay2: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return relay1.getState();
                            }
                        })
                );
        telemetry.addLine
                (
                        this.telemetry.item("Voltage: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(currentSensor.getBusVoltage_V());
                            }
                        }),
                        this.telemetry.item("Current: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(currentSensor.getCurrent_mA());
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
                                return Math.round(eTime.time());
                            }
                        })
                );

        telemetry.addLine(
                telemetry.item("config ", new IFunc<Object>() {
                    public Object value() {
                        int config = currentSensor.getConfiguration();
                        return formatConfig(config);
                    }
                }));

        telemetry.addLine(
                telemetry.item("calibration ", new IFunc<Object>() {
                    public Object value() {
                        int config = currentSensor.getCalibration();
                        return formatConfig(config);
                    }
                }));

        /*
         * 3 extra lines are added, because there are messages
         * getting logged other than the periodical voltage reading
         */
        //telemetry.log.setCapacity(testTime * 60 / period + 3);
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