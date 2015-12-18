package org.usfirst.ftc.exampleteam.yourcodehere;

/**
 * Created by Cole on 12/16/2015.
 */
public enum Motor6220
{
    RightBack,
    LeftBack,
    RightTriangle,
    LeftTriangle,
    RightClimber,
    LeftClimber,
    MotorHanger;

    public static String[] GetNames()
    {
        return new String[] {"MotorRightBack", "MotorLeftBack", "MotorRightTriangle", "MotorLeftTriangle", "MotorRightClimber", "MotorLeftClimber", "MotorHanger"};
    }
}
