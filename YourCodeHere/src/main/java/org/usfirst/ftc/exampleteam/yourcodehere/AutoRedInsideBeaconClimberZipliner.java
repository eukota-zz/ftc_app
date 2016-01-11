package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Robot starts on red side, goes to beacon,
 * presses beacon button, and scores climber,
 * parks in floor goal
 */
@Autonomous(name="AutoRedInsideBeaconClimberZipliner")
public class AutoRedInsideBeaconClimberZipliner extends MasterAutonomous
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
        alignWithRedSideWhiteLine();
        correctDistanceToWall();

        readBeaconColors();
        // Decide which side is red, and set servo
        if(leftRed > rightRed + colorDifferenceThreshold && rightBlue > leftBlue + colorDifferenceThreshold)
            servoLeftZipline.setPosition(LEFT_BEACON_BUTTON_POSITION);
        else if(rightRed > leftRed + colorDifferenceThreshold && leftBlue > rightBlue + colorDifferenceThreshold)
            servoLeftZipline.setPosition(RIGHT_BEACON_BUTTON_POSITION);
        pressBeaconButton();

        dumpClimbers();
        driveForwardDistance(DRIVE_POWER, FOO);
        turnRightDistance(DRIVE_POWER, FOO);
        servoLeftZipline.setPosition(ZIPLINE_RIGHT_OUT);
        driveBackwardDistance(DRIVE_POWER, FOO);
        servoLeftZipline.setPosition(ZIPLINE_RIGHT_UP);
        stopDriving();
    }
}