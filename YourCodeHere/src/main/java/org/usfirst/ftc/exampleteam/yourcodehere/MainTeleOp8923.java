package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Disabled;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Main TeleOp file for 8923 bot
 */
@TeleOp(name="8923 Main TeleOp")
public class MainTeleOp8923 extends SynchronousOpMode
{
    // Declare motors and servos
    DcMotor motorLeft = null;
    DcMotor motorRight = null;
    DcMotor motorCollector = null;
    DcMotor motorScorer = null;
    DcMotor motorTapeMeasure = null;
    Servo servoLeftZipline = null;
    Servo servoRightZipline = null;
    Servo servoTapeMeasureElevation = null;
    Servo servoCollectorHinge = null;
    Servo servoClimberArm = null;

    // Declare variables
    boolean ziplineLeftIsOut = false;
    boolean ziplineRightIsOut = false;
    boolean collectorHingeIsUp = false;
    boolean climberArmOut = false;

    // Declare constants
    double POWER_FULL = 1.0;
    double POWER_STOP = 0.0;
    double POWER_SCORER = 0.25;
    double ZIPLINE_LEFT_UP = 1.0;
    double ZIPLINE_LEFT_OUT = 0.4;
    double ZIPLINE_RIGHT_UP = 0.0;
    double ZIPLINE_RIGHT_OUT = 0.6;
    double COLLECTOR_HINGE_DOWN = 0.7;
    double COLLECTOR_HINGE_UP = 1.0;
    double TAPE_MEASURE_ELEVATION_RATE = 0.05;
    double CLIMBER_ARM_OUT = 0.1;
    double CLIMBER_ARM_IN = 0.0;

    @Override protected void main() throws InterruptedException
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
        servoClimberArm = hardwareMap.servo.get("servoClimberArm");

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
        //servoClimberArm.setPosition(CLIMBER_ARM_IN);

        // Configure dashboard
        telemetry.addLine
                (
                        this.telemetry.item("Left:", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return motorLeft.getPower();
                            }
                        }),
                        this.telemetry.item("Right: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return motorRight.getPower();
                            }
                        })
                );

        // Wait for the game to begin
        this.waitForStart();

        // Loop until the game is finished
        while(this.opModeIsActive())
        {
            if(this.updateGamepads())
            {
                driver1Controls();
                driver2Controls();
            }

            // Emit the latest telemetry and wait, letting other things run
            this.telemetry.update();
            this.idle();
        }
    }

    public void driver1Controls()
    {
        // Tank drive based on joysticks of controller 1
        motorLeft.setPower(gamepad1.left_stick_y);
        motorRight.setPower(gamepad1.right_stick_y);

        // Tape Measure of Doom extension based on controller 1
        if(gamepad1.left_trigger > 0)
        {
            motorTapeMeasure.setPower(-POWER_FULL);
        }
        else if(gamepad1.right_trigger > 0)
        {
            motorTapeMeasure.setPower(POWER_FULL);
        }
        else
        {
            motorTapeMeasure.setPower(POWER_STOP);
        }

        // Tape Measure of Doom elevation based on controller 1
        if(gamepad1.left_bumper && servoTapeMeasureElevation.getPosition() <= 1 - TAPE_MEASURE_ELEVATION_RATE)
        {
            servoTapeMeasureElevation.setPosition(servoTapeMeasureElevation.getPosition() + TAPE_MEASURE_ELEVATION_RATE);
        }
        else if(gamepad1.right_bumper && servoTapeMeasureElevation.getPosition() >= 0 + TAPE_MEASURE_ELEVATION_RATE)
        {
            servoTapeMeasureElevation.setPosition(servoTapeMeasureElevation.getPosition() - TAPE_MEASURE_ELEVATION_RATE);
        }
    }

    public void driver2Controls() throws InterruptedException
    {
        // Move collector based on triggers on controller 2 if the bottom isn't up
        if(gamepad2.right_trigger > 0 && !collectorHingeIsUp)
            motorCollector.setPower(gamepad2.right_trigger);
        else if(gamepad2.left_trigger > 0 && !collectorHingeIsUp)
            motorCollector.setPower(-gamepad2.left_trigger);
        else
            motorCollector.setPower(POWER_STOP);

        // Move scorer based on D-pad on controller 2
        if(gamepad2.dpad_left)
            motorScorer.setPower(POWER_SCORER);
        else if(gamepad2.dpad_right)
            motorScorer.setPower(-POWER_SCORER);
        else
            motorScorer.setPower(POWER_STOP);

        // Move zipline servos based on left and right bumpers
        if (gamepad2.left_bumper)
        {
            ziplineLeftIsOut = !ziplineLeftIsOut;
            if(ziplineLeftIsOut)
                servoLeftZipline.setPosition(ZIPLINE_LEFT_OUT);
            else
                servoLeftZipline.setPosition(ZIPLINE_LEFT_UP);
        }
        if(gamepad2.right_bumper)
        {
            ziplineRightIsOut = !ziplineRightIsOut;
            if (ziplineRightIsOut)
                servoRightZipline.setPosition(ZIPLINE_RIGHT_OUT);
            else
                servoRightZipline.setPosition(ZIPLINE_RIGHT_UP);
        }

        // Move collector ramp up and down based on x and y
        if(gamepad2.y)
        {
            collectorHingeIsUp = true;
            motorCollector.setPower(POWER_STOP);
            Thread.sleep(250);
            servoCollectorHinge.setPosition(COLLECTOR_HINGE_UP);
        }
        else if(gamepad2.x)
        {
            collectorHingeIsUp = false;
            servoCollectorHinge.setPosition(COLLECTOR_HINGE_DOWN);
        }

        // Toggle climber dumper servo position
        if (gamepad2.a)
        {
            climberArmOut = !climberArmOut;
            if(climberArmOut)
                servoClimberArm.setPosition(CLIMBER_ARM_OUT);
            else
                servoClimberArm.setPosition(CLIMBER_ARM_IN);
        }
    }
}
