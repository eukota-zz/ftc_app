package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Robot starts on blue side, goes to beacon,
 * presses beacon button, and scores climber,
 * parks in floor goal
 */
@Autonomous(name="BeaconClimberZiplinerSkeleton")
public class BeaconClimberZiplinerSkeleton extends SynchronousOpMode
{
    // Declare motors
    DcMotor motorLeft = null;
    DcMotor motorRight = null;
    DcMotor motorCollector = null;
    DcMotor motorScorer = null;

    // Declare servos
    Servo servoPressBeaconButton = null;
    Servo servoClimberDump = null;

    // Declare sensors
    ColorSensor colorSensorBeacon = null;
    ColorSensor followLineSensorFront = null;
    ColorSensor followLineSensorBack = null;


    double DRIVE_POWER = 1.0;
    double CLIMBER_DUMP_POSITION = 0.8;
    double CLIMBER_RETURN_POSITION = 0.2;

    // TODO Change this
    int FOO = 1;
    double BAR = 1;

    @Override public void main() throws InterruptedException {}

    public void DriveForward(double power) {
        motorLeft.setPower(power);
        motorRight.setPower(power);
    }

    public void DriveForwardDistance(double power, int distance)
    {
        DriveForward(power);
        while(motorLeft.getCurrentPosition() < distance)
        {
            // Wait until distance is reached
        }
        StopDriving();
    }

    public void TurnLeft(double power)
    {
        motorLeft.setPower(-power);
        motorRight.setPower(power);
    }

    public void TurnRight(double power)
    {
        TurnLeft(-power);
    }

    public void StopDriving()
    {
        DriveForward(0);
    }

    public void TurnLeftDistance(double power, int distance)
    {
        TurnLeft(power);
        while(motorLeft.getCurrentPosition() < distance)
        {
            // Wait until distance is reached
        }
        StopDriving();
    }

    public void TurnRightDistance(double power, int distance) {
        TurnLeftDistance(-power, distance);
    }

    public void DumpClimbers() throws InterruptedException
    {
        //servoClimberDump.setPosition(CLIMBER_DUMP_POSITION);
        wait(1000);
        //servoClimberDump.setPosition(CLIMBER_RETURN_POSITION);
    }

    public void PressBeaconButton()
    {
        if(colorSensorBeacon.blue() <= 3)
        {
            servoPressBeaconButton.setPosition(0.8);
        }
        else
        {
            servoPressBeaconButton.setPosition(0.2);
        }
    }

    public void FollowLine() throws InterruptedException
    {
        while(true)
        {
            this.telemetry.update();

            int green = followLineSensorFront.green();
            int blue = followLineSensorFront.blue();
            int red = followLineSensorBack.red();

            telemetry.addData("Green", green);
            telemetry.addData("Blue", blue);
            telemetry.addData("Red", red);
        }
    }
}
