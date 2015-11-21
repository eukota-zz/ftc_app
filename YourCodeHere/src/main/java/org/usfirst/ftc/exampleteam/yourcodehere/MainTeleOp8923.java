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
    Servo ziplinerReleaseLeft = null;
    Servo ziplinerReleaseRight = null;

    // Variable declarations
    double POWER_FULL = 1.0;
    double POWER_STOP = 0.0;
    double POWER_SCORER = 0.4;
    double SERVO_UP_LEFT = 1.0;
    double SERVO_UP_RIGHT = 0.0;
    double SERVO_OUT = 0.5;
    boolean SERVO_POSITION_RIGHT = true;
    boolean SERVO_POSITION_LEFT = true;

    @Override protected void main() throws InterruptedException
    {
        // Initialize motors and servos
        motorLeft = hardwareMap.dcMotor.get("motorLeft");
        motorRight = hardwareMap.dcMotor.get("motorRight");
        motorCollector = hardwareMap.dcMotor.get("motorCollector");
        motorScorer = hardwareMap.dcMotor.get("motorScorer");
        ziplinerReleaseLeft = hardwareMap.servo.get("servoLeftZipline");
        ziplinerReleaseRight = hardwareMap.servo.get("servoRightZipline");


        // Set motor channel modes
        motorLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorCollector.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        motorScorer.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        // Reverse left motors so we don't spin in a circle
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

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
                if (gamepad1.right_bumper)
                {
                    SERVO_POSITION_RIGHT = !SERVO_POSITION_RIGHT;
                    if(SERVO_POSITION_RIGHT == true)
                    {
                        ziplinerReleaseRight.setPosition(SERVO_UP_RIGHT);
                    }

                    else if(SERVO_POSITION_RIGHT != true)
                    {
                        ziplinerReleaseRight.setPosition(SERVO_OUT);
                    }
                }

                if(gamepad1.left_bumper)
                {
                    SERVO_POSITION_LEFT = !SERVO_POSITION_LEFT
                    if (SERVO_POSITION_LEFT == true)
                    {
                        ziplinerReleaseLeft.setPosition(SERVO_UP_LEFT);
                    }

                    else if(SERVO_POSITION_LEFT != true)
                    {
                        ziplinerReleaseLeft.setPosition(SERVO_OUT);
                    }
                }
            }

            // Emit the latest telemetry and wait, letting other things run
            this.telemetry.update();
            this.idle();
        }
    }
}
