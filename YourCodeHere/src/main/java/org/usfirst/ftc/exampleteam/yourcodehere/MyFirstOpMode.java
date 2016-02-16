package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;

/**
 * An example program that one might
 * create that has poor structure
 */
@Autonomous(name="MyFirstOpMode")
public class MyFirstOpMode extends SynchronousOpMode
{
    // Declare hardware
    DcMotor motorFrontLeft = null;
    DcMotor motorBackLeft = null;
    DcMotor motorFrontRight = null;
    DcMotor motorBackRight = null;

    Servo servoArm = null;
    Servo servoGripper = null;

    @Override public void main() throws InterruptedException
    {
        // Initialize hardware
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

        servoArm.setPosition(0.2);
        servoGripper.setPosition(0.4);

        // Wait for the game to start
        waitForStart();

        // Drive to beacon
        motorFrontLeft.setPower(1.0);
        motorBackLeft.setPower(1.0);
        motorFrontRight.setPower(1.0);
        motorBackRight.setPower(1.0);
        Thread.sleep(5000);

        // Align with beacon
        motorFrontLeft.setPower(-1.0);
        motorBackLeft.setPower(-1.0);
        motorFrontRight.setPower(1.0);
        motorBackRight.setPower(1.0);
        Thread.sleep(750);

        // Stop
        motorFrontLeft.setPower(0.0);
        motorBackLeft.setPower(0.0);
        motorFrontRight.setPower(0.0);
        motorBackRight.setPower(0.0);

        // Dump Climbers
        servoArm.setPosition(0.8);
        Thread.sleep(500);
        servoGripper.setPosition(0.6);
        Thread.sleep(500);
        servoArm.setPosition(0.2);
        servoGripper.setPosition(0.4);
        Thread.sleep(500);

        // Turn to parking zone
        motorFrontLeft.setPower(1.0);
        motorBackLeft.setPower(1.0);
        motorFrontRight.setPower(-1.0);
        motorBackRight.setPower(-1.0);
        Thread.sleep(1500);

        // Drive into parking zone
        motorFrontLeft.setPower(-1.0);
        motorBackLeft.setPower(-1.0);
        motorFrontRight.setPower(-1.0);
        motorBackRight.setPower(-1.0);
        Thread.sleep(1000);

        // Stop
        motorFrontLeft.setPower(0.0);
        motorBackLeft.setPower(0.0);
        motorFrontRight.setPower(0.0);
        motorBackRight.setPower(0.0);
    }
}