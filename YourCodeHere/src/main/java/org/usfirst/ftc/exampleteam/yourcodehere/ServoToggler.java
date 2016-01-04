package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Mridula on 1/3/2016.
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
