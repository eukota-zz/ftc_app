package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.ftcrobotcontroller.opmodes.MatrixControllerDemo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;
import org.swerverobotics.library.interfaces.IFunc;

/*
 * Skeleton program to be used for specific autonomous programs
 * Drives to beacon repair zone
 * Presses beacon button
 * Dumps climbers into basket
 * Drives into floor goal and triggers low zipliner
 */
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
    Servo servoLeftZipline = null;
    Servo servoRightZipline = null;
    Servo servoCollectorHinge = null;

    // Declare sensors
    ColorSensor colorSensorBeacon;
    LightSensor lightSensorFront;
    LightSensor lightSensorBack;
    UltrasonicSensor ultrasonicSensor;

    //Declare Other Objects
    //ColorSensorCalibration colorCalibrate = new ColorSensorCalibration();
    int calibratedBlue;
    int calibratedRed;

    //LightSensorCalibration lightCalibrate = new LightSensorCalibration();
    double calibratedWhite;


    double DRIVE_POWER = 1.0;
    double CLIMBER_DUMP_POSITION = 1.0;
    double CLIMBER_RETURN_POSITION = 0.0;
    double POWER_FULL = 1.0;
    double POWER_STOP = 0.0;
    double POWER_SCORER = 0.25;
    double ZIPLINE_LEFT_UP = 1.0;
    double ZIPLINE_LEFT_OUT = 0.4;
    double ZIPLINE_RIGHT_UP = 0.1;
    double ZIPLINE_RIGHT_OUT = 0.6;
    double COLLECTOR_HINGE_DOWN = 0.7;
    double COLLECTOR_HINGE_UP = 1.0;
    double TAPE_MEASURE_ELEVATION_RATE = 0.05;
    double CLIMBER_ARM_OUT = 1.0;
    double CLIMBER_ARM_IN = 0.0;

    // TODO Change this
    int FOO = 1;

    @Override public void main() throws InterruptedException {}

    public void initHardware() throws InterruptedException
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
        //colorSensorBeacon = hardwareMap.colorSensor.get("colorSensorBeacon");
        //colorSensorBeacon.enableLed(false);
        lightSensorFront = hardwareMap.lightSensor.get("lightSensorFront");
        lightSensorBack = hardwareMap.lightSensor.get("lightSensorBack");
        ultrasonicSensor = hardwareMap.ultrasonicSensor.get("ultrasonicSensor");

        // Initialize servos
        servoCollectorHinge = hardwareMap.servo.get("servoCollectorHinge");
        servoClimberDump = hardwareMap.servo.get("servoClimberArm");
        servoLeftZipline = hardwareMap.servo.get("servoLeftZipline");
        servoRightZipline = hardwareMap.servo.get("servoRightZipline");
        //servoPressBeaconButton = hardwareMap.servo.get("pressBeaconButton");

        //servoClimberDump.setPosition(CLIMBER_RETURN_POSITION);
        //servoLeftZipline.setPosition(ZIPLINE_LEFT_UP);
        //servoRightZipline.setPosition(ZIPLINE_RIGHT_UP);

        //calibratedRed = colorCalibrate.calibrateRed();
        //calibratedBlue = colorCalibrate.calibrateBlue();

        // Current ambient light
        lightSensorFront.enableLed(true);
        idle();
        calibratedWhite = lightSensorFront.getLightDetected();

        configureTelemtry();
    }

    public void configureTelemtry()
    {
        // Left drive motor info
        telemetry.addLine
                (
                        this.telemetry.item("Left Power:", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return motorLeft.getPower();
                            }
                        }),
                        this.telemetry.item("Left Position: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return motorLeft.getCurrentPosition();
                            }
                        })
                );

        // Right drive motor info
        telemetry.addLine
                (
                        this.telemetry.item("Right Power: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return motorRight.getPower();
                            }
                        }),
                        this.telemetry.item("Right Position: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return motorRight.getCurrentPosition();
                            }
                        })
                );

        // Light sensor info
        telemetry.addLine
                (
                        this.telemetry.item("Front light sensor: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return lightSensorFront.getLightDetected();
                            }
                        }),
                        this.telemetry.item("Back light sensor: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return lightSensorBack.getLightDetected();
                            }
                        })
                );
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
        servoClimberDump.setPosition(CLIMBER_DUMP_POSITION);
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
