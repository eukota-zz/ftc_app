package org.swerverobotics.library.examples;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TSL2561LightSensor;
import org.swerverobotics.library.interfaces.TeleOp;
import org.swerverobotics.library.internal.AdaFruitTSL2561LightSensor;

/**
 * Program used to test line following bots
 */
@TeleOp(name="Line-A-Bot", group="Swerve Examples")
public class LineFollowingBot extends SynchronousOpMode
{
    DcMotor motorLeft = null;
    DcMotor motorRight = null;

    TSL2561LightSensor lightSensorLeft;
    TSL2561LightSensor lightSensorRight;
    AdaFruitTSL2561LightSensor.Parameters parameters = new  AdaFruitTSL2561LightSensor.Parameters();

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
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        // Setup light sensors to see white line on black surface quickly
        parameters.gain = TSL2561LightSensor.GAIN.GAIN_16;
        parameters.detectionMode = TSL2561LightSensor.LIGHT_DETECTION_MODE.VISIBLE;
        parameters.integrationTime = TSL2561LightSensor.INTEGRATION_TIME.MS_13;

        // Initialize light sensors
        lightSensorLeft = ClassFactory.createAdaFruitTSL2561LightSensor(hardwareMap.i2cDevice.get("lightSensorLeft"), parameters);
        lightSensorRight = ClassFactory.createAdaFruitTSL2561LightSensor(hardwareMap.i2cDevice.get("lightSensorRight"), parameters);

        // Set up telemetry data
        configureDashboard();
    }

    public void configureDashboard()
    {
        // Drive motor power
        telemetry.addLine
                (
                        telemetry.item("Power | Left:", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(motorLeft.getPower());
                            }
                        }),
                        telemetry.item("Right: ", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(motorLeft.getPower());
                            }
                        })
                );
        // Light sensor readings
        telemetry.addLine
                (
                        telemetry.item("Light | Left:", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(lightSensorLeft.getLightDetected());
                            }
                        }),
                        telemetry.item("Right: ", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(lightSensorRight.getLightDetected());
                            }
                        })
                );
    }

    // Truncate doubles to 3 decimal places
    public String formatNumber(double d)
    {
        return String.format("%.3f", d);
    }
}
