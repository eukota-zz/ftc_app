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
    DcMotor MotorHanger = null;

    // Declare servos
    //Servo CollectorServo = null;
    Servo LeftZiplineHitter = null;
    Servo RightZiplineHitter = null;
    Servo HikerDropper = null;
    Servo HangerServo = null;
    Servo HolderServoLeft = null;
    Servo HolderServoRight = null;

    boolean LeftZiplineHitterDeployed = false;
    boolean RightZiplineHitterDeployed = false;
    boolean HolderServoLeftDeployed = false;
    boolean HolderServoRightDeployed = false;

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
    static final double STOP = 0.0;

    static double currentDrivePowerFactor = FULL_POWER;

    //the drive wheels are larger than the triangle wheels so we drive them at less power
    double WHEEL_DRIVE_MULTIPLIER = 0.5;

    //servo constants
    static final double LEFT_ZIPLINEHITTER_NOTDEPLOYED = -1.0;
    static final double LEFT_ZIPLINEHITTER_DEPLOYED = 0.6;
    static final double RIGHT_ZIPLINEHITTER_NOTDEPLOYED = -1.0;
    static final double RIGHT_ZIPLINEHITTER_DEPLOYED = 0.6;
    static final double HIKER_DROPPER_NOTDEPLOYED = -1.0;
    static final double HIKER_DROPPER_DEPLOYED = 1.0;
    static final double HANGER_SERVO_NOTDEPLOYED = 0.0;
    static final double HANGER_SERVO_STOP = 0.5;
    static final double HANGER_SERVO_DEPLOYED = 1.0;
    static final double HOLDER_SERVO_LEFT_NOTDEPLOYED = -1.0;
    static final double HOLDER_SERVO_LEFT_DEPLOYED = 1.0;
    static final double HOLDER_SERVO_RIGHT_NOTDEPLOYED = 1.0;
    static final double HOLDER_SERVO_RIGHT_DEPLOYED = -1.0;

    //deadzone constant
    static final float JOYSTICK_DEADZONE = 0.05f;

    @Override
    protected void main() throws InterruptedException
    {
        //Initialize our hardware
        this.initializeHardware();

        // Configure the dashboard however we want it
        //this.configureDashboard();

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
            //this.telemetry.update();

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
        this.MotorHanger = this.hardwareMap.dcMotor.get("MotorHanger");

        // Configure the knobs of the hardware according to how you've wired your
        // robot. Here, we assume that there are no encoders connected to the motors,
        // so we inform the motor objects of that fact (we might use encoders later.)
        this.MotorRightBack.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorLeftBack.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorLeftTriangle.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorLeftClimber.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorRightClimber.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorRightTriangle.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorHanger.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);


        //the correct motors should be reversed, since they are on the opposite side of the robot.
        this.MotorRightBack.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftTriangle.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftClimber.setDirection(DcMotor.Direction.REVERSE);

        stopAllMotors();

        //this.CollectorServo = this.hardwareMap.servo.get("CollectorServo");
        this.LeftZiplineHitter = this.hardwareMap.servo.get("LeftZiplineHitter");
        this.RightZiplineHitter = this.hardwareMap.servo.get("RightZiplineHitter");
        this.HikerDropper = this.hardwareMap.servo.get("HikerDropper");
        this.HangerServo = this.hardwareMap.servo.get("HangerServo");
        this.HolderServoLeft = this.hardwareMap.servo.get("HolderServoLeft");
        this.HolderServoRight = this.hardwareMap.servo.get("HolderServoRight");

        RightZiplineHitter.setDirection(Servo.Direction.REVERSE);

        LeftZiplineHitter.setPosition(LEFT_ZIPLINEHITTER_NOTDEPLOYED);
        RightZiplineHitter.setPosition(RIGHT_ZIPLINEHITTER_NOTDEPLOYED);
        HikerDropper.setPosition(HIKER_DROPPER_NOTDEPLOYED);
        HangerServo.setPosition(HANGER_SERVO_STOP);
        HolderServoLeft.setPosition(HOLDER_SERVO_LEFT_NOTDEPLOYED);
        HolderServoRight.setPosition(HOLDER_SERVO_RIGHT_NOTDEPLOYED);


        this.gamepad1.setJoystickDeadzone(JOYSTICK_DEADZONE);
        this.gamepad2.setJoystickDeadzone(JOYSTICK_DEADZONE);
    }

    private void stopAllMotors()
    {
        this.MotorRightBack.setPower(0);
        this.MotorRightClimber.setPower(0);
        this.MotorRightTriangle.setPower(0);
        this.MotorLeftBack.setPower(0);
        this.MotorLeftTriangle.setPower(0);
        this.MotorLeftClimber.setPower(0);
        this.MotorHanger.setPower(0);

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

        wheelPowerLeft = leftSidePower * WHEEL_DRIVE_MULTIPLIER * currentDrivePowerFactor;
        wheelPowerRight = rightSidePower * WHEEL_DRIVE_MULTIPLIER * currentDrivePowerFactor;

        trianglePowerLeft = leftSidePower * currentDrivePowerFactor;
        trianglePowerRight = rightSidePower * currentDrivePowerFactor;

        wheelClimberLeft = leftTrianglePower * currentDrivePowerFactor;
        wheelClimberRight = rightTrianglePower * currentDrivePowerFactor;

        //set the powers on the motors
        MotorRightBack.setPower( wheelPowerRight );
        MotorLeftBack.setPower( wheelPowerLeft );
        MotorRightTriangle.setPower( trianglePowerRight );
        MotorLeftTriangle.setPower( trianglePowerLeft );
        MotorLeftClimber.setPower(  wheelClimberLeft);
        MotorRightClimber.setPower( wheelClimberRight );
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
           driveBackwards(leftSidePower, rightSidePower, climberPower, climberPower);
        }
        //drive climb mode
        else if (currentDriveMode == DriveModeEnum.DriveModeRamp)
        {
            //since we want both our climbers and wheels to have the same power, we set the climbers equal to the left and right sides
            driveBackwards(leftSidePower, rightSidePower, leftSidePower, rightSidePower);
        }

    }

    private void handleDriverInput(Gamepad pad1, Gamepad pad2)
    {
        if (pad2.y)
        {
            HikerDropper.setPosition(HIKER_DROPPER_DEPLOYED);
        }
        else
        {
            HikerDropper.setPosition(HIKER_DROPPER_NOTDEPLOYED);
        }

        if (pad2.dpad_left)
        {
            HangerServo.setPosition(HANGER_SERVO_DEPLOYED);
        }
        else if (pad2.dpad_right)
        {
            HangerServo.setPosition(HANGER_SERVO_NOTDEPLOYED);
        }
        else
        {
            HangerServo.setPosition(HANGER_SERVO_STOP);
        }

        if (pad2.dpad_down)
        {
            MotorHanger.setPower(-1*FULL_POWER);
        }
        else if (pad2.dpad_up)
        {
            MotorHanger.setPower(FULL_POWER);
        }
        else
        {
            MotorHanger.setPower(STOP);
        }

        //deploy the holder
        if (pad2.b & !HolderServoRightDeployed)
        {
            HolderServoRight.setPosition(HOLDER_SERVO_RIGHT_DEPLOYED);
            HolderServoRightDeployed = true;
        }
        else if (pad2.b & HolderServoRightDeployed)
        {
            HolderServoRight.setPosition(HOLDER_SERVO_RIGHT_NOTDEPLOYED);
            HolderServoRightDeployed = false;
        }

        if (pad2.x & !HolderServoLeftDeployed)
        {
            HolderServoLeft.setPosition(HOLDER_SERVO_LEFT_DEPLOYED);
            HolderServoLeftDeployed = true;
        }
        else if (pad2.x & HolderServoLeftDeployed)
        {
            HolderServoLeft.setPosition(HOLDER_SERVO_LEFT_NOTDEPLOYED);
            HolderServoLeftDeployed = false;
        }

        /*if (pad2.a)
        {
            CollectorServo.setPosition(0.0);
        }
        else
        {
            CollectorServo.setPosition(0.5);
        }*/

        if (pad2.left_bumper & !LeftZiplineHitterDeployed)
        {
            LeftZiplineHitter.setPosition(LEFT_ZIPLINEHITTER_DEPLOYED);
            LeftZiplineHitterDeployed = true;
            telemetry.log.add("left bumper:deployed");
        }
        else if (pad2.left_bumper & LeftZiplineHitterDeployed)
        {
            LeftZiplineHitter.setPosition(LEFT_ZIPLINEHITTER_NOTDEPLOYED);
            LeftZiplineHitterDeployed = false;
            telemetry.log.add("left bumper:notdeployed");
        }
        else telemetry.log.add("no bumper");

        //The RightZiplineHitter reads from (0-1), which is different than the LeftZiplineHitter(0-360)
        if (pad2.right_bumper & !RightZiplineHitterDeployed)
        {
            RightZiplineHitter.setPosition(RIGHT_ZIPLINEHITTER_DEPLOYED);
            RightZiplineHitterDeployed = true;
        }
        else if (pad2.right_bumper & RightZiplineHitterDeployed)
        {
            RightZiplineHitter.setPosition(RIGHT_ZIPLINEHITTER_NOTDEPLOYED);
            RightZiplineHitterDeployed = false;
        }
        //toggle field driving mode
        if (pad1.a )
        {
            setFieldDrivingMode();
        }
        //toggle "ready" mode for getting ready to climb the ramp
        //need to drive backwards so we can line up against the ramp
        else if (pad1.b) {
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
