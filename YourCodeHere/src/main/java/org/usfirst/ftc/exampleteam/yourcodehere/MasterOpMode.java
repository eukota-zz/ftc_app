package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import org.swerverobotics.library.interfaces.IBNO055IMU;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cole on 12/28/2015.
 */
public abstract class MasterOpMode extends SynchronousOpMode
{
    enum Servo6220
    {
        LeftZiplineHitter,
        RightZiplineHitter,
        HikerDropper,
        HangerServo,
        HolderServoLeft,
        HolderServoRight;

        public static String[] GetNames()
        {
            return new String[] {"ServoLeftZiplineHitter", "ServoRightZiplineHitter", "HikerDropper", "HangerServo", "HolderServoLeft", "HolderServoRight"};
        }
    }

    enum Motor6220
    {
        RightBack,
        LeftBack,
        RightTriangle,
        LeftTriangle,
        RightClimber,
        LeftClimber,
        MotorHanger;

        public static String[] GetNames()
        {
            return new String[] {"MotorRightBack", "MotorLeftBack", "MotorRightTriangle", "MotorLeftTriangle", "MotorRightClimber", "MotorLeftClimber", "MotorHanger"};
        }
    }

    static double currentDrivePowerFactor = Constants.FULL_POWER;

    //sensors
    IBNO055IMU imu = null;

    DcMotor MotorRightBack = null;
    DcMotor MotorLeftBack = null;
    DcMotor MotorRightTriangle = null;
    DcMotor MotorLeftTriangle = null;
    DcMotor MotorRightClimber = null;
    DcMotor MotorLeftClimber = null;
    DcMotor MotorHanger = null;
    // Declare servos
    Servo ServoLeftZiplineHitter = null;
    Servo ServoRightZiplineHitter = null;
    Servo HikerDropper = null;
    Servo HangerServo = null;
    Servo HolderServoLeft = null;
    Servo HolderServoRight = null;
    List<DcMotor> motors = new ArrayList<>();
    List<Servo> servos = new ArrayList<>();
    DriveModeEnum currentDriveMode = DriveModeEnum.DriveModeField;
    //the drive wheels are larger than the triangle wheels so we drive them at less power

    ServoToggler LeftZiplineHitter;
    ServoToggler RightZiplineHitter;
    ServoToggler LeftHolder;
    ServoToggler RightHolder;


    @Override
    protected void main() throws InterruptedException {

    }

    //drive the small wheels, but not the climbers
    public void driveSmallWheels(double leftPower, double rightPower)
    {

        leftPower  *= Constants.FULL_POWER;
        rightPower *= Constants.FULL_POWER;

        MotorLeftTriangle.setPower(Constants.LEFT_ASSEMBLY_DIFF * leftPower );
        MotorRightTriangle.setPower(rightPower);
        MotorLeftBack.setPower(Constants.LEFT_ASSEMBLY_DIFF * Constants.REAR_WHEEL_POWER_FACTOR * leftPower );
        MotorRightBack.setPower(Constants.REAR_WHEEL_POWER_FACTOR * rightPower);
    }

    protected void initializeHardware()
    {
        //dynamically load all servos
        for (String curServoName : Servo6220.GetNames())
        {
            servos.add(this.hardwareMap.servo.get(curServoName));
        }
        //initialize old servo references for backwards compatibility

        //dynamically load all motors
        for (String curMotorName : Motor6220.GetNames())
        {
            motors.add(this.hardwareMap.dcMotor.get(curMotorName));
        }

        //initialize old motor references for backwards compatibility
        this.HolderServoLeft = servos.get(Servo6220.HolderServoLeft.ordinal());
        this.MotorHanger = motors.get(Motor6220.MotorHanger.ordinal());
        this.ServoLeftZiplineHitter = servos.get(Servo6220.LeftZiplineHitter.ordinal());
        this.HikerDropper = servos.get(Servo6220.HikerDropper.ordinal());
        this.MotorRightTriangle = motors.get(Motor6220.RightTriangle.ordinal());
        this.ServoRightZiplineHitter = servos.get(Servo6220.RightZiplineHitter.ordinal());
        this.MotorLeftClimber = motors.get(Motor6220.LeftClimber.ordinal());
        this.MotorLeftTriangle = motors.get(Motor6220.LeftTriangle.ordinal());
        this.MotorLeftBack = motors.get(Motor6220.LeftBack.ordinal());
        this.MotorRightClimber = motors.get(Motor6220.RightClimber.ordinal());
        this.HangerServo = servos.get(Servo6220.HangerServo.ordinal());
        this.MotorRightBack = motors.get(Motor6220.RightBack.ordinal());
        this.HolderServoRight = servos.get(Servo6220.HolderServoRight.ordinal());

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

        this.ServoRightZiplineHitter.setDirection(Servo.Direction.REVERSE);

        this.LeftZiplineHitter = new ServoToggler(ServoLeftZiplineHitter, Constants.ZIPLINEHITTER_NOTDEPLOYED, Constants.ZIPLINEHITTER_DEPLOYED);
        this.LeftZiplineHitter.setStartingPosition();
        this.RightZiplineHitter = new ServoToggler(ServoRightZiplineHitter, Constants.ZIPLINEHITTER_NOTDEPLOYED, Constants.ZIPLINEHITTER_DEPLOYED);
        this.RightZiplineHitter.setStartingPosition();

        this.HolderServoLeft.setDirection(Servo.Direction.REVERSE);

        this.LeftHolder = new ServoToggler(HolderServoLeft, Constants.HOLDER_SERVO_NOTDEPLOYED, Constants.HOLDER_SERVO_DEPLOYED);
        this.LeftHolder.setStartingPosition();
        this.RightHolder = new ServoToggler(HolderServoRight, Constants.HOLDER_SERVO_NOTDEPLOYED, Constants.HOLDER_SERVO_DEPLOYED);
        this.RightHolder.setStartingPosition();

        this.HikerDropper.setPosition(Constants.HIKER_DROPPER_NOTDEPLOYED);
        this.HangerServo.setPosition(Constants.HANGER_SERVO_STOP);

        stopAllMotors();
    }

    protected void stopDriveMotors()
    {
        MotorLeftBack.setPower(0);
        MotorRightBack.setPower(0);
        MotorLeftTriangle.setPower(0);
        MotorRightTriangle.setPower(0);
        MotorLeftClimber.setPower(0);
        MotorRightClimber.setPower(0);

    }

    protected void stopAllMotors()
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
    protected void driveForwards(double leftSidePower, double rightSidePower, double leftTrianglePower, double rightTrianglePower)
    {
        this.MotorRightBack.setPower(rightSidePower * Constants.REAR_WHEEL_POWER_FACTOR * currentDrivePowerFactor);

        this.MotorLeftBack.setPower(leftSidePower * Constants.REAR_WHEEL_POWER_FACTOR * currentDrivePowerFactor);

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
    protected void driveBackwards(double leftSidePower, double rightSidePower, double leftTrianglePower, double rightTrianglePower)
    {
        driveForwards(-1 * leftSidePower, -1 * rightSidePower, -1 * leftTrianglePower, -1 * rightTrianglePower);
    }



    //This is the driving mode for going up the ramp
    protected void setRampClimbingMode() {
        setDriveMode(DriveModeEnum.DriveModeRamp);
    }

    //This is the driving mode we use when we want to drive backwards to align with the ramp
    protected void setBackwardsDriveMode() {
        setDriveMode(DriveModeEnum.DriveModeBackwards);
    }

    //This is the driving mode we use when driving around the field
    protected void setFieldDrivingMode() {
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

    enum DriveModeEnum
    {
        DriveModeField,
        DriveModeBackwards,
        DriveModeRamp
    }
}
