package org.swerverobotics.library.shared;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

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

    // Declare light sensors
    TSL2561LightSensor lightSensorLeft;
    TSL2561LightSensor lightSensorRight;
    AdaFruitTSL2561LightSensor.Parameters parameters = new  AdaFruitTSL2561LightSensor.Parameters();

    // TODO: Change these to reasonable values via testing
    // Declare constants
    static final double MIN_LIGHT_THRESHOLD = 0.1;
    static final double MAX_LIGHT_THRESHOLD = 0.5;
    static final double MAX_ERROR = 1.5;
    static final double BASE_MOTOR_POWER = 0.5;
    static final double P_CONSTANT = 0.1;
    static final double I_CONSTANT = 0.1;
    static final double D_CONSTANT = 0.1;

    // Declare variables
    double error = 0.0;
    double errorSum = 0.0;
    double deltaError = 0.0;
    double P =0.0, I = 0.0, D = 0.0;
    double PID = 0.0;
    double lastPosition = 0.0;
    double currentPosition = 0.0;
    double deltaPosition = 0.0;

    @Override protected void main() throws InterruptedException
    {
        // Initialize hardware and other important things
        initializeRobot();

        // Wait until start button has been pressed
        waitForStart();

        // Main loop
        while(opModeIsActive() && isOnLine())
        {
            // Calculate error to calculate PID to control motors
            updateVariables();
            calculatePID();
            setMotorPower();

            telemetry.update();
            idle();
        }

        // Stop the robot!
        motorLeft.setPower(0);
        motorRight.setPower(0);
    }

    public void updateVariables()
    {
        // Calculate current position based on average of encoders
        currentPosition = (motorLeft.getCurrentPosition() + motorRight.getCurrentPosition()) / 2;
        deltaPosition = currentPosition - lastPosition;
        lastPosition = currentPosition;

        // Used to calculate delta error below
        double lastError = error;

        /*
         * Takes the average of the 2 sensors to find the error.
         * Averaging doesn't work if one sensor is off the line,
         * so a different calculation is made to compensate.
         */
        error = (lightSensorLeft.getLightDetected() + lightSensorRight.getLightDetected()) / 2;
        if(lightSensorLeft.getLightDetected() < MIN_LIGHT_THRESHOLD)
            error = MAX_ERROR - lightSensorRight.getLightDetected();
        else if(lightSensorRight.getLightDetected() < MIN_LIGHT_THRESHOLD)
            error = MAX_ERROR - lightSensorLeft.getLightDetected();

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
         */
        // Just in case delta time is 0
        try
        {
            D = D_CONSTANT * deltaError / deltaPosition;
        }
        catch (ArithmeticException e)
        {
            D = 0;
        }
    }

    public void setMotorPower()
    {
        // Adjust motor power using PID result
        motorLeft.setPower(BASE_MOTOR_POWER + PID);
        motorRight.setPower(BASE_MOTOR_POWER - PID);
    }

    public boolean isOnLine()
    {
        // Check to see if robot has reached the end of the line
        if(lightSensorLeft.getLightDetected() > MAX_LIGHT_THRESHOLD && lightSensorRight.getLightDetected() > MAX_LIGHT_THRESHOLD)
        {
            telemetry.log.add("End of the line!");
            return false;
        }
        // Check to see if robot can still see the line
        else if(lightSensorLeft.getLightDetected() < MIN_LIGHT_THRESHOLD && lightSensorRight.getLightDetected() < MIN_LIGHT_THRESHOLD)
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

        // Initialize light sensors
        lightSensorLeft = ClassFactory.createAdaFruitTSL2561LightSensor(hardwareMap.i2cDevice.get("lightSensorLeft"), parameters);
        lightSensorRight = ClassFactory.createAdaFruitTSL2561LightSensor(hardwareMap.i2cDevice.get("lightSensorRight"), parameters);

        // Set up telemetry data
        configureDashboard();
    }

    public void configureDashboard()
    {
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
                                return formatNumber(motorLeft.getPower());
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
