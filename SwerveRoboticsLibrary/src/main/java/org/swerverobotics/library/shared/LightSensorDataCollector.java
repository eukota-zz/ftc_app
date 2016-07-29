package org.swerverobotics.library.shared;

import android.os.Environment;

import com.qualcomm.robotcore.hardware.I2cDevice;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Disabled;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TSL2561LightSensor;
import org.swerverobotics.library.interfaces.TeleOp;
import org.swerverobotics.library.internal.AdaFruitTSL2561LightSensor;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Records data from a light sensor on an XY plane
 * Primary purpose is for learning how the sensor works
 * Ask Hank or Dryw for further information
 */
@TeleOp(name = "Light Sensor Data Collector")
@Disabled
public class LightSensorDataCollector extends SynchronousOpMode
{
    I2cDevice i2cDevice;
    TSL2561LightSensor lightSensor;
    AdaFruitTSL2561LightSensor.Parameters parameters = new  AdaFruitTSL2561LightSensor.Parameters();

    // Substring is used to take only the date and time from the Date object
    String FILENAME = "LightSensorData " + String.format("%tc", new Date(System.currentTimeMillis())).substring(4, 19) + ".txt";
    PrintWriter outputFile;

    // XY coordinate variables, can be any units
    int x = 0;
    int y = 0;

    @Override
    public void main() throws InterruptedException
    {
        parameters.gain = TSL2561LightSensor.GAIN.GAIN_1; //select the chip's gain
        parameters.detectionMode = TSL2561LightSensor.LIGHT_DETECTION_MODE.BROADBAND; //measure visible light + IR light
        parameters.integrationTime = TSL2561LightSensor.INTEGRATION_TIME.MS_13; //fast but low resolution

        i2cDevice = hardwareMap.i2cDevice.get("adalight");
        lightSensor = ClassFactory.createAdaFruitTSL2561LightSensor(i2cDevice, parameters);

        openPublicFileForWriting(FILENAME);

        waitForStart();

        composeDashboard();

        while(this.opModeIsActive())
        {
            if(updateGamepads())
            {
                // Move XY coordinates based on relative position of sensor
                if(gamepad1.dpad_left)
                    x --;
                if(gamepad1.dpad_right)
                    x ++;
                if(gamepad1.dpad_down)
                    y --;
                if(gamepad1.dpad_up)
                    y ++;
                if(gamepad1.a)
                    recordData();
            }

            telemetry.update();
            idle();
        }
        closePublicFile();
    }

    void composeDashboard()
    {
        telemetry.addLine
                (
                        this.telemetry.item("X: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return x;
                            }
                        }),
                        this.telemetry.item("Y: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return y;
                            }
                        })
                );
        telemetry.addLine
                (
                        this.telemetry.item("Light: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return lightSensor.getLightDetectedRaw();
                            }
                        })
                );
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

    public void recordData()
    {
        // Records XY position and light level
        String s = x + ", " + y + ", " + lightSensor.getLightDetectedRaw() + "\r\n";
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