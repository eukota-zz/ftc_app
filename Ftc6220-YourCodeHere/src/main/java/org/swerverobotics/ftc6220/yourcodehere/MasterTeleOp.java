package org.swerverobotics.ftc6220.yourcodehere;

import com.qualcomm.robotcore.hardware.Gamepad;


        import com.qualcomm.robotcore.hardware.Gamepad;

/*
    This contains joystick nput handling methods and initialization
 */
public abstract class MasterTeleOp extends MasterOpMode
{

    Hanger hanger;
    double hangerServoTrim = 0.0;

    public static final double hangerServoTrimValue = -0.025;

    protected void initialize()
    {
        super.initialize();

        hanger = new Hanger(LeftMotorHanger, RightMotorHanger);

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
    protected void setRampClimbingMode() 
    {
        setDriveMode(DriveMode.Ramp);
        ledManager.turnOnYellowLight();
    }

    //This is the driving mode we use when we want to drive backwards to align with the ramp
    protected void setBackwardsDriveMode()
    {
        setDriveMode(DriveMode.PreRamp);
        ledManager.turnOnBlueLight();
    }

    //This is the driving mode we use when driving around the field
    protected void setFieldDrivingMode()
    {
        setDriveMode(DriveMode.Field);
        ledManager.turnOnGreenLight();
    }

    private void setDriveMode(DriveMode mode) {
        currentDriveMode = mode;
    }

    //convert a linear stick behaviour to a super-egg/circular curve
    //  rho = 2 :: squarish
    //  rho = 1 :: circle
    //  rho = 0 :: linear
    protected double stickCurve(double value)
    {
        return (Math.pow(value, 3) + value) / 2;
    }


    //This is the methods that passes stick values to the drive
    void driveRobot(Gamepad pad, Gamepad pad2) throws InterruptedException
    {
        // Remember that the gamepad sticks range from -1 to +1, and that the motor
        // power levels range over the same amount

        //using the stickCurve function allows the driver to control the robot more precisely, so we use it for power input
        double p1LeftStickPower  = stickCurve(pad.left_stick_y) * currentDrivePowerFactor;
        double p1RightStickPower = stickCurve(pad.right_stick_y) * currentDrivePowerFactor;

        double p2LeftStickPower  = pad2.left_stick_y * currentDrivePowerFactor;
        double p2RightStickPower = pad2.right_stick_y * currentDrivePowerFactor;
        double adjustedPower = p2RightStickPower; //hanger.checkStalled(p2RightStickPower);

        double leftSidePower;
        double rightSidePower;
        double climberPower = pad.right_trigger * -1;

        //adjusted power is commented out for now
        hanger.moveHanger(adjustedPower);

        telemetry.addData("Hanger Power:", adjustedPower);


        HangerServo.setPosition(hangerServoTrim + ((0.5 * p2LeftStickPower + 1) / 2));

        //field driving mode
        if ((currentDriveMode == DriveMode.Field))
        {
            //the sticks and power are flipped
            leftSidePower  = p1RightStickPower;
            rightSidePower = p1LeftStickPower;
            driveWheels(leftSidePower, rightSidePower);
            driveClimbers(climberPower, climberPower);
        }

        //"ready" mode for getting ready to climb the ramp
        //we need to drive backwards when aligning with the ramp
        else if ((currentDriveMode == DriveMode.PreRamp))
        {
            //read input from the controller
            leftSidePower  = -p1LeftStickPower;
            rightSidePower = -p1RightStickPower;
            driveWheels(leftSidePower, rightSidePower);
            driveClimbers(-climberPower, -climberPower);
        }

        //drive climb mode
        else if ((currentDriveMode == DriveMode.Ramp))
        {
            //read input from the controller. The climbers should turn with the wheels
            if (Math.signum(adjustedPower) == 1.0)
            {
                MotorLeftBack.setPower(0.625 * -p1LeftStickPower);
                MotorRightBack.setPower(0.625 * -p1RightStickPower);
                MotorLeftTriangle.setPower(-p1LeftStickPower);
                MotorRightTriangle.setPower(-p1RightStickPower);
                driveClimbers(0.5 * -p1LeftStickPower, 0.5 * -p1RightStickPower);
            }

            else
            {
                MotorLeftBack.setPower(1.25 * -p1LeftStickPower);
                MotorRightBack.setPower(1.25 * -p1RightStickPower);
                MotorLeftTriangle.setPower(-p1LeftStickPower);
                MotorRightTriangle.setPower(-p1RightStickPower);
                driveClimbers(-p1LeftStickPower,-p1RightStickPower);
            }
        }
    }
}
