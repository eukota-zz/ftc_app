package org.swerverobotics.ftc6220.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Mridula on 1/23/2016.
 */
public class Hanger
{
    DcMotor LeftMotor;
    DcMotor RightMotor;

    public Hanger(DcMotor Left, DcMotor Right)
    {
        LeftMotor = Left;
        RightMotor = Right;
    }

    public void moveHanger(double power)
    {
        LeftMotor.setPower(power);
        RightMotor.setPower(power);
    }

    public void stopHangerMotors()
    {
        LeftMotor.setPower(0);
        RightMotor.setPower(0);
    }

    //this method does not need to do anything for a hanger without encoders
    public void checkHanger()
    {

    }
}
