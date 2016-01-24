package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Mridula on 1/23/2016.
 */
public class Hanger
{
    static final int MINPOSITION = 0;
    static final int MAXPOSITION = 5000;
    DcMotor LeftMotor;
    DcMotor RightMotor;

    public Hanger(DcMotor Left, DcMotor Right)
    {
        LeftMotor = Left;
        RightMotor = Right;
    }

    public void drive(double power)
    {
        int currentPosition = getTapePosition();

        if ( (currentPosition < MINPOSITION) || (currentPosition > MAXPOSITION))
        {
            stop();
        }
        else
        {
            LeftMotor.setPower(power);
            RightMotor.setPower(power);
        }
    }

    public void stop()
    {
        LeftMotor.setPower(0);
        RightMotor.setPower(0);
    }

    public void checkRange()
    {
        int currentPosition = getTapePosition();

        if ( (currentPosition < MINPOSITION) || (currentPosition > MAXPOSITION))
        {
            stop();
        }
    }

    public int getTapePosition()
    {
        return LeftMotor.getCurrentPosition();
    }
}
