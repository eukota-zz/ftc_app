package org.usfirst.ftc.exampleteam.yourcodehere;

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

    public ServoToggler(Servo s, double retractedPosition, double deployedPostition)
    {
        servo = s;
        servoRetractedPosition = retractedPosition;
        servoDeployedPosition = deployedPostition;
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
}
