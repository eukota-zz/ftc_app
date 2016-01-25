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

    double hangerPower = LeftMotor.getPower();

    boolean isStalled = false;

    double oldEncoderValue;
    double newEncoderValue;
    double oldTime;
    double newTime;
    double delta;

    public Hanger(DcMotor Left, DcMotor Right)
    {
        LeftMotor = Left;
        RightMotor = Right;

        isStalled = false;
        oldTime = System.nanoTime()/1000000;
        newTime = oldTime;
    }

    public void moveHanger(double power)
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

    double checkStalled(double power)
    {
        double newPower = power;
        newTime = System.nanoTime()/1000000;

        if (newTime - oldTime >= 250)
        {
            newEncoderValue = getTapePosition();

            if (isStalled == true)
            {
                isStalled = false;
            }
            else
            {
                delta = newEncoderValue - oldEncoderValue;

                if (delta < 100 & hangerPower > 0.4)
                {
                    power = Math.signum(power) * 0.4;
                }
                isStalled = true;
            }
            oldEncoderValue = newEncoderValue;
            oldTime = newTime;
        }
        return newPower;
    }
}
