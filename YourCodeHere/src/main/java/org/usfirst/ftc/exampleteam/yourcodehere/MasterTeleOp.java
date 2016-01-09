package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.Gamepad;

/*
    This contains joystick nput handling methods and initialization
 */
//TODO variable speed for the hanger servo and motors
public abstract class MasterTeleOp extends MasterOpMode
{
    protected void initialize()
    {
        initializeHardware();

        //this makes sure the joystick does not take minute data
        this.gamepad1.setJoystickDeadzone(Constants.JOYSTICK_DEADZONE);
        this.gamepad2.setJoystickDeadzone(Constants.JOYSTICK_DEADZONE);
    }

    private enum DriveMode
    {
        Field,
        PreRamp,
        Ramp
    }
    DriveMode currentDriveMode = DriveMode.Field;

    //This is the driving mode for going up the ramp
    protected void setRampClimbingMode() {
        setDriveMode(DriveMode.Ramp);
    }

    //This is the driving mode we use when we want to drive backwards to align with the ramp
    protected void setBackwardsDriveMode() {
        setDriveMode(DriveMode.PreRamp);
    }

    //This is the driving mode we use when driving around the field
    protected void setFieldDrivingMode() {
        setDriveMode(DriveMode.Field);
    }

    private void setDriveMode(DriveMode mode) {
        currentDriveMode = mode;
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


    //This is the methods that passes stick values to the drive
    void driveRobot(Gamepad pad) throws InterruptedException
    {
        // Remember that the gamepad sticks range from -1 to +1, and that the motor
        // power levels range over the same amount

        //using the stickCurve function allows the driver to control the robot more precisely, so we use it for power input
        double leftStickPower  = stickCurve(pad.left_stick_y,  -0.1) * currentDrivePowerFactor;
        double rightStickPower = stickCurve(pad.right_stick_y, -0.1) * currentDrivePowerFactor;

        double leftSidePower;
        double rightSidePower;
        double climberPower = pad.right_trigger * -1;


        //field driving mode
        if (currentDriveMode == DriveMode.Field)
        {
            //the sticks and power are flipped
            leftSidePower  = rightStickPower;
            rightSidePower = leftStickPower;
            driveWheels(leftSidePower, rightSidePower);
            driveClimbers(climberPower, climberPower);
        }
        //"ready" mode for getting ready to climb the ramp
        //we need to drive backwards when aligning with the ramp
        else if (currentDriveMode == DriveMode.PreRamp)
        {
            //read input from the controller
            leftSidePower  = -leftStickPower;
            rightSidePower = -rightStickPower;
            driveWheels(leftSidePower, rightSidePower);
            driveClimbers(climberPower, climberPower);
        }
        //drive climb mode
        else if (currentDriveMode == DriveMode.Ramp)
        {
            //read input from the controller. The climbers should turn with the wheels
            leftSidePower  = -leftStickPower;
            rightSidePower = -rightStickPower;
            driveWheels(leftSidePower, rightSidePower);
            driveClimbers(leftSidePower, rightSidePower);
        }

    }
}
