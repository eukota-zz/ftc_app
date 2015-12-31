package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Robot starts on red side, goes to beacon,
 * presses beacon button, and scores climber,
 * parks in floor goal
 */
@Autonomous(name="AutoRedInsideBeaconClimberZipliner")
public class AutoRedInsideBeaconClimberZipliner extends Master8923Autonomous
{
    @Override public void main() throws InterruptedException
    {
        robotInit();
        configureTelemtry();

        waitForStart();

        // This is a hack to keep us inside the 18" limit
        // None of the servos move if none are set to a position
        // If any servo is set to a position, they all go
        servoStartingPositions();

        driveBackwardDistance(DRIVE_POWER, FOO);
        allignWithRedSideWhiteLine();
        stopDriving();
        pressBeaconButton();
        dumpClimbers();
        driveForwardDistance(DRIVE_POWER, FOO);
        turnLeftDistance(DRIVE_POWER, FOO);
        setLeftZiplineOut();
        driveForwardDistance(DRIVE_POWER, FOO);
        setLeftZiplineUp();
        stopDriving();
    }
}
