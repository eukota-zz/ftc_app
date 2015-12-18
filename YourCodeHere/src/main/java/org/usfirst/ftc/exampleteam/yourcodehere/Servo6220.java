package org.usfirst.ftc.exampleteam.yourcodehere;

/**
 * Created by Cole on 12/16/2015.
 */
public enum Servo6220
{
        LeftZiplineHitter,
        RightZiplineHitter,
        HikerDropper,
        HangerServo,
        HolderServoLeft,
        HolderServoRight;

        public static String[] GetNames()
        {
            return new String[] {"LeftZiplineHitter", "RightZiplineHitter", "HikerDropper", "HangerServo", "HolderServoLeft", "HolderServoRight"};
        }
}
