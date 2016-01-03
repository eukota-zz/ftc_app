package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Mridula on 1/3/2016.
 */
public class ZiplineHitter
{
    Servo servo;
    boolean isDeployed;

    public ZiplineHitter (Servo s)
    {
        servo = s;
    }

    public void setStartingPosition ()
    {
        retract();
    }

    public void deploy ()
    {
        servo.setPosition(Constants.ZIPLINEHITTER_DEPLOYED);
        isDeployed = true;
    }

    public void retract ()
    {
        servo.setPosition(Constants.ZIPLINEHITTER_NOTDEPLOYED);
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
