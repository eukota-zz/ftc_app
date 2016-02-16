package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;

/**
 * An example program that one might create
 * that uses methods, but no inheritance structure
 */
@Autonomous(name="MethodOpMode")
public class MethodOpMode extends SynchronousOpMode
{
    // Declare motors
    DcMotor motorFrontLeft;
    DcMotor motorBackLeft;
    DcMotor motorFrontRight;
    DcMotor motorBackRight;

    Servo servoArm = null;
    Servo servoGripper = null;

    static final double ARM_DOWN_POS = 0.2;
    static final double ARM_UP_POS = 0.8;
    static final double GRIPPER_GRASP_POS = 0.4;
    static final double GRIPPER_RELEASE_POS = 0.6;
    static final double DRIVE_POWER = 1.0;

    @Override public void main() throws InterruptedException
    {
        initHardware();

        // Wait for the game to start
        waitForStart();

        // Drive to beacon
        driveForward(DRIVE_POWER);
        Thread.sleep(5000);

        // Align with beacon
        turnLeft(DRIVE_POWER);
        Thread.sleep(750);

        stopDriving();

        dumpClimbers();

        // Turn to parking zone
        turnRight(DRIVE_POWER);
        Thread.sleep(1500);

        // Drive into parking zone
        driveBackward(DRIVE_POWER);
        Thread.sleep(1000);

        stopDriving();
    }

    public void driveForward(double power)
    {
        motorFrontLeft.setPower(power);
        motorBackLeft.setPower(power);
        motorFrontRight.setPower(power);
        motorBackRight.setPower(power);
    }

    public void driveBackward(double power)
    {
        driveForward(-power);
    }

    public void turnLeft(double power)
    {
        motorFrontLeft.setPower(-power);
        motorBackLeft.setPower(-power);
        motorFrontRight.setPower(power);
        motorBackRight.setPower(power);
    }

    public void turnRight(double power)
    {
        turnLeft(-power);
    }

    public void stopDriving()
    {
        driveForward(0.0);
    }

    public void dumpClimbers() throws InterruptedException
    {
        servoArm.setPosition(ARM_UP_POS);
        Thread.sleep(500);
        servoGripper.setPosition(GRIPPER_RELEASE_POS);
        Thread.sleep(500);
        servoArm.setPosition(ARM_DOWN_POS);
        servoGripper.setPosition(GRIPPER_GRASP_POS);
        Thread.sleep(500);
    }

    public void initHardware()
    {
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");

        motorFrontLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorBackLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorFrontRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorBackRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        motorFrontLeft.setDirection(DcMotor.Direction.REVERSE);
        motorBackLeft.setDirection(DcMotor.Direction.REVERSE);

        servoArm = hardwareMap.servo.get("servoArm");
        servoGripper = hardwareMap.servo.get("servoGripper");

        servoArm.setPosition(ARM_DOWN_POS);
        servoGripper.setPosition(GRIPPER_GRASP_POS);
    }
}