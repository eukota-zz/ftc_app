package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

import java.util.ArrayList;
import java.util.List;

/**
 * 6220's TeleOp for driving our triangle wheels robot.
 */
@TeleOp(name = "Synch6220TeleOp", group = "Swerve Examples")
public class Synch6220TeleOp extends Master6220OpMode
{
    public Synch6220TeleOp()
    {
        super();
    }

    @Override
    protected void main() throws InterruptedException
    {
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

    /**
     * This is the body of the TeleOp that allows the driver to control the robot.
     */
    //TO DO:  we need to finish refactoring this.
    void driveRobot(Gamepad pad) throws InterruptedException
    {
        // Remember that the gamepad sticks range from -1 to +1, and that the motor
        // power levels range over the same amount


        //read input from the controller
        double leftSidePower = pad.left_stick_y;
        double rightSidePower = pad.right_stick_y;
        double climberPower = pad.right_trigger * -1;

        /**
         * Calculate motor power based on drive mode and controller input
         */

        //field driving mode
        if (currentDriveMode == DriveModeEnum.DriveModeField)
        {
            leftSidePower = pad.right_stick_y;
            rightSidePower = pad.left_stick_y;
            climberPower = pad.right_trigger;
            driveForwards(leftSidePower, rightSidePower, climberPower, climberPower);
        }
        //"ready" mode for getting ready to climb the ramp
        //we need to drive backwards when aligning with the ramp
        else if (currentDriveMode == DriveModeEnum.DriveModeBackwards)
        {
            driveBackwards(leftSidePower, rightSidePower, climberPower, climberPower);
        }
        //drive climb mode
        else if (currentDriveMode == DriveModeEnum.DriveModeRamp)
        {
            //since we want both our climbers and wheels to have the same power, we set the climbers equal to the left and right sides
            driveBackwards(leftSidePower, rightSidePower, leftSidePower, rightSidePower);
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

        if (pad2.left_bumper & !LeftZiplineHitterDeployed)
        {
            LeftZiplineHitter.setPosition(Constants.LEFT_ZIPLINEHITTER_DEPLOYED);
            LeftZiplineHitterDeployed = true;
            telemetry.log.add("left bumper:deployed");
        } else if (pad2.left_bumper & LeftZiplineHitterDeployed)
        {
            LeftZiplineHitter.setPosition(Constants.LEFT_ZIPLINEHITTER_NOTDEPLOYED);
            LeftZiplineHitterDeployed = false;
            telemetry.log.add("left bumper:notdeployed");
        } else telemetry.log.add("no bumper");

        //The RightZiplineHitter reads from (0-1), which is different than the LeftZiplineHitter(0-360)
        if (pad2.right_bumper & !RightZiplineHitterDeployed)
        {
            RightZiplineHitter.setPosition(Constants.RIGHT_ZIPLINEHITTER_DEPLOYED);
            RightZiplineHitterDeployed = true;
        } else if (pad2.right_bumper & RightZiplineHitterDeployed)
        {
            RightZiplineHitter.setPosition(Constants.RIGHT_ZIPLINEHITTER_NOTDEPLOYED);
            RightZiplineHitterDeployed = false;
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
