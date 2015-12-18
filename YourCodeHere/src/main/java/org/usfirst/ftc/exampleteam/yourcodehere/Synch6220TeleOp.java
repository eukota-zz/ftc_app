package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

import java.util.ArrayList;
import java.util.List;

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
    //Servo6220 CollectorServo = null;
    Servo LeftZiplineHitter = null;
    Servo RightZiplineHitter = null;
    Servo HikerDropper = null;
    Servo HangerServo = null;
    Servo HolderServoLeft = null;
    Servo HolderServoRight = null;

    List<DcMotor> motors = new ArrayList<>();
    List<Servo> servos = new ArrayList<>();


    boolean LeftZiplineHitterDeployed = false;
    boolean RightZiplineHitterDeployed = false;
    boolean HolderServoLeftDeployed = false;
    boolean HolderServoRightDeployed = false;

    public Synch6220TeleOp()
    {
        // Initialize our hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names you assigned during the robot configuration
        // step you did in the FTC Robot Controller app on the phone.

        //dynamically load all servos
        for (String curServoName : Servo6220.GetNames())
        {
            servos.add(this.hardwareMap.servo.get(curServoName));
        }
        //initialize old servo references for backwards compatibility
        this.LeftZiplineHitter = servos.get(Servo6220.LeftZiplineHitter.ordinal());
        this.RightZiplineHitter = servos.get(Servo6220.RightZiplineHitter.ordinal());
        this.HikerDropper = servos.get(Servo6220.HikerDropper.ordinal());
        this.HangerServo = servos.get(Servo6220.HangerServo.ordinal());
        this.HolderServoLeft = servos.get(Servo6220.HolderServoLeft.ordinal());
        this.HolderServoRight = servos.get(Servo6220.HolderServoRight.ordinal());

        //dynamically load all motors
        for (String curMotorName : Motor6220.GetNames())
        {
            motors.add(this.hardwareMap.dcMotor.get(curMotorName));
        }
        //initialize old motor references for backwards compatibility
        this.MotorRightBack = motors.get(Motor6220.RightBack.ordinal());
        this.MotorLeftBack = motors.get(Motor6220.LeftBack.ordinal());
        this.MotorLeftTriangle = motors.get(Motor6220.RightTriangle.ordinal());
        this.MotorLeftClimber = motors.get(Motor6220.LeftTriangle.ordinal());
        this.MotorRightClimber = motors.get(Motor6220.RightClimber.ordinal());
        this.MotorRightTriangle = motors.get(Motor6220.LeftClimber.ordinal());
        this.MotorHanger = motors.get(Motor6220.MotorHanger.ordinal());
    }

    enum DriveModeEnum
    {
        DriveModeField,
        DriveModeBackwards,
        DriveModeRamp
    }

    DriveModeEnum currentDriveMode = DriveModeEnum.DriveModeField;

    //constants to control motor power to support normal speed and slow speed driving.
    static final double FULL_POWER = 1.0;
    static final double LOW_POWER = 0.3;
    static final double STOP = 0.0;

    static double currentDrivePowerFactor = FULL_POWER;

    //the drive wheels are larger than the triangle wheels so we drive them at less power
    //TODO change this multiplier to a more accurate number, perhaps closer to 0.8
    double WHEEL_DRIVE_MULTIPLIER = 0.3;

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
    protected void main() throws InterruptedException {
        //Initialize our hardware
        this.initializeHardware();

        // Configure the dashboard however we want it
        //this.configureDashboard();

        // Wait until we've been given the ok to go
        this.waitForStart();

        // Enter a loop processing all the input we receive
        while (this.opModeIsActive()) {
            if (this.updateGamepads()) {
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
        // Configure the knobs of the hardware according to how you've wired your
        // robot. Here, we assume that there are no encoders connected to the motors,
        // so we inform the motor objects of that fact (we might use encoders later.)
        for (DcMotor curMotor : motors)
        {
            curMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        }

        //the correct motors should be reversed, since they are on the opposite side of the robot.
        this.MotorRightBack.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftTriangle.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftClimber.setDirection(DcMotor.Direction.REVERSE);

        stopAllMotors();

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
        for (DcMotor curMotor: this.motors)
        {
            curMotor.setPower(0.0);
        }
    }

    /**
     * This function is used to make field driving easier
     * and available as a method able to be called.
     */
    private void driveForwards(double leftSidePower, double rightSidePower, double leftTrianglePower, double rightTrianglePower)
    {
        this.MotorRightBack.setPower(rightSidePower * WHEEL_DRIVE_MULTIPLIER * currentDrivePowerFactor);

        this.MotorLeftBack.setPower(leftSidePower * WHEEL_DRIVE_MULTIPLIER * currentDrivePowerFactor);

        //TODO triangle vs climber naming is confusing
        this.MotorRightTriangle.setPower(rightSidePower * currentDrivePowerFactor);

        this.MotorLeftTriangle.setPower(leftSidePower * currentDrivePowerFactor);

        this.MotorRightClimber.setPower(rightTrianglePower * currentDrivePowerFactor);

        this.MotorLeftClimber.setPower(leftTrianglePower * currentDrivePowerFactor);
    }

    /**
     * This does the same thing as the driveForwards function,
     * but it prepares to drive onto the ramp
     */
    private void driveBackwards(double leftSidePower, double rightSidePower, double leftTrianglePower, double rightTrianglePower)
    {
        driveForwards(-1 * leftSidePower, -1 * rightSidePower, -1 * leftTrianglePower, -1 * rightTrianglePower);
    }

    /**
     * This is the body of the TeleOp that allows the driver to control the robot.
     */
    //TO DO:  we need to finish refactoring this.
    void driveRobot(Gamepad pad) throws InterruptedException {
        // Remember that the gamepad sticks range from -1 to +1, and that the motor
        // power levels range over the same amount


        //read input from the controller
        double leftSidePower = pad.left_stick_y;
        double rightSidePower = pad.right_stick_y;
        double climberPower = pad.right_trigger * -1;

        /**
         * Calculate motor power based on drive mode and controller input
         */

        //field driving mode
        if (currentDriveMode == DriveModeEnum.DriveModeField) {
            leftSidePower = pad.right_stick_y;
            rightSidePower = pad.left_stick_y;
            climberPower = pad.right_trigger;
            driveForwards(leftSidePower, rightSidePower, climberPower, climberPower);
        }
        //"ready" mode for getting ready to climb the ramp
        //we need to drive backwards when aligning with the ramp
        else if (currentDriveMode == DriveModeEnum.DriveModeBackwards) {
            driveBackwards(leftSidePower, rightSidePower, climberPower, climberPower);
        }
        //drive climb mode
        else if (currentDriveMode == DriveModeEnum.DriveModeRamp) {
            //since we want both our climbers and wheels to have the same power, we set the climbers equal to the left and right sides
            driveBackwards(leftSidePower, rightSidePower, leftSidePower, rightSidePower);
        }

    }

    private void handleDriverInput(Gamepad pad1, Gamepad pad2)
    {
        if (pad2.y)
        {
            HikerDropper.setPosition(HIKER_DROPPER_DEPLOYED);
        } else
        {
            HikerDropper.setPosition(HIKER_DROPPER_NOTDEPLOYED);
        }

        if (pad2.dpad_left)
        {
            HangerServo.setPosition(HANGER_SERVO_DEPLOYED);
        } else if (pad2.dpad_right)
        {
            HangerServo.setPosition(HANGER_SERVO_NOTDEPLOYED);
        } else
        {
            HangerServo.setPosition(HANGER_SERVO_STOP);
        }

        if (pad2.dpad_down)
        {
            MotorHanger.setPower(-1 * FULL_POWER);
        } else if (pad2.dpad_up)
        {
            MotorHanger.setPower(FULL_POWER);
        } else
        {
            MotorHanger.setPower(STOP);
        }

        //deploy the holder
        if (pad2.b & !HolderServoRightDeployed)
        {
            HolderServoRight.setPosition(HOLDER_SERVO_RIGHT_DEPLOYED);
            HolderServoRightDeployed = true;
        } else if (pad2.b & HolderServoRightDeployed)
        {
            HolderServoRight.setPosition(HOLDER_SERVO_RIGHT_NOTDEPLOYED);
            HolderServoRightDeployed = false;
        }

        if (pad2.x & !HolderServoLeftDeployed)
        {
            HolderServoLeft.setPosition(HOLDER_SERVO_LEFT_DEPLOYED);
            HolderServoLeftDeployed = true;
        } else if (pad2.x & HolderServoLeftDeployed)
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
        } else if (pad2.left_bumper & LeftZiplineHitterDeployed)
        {
            LeftZiplineHitter.setPosition(LEFT_ZIPLINEHITTER_NOTDEPLOYED);
            LeftZiplineHitterDeployed = false;
            telemetry.log.add("left bumper:notdeployed");
        } else telemetry.log.add("no bumper");

        //The RightZiplineHitter reads from (0-1), which is different than the LeftZiplineHitter(0-360)
        if (pad2.right_bumper & !RightZiplineHitterDeployed)
        {
            RightZiplineHitter.setPosition(RIGHT_ZIPLINEHITTER_DEPLOYED);
            RightZiplineHitterDeployed = true;
        } else if (pad2.right_bumper & RightZiplineHitterDeployed)
        {
            RightZiplineHitter.setPosition(RIGHT_ZIPLINEHITTER_NOTDEPLOYED);
            RightZiplineHitterDeployed = false;
        }
        //toggle field driving mode
        if (pad1.a)
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
        } else
        {
            currentDrivePowerFactor = FULL_POWER;
        }
    }

    //This is the driving mode for going up the ramp
    private void setRampClimbingMode() {
        setDriveMode(DriveModeEnum.DriveModeRamp);
    }

    //This is the driving mode we use when we want to drive backwards to align with the ramp
    private void setBackwardsDriveMode() {
        setDriveMode(DriveModeEnum.DriveModeBackwards);
    }

    //This is the driving mode we use when driving around the field
    private void setFieldDrivingMode() {
        setDriveMode(DriveModeEnum.DriveModeField);
    }

    private void setDriveMode(DriveModeEnum mode) {
        currentDriveMode = mode;
    }


    void configureDashboard() {

        this.telemetry.log.setDisplayOldToNew(false);   // And we show the log in new to old order, just because we want to
        this.telemetry.log.setCapacity(10);             // We can control the number of lines used by the log

        // Configure the dashboard. Here, it will have one line, which will contain three items
        this.telemetry.addLine
                (
                        this.telemetry.item("left back:", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return format(MotorLeftBack.getPower());
                            }
                        }),
                        this.telemetry.item("right back: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
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
