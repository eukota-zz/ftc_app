package org.swerverobotics.library.shared;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TSL2561LightSensor;
import org.swerverobotics.library.interfaces.TeleOp;
import org.swerverobotics.library.internal.AdaFruitTSL2561LightSensor;

/**
 * Program used to test line following bots
 */
@TeleOp(name="Line-A-Bot")
public class LineFollowingBot extends SynchronousOpMode
{
    DcMotor motorLeft = null;
    DcMotor motorRight = null;
    LED led;

    TSL2561LightSensor lightSensorRight;
    TSL2561LightSensor lightSensorLeft;

    //NOTE: unlike the base LightSensor class, the Adafruit light sensor does not have an LED

    AdaFruitTSL2561LightSensor.Parameters parameters = new  AdaFruitTSL2561LightSensor.Parameters();

    boolean led_state = false;

    @Override protected void main() throws InterruptedException
    {
        // Initialize hardware and other important things
        initializeRobot();

        // Wait until start button has been pressed
        waitForStart();

        // Main loop
        while(opModeIsActive())
        {
            // Gamepads have a new state, so update things that need updating
            if(updateGamepads())
            {
                if (this.gamepad1.a)
                {
                    led_state = true;
                    led.enable(led_state);
                } else if (this.gamepad1.b)
                {
                    led_state = false;
                    led.enable(led_state);
                }
                tankDrive(); //use tank drive. DO NOT change this without talking to Heidi first!!!
            }

            telemetry.update();
            idle();
        }
    }

    public void tankDrive()
    {
        motorLeft.setPower(gamepad1.left_stick_y);
        motorRight.setPower(gamepad1.right_stick_y);
    }

    public void initializeRobot()
    {
        // Initialize motors
        motorLeft = hardwareMap.dcMotor.get("motorLeft");
        motorRight = hardwareMap.dcMotor.get("motorRight");

        // We're not using encoders, so tell the motor controller
        motorLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        // The motors will run in opposite directions, so flip one
        motorRight.setDirection(DcMotor.Direction.REVERSE);

        // Setup light sensors to see white line on black surface quickly
        parameters.gain = TSL2561LightSensor.GAIN.GAIN_16;
        parameters.detectionMode = TSL2561LightSensor.LIGHT_DETECTION_MODE.VISIBLE;
        parameters.integrationTime = TSL2561LightSensor.INTEGRATION_TIME.MS_13;
        parameters.i2cAddress = TSL2561LightSensor.I2CADDR.DEFAULT;

        // Initialize light sensors
        lightSensorLeft = ClassFactory.createAdaFruitTSL2561LightSensor(hardwareMap.i2cDevice.get("lightSensorLeft"), parameters);
        parameters.i2cAddress = TSL2561LightSensor.I2CADDR.ADDR_29; //reusing variable
        lightSensorRight = ClassFactory.createAdaFruitTSL2561LightSensor(hardwareMap.i2cDevice.get("lightSensorRight"), parameters);

        // Initialize LED
        led = hardwareMap.led.get("led");
        led.enable(true);

        // Set up telemetry data
        configureDashboard();
    }


    public void configureDashboard()
    {
        // The default dashboard update rate is a little too slow for our taste here, so we update faster
        telemetry.setUpdateIntervalMs(200);


        telemetry.addLine(
                telemetry.item("light right: ", new IFunc<Object>() {
                    public Object value() {
                        return formatNumber(lightSensorRight.getLightDetected());
                    }
                }),
                telemetry.item("raw right: ", new IFunc<Object>() {
                    public Object value() {
                        return (lightSensorRight.getLightDetectedRaw());
                    }
                })
        );


        telemetry.addLine(
                telemetry.item("light left: ", new IFunc<Object>() {
                    public Object value() {
                        return formatNumber(lightSensorLeft.getLightDetected());
                    }
                }),
                telemetry.item("raw left: ", new IFunc<Object>() {
                    public Object value() {
                        return (lightSensorLeft.getLightDetectedRaw());
                    }
                })
        );

        // Drive motor power
        telemetry.addLine
                (
                        telemetry.item("Power | Left:", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(motorLeft.getPower());
                            }
                        }),
                        telemetry.item("Right: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(motorLeft.getPower());
                            }
                        })
                );


        telemetry.addLine(
                telemetry.item("controls: ", new IFunc<Object>() {
                    @Override
                    public Object value() {
                        return "a:on, b:off";
                    }
                }));
        telemetry.addLine(
                telemetry.item("led state: ", new IFunc<Object>() {
                    @Override
                    public Object value() {
                        //return led.getState(); //unlike DigitalChannel, leds don't let you read their state, so use the variable here instead.
                        return led_state;
                    }
                }));
    }

    // Truncate doubles to 3 decimal places
    public String formatNumber(double d)
    {
        return String.format("%.3f", d);
    }

}
