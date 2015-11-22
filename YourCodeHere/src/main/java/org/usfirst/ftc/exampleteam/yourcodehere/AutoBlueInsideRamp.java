package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Robot starts on blue side, goes to beacon,
 * presses beacon button, and scores climber
 */
@Autonomous(name="AutoBlueInsideRamp")
public class AutoBlueInsideRamp extends SynchronousOpMode
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

        motorRight.setDirection(DcMotor.Direction.REVERSE);

        // Initialize sensors
        //colorSensorBeacon = hardwareMap.colorSensor.get("colorSensorBeacon");
        //colorSensorBeacon.enableLed(false);
        //followLineSensorFront = hardwareMap.colorSensor.get("followLineSensorFront");
        //followLineSensorBack = hardwareMap.colorSensor.get("followLineSensorBack");

        // Initialize servos
        //servoClimberDump = hardwareMap.servo.get("servoClimberDump");
        //servoPressBeaconButton = hardwareMap.servo.get("pressBeaconButton");

        waitForStart();

        DriveForwardTime(DRIVE_POWER, 2850);
        StopDriving();
        Thread.sleep(20);
        TurnLeftTime(DRIVE_POWER, 800);
        StopDriving();
        Thread.sleep(20);
        DriveForwardTime(-DRIVE_POWER, 1500);
        StopDriving();
    }

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

    public void DriveForwardTime(double power, long time) throws InterruptedException
    {
        DriveForward(power);
        Thread.sleep(time);
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

    public void TurnRightDistance(double power, int distance)
    {
        TurnLeftDistance(-power, distance);
    }

    public void TurnLeftTime(double power, long time) throws InterruptedException
    {
        TurnLeft(power);
        Thread.sleep(time);
    }

    public void TurnRightTime(double power, long time) throws InterruptedException
    {
        TurnLeftTime(-power, time);
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

    enum test {ALWAYS, NOT, EVER}
    public void EnumTest()
    {
        //colorSensorBeacon.green()
    }
}
