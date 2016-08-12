package org.swerverobotics.library.shared;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Disabled;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TSL2561LightSensor;
import org.swerverobotics.library.interfaces.TeleOp;
import org.swerverobotics.library.internal.AdaFruitTSL2561LightSensor;

/*
 * Designed to have a robot autonomously
 * follow a line using a PID loop.
 * Created by Hank and Dryw
 */
@TeleOp(name="Dryw and Hank Line Following")
@Disabled
public class LineFollowingBotDrywHank extends SynchronousOpMode
{
    // Declare motors
    DcMotor motorLeft = null;
    DcMotor motorRight = null;

    // Declare light sensors and LEDs
    TSL2561LightSensor lightSensorLeft;
    TSL2561LightSensor lightSensorRight;
    AdaFruitTSL2561LightSensor.Parameters parameters = new  AdaFruitTSL2561LightSensor.Parameters();
    LED ledRight;
    LED ledLeft;

    // TODO: Change these to reasonable values via testing
    // Declare constants
    static final double MIN_LIGHT_THRESHOLD = 23;
    static final double MAX_LIGHT_THRESHOLD = 70;
    static final double MAX_ERROR = 10;
    double BASE_MOTOR_POWER = 0.15;
    double P_CONSTANT = 0.01;
    double I_CONSTANT = 0.005;
    double D_CONSTANT = 0.0;
    double constantDelta = 0.005;

    // Declare variables
    double error = 0.0;
    double errorSum = 0.0;
    double deltaError = 0.0;
    double P =0.0, I = 0.0, D = 0.0;
    double PID = 0.0;
    double lastPosition = 0.0;
    double currentPosition = 0.0;
    double deltaPosition = 0.0;
    int lightLeft, lightRight;

    @Override protected void main() throws InterruptedException
    {
        // Initialize hardware and other important things
        initializeRobot();

        // Wait until start button has been pressed
        waitForStart();

        // Main loop
        while(opModeIsActive() /*&& isOnLine()*/)
        {
            // Calculate error to calculate PID to control motors
            updateVariables();
            calculatePID();
            setMotorPower();

            // Adjusts constants so we don't have to keep downloading code
            if(updateGamepads())
            {
                adjustVaiables();
            }

            telemetry.update();
            idle();
        }

        // Stop the robot!
        motorLeft.setPower(0);
        motorRight.setPower(0);
    }

    public void adjustVaiables()
    {
        if(gamepad1.dpad_up)
            P_CONSTANT += constantDelta;
        if(gamepad1.dpad_down)
            P_CONSTANT -= constantDelta;
        if(gamepad1.dpad_right)
            I_CONSTANT += constantDelta/10;
        if(gamepad1.dpad_left)
            I_CONSTANT -= constantDelta/10;

        if(gamepad1.b)
            BASE_MOTOR_POWER += constantDelta * 10;
        if(gamepad1.a)
            BASE_MOTOR_POWER -= constantDelta * 10;
    }

    public void updateVariables()
    {
        // Calculate current position based on average of encoders
        currentPosition = (motorLeft.getCurrentPosition() - motorRight.getCurrentPosition()) / 2;
        deltaPosition = currentPosition - lastPosition;
        lastPosition = currentPosition;

        // Used to calculate delta error below
        double lastError = error;

        lightLeft = lightSensorLeft.getLightDetectedRaw();
        lightRight = lightSensorRight.getLightDetectedRaw();

        /*
         * Takes the average of the 2 sensors to find the error.
         * Averaging doesn't work if one sensor is off the line,
         * so a different calculation is made to compensate.
         */
        error = (lightLeft - lightRight) / 2;
        if(lightLeft < MIN_LIGHT_THRESHOLD)
            error = -MAX_ERROR; // TODO: Change to a calculation
        else if(lightRight < MIN_LIGHT_THRESHOLD)
            error = MAX_ERROR; // TODO: Change to a calculation

        // Also calculate error sum and delta error
        errorSum += error;
        deltaError = error - lastError;
    }

    public void calculatePID()
    {
        // Standard PID calculation
        P = P_CONSTANT * error;
        I = I_CONSTANT * errorSum;
        /*
         * TODO: Is is necessary to handle this divide by 0 exception?
         * It shouldn't be a problem in normal operation, but might be during the first calculation
         *
         * Update 8/9/16: It was an issue, and the try/catch didn't protect it. TODO: Fix / 0
         */
        // Just in case delta time is 0
        /*try
        {
            D = D_CONSTANT * deltaError / deltaPosition;
        }
        catch (ArithmeticException e)
        {
            D = 0;
        }*/

        PID = P + I + D;
    }

    public void setMotorPower()
    {
        // Adjust motor power using PID result
        double leftPow = BASE_MOTOR_POWER - PID;
        double rightPow = BASE_MOTOR_POWER + PID;

        // Don't exceed 1
        if(Math.abs(leftPow) > 1)
            leftPow = Math.signum(leftPow);
        if(Math.abs(rightPow) > 1)
            rightPow = Math.signum(rightPow);

        motorLeft.setPower(leftPow);
        motorRight.setPower(rightPow);
    }

    public boolean isOnLine()
    {
        // Check to see if robot has reached the end of the line
        if(lightLeft > MAX_LIGHT_THRESHOLD && lightRight > MAX_LIGHT_THRESHOLD)
        {
            telemetry.log.add("End of the line!");
            return false;
        }
        // Check to see if robot can still see the line
        else if(lightLeft < MIN_LIGHT_THRESHOLD && lightRight < MIN_LIGHT_THRESHOLD)
        {
            telemetry.log.add("Fell off of the line!");
            return false;
        }
        // Must still be on the line
        else
            return true;
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
        parameters.i2cAddress = TSL2561LightSensor.I2CADDR.DEFAULT;

        // Initialize light sensors
        lightSensorRight = ClassFactory.createAdaFruitTSL2561LightSensor(hardwareMap.i2cDevice.get("lightSensorRight"), parameters);
        parameters.i2cAddress = TSL2561LightSensor.I2CADDR.ADDR_29; //reusing variable
        lightSensorLeft = ClassFactory.createAdaFruitTSL2561LightSensor(hardwareMap.i2cDevice.get("lightSensorLeft"), parameters);

        // Initialize LED
        ledLeft = hardwareMap.led.get("ledLeft");
        ledRight = hardwareMap.led.get("ledRight");
        ledLeft.enable(true);
        ledRight.enable(true);

        // Set up telemetry data
        configureDashboard();
    }

    public void configureDashboard()
    {
        telemetry.setUpdateIntervalMs(100);
        // Light sensor readings
        telemetry.addLine
                (
                        telemetry.item("Light | Left:", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(lightLeft);
                            }
                        }),
                        telemetry.item("Right: ", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(lightRight);
                            }
                        })
                );
        // PID values
        telemetry.addLine
                (
                        telemetry.item("PID | P:", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(P);
                            }
                        }),
                        telemetry.item("I: ", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(I);
                            }
                        }),
                        telemetry.item("D: ", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(D);
                            }
                        }),
                        telemetry.item("E: ", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(error);
                            }
                        })
                );
        // Constants
        telemetry.addLine
                (
                        telemetry.item("Constants | P:", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(P_CONSTANT);
                            }
                        }),
                        telemetry.item("I: ", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(I_CONSTANT);
                            }
                        }),
                        telemetry.item("D: ", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(D_CONSTANT);
                            }
                        }),
                        telemetry.item("Pow: ", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(BASE_MOTOR_POWER);
                            }
                        })
                );
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
                                return formatNumber(motorRight.getPower());
                            }
                        })
                );
        // Misc telemetry info
        telemetry.addLine
                (
                        telemetry.item("Distance Travelled:", new IFunc<Object>()
                        {
                            @Override public Object value()
                            {
                                return formatNumber(deltaPosition);
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
