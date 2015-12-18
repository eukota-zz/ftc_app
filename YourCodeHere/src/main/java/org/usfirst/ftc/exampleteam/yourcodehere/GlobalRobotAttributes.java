package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import org.swerverobotics.library.SynchronousOpMode;
/*
 * Global robot attributes used in all programs
 */

public class GlobalRobotAttributes extends SynchronousOpMode
{
    DcMotor motorLeft = null;
    DcMotor motorRight = null;
    DcMotor motorCollector = null;
    DcMotor motorScorer = null;
    DcMotor motorTapeMeasure = null;

    Servo servoLeftZipline = null;
    Servo servoRightZipline = null;
    Servo servoTapeMeasureElevation = null;
    Servo servoCollectorHinge = null;
    Servo servoClimberDumper = null;
    Servo servoPressBeaconButton;

    ColorSensor colorSensorBeacon;
    LightSensor lightSensorFront;
    LightSensor lightSensorBack;
    UltrasonicSensor ultrasonicSensor;

    // Declare variables
    boolean ziplineLeftIsOut = false;
    boolean ziplineRightIsOut = false;
    boolean collectorHingeIsUp = false;
    boolean climberArmOut = false;
    double slowModeFactor = 1.0;

    // Declare constants
    double POWER_FULL = 1.0;
    double POWER_STOP = 0.0;
    double POWER_SCORER = 0.25;
    double ZIPLINE_LEFT_UP = 1.0;
    double ZIPLINE_LEFT_OUT = 0.4;
    double ZIPLINE_RIGHT_UP = 0.1;
    double ZIPLINE_RIGHT_OUT = 0.7;
    double COLLECTOR_HINGE_DOWN = 0.6;
    double COLLECTOR_HINGE_UP = 0.8;
    double TAPE_MEASURE_ELEVATION_RATE = 0.05;
    double CLIMBER_ARM_OUT = 1.0;
    double CLIMBER_ARM_IN = 0.0;
    double DRIVE_POWER = 1.0;
    double CLIMBER_DUMP_POSITION = 1.0;
    double CLIMBER_RETURN_POSITION = 0.0;
    int calibratedBlue;


    @Override protected void main() throws InterruptedException {}

    public void robotInit()
    {
        // Initialize motors and servos
        motorLeft = hardwareMap.dcMotor.get("motorLeft");
        motorRight = hardwareMap.dcMotor.get("motorRight");
        motorCollector = hardwareMap.dcMotor.get("motorCollector");
        motorScorer = hardwareMap.dcMotor.get("motorScorer");
        motorTapeMeasure = hardwareMap.dcMotor.get("motorTapeMeasure");

        servoLeftZipline = hardwareMap.servo.get("servoLeftZipline");
        servoRightZipline = hardwareMap.servo.get("servoRightZipline");
        servoTapeMeasureElevation = hardwareMap.servo.get("servoTapeMeasureElevation");
        servoCollectorHinge = hardwareMap.servo.get("servoCollectorHinge");
        servoClimberDumper = hardwareMap.servo.get("servoClimberDumper");
        servoPressBeaconButton = hardwareMap.servo.get("pressBeaconButton");


        // Set motor channel modes
        motorLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorCollector.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorScorer.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        // Reverse left motors so we don't spin in a circle
        motorRight.setDirection(DcMotor.Direction.REVERSE);

        // Initialize zipline servos to be up
        servoLeftZipline.setPosition(ZIPLINE_LEFT_UP);
        servoRightZipline.setPosition(ZIPLINE_RIGHT_UP);
        servoCollectorHinge.setPosition(COLLECTOR_HINGE_DOWN);
        //servoClimberDumper.setPosition(CLIMBER_ARM_IN);

        // Initialize sensors
        //colorSensorBeacon = hardwareMap.colorSensor.get("colorSensorBeacon");
        //colorSensorBeacon.enableLed(false);
        lightSensorFront = hardwareMap.lightSensor.get("lightSensorFront");
        lightSensorBack = hardwareMap.lightSensor.get("lightSensorBack");
        ultrasonicSensor = hardwareMap.ultrasonicSensor.get("ultrasonicSensor");
    }

    public void driveForward(double power) {
        motorLeft.setPower(power);
        motorRight.setPower(power);
    }

    public void driveForwardDistance(double power, int distance) throws InterruptedException
    {
        motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        motorLeft.setTargetPosition(distance);
        motorRight.setTargetPosition(distance);

        motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        driveForward(power);

        while(Math.abs(motorLeft.getCurrentPosition()) < Math.abs(distance) && Math.abs(motorRight.getCurrentPosition()) < Math.abs(distance))
        {
            // Wait until distance is reached
            telemetry.update();
            idle();
        }

        stopDriving();

        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    public void driveBackwardDistance(double power, int distance) throws InterruptedException
    {
        driveForwardDistance(-power, -distance);
    }

    public void turnLeft(double power)
    {
        motorLeft.setPower(-power);
        motorRight.setPower(power);
    }

    public void turnRight(double power)
    {
        turnLeft(-power);
    }

    public void stopDriving()
    {
        driveForward(0);
    }

    public void turnLeftDistance(double power, int distance) throws InterruptedException
    {
        motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        motorLeft.setTargetPosition(-distance);
        motorRight.setTargetPosition(distance);

        motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        turnLeft(power);

        while(Math.abs(motorLeft.getCurrentPosition()) < Math.abs(distance) && Math.abs(motorRight.getCurrentPosition()) < Math.abs(distance))
        {
            // Wait until distance is reached
            telemetry.update();
            idle();
        }

        stopDriving();

        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    public void turnRightDistance(double power, int distance) throws InterruptedException
    {
        turnLeftDistance(-power, distance);
    }

    public void dumpClimbers() throws InterruptedException
    {
        servoClimberDumper.setPosition(CLIMBER_DUMP_POSITION);
    }


    public void pressBeaconButton() throws InterruptedException
    {
        // Check for a range of blue
        if(colorSensorBeacon.blue() <= calibratedBlue + 50 && colorSensorBeacon.blue() < calibratedBlue - 50)
        {
            // Press Blue
            servoPressBeaconButton.setPosition(0.8);
            Thread.sleep(500);
            servoPressBeaconButton.setPosition(0.5);
        }
        else
        {
            // Otherwise press Red
            servoPressBeaconButton.setPosition(0.2);
            Thread.sleep(500);
            servoPressBeaconButton.setPosition(0.5);
        }

    }

    public void followLine() throws InterruptedException
    {
        while(lightSensorBack.getLightDetected() > calibratedWhite + FOO)
        {
            driveForward(DRIVE_POWER);
        }
        stopDriving();
        while (lightSensorFront.getLightDetected() > calibratedWhite + FOO)
        {
            motorRight.setPower(DRIVE_POWER);
        }
        stopDriving();
    }

    public double getDistance()
    {
        return ultrasonicSensor.getUltrasonicLevel();
    }
}