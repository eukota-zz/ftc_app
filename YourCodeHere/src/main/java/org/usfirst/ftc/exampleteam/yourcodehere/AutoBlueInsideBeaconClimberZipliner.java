package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Robot starts on blue side, goes to beacon,
 * presses beacon button, and scores climber,
 * parks in floor goal
 */
@Autonomous(name="AutoBlueInsideBeaconClimberZipliner")
public class AutoBlueInsideBeaconClimberZipliner extends MasterAutonomous
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
        allignWithBlueSideWhiteLine();
        correctDistanceToWall();
        pressBeaconButton();
        dumpClimbers();
        driveForwardDistance(DRIVE_POWER, FOO);
        turnRightDistance(DRIVE_POWER, FOO);
        servoRightZipline.setPosition(ZIPLINE_RIGHT_OUT);
        driveForwardDistance(DRIVE_POWER, FOO);
        servoRightZipline.setPosition(ZIPLINE_RIGHT_UP);
        stopDriving();
    }
}