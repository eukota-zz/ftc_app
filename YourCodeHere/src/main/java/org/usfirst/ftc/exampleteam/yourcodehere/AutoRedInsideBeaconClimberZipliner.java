package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Robot starts on red side, goes to beacon,
 * presses beacon button, and scores climber,
 * parks in floor goal
 */
@Autonomous(name="AutoRedInsideBeaconClimberZipliner")
public class AutoRedInsideBeaconClimberZipliner extends BeaconClimberZiplinerSkeleton
{
    @Override public void main() throws InterruptedException
    {
        initHardware();

        waitForStart();
        
        driveForwardDistance(DRIVE_POWER, FOO);
        turnLeftDistance(DRIVE_POWER, FOO);
        followLine();
        driveForwardDistance(DRIVE_POWER, FOO);
        stopDriving();
        pressBeaconButton();
        dumpClimbers();
        driveForwardDistance(-DRIVE_POWER, FOO);
        turnLeftDistance(DRIVE_POWER, FOO);
        driveForwardDistance(DRIVE_POWER, FOO);
        stopDriving();
    }
}
