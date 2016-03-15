package org.swerverobotics.library.examples;

import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TCS34725;
import org.swerverobotics.library.interfaces.TeleOp;
import org.swerverobotics.library.internal.AdaFruitTCS34725ColorSensor;

/**
 * This SynchronousOpMode illustrates using an AdaFruit Color Sensor over i2c.
 */
@TeleOp(name = "AdaFruit I2C Color Demo", group = "Swerve Examples")
public class SynchColorDemoAdafruitI2C extends SynchronousOpMode
{
    I2cDevice i2cDevice;
    TCS34725 colorSensor;

    //NOTE: the Adafruit color sensor's LED is not controllable via I2C.
    //You'll need to connect it to a digital channel and control it that way
    //boolean ledIsOn;

    AdaFruitTCS34725ColorSensor.Parameters parameters = new  AdaFruitTCS34725ColorSensor.Parameters();

    // Here we have state we use for updating the dashboard.
    ElapsedTime elapsed = new ElapsedTime();
    int loopCycles;
    int i2cCycles;
    double ms;
    boolean i2cArmed;
    boolean i2cEngaged;


    protected void main() throws InterruptedException
    {
        i2cDevice = hardwareMap.i2cDevice.get("adacolor");
        //this.color = this.hardwareMap.colorSensor.get("colorSensor");
        this.colorSensor = ClassFactory.createAdaFruitTCS34725(i2cDevice, parameters);

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
        // from the INA219 that we will display in separate lines.
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
                telemetry.item("red: ", new IFunc<Object>() {
                    public Object value() {
                        return (colorSensor.red());
                    }
                }),
                telemetry.item("green: ", new IFunc<Object>() {
                    public Object value() {
                        return (colorSensor.green());
                    }
                }),
                telemetry.item("blue: ", new IFunc<Object>() {
                    public Object value() {
                        return (colorSensor.blue());
                    }
                })
                );


        telemetry.addLine(
                telemetry.item("deviceID: ", new IFunc<Object>() {
                    public Object value() {
                        return formatHEXByte(colorSensor.getDeviceID());
                    }
                }),
                telemetry.item("state: ", new IFunc<Object>() {
                    public Object value() {
                        return formatHEXByte(colorSensor.getState());
                    }
                })
        );
    }

    String formatHEXByte(byte b)
    {
        return String.format("0x%02X", b);
    }
}
