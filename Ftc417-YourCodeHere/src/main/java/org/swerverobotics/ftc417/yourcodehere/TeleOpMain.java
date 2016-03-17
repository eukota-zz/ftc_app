package org.swerverobotics.ftc417.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * 417 teleop
 */
@org.swerverobotics.library.interfaces.TeleOp(name="TeleOpMain", group="417")
public class TeleOpMain extends MasterTeleOp
{

    boolean controllerSwitch = false;

    @Override protected void main() throws InterruptedException
    {
        initialize();

        // Wait until the game begins
        this.waitForStart();

        // Enter a loop processing all the input we receive
        while (this.opModeIsActive())
        {
            //telemetry.addData("motorDeliverySlider_encoder",this.motorDeliverySlider.getCurrentPosition());
            if (this.updateGamepads())
            {
                //Controller 2

                //Delivery tilt
                if(this.gamepad2.dpad_left)
                {
                    servoDelivery.setPosition(Constants.TILT_LEFT);
                }
                else if(this.gamepad2.dpad_right)
                {
                    servoDelivery.setPosition(Constants.TILT_RIGHT);
                }
                else
                {
                    servoDelivery.setPosition(Constants.FLAT);
                }

                //delivery mech
                if(this.gamepad2.x)
                {
                    debrisDoorLeftToggler.toggle();
                }

                if(this.gamepad2.b)
                {
                    debrisDoorRightToggler.toggle();
                }

                //slow mode
                if(this.gamepad1.left_trigger > Constants.SLOW_MODE_DEADZONE || this.gamepad1.right_trigger > Constants.SLOW_MODE_DEADZONE)
                {
                    wheelPowerMultiply = Constants.SLOW_MODE_MULTIPLIER;
                }
                else
                {
                    wheelPowerMultiply = FULL_SPEED;
                }
                if(this.gamepad1.left_bumper || this.gamepad1.right_bumper)
                {
                    reverseMode = true;
                }
                else
                {
                    reverseMode = false;
                }


                if(this.gamepad1.start || this.gamepad2.start)
                {
                    this.motorDeliverySlider.setMode(DcMotorController.RunMode.RESET_ENCODERS);
                    this.motorDeliverySlider.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

                }
                // Tell the motors
                driveLeft(gamepad1.left_stick_y);
                driveRight(gamepad1.right_stick_y);

                collectorToggler.setSpeed(gamepad2.right_stick_y);
                slideToggler.setSpeed(gamepad2.left_stick_y * 0.75);

            }

            //slideToggler.checkPositionInRange();


            // Emit telemetry
            this.telemetry.addData("Drive mode", driveModeLabel[driveMode.ordinal()]);
            telemetry.addData("leftstick", this.gamepad1.left_stick_y);
            telemetry.addData("rightstick", this.gamepad1.right_stick_y);
            this.telemetry.update();

            this.idle(); // Let the rest of the system run until there's a stimulus from the robot controller runtime.
        }
    }


}
