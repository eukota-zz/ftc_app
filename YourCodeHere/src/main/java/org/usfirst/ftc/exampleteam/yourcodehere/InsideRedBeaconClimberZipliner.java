package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Robot starts on blue side, goes to beacon,
 * presses beacon button, and scores climber,
 * parks in floor goal
 */
@Autonomous(name="InsideRedBeaconClimberZipliner")
public class InsideRedBeaconClimberZipliner extends BeaconClimberZiplinerSkeleton
{
    @Override public void main() throws InterruptedException
    {
        // Initialize motors
        motorLeft = hardwareMap.dcMotor.get("motorLeft");
        motorRight = hardwareMap.dcMotor.get("motorRight");
        motorCollector = hardwareMap.dcMotor.get("motorCollector");
        motorScorer = hardwareMap.dcMotor.get("motorScorer");

        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorCollector.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorScorer.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        // Initialize sensors
        colorSensorBeacon = hardwareMap.colorSensor.get("colorSensorBeacon");
        colorSensorBeacon.enableLed(false);
        followLineSensorFront = hardwareMap.colorSensor.get("followLineSensorFront");
        followLineSensorBack = hardwareMap.colorSensor.get("followLineSensorBack");

        // Initialize servos
        //servoClimberDump = hardwareMap.servo.get("servoClimberDump");
        //servoPressBeaconButton = hardwareMap.servo.get("pressBeaconButton");

        waitForStart();

        /*
         * drive to beacon
         * turn to face beacon
         * follow line to wall
         * determine beacon color
         * press correct button
         * dump climbers
         * back up
         * turn towards floor goal
         * drive into floor goal
         */


        DriveForwardDistance(DRIVE_POWER, FOO);
        TurnLeftDistance(DRIVE_POWER, FOO);
        FollowLine();
        StopDriving();
        PressBeaconButton();
        DumpClimbers();
        DriveForwardDistance(-DRIVE_POWER, FOO);
        TurnLeftDistance(DRIVE_POWER, FOO);
        DriveForwardDistance(DRIVE_POWER, FOO);
        StopDriving();
    }
}
