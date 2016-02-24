package org.swerverobotics.ftc6220.yourcodehere;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.swerverobotics.library.interfaces.TeleOp;

/**
 * 6220's TeleOp for driving our triangle wheels robot.
 */
@TeleOp(name = "SynchTeleOp", group = "Swerve Examples")
public class SynchTeleOp extends MasterTeleOp
{

    //temporary array to store button previous states to fix toggles
    //TODO encapsulate
    //lefthang,righthang,leftzip,rightzip,hiker
    boolean[] lastBtn = new boolean[5];
    static final int LEFT_BUMPER = 0;
    static final int RIGHT_BUMPER = 1;
    static final int B = 2;
    static final int X = 3;
    static final int Y = 4;

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

            hanger.checkHanger();
            //telemetry.addData("tapeposition", hanger.getTapePosition());

            // Emit telemetry with the newest possible values
            this.telemetry.update();

            // Let the rest of the system run until there's a stimulus from the robot controller runtime.
            this.idle();
        }
    }

    private void handleDriverInput(Gamepad pad1, Gamepad pad2)
    {
        if (pad2.y && !lastBtn[Y])
        {
            HikerDropper.toggle();
        }

        double p2LeftStickPower  = pad2.left_stick_y * currentDrivePowerFactor;
        double p2RightStickPower = pad2.right_stick_y * currentDrivePowerFactor;

        //set pad 2 servos equal to stick input
        //adjusted power is commented out for now
        double adjustedPower = p2RightStickPower; //hanger.checkStalled(p2RightStickPower);
        hanger.moveHanger(adjustedPower);

        telemetry.addData("Hanger Power:", adjustedPower);

        telemetry.update();

        HangerServo.setPosition((p2LeftStickPower + 1) / 2);

        //deploy the holder
        if (pad2.b && !lastBtn[B])
        {
            RightHolder.toggle();
        }

        if (pad2.x && !lastBtn[X])
        {
            LeftHolder.toggle();
        }

        if (pad2.left_bumper && !lastBtn[LEFT_BUMPER])
        {
            LeftZiplineHitter.toggle();
        }

        if (pad2.right_bumper && !lastBtn[RIGHT_BUMPER])
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

        //update button prev states
        lastBtn[LEFT_BUMPER] = pad2.left_bumper;
        lastBtn[RIGHT_BUMPER] = pad2.right_bumper;
        lastBtn[B] = pad2.b;
        lastBtn[X] = pad2.x;
        lastBtn[Y] = pad2.y;
    }


}
