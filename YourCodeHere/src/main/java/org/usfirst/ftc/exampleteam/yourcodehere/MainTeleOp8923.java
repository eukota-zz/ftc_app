package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * Main TeleOp file for 8923 bot
 */
@TeleOp(name="8923 Main TeleOp")
public class MainTeleOp8923 extends GlobalRobotAttributes
{
    @Override protected void main() throws InterruptedException
    {
        robotInit();

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
        if(gamepad1.y)
        {
            slowModeFactor = 2.5;
        }
        else if(gamepad1.a)
        {
            slowModeFactor = 1.0;
        }

        // Motors aren't even, so only right motor needs power reduction
        motorLeft.setPower(gamepad1.left_stick_y);
        motorRight.setPower(gamepad1.right_stick_y / slowModeFactor);

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
                servoClimberDumper.setPosition(CLIMBER_ARM_OUT);
            else
                servoClimberDumper.setPosition(CLIMBER_ARM_IN);
        }
    }
}
