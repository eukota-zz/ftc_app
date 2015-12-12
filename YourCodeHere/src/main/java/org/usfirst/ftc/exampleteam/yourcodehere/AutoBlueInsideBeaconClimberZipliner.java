package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Robot starts on blue side, goes to beacon,
 * presses beacon button, and scores climber,
 * parks in floor goal
 */
@Autonomous(name="AutoBlueInsideBeaconClimberZipliner")
public class AutoBlueInsideBeaconClimberZipliner extends BeaconClimberZiplinerSkeleton
{
    @Override public void main() throws InterruptedException
    {
        initHardware();

        waitForStart();
        
        driveForwardDistance(DRIVE_POWER, FOO);
        turnRightDistance(DRIVE_POWER, FOO);
        followLine();
        driveForwardDistance(DRIVE_POWER, FOO);
        stopDriving();
        pressBeaconButton();
        dumpClimbers();
        driveForwardDistance(-DRIVE_POWER, FOO);
        turnRightDistance(DRIVE_POWER, FOO);
        driveForwardDistance(DRIVE_POWER, FOO);
        stopDriving();
    }
}
