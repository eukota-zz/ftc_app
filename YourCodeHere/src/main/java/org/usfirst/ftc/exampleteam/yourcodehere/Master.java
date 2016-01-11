package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IBNO055IMU;
import org.swerverobotics.library.interfaces.IFunc;

/*
 * Global robot attributes used in all programs
 */
public class Master extends SynchronousOpMode
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
    IBNO055IMU imu;
    int calibratedBlue;

    //TODO change this
    int FOO = 1;

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
    boolean ON = true;
    boolean OFF = false;

    // Declare variables
    boolean ziplineLeftIsOut = false;
    boolean ziplineRightIsOut = false;
    boolean collectorHingeIsUp = false;
    boolean climberArmOut = false;
    double slowModeFactor = 1.0;
    IBNO055IMU.Parameters   parameters = new IBNO055IMU.Parameters();

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
        servoClimberDumper.setPosition(CLIMBER_ARM_IN);

        // Initialize sensors
        colorSensorBeacon = hardwareMap.colorSensor.get("colorSensorBeacon");
        colorSensorBeacon.enableLed(false);
        lightSensorFront = hardwareMap.lightSensor.get("lightSensorFront");
        lightSensorBack = hardwareMap.lightSensor.get("lightSensorBack");
        ultrasonicSensor = hardwareMap.ultrasonicSensor.get("ultrasonicSensor");
        parameters.angleunit = IBNO055IMU.ANGLEUNIT.DEGREES;
        imu = ClassFactory.createAdaFruitBNO055IMU(hardwareMap.i2cDevice.get("imu"), parameters);
    }

    public void configureTelemtry()
    {
        // Left drive motor info
        telemetry.addLine
                (
                        this.telemetry.item("Left Power: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(motorLeft.getPower());
                            }
                        }),
                        this.telemetry.item("Encoder: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(motorLeft.getCurrentPosition());
                            }
                        })
                );

        // Right drive motor info
        telemetry.addLine
                (
                        this.telemetry.item("Right Power: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(motorRight.getPower());
                            }
                        }),
                        this.telemetry.item("Encoder: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(motorRight.getCurrentPosition());
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

        telemetry.addLine
                (
                        this.telemetry.item("Ultrasonic: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return ultrasonicSensor.getUltrasonicLevel();
                            }
                        })
                );
    }

    public String formatNumber(double number)
    {
        return String.format("%.1f", number);
    }
}