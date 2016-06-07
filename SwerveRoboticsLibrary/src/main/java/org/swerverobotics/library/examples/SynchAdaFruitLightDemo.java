package org.swerverobotics.library.examples;

import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Disabled;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TCS34725;
import org.swerverobotics.library.interfaces.TSL2561LightSensor;
import org.swerverobotics.library.interfaces.TeleOp;
import org.swerverobotics.library.internal.AdaFruitTCS34725ColorSensor;
import org.swerverobotics.library.internal.AdaFruitTSL2561LightSensor;

/**
 * This SynchronousOpMode illustrates using an AdaFruit TSL2561 Light Sensor over i2c.
 * http://adafru.it/439
 * It includes support for setting the Gain and Integration Time of the chip,
 * as well as reading various components of the light: broadband, IR-only, or visible spectrum only.
 *
 * Note: when configuring your robot controller, choose "I2C_DEVICE" for this device.
 * This sample assumes a single light sensor called "adalight" using its default i2c address.
 */
@TeleOp(name = "AdaFruit I2C Light Demo", group = "Swerve Examples")
@Disabled
public class SynchAdaFruitLightDemo extends SynchronousOpMode
{
    I2cDevice i2cDevice;
    TSL2561LightSensor lightSensor;

    //NOTE: unlike the base LightSensor class, the Adafruit light sensor does not have an LED

    AdaFruitTSL2561LightSensor.Parameters parameters = new  AdaFruitTSL2561LightSensor.Parameters();

    // Here we have state we use for updating the dashboard.
    ElapsedTime elapsed = new ElapsedTime();
    int loopCycles;
    int i2cCycles;
    double ms;
    boolean i2cArmed;
    boolean i2cEngaged;


    protected void main() throws InterruptedException
    {
        i2cDevice = hardwareMap.i2cDevice.get("adalight");

        parameters.gain = TSL2561LightSensor.GAIN.GAIN_1; //select the chip's gain
        parameters.detectionMode = TSL2561LightSensor.LIGHT_DETECTION_MODE.BROADBAND; //measure visible light + IR light
        parameters.integrationTime = TSL2561LightSensor.INTEGRATION_TIME.MS_13; //fast but low resolution

        this.lightSensor = ClassFactory.createAdaFruitTSL2561LightSensor(i2cDevice, parameters);

        // Set up our dashboard computations
        composeDashboard();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.update();
            this.idle();
        }
    }

    void composeDashboard() {
        // The default dashboard update rate is a little too slow for our taste here, so we update faster
        telemetry.setUpdateIntervalMs(200);

        // At the beginning of each telemetry update, grab a bunch of data
        // from the device that we will display in separate lines.
        telemetry.addAction(new Runnable() { @Override public void run()
        {
            loopCycles = getLoopCount();
            i2cCycles  = i2cDevice.getCallbackCount();
            ms         = elapsed.milliseconds();
            //i2cArmed = ((I2cDeviceSynchUser) currentSensor).getI2cDeviceSynch().isArmed();
            //i2cEngaged = ((I2cDeviceSynchUser) currentSensor).getI2cDeviceSynch().isEngaged();
        }
        });

        telemetry.addLine(
                telemetry.item("loop count: ", new IFunc<Object>() {
                    public Object value() {
                        return loopCycles;
                    }
                }),
                telemetry.item("i2c cycle count: ", new IFunc<Object>() {
                    public Object value() {
                        return i2cCycles;
                    }
                }));
/*
        telemetry.addLine(
                telemetry.item("loop rate: ", new IFunc<Object>() {
                    public Object value() {
                        return formatRate(ms / loopCycles);
                    }
                }),
                telemetry.item("i2c cycle rate: ", new IFunc<Object>() {
                    public Object value() {
                        return formatRate(ms / i2cCycles);
                    }
                }));
*/
/*
        telemetry.addLine(
                telemetry.item("i2c armed: ", new IFunc<Object>() {
                    public Object value() {
                        return i2cArmed;
                    }
                }),
                telemetry.item("i2c engaged: ", new IFunc<Object>() {
                    public Object value() {
                        return i2cEngaged;
                    }
                }));
*/


        telemetry.addLine(
                telemetry.item("light: ", new IFunc<Object>() {
                    public Object value() {
                        return formatLightLevel(lightSensor.getLightDetected());
                    }
                }),
                telemetry.item("raw: ", new IFunc<Object>() {
                    public Object value() {
                        return (lightSensor.getLightDetectedRaw());
                    }
                })
                );


        telemetry.addLine(
                telemetry.item("deviceID: ", new IFunc<Object>() {
                    public Object value() {
                        return formatHEXByte(lightSensor.getDeviceID());
                    }
                }),
                telemetry.item("state: ", new IFunc<Object>() {
                    public Object value() {
                        return formatHEXByte(lightSensor.getState());
                    }
                })
        );
    }

    String formatHEXByte(byte b)
    {
        return String.format("0x%02X", b);
    }
    String formatLightLevel(double light) { return String.format("%.3f", light); }
}
