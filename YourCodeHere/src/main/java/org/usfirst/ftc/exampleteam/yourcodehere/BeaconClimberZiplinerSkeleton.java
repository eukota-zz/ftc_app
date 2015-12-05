package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/*
 *
 */
@Autonomous(name="BeaconClimberZiplinerSkeleton")
@Disabled
public class BeaconClimberZiplinerSkeleton extends SynchronousOpMode
{
    // Declare motors
    DcMotor motorLeft;
    DcMotor motorRight;
    DcMotor motorCollector;
    DcMotor motorScorer;

    // Declare servos
    Servo servoPressBeaconButton;
    Servo servoClimberDump;

    // Declare sensors
    ColorSensor colorSensorBeacon;
    LightSensor followLineSensorFront;
    LightSensor followLineSensorBack;

    //Declare Other Objects
    colorSensorCalibration calibrate = new colorSensorCalibration();
    int calibratedBlue;
    int calibratedRed;


    double DRIVE_POWER = 1.0;
    double CLIMBER_DUMP_POSITION = 0.8;
    double CLIMBER_RETURN_POSITION = 0.2;

    // TODO Change this
    int FOO = 1;
    double BAR = 1;

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
        followLineSensorFront = hardwareMap.lightSensor.get("followLineSensorFront");
        followLineSensorBack = hardwareMap.lightSensor.get("followLineSensorBack");

        // Initialize servos
        //servoClimberDump = hardwareMap.servo.get("servoClimberDump");
        //servoPressBeaconButton = hardwareMap.servo.get("pressBeaconButton");

        calibratedRed = calibrate.calibrateRed();
        calibratedBlue = calibrate.calibrateBlue();
        waitForStart();

        }

    public void DriveForward(double power) {
        motorLeft.setPower(power);
        motorRight.setPower(power);
    }

    public void DriveForwardDistance(double power, int distance)
    {
        motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        motorLeft.setTargetPosition(distance);
        motorRight.setTargetPosition(distance);

        motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        DriveForward(power);

        while(motorLeft.isBusy() && motorRight.isBusy())
        {
            // Wait until distance is reached
        }

        StopDriving();

        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
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

    }
}
