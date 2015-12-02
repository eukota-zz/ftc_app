package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * An example of a synchronous opmode that implements a simple drive-a-bot.
 */
@TeleOp(name = "Synch6220TeleOp", group = "Swerve Examples")
public class Synch6220TeleOp extends SynchronousOpMode {
    // All hardware variables can only be initialized inside the main() function,
    // not here at their member variable declarations.
    DcMotor MotorRightBack = null;
    DcMotor MotorLeftBack = null;
    DcMotor MotorLeftTriangle = null;
    DcMotor MotorLeftClimber = null;
    DcMotor MotorRightClimber = null;
    DcMotor MotorRightTriangle = null;
    //Servo CollectorServo = null;

    enum DriveMode
    {
        FieldDriving,
        BackwardsDriving,
        RampClimbing
    };

    DriveMode currentDriveMode = DriveMode.FieldDriving;

    //variables to control motor power to support normal speed and slow speed driving
    double FULL_POWER = 1.0;
    double LOW_POWER = 0.3;
    double currentDrivePowerFactor = FULL_POWER;


    @Override
    protected void main() throws InterruptedException
    {
        //Initialize our hardware
        InitializeHardware();

        // Configure the dashboard however we want it
        this.configureDashboard();

        // Wait until we've been given the ok to go
        this.waitForStart();

        // Enter a loop processing all the input we receive
        while (this.opModeIsActive())
        {
            if (this.updateGamepads())
            {
                // There is (likely) new gamepad input available.
                SetDrivingMode(this.gamepad1);

                this.doManualDrivingControl(this.gamepad1);
            }

            // Emit telemetry with the freshest possible values
            this.telemetry.update();

            // Let the rest of the system run until there's a stimulus from the robot controller runtime.
            this.idle();
        }
    }

    private void InitializeHardware()
    {
        // Initialize our hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names you assigned during the robot configuration
        // step you did in the FTC Robot Controller app on the phone.
        this.MotorRightBack = this.hardwareMap.dcMotor.get("MotorRightBack");
        this.MotorLeftBack = this.hardwareMap.dcMotor.get("MotorLeftBack");
        this.MotorLeftTriangle = this.hardwareMap.dcMotor.get("MotorLeftTriangle");
        this.MotorLeftClimber = this.hardwareMap.dcMotor.get("MotorLeftClimber");
        this.MotorRightClimber = this.hardwareMap.dcMotor.get("MotorRightClimber");
        this.MotorRightTriangle = this.hardwareMap.dcMotor.get("MotorRightTriangle");

        //this.CollectorServo = this.hardwareMap.servo.get("CollectorServo");

        // Configure the knobs of the hardware according to how you've wired your
        // robot. Here, we assume that there are no encoders connected to the motors,
        // so we inform the motor objects of that fact.
        this.MotorRightBack.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.MotorLeftBack.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.MotorLeftTriangle.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorLeftClimber.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorRightClimber.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorRightTriangle.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        // One of the two motors (here, the left) should be set to reversed direction
        // so that it can take the same power level values as the other motor.
        this.MotorRightBack.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftTriangle.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftClimber.setDirection(DcMotor.Direction.REVERSE);

        this.MotorRightBack.setPower(0);
        this.MotorRightClimber.setPower(0);
        this.MotorRightTriangle.setPower(0);
        this.MotorLeftBack.setPower(0);
        this.MotorLeftTriangle.setPower(0);
        this.MotorLeftClimber.setPower(0);
    }

    /**
     * Implement a simple two-motor driving logic using the left and right
     * right joysticks on the indicated game pad.
     */
    void doManualDrivingControl(Gamepad pad) throws InterruptedException
    {
        // Remember that the gamepad sticks range from -1 to +1, and that the motor
        // power levels range over the same amount

        /**
         * Useful constants for setting motor power
         */

        //direction constants
        int DIRECTION_FORWARDS = 1;
        int DIRECTION_BACKWARDS = -1;

        //the drive wheels are larger than the triangle wheels so we drive them at less power
        double WHEEL_DRIVE_FACT = 0.5;


        /**
         * Useful variables for setting motor power
         */

        //motor powers
        double wheelPowerLeft = 0.0;
        double wheelPowerRight = 0.0;
        double wheelClimberLeft = 0.0;
        double wheelClimberRight = 0.0;
        double trianglePowerLeft = 0.0;
        double trianglePowerRight = 0.0;

        //read input from the controller
        double  leftSidePower = ignoreControllerDeadZone(pad.left_stick_y);
        double rightSidePower = ignoreControllerDeadZone(pad.right_stick_y);
        double climberPower = ignoreControllerDeadZone(pad.right_trigger);

        /**
         * Calculate motor power based on drive mode and controller input
         */

        //field driving mode
        if (currentDriveMode == DriveMode.FieldDriving)
        {
            wheelPowerLeft = rightSidePower * WHEEL_DRIVE_FACT * DIRECTION_FORWARDS * currentDrivePowerFactor;
            wheelPowerRight = leftSidePower * WHEEL_DRIVE_FACT * DIRECTION_FORWARDS * currentDrivePowerFactor;

            trianglePowerLeft = rightSidePower * DIRECTION_FORWARDS * currentDrivePowerFactor;
            trianglePowerRight = leftSidePower * DIRECTION_FORWARDS * currentDrivePowerFactor;

            wheelClimberLeft = climberPower * DIRECTION_FORWARDS * currentDrivePowerFactor;
            wheelClimberRight = climberPower * DIRECTION_FORWARDS * currentDrivePowerFactor;
        }
        //"ready" mode for getting ready to climb the ramp
        //we need to drive backwards when aligning with the ramp
        else if (currentDriveMode == DriveMode.BackwardsDriving)
        {
            wheelPowerLeft = leftSidePower * WHEEL_DRIVE_FACT * DIRECTION_BACKWARDS * currentDrivePowerFactor;
            wheelPowerRight = rightSidePower * WHEEL_DRIVE_FACT * DIRECTION_BACKWARDS * currentDrivePowerFactor;

            trianglePowerLeft = leftSidePower * DIRECTION_BACKWARDS * currentDrivePowerFactor;
            trianglePowerRight = rightSidePower * DIRECTION_BACKWARDS * currentDrivePowerFactor;

            wheelClimberLeft =  climberPower * DIRECTION_BACKWARDS * currentDrivePowerFactor;
            wheelClimberRight = climberPower * DIRECTION_BACKWARDS * currentDrivePowerFactor;
        }
        //drive climb mode
        else if (currentDriveMode == DriveMode.RampClimbing)
        {
            wheelPowerLeft = leftSidePower * WHEEL_DRIVE_FACT * DIRECTION_BACKWARDS * currentDrivePowerFactor;
            wheelPowerRight = rightSidePower * WHEEL_DRIVE_FACT * DIRECTION_BACKWARDS * currentDrivePowerFactor;

            trianglePowerLeft = leftSidePower * DIRECTION_BACKWARDS * currentDrivePowerFactor;
            trianglePowerRight = rightSidePower * DIRECTION_BACKWARDS * currentDrivePowerFactor;

            wheelClimberLeft =  leftSidePower * DIRECTION_BACKWARDS * currentDrivePowerFactor;
            wheelClimberRight = rightSidePower * DIRECTION_BACKWARDS * currentDrivePowerFactor;
        }

        //set the powers on the motors
        MotorRightBack.setPower( wheelPowerRight );
        MotorLeftBack.setPower( wheelPowerLeft );
        MotorRightTriangle.setPower( trianglePowerRight );
        MotorLeftTriangle.setPower( trianglePowerLeft );
        MotorLeftClimber.setPower(  wheelClimberLeft );
        MotorRightClimber.setPower( wheelClimberRight );

    }

    private void SetDrivingMode(Gamepad pad)
    {
        //toggle field driving mode
        if (pad.a )
        {
            SetFieldDrivingMode();
        }
        //toggle "ready" mode for getting ready to climb the ramp
        //need to drive backwards so we can line up against the ramp
        else if (pad.b)
        {
            SetBackwardsDriveMode();
        }
        //toggle drive climb mode
        else if (pad.y)
        {
            SetRampClimbingMode();
        }

        //reduce power so we can go slower ("slow mode") and have more control
        if (pad.right_bumper)
        {
            currentDrivePowerFactor = LOW_POWER;
        }
        else
        {
            currentDrivePowerFactor = FULL_POWER;
        }

    }

    //This is the driving mode for going up the ramp
    private void SetRampClimbingMode()
    {
        currentDriveMode = DriveMode.RampClimbing;
    }

    //This is the driving mode we use when we want to drive backwards to align with the ramp
    private void SetBackwardsDriveMode()
    {
        currentDriveMode = DriveMode.BackwardsDriving;
    }

    //This is the driving mode we use when driving around the field
    private void SetFieldDrivingMode()
    {
        currentDriveMode = DriveMode.FieldDriving;;
    }


    //Ignore the controller region that is very close to zero; treat it as a dead zone
    double ignoreControllerDeadZone(double value)
    {
        double deadZone = 0.05;
        double newSlope = (1.0 - deadZone);
        double output = 0.0;

        if (Math.abs(value) > deadZone)
        {
            output = newSlope * (value + Math.signum(value) * deadZone);
        }
        return output;
    }

    void configureDashboard()
    {

        this.telemetry.log.setDisplayOldToNew(false);   // And we show the log in new to old order, just because we want to
        this.telemetry.log.setCapacity(10);             // We can control the number of lines used by the log

        // Configure the dashboard. Here, it will have one line, which will contain three items
        this.telemetry.addLine
                (
                        this.telemetry.item("left back:", new IFunc<Object>()
                        {
                            @Override
                            public Object value()
                            {
                                return format(MotorLeftBack.getPower());
                            }
                        }),
                        this.telemetry.item("right back: ", new IFunc<Object>()
                        {
                            @Override
                            public Object value()
                            {
                                return format(MotorRightBack.getPower());
                            }
                        })
                );
    }


    // Handy functions for formatting data for the dashboard
    String format(double d) {
        return String.format("%.1f", d);
    }
}
