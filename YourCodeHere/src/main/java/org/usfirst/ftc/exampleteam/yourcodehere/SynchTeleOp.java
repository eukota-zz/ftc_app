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
        // Configure the dashboard however we want it
        //this.configureDashboard();

        // Wait until we've been given the ok to go
        this.waitForStart();

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
            HikerDropper.setPosition(Constants.HIKER_DROPPER_DEPLOYED);
        } else
        {
            HikerDropper.setPosition(Constants.HIKER_DROPPER_NOTDEPLOYED);
        }

        if (pad2.dpad_left)
        {
            HangerServo.setPosition(Constants.HANGER_SERVO_DEPLOYED);
        } else if (pad2.dpad_right)
        {
            HangerServo.setPosition(Constants.HANGER_SERVO_NOTDEPLOYED);
        } else
        {
            HangerServo.setPosition(Constants.HANGER_SERVO_STOP);
        }

        if (pad2.dpad_down)
        {
            MotorHanger.setPower(-1 * Constants.FULL_POWER);
        } else if (pad2.dpad_up)
        {
            MotorHanger.setPower(Constants.FULL_POWER);
        } else
        {
            MotorHanger.setPower(Constants.STOP);
        }

        //deploy the holder
        if (pad2.b & !HolderServoRightDeployed)
        {
            HolderServoRight.setPosition(Constants.HOLDER_SERVO_RIGHT_DEPLOYED);
            HolderServoRightDeployed = true;
        } else if (pad2.b & HolderServoRightDeployed)
        {
            HolderServoRight.setPosition(Constants.HOLDER_SERVO_RIGHT_NOTDEPLOYED);
            HolderServoRightDeployed = false;
        }

        if (pad2.x & !HolderServoLeftDeployed)
        {
            HolderServoLeft.setPosition(Constants.HOLDER_SERVO_LEFT_DEPLOYED);
            HolderServoLeftDeployed = true;
        } else if (pad2.x & HolderServoLeftDeployed)
        {
            HolderServoLeft.setPosition(Constants.HOLDER_SERVO_LEFT_NOTDEPLOYED);
            HolderServoLeftDeployed = false;
        }

        /*if (pad2.a)
        {
            CollectorServo.setPosition(0.0);
        }
        else
        {
            CollectorServo.setPosition(0.5);
        }*/

        if (pad2.left_bumper)
        {
           LeftZiplineHitter.toggle();
        }

        //The ServoRightZiplineHitter reads from (0-1), which is different than the ServoLeftZiplineHitter(0-360)
        if (pad2.right_bumper)
        {
            RightZiplineHitter.toggle();
        }

        //toggle field driving mode
        if (pad1.a)
        {
            setFieldDrivingMode();
        }
        //toggle "ready" mode for getting ready to climb the ramp
        //need to drive backwards so we can line up against the ramp
        else if (pad1.b)
        {
            setBackwardsDriveMode();
        }
        //toggle drive climb mode
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
