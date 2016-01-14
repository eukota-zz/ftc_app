package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * 6220's TeleOp for driving our triangle wheels robot.
 */
@TeleOp(name = "SynchTeleOp", group = "Swerve Examples")
public class SynchTeleOp extends MasterTeleOp
{

    @Override
    protected void main() throws InterruptedException
    {
        initialize();

        // Wait until we've been given the ok to go
        this.waitForStart();

        initializeServoPositions();

        // Enter a loop processing all the input we receive
        while (this.opModeIsActive())
        {
            if (this.updateGamepads())
            {
                // There is (likely) new gamepad input available.
                this.handleDriverInput(this.gamepad1, this.gamepad2);

                this.driveRobot(this.gamepad1);
            }

            // Emit telemetry with the newest possible values
            //this.telemetry.update();

            // Let the rest of the system run until there's a stimulus from the robot controller runtime.
            this.idle();
        }
    }

    private void handleDriverInput(Gamepad pad1, Gamepad pad2)
    {
        if (pad2.y)
        {
            HikerDropper.toggle();
        }

        double p2LeftStickPower  = pad2.left_stick_y * currentDrivePowerFactor;
        double p2RightStickPower = pad2.right_stick_y * currentDrivePowerFactor;

        //set pad 2 servos equal to stick input
        LeftMotorHanger.setPower(p2RightStickPower * -1);
        RightMotorHanger.setPower(p2RightStickPower * -1);

        HangerServo.setPosition((p2LeftStickPower + 1) / 2);

        //deploy the holder
        if (pad2.b)
        {
            RightHolder.toggle();
        }
        if (pad2.x)
        {
            LeftHolder.toggle();
        }


        if (pad2.left_bumper)
        {
           LeftZiplineHitter.toggle();
        }

        //The ServoRightZiplineHitter reads from (0-1), which is different than the ServoLeftZiplineHitter(0-360)
        if (pad2.right_bumper)
        {
            RightZiplineHitter.toggle();
        }

        //set field driving mode
        if (pad1.a)
        {
            setFieldDrivingMode();
        }
        //set "ready" mode for getting ready to climb the ramp
        //need to drive backwards so we can line up against the ramp
        else if (pad1.b)
        {
            setBackwardsDriveMode();
        }
        //set drive climb mode
        else if (pad1.y)
        {
            setRampClimbingMode();
        }

        //reduce power so we can go slower ("slow mode") and have more control
        if (pad1.right_bumper)
        {
            currentDrivePowerFactor = Constants.LOW_POWER;
        } else
        {
            currentDrivePowerFactor = Constants.FULL_POWER;
        }
    }


}
