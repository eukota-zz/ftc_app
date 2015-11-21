package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

import org.swerverobotics.library.SynchronousOpMode;
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
    Servo servoLeftZipline = null;
    Servo servoRightZipline = null;

    // Variable declarations
    boolean ziplineLeftIsOut = false;
    boolean ziplineRightIsOut = false;

    // Constant decarations
    double POWER_FULL = 1.0;
    double POWER_STOP = 0.0;
    double POWER_SCORER = 0.4;
    double ZIPLINE_LEFT_UP = 1.0;
    double ZIPLINE_LEFT_OUT = 0.7;
    double ZIPLINE_RIGHT_UP = 0.0;
    double ZIPLINE_RIGHT_OUT = 0.3;

    @Override protected void main() throws InterruptedException
    {
        // Initialize motors and servos
        motorLeft = hardwareMap.dcMotor.get("motorLeft");
        motorRight = hardwareMap.dcMotor.get("motorRight");
        motorCollector = hardwareMap.dcMotor.get("motorCollector");
        motorScorer = hardwareMap.dcMotor.get("motorScorer");
        servoLeftZipline = hardwareMap.servo.get("servoLeftZipline");
        servoRightZipline = hardwareMap.servo.get("servoRightZipline");


        // Set motor channel modes
        motorLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorCollector.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorScorer.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        // Reverse left motors so we don't spin in a circle
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        // Initialize zipline servos to be up
        servoLeftZipline.setPosition(ZIPLINE_LEFT_UP);
        servoRightZipline.setPosition(ZIPLINE_RIGHT_UP);

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
                                return motorLeft.getPower();
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
                // Main drive code
                motorLeft.setPower(gamepad1.left_stick_y);
                motorRight.setPower(gamepad1.right_stick_y);

                // Move collector based on triggers
                if(gamepad2.right_trigger > 0)
                    motorCollector.setPower(POWER_FULL);
                else if(gamepad2.left_trigger > 0)
                    motorCollector.setPower(-POWER_FULL);
                else
                    motorCollector.setPower(POWER_STOP);

                // Move scorer based on D-pad
                if(gamepad2.dpad_left)
                    motorScorer.setPower(-POWER_SCORER);
                else if(gamepad2.dpad_right)
                    motorScorer.setPower(POWER_SCORER);
                else
                    motorScorer.setPower(POWER_STOP);

                // Move servos based on R and L buttons\
                if (gamepad1.left_bumper)
                {
                    ziplineLeftIsOut = !ziplineLeftIsOut;
                    if(ziplineLeftIsOut)
                        servoLeftZipline.setPosition(ZIPLINE_LEFT_OUT);
                    else
                        servoLeftZipline.setPosition(ZIPLINE_LEFT_UP);
                }
                if(gamepad1.right_bumper)
                {
                    ziplineRightIsOut = !ziplineRightIsOut;
                    if (ziplineRightIsOut)
                        servoRightZipline.setPosition(ZIPLINE_RIGHT_OUT);
                    else
                        servoRightZipline.setPosition(ZIPLINE_RIGHT_UP);
                }
            }

            // Emit the latest telemetry and wait, letting other things run
            this.telemetry.update();
            this.idle();
        }
    }
}
