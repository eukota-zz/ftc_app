package org.swerverobotics.library.examples;

import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TCA9548A;
import org.swerverobotics.library.interfaces.TCS34725;
import org.swerverobotics.library.interfaces.TeleOp;
import org.swerverobotics.library.internal.AdaFruitTCS34725ColorSensor;

/**
 * SyncI2CMultiplexerDemo gives a short demo on how to use the Adafruit I2C Multiplexer board.
 * http://www.adafruit.com/products/2717
 */
@TeleOp(name = "I2C Multiplexer New Demo", group = "Swerve Examples")
//@Disabled
public class SyncI2CMultiplexerNewDemo extends SynchronousOpMode
{
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    //multiplexer device
    I2cDevice i2cDeviceMux;
    TCA9548A multiplexer;
    TCA9548A.Parameters multiplexerParameters = new TCA9548A.Parameters();

    //first color sensor
    I2cDevice i2cDeviceColor1;
    TCS34725 colorSensor1;
    AdaFruitTCS34725ColorSensor.Parameters colorSensorParameters = new  AdaFruitTCS34725ColorSensor.Parameters();

    //second color sensor
    I2cDevice i2cDeviceColor2;
    TCS34725 colorSensor2;
    //this color sensor will use the same configuration parameters as the first color sensor

    ElapsedTime elapsed = new ElapsedTime();

    // Here we have state we use for updating the dashboard.
    int loopCycles;
    int i2cCycles;
    double ms;
    boolean i2cArmed;
    boolean i2cEngaged;

    //----------------------------------------------------------------------------------------------
    // main() loop
    //----------------------------------------------------------------------------------------------

    @Override
    public void main() throws InterruptedException {


        // To use this demo, connect your multiplexer to an I2C port on the core device interface and name it "multiplexer".
        // Then connect two color sensor boards to ports 1 and 7 on the multiplexer board.

        // Unfortunately, to get an i2cDevice, we still need to tell the config file we have color sensors on
        // specific i2c ports, even though the color sensors are actually connected to ports
        // on the multiplexer boards.  I would like to eliminate the need to do that in the future,
        // but for now, that's the way it is.
        // So, you'll need to set up your robot config file to think
        // that the color sensors are connected to (any other) i2c ports on the core device interface module.
        // This opmode expects the color sensors to be called "adacolor1" and "adacolor2".

        ///instantiate our multiplexer
        this.i2cDeviceMux = hardwareMap.i2cDevice.get("multiplexer");
        this.multiplexerParameters.loggingEnabled = false;
        this.multiplexer = ClassFactory.createAdaFruitTCSTCA9548A(i2cDeviceMux, multiplexerParameters);

        //create a color sensor connected to multiplexer channel 1
        //note: to get an i2cDevice, we still need to tell the config file we have a color sensor on
        //a specific i2c port, even though it's not actually connected there!
        //I would like to eliminate the need to do that in the future, but for now, that's the way it is.

        //first, you must switch to the proper mux channel so the device is connected when we try to create it.
        this.multiplexer.switchToChannel(TCA9548A.MULTIPLEXER_CHANNEL.CHANNEL1);
        //then, create the sensor
        this.i2cDeviceColor1 = hardwareMap.i2cDevice.get("adacolor1");
        colorSensor1 = ClassFactory.createAdaFruitTCS34725(i2cDeviceColor1, colorSensorParameters);
        //finally, add the first color sensor to the multiplexer
        multiplexer.addMultiplexableDevice(colorSensor1, TCA9548A.MULTIPLEXER_CHANNEL.CHANNEL1);

        //create a color sensor connected to multiplexer channel 7
        //note: to get an i2cDevice, we still need to tell the config file we have a color sensor on
        //a specific i2c port, even though it's not actually connected there!
        //I would like to eliminate the need to do that in the future, but for now, that's the way it is.

        //Remember to switch to the channel first before trying to create a device
        this.multiplexer.switchToChannel(TCA9548A.MULTIPLEXER_CHANNEL.CHANNEL7);
        //then, create the sensor
        this.i2cDeviceColor2 = hardwareMap.i2cDevice.get("adacolor2");
        colorSensor2 = ClassFactory.createAdaFruitTCS34725(i2cDeviceColor2, colorSensorParameters);
        //finally, add the second color sensor to the multiplexer
        multiplexer.addMultiplexableDevice(colorSensor2, TCA9548A.MULTIPLEXER_CHANNEL.CHANNEL7);

        /*
         *   At this point, you can use each color sensors as you usually would.
         *   The multiplexer will automatically switch to the proper sensor
         *   whenever you read from it or write to it.
         */

        // Set up our dashboard computations
        composeDashboard();

        // Wait until we're told to go
        waitForStart();

        int i=0;

        // Loop and update the dashboard
       while (opModeIsActive()) {
            telemetry.update();
            idle();

        }

    }

    //----------------------------------------------------------------------------------------------
    // dashboard configuration
    //----------------------------------------------------------------------------------------------

    void composeDashboard() {
        // The default dashboard update rate is a little too slow for our taste here, so we update faster
        telemetry.setUpdateIntervalMs(200);

        // At the beginning of each telemetry update, grab a bunch of data
        // from the device that we will display in separate lines.
        telemetry.addAction(new Runnable() { @Override public void run()
        {
            loopCycles = getLoopCount();
            i2cCycles  = i2cDeviceMux.getCallbackCount();
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
                telemetry.item("sensor 1  red: ", new IFunc<Object>() {
                    public Object value() {
                        return (colorSensor1.red());
                    }
                }),
                telemetry.item("green: ", new IFunc<Object>() {
                    public Object value() {
                        return (colorSensor1.green());
                    }
                }),
                telemetry.item("blue: ", new IFunc<Object>() {
                    public Object value() {
                        return (colorSensor1.blue());
                    }
                })
        );



        telemetry.addLine(
                telemetry.item("sensor 2  red: ", new IFunc<Object>() {
                    public Object value() {
                        return (colorSensor2.red());
                    }
                }),
                telemetry.item("green: ", new IFunc<Object>() {
                    public Object value() {
                        return (colorSensor2.green());
                    }
                }),
                telemetry.item("blue: ", new IFunc<Object>() {
                    public Object value() {
                        return (colorSensor2.blue());
                    }
                })
        );

    }


}
