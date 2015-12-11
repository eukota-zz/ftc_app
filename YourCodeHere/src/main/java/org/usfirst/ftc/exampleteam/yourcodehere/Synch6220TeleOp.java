package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * 6220's TeleOp for driving our triangle wheels robot.
 */
@TeleOp(name = "Synch6220TeleOp", group = "Swerve Examples")
public class Synch6220TeleOp extends SynchronousOpMode
{
    DcMotor MotorRightBack = null;
    DcMotor MotorLeftBack = null;
    DcMotor MotorLeftTriangle = null;
    DcMotor MotorLeftClimber = null;
    DcMotor MotorRightClimber = null;
    DcMotor MotorRightTriangle = null;
    // Declare servos
    Servo CollectorServo = null;
    Servo LeftZiplineHitter = null;
    Servo RightZiplineHitter = null;
    Servo HikerDropper = null;

    enum DriveModeEnum
    {
        DriveModeField,
        DriveModeBackwards,
        DriveModeRamp
    };

    DriveModeEnum currentDriveMode = DriveModeEnum.DriveModeField;

    //constants to control motor power to support normal speed and slow speed driving.
    static final double FULL_POWER = 1.0;
    static final double LOW_POWER = 0.3;

    static double currentDrivePowerFactor = FULL_POWER;

    //the drive wheels are larger than the triangle wheels so we drive them at less power
    double WHEEL_DRIVE_MULTIPLIER = 0.5;

    @Override
    protected void main() throws InterruptedException
    {
        //Initialize our hardware
        this.initializeHardware();

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
                this.handleDriverInput(this.gamepad1, this.gamepad2);

                this.driveRobot(this.gamepad1);
            }

            // Emit telemetry with the newest possible values
            this.telemetry.update();

            // Let the rest of the system run until there's a stimulus from the robot controller runtime.
            this.idle();
        }
    }

    private void initializeHardware()
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

        // Configure the knobs of the hardware according to how you've wired your
        // robot. Here, we assume that there are no encoders connected to the motors,
        // so we inform the motor objects of that fact (we might use encoders later.)
        this.MotorRightBack.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.MotorLeftBack.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.MotorLeftTriangle.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorLeftClimber.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorRightClimber.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorRightTriangle.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        //the correct motors should be reversed, since they are on the opposite side of the robot.
        this.MotorRightBack.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftTriangle.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftClimber.setDirection(DcMotor.Direction.REVERSE);

        stopAllMotors();

        this.CollectorServo = this.hardwareMap.servo.get("CollectorServo");
        this.LeftZiplineHitter = this.hardwareMap.servo.get("LeftZiplineHitter");
        this.RightZiplineHitter = this.hardwareMap.servo.get("RightZiplineHitter");
        this.HikerDropper = this.hardwareMap.servo.get("HikerDropper");
        RightZiplineHitter.setDirection(Servo.Direction.REVERSE);

        LeftZiplineHitter.setPosition(-10.0);
        RightZiplineHitter.setPosition(-0.75);
        HikerDropper.setPosition(0.95);
    }

    private void stopAllMotors()
    {
        this.MotorRightBack.setPower(0);
        this.MotorRightClimber.setPower(0);
        this.MotorRightTriangle.setPower(0);
        this.MotorLeftBack.setPower(0);
        this.MotorLeftTriangle.setPower(0);
        this.MotorLeftClimber.setPower(0);
    }

    /**
     * This function is used to make field driving easier
     * and available as a method able to be called.
     */
    private void driveForwards(double leftSidePower, double rightSidePower, double leftTrianglePower, double rightTrianglePower)
    {
        //motor powers
        double wheelPowerLeft = 0.0;
        double wheelPowerRight = 0.0;
        double wheelClimberLeft = 0.0;
        double wheelClimberRight = 0.0;
        double trianglePowerLeft = 0.0;
        double trianglePowerRight = 0.0;

        wheelPowerLeft = rightSidePower * WHEEL_DRIVE_MULTIPLIER * currentDrivePowerFactor;
        wheelPowerRight = leftSidePower * WHEEL_DRIVE_MULTIPLIER * currentDrivePowerFactor;

        trianglePowerLeft = rightSidePower * currentDrivePowerFactor;
        trianglePowerRight = leftSidePower * currentDrivePowerFactor;

        wheelClimberLeft = leftTrianglePower * currentDrivePowerFactor;
        wheelClimberRight = rightTrianglePower * currentDrivePowerFactor;

        //set the powers on the motors
        MotorRightBack.setPower( wheelPowerRight );
        MotorLeftBack.setPower( wheelPowerLeft );
        MotorRightTriangle.setPower( trianglePowerRight );
        MotorLeftTriangle.setPower( trianglePowerLeft );
        MotorLeftClimber.setPower(  wheelClimberLeft );
        MotorRightClimber.setPower( wheelClimberRight );

        this.gamepad1.setJoystickDeadzone(0.05f);
    }

    /**
     * This does the same thing as the driveForwards function,
     * but it prepares to drive onto the ramp
     *
     */
    private void driveBackwards(double leftSidePower, double rightSidePower, double leftTrianglePower, double rightTrianglePower)
    {
       driveForwards(-1*leftSidePower, -1*rightSidePower, -1*leftTrianglePower, -1*rightTrianglePower);
    }

    /**
     * This is the body of the TeleOp that allows the driver to control the robot.
     */
    //TO DO:  we need to finish refactoring this.
    void driveRobot(Gamepad pad) throws InterruptedException
    {
        // Remember that the gamepad sticks range from -1 to +1, and that the motor
        // power levels range over the same amount


        //read input from the controller
        double  leftSidePower = pad.left_stick_y;
        double rightSidePower = pad.right_stick_y;
        double climberPower = pad.right_trigger;

        /**
         * Calculate motor power based on drive mode and controller input
         */

        //field driving mode
        if (currentDriveMode == DriveModeEnum.DriveModeField)
        {
            driveForwards(leftSidePower, rightSidePower, climberPower, climberPower);
        }
        //"ready" mode for getting ready to climb the ramp
        //we need to drive backwards when aligning with the ramp
        else if (currentDriveMode == DriveModeEnum.DriveModeBackwards)
        {
           driveBackwards(rightSidePower, leftSidePower, climberPower, climberPower);
        }
        //drive climb mode
        else if (currentDriveMode == DriveModeEnum.DriveModeRamp)
        {
            driveBackwards(leftSidePower, rightSidePower, leftSidePower, rightSidePower);
        }

    }

    private void handleDriverInput(Gamepad pad1, Gamepad pad2)
    {
        if (pad2.y)
        {
            HikerDropper.setPosition(-1.0);
            this.telemetry.update();
        }
        else
        {
            HikerDropper.setPosition(0.95);
            this.telemetry.update();
        }

        if (pad2.a)
        {
            CollectorServo.setPosition(0.0);
            this.telemetry.update();
        }
        else
        {
            CollectorServo.setPosition(0.5);
            this.telemetry.update();
        }

        if (pad2.left_bumper)
        {
            LeftZiplineHitter.setPosition(90);
            this.telemetry.update();
        }

        if ((pad2.left_trigger > 0.2))
        {
            LeftZiplineHitter.setPosition(-90);
            this.telemetry.update();
        }
        //The RightZiplineHitter reads from (0-1), which is different than the LeftZiplineHitter(0-360)
        if (pad2.right_bumper)
        {
            RightZiplineHitter.setPosition(0.75);
            this.telemetry.update();
        }

        if ((pad2.right_trigger > 0.2))
        {
            RightZiplineHitter.setPosition(-0.75);
            this.telemetry.update();
        }
        //toggle field driving mode
        if (pad1.a )
        {
            setFieldDrivingMode();
        }
        //toggle "ready" mode for getting ready to climb the ramp
        //need to drive backwards so we can line up against the ramp
        else if (pad1.b)
        {
            setBackwardsDriveMode();
        }
        //toggle drive climb mode
        else if (pad1.y)
        {
            setRampClimbingMode();
        }

        //reduce power so we can go slower ("slow mode") and have more control
        if (pad1.right_bumper)
        {
            currentDrivePowerFactor = LOW_POWER;
        }
        else
        {
            currentDrivePowerFactor = FULL_POWER;
        }

    }

    //This is the driving mode for going up the ramp
    private void setRampClimbingMode()
    {
        setDriveMode(DriveModeEnum.DriveModeRamp);
    }

    //This is the driving mode we use when we want to drive backwards to align with the ramp
    private void setBackwardsDriveMode()
    {
        setDriveMode(DriveModeEnum.DriveModeBackwards);
    }

    //This is the driving mode we use when driving around the field
    private void setFieldDrivingMode()
    {
        setDriveMode(DriveModeEnum.DriveModeField);
    }

    private void setDriveMode(DriveModeEnum mode)
    {
        currentDriveMode = mode;
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
