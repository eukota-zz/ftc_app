package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Robot starts on blue side, goes to beacon,
 * presses beacon button, and scores climber,
 * parks in floor goal
 */
@Autonomous(name="AutoRedInsideBeaconClimberZipliner")
@Disabled
public class AutoRedInsideBeaconClimberZipliner extends BeaconClimberZiplinerSkeleton
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


        driveForwardDistance(DRIVE_POWER, FOO);
        TurnLeftDistance(DRIVE_POWER, FOO);
        FollowLine();
        driveForwardDistance(DRIVE_POWER, FOO);
        StopDriving();
        PressBeaconButton();
        DumpClimbers();
        driveForwardDistance(-DRIVE_POWER, FOO);
        TurnLeftDistance(DRIVE_POWER, FOO);
        driveForwardDistance(DRIVE_POWER, FOO);
        StopDriving();
    }
}
