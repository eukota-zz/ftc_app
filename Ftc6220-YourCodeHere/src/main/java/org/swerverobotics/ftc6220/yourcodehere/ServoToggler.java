package org.swerverobotics.ftc6220.yourcodehere;

import com.qualcomm.robotcore.hardware.Servo;

/*
    Handles the toggling behavior that is present for many of our manipulators.
*/
public class ServoToggler
{
    Servo servo;
    boolean isDeployed;
    double servoRetractedPosition;
    double servoDeployedPosition;
    MasterOpMode opmode;

    public ServoToggler(Servo s, double retractedPosition, double deployedPostition, MasterOpMode opmodeContext)
    {
        servo = s;
        servoRetractedPosition = retractedPosition;
        servoDeployedPosition = deployedPostition;
        opmode = opmodeContext;
        setStartingPosition();
    }

    public void setStartingPosition ()
    {
        retract();
    }

    public void deploy ()
    {
        servo.setPosition(servoDeployedPosition);
        isDeployed = true;
    }

    public void retract ()
    {
        servo.setPosition(servoRetractedPosition);
        isDeployed = false;
    }

    public void toggle ()
    {
        if (isDeployed)
        {
            retract();
        }
        else
        {
            deploy();
        }
    }

    public void slowToggle () throws InterruptedException
    {
        if (isDeployed)
        {
            slowRetract();
        }
        else
        {
            slowDeploy();
        }
    }

    public void slowDeploy() throws InterruptedException
    {
        double stepServoForward = (servoDeployedPosition-servoRetractedPosition)/24;

        opmode.stopAllMotors();

        for (double f = servoRetractedPosition; f < servoDeployedPosition; f+=stepServoForward)
        {
            servo.setPosition(f);

            opmode.pause(42);
        }
        servo.setPosition(servoDeployedPosition);
        isDeployed = true;
    }

    public void slowRetract() throws InterruptedException
    {
        double stepServoBack = (servoRetractedPosition-servoDeployedPosition)/Constants.SERVO_STEPS;

        opmode.stopAllMotors();

        for (double b = servoDeployedPosition; b > servoRetractedPosition; b+=stepServoBack)
        {
            servo.setPosition(b);

            opmode.pause(42);
        }
        servo.setPosition(servoRetractedPosition);
        isDeployed = false;
    }

    public void halfDeploy() throws InterruptedException
    {
        servo.setPosition(Constants.HIKER_DROPPER_HALF_DEPLOYED);
    }
}
