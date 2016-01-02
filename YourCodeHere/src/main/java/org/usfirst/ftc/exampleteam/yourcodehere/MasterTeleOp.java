package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by Cole on 12/30/2015.
 */
public abstract class MasterTeleOp extends MasterOpMode
{
    protected void initialize()
    {
        initializeHardware();

        //this makes sure the joystick does not take minute data
        this.gamepad1.setJoystickDeadzone(Constants.JOYSTICK_DEADZONE);
        this.gamepad2.setJoystickDeadzone(Constants.JOYSTICK_DEADZONE);
    }

    //convert a linear stick behaviour to a super-egg/circular curve
    //  rho = 2 :: squarish
    //  rho = 1 :: circle
    //  rho = 0 :: linear
    protected double stickCurve(double value, double rho)
    {
        double output;
        if (value > 0)
        {
            output =  Math.pow(1- Math.pow((1-value),(rho+1)),1/(rho+1));
        }
        else if (value < 0)
        {
            output = Math.pow(1- Math.pow((1-value),rho),1/rho);
        }
        else
        {
            output = 0.0;
        }
        return Math.abs(output) * Math.signum(value);
    }

    /**
     * This is the body of the TeleOp that allows the driver to control the robot.
     */
    //TO DO:  we need to finish refactoring this.
    void driveRobot(Gamepad pad) throws InterruptedException
    {
        // Remember that the gamepad sticks range from -1 to +1, and that the motor
        // power levels range over the same amount

        //using the stickCurve function allows the driver to control the robot more precisely, so we use it for power input
        double leftStickPower = stickCurve(pad.left_stick_y, -0.1) * currentDrivePowerFactor;
        double rightStickPower = stickCurve(pad.right_stick_y, -0.1) * currentDrivePowerFactor;

        double leftSidePower;
        double rightSidePower;
        double climberPower;

        /**
         * Calculate motor power based on drive mode and controller input
         */

        //field driving mode
        if (currentDriveMode == DriveModeEnum.DriveModeField)
        {
            leftSidePower = rightStickPower;
            rightSidePower = leftStickPower;
            climberPower = pad.right_trigger;
            driveForwards(leftSidePower, rightSidePower, climberPower, climberPower);
        }
        //"ready" mode for getting ready to climb the ramp
        //we need to drive backwards when aligning with the ramp
        else if (currentDriveMode == DriveModeEnum.DriveModeBackwards)
        {
            //read input from the controller
            leftSidePower = leftStickPower;
            rightSidePower = rightStickPower;
            climberPower = pad.right_trigger * -1;

            driveBackwards(leftSidePower, rightSidePower, climberPower, climberPower);
        }
        //drive climb mode
        else if (currentDriveMode == DriveModeEnum.DriveModeRamp)
        {
            //read input from the controller
            leftSidePower = leftStickPower;
            rightSidePower = rightStickPower;
            climberPower = pad.right_trigger * -1;

            //since we want both our climbers and wheels to have the same power, we set the climbers equal to the left and right sides
            driveBackwards(leftSidePower, rightSidePower, leftSidePower, rightSidePower);
        }
    }
}
