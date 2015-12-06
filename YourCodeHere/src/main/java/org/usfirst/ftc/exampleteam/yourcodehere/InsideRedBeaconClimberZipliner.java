package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Robot starts on blue side, goes to beacon,
 * presses beacon button, and scores climber,
 * parks in floor goal
 */
@Autonomous(name="InsideRedBeaconClimberZipliner")
@Disabled
public class InsideRedBeaconClimberZipliner extends BeaconClimberZiplinerSkeleton
{
    @Override public void main() throws InterruptedException
    {
        /*
         * drive to beacon
         * turn to face beacon
         * follow line to wall
         * determine beacon color
         * press correct button
         * dump climbers
         * back up
         * turn towards floor goal
         * drive into floor goal
         */


        DriveForwardDistance(DRIVE_POWER, FOO);
        TurnLeftDistance(DRIVE_POWER, FOO);
        FollowLine();
        DriveForwardDistance(DRIVE_POWER, FOO);
        StopDriving();
        PressBeaconButton();
        DumpClimbers();
        DriveForwardDistance(-DRIVE_POWER, FOO);
        TurnLeftDistance(DRIVE_POWER, FOO);
        DriveForwardDistance(DRIVE_POWER, FOO);
        StopDriving();
    }
}
