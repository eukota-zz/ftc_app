package org.swerverobotics.library.examples;

import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Disabled;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.INA219;
import org.swerverobotics.library.interfaces.TCA9548A;
import org.swerverobotics.library.interfaces.TeleOp;
import org.swerverobotics.library.internal.AdaFruitINA219CurrentSensor;


/**
 * SyncI2CMultiplexerDemo gives a short demo on how to use the Adafruit I2C Multiplexer board.
 * http://www.adafruit.com/products/2717
 */
@TeleOp(name = "I2C Multiplexer Demo", group = "Swerve Examples")
@Disabled
public class SyncI2CMultiplexerDemo extends SynchronousOpMode
{
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    // Our sensors, motors, and other devices go here, along with other long term state
    I2cDevice i2cDevice;
    TCA9548A multiplexer;
    ElapsedTime elapsed = new ElapsedTime();

    TCA9548A.Parameters parameters = new TCA9548A.Parameters();


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
        // We are expecting the current sensor to be attached to an I2C port on a core device interface
        // module and named "current". Retrieve that raw I2cDevice and then wrap it in an object that
        // semantically understands this particular kind of sensor.

        parameters.loggingEnabled = false;

        ///instantiate our object
        i2cDevice = hardwareMap.i2cDevice.get("multiplexer");
        multiplexer = ClassFactory.createAdaFruitTCSTCA9548A(i2cDevice, parameters);

        // Set up our dashboard computations
        composeDashboard();

        // Wait until we're told to go
        waitForStart();


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

    }

    
}
