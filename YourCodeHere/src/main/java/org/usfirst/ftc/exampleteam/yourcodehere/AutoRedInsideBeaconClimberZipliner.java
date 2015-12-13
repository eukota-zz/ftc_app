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

        driveBackwardDistance(DRIVE_POWER, 12000);
        driveForward(-DRIVE_POWER / 4);
        lightSensorBack.enableLed(true);
        lightSensorFront.enableLed(true);
        while(lightSensorFront.getLightDetected() > 0.6)
        {
            // Wait until front light sensor detects line
            telemetry.update();
            idle();
        }
        turnLeftDistance(DRIVE_POWER, 750);
        stopDriving();/*
        followLine();
        driveBackwardDistance(DRIVE_POWER, FOO);
        stopDriving();
        pressBeaconButton();
        dumpClimbers();
        driveForwardDistance(-DRIVE_POWER, FOO);
        turnLeftDistance(DRIVE_POWER, FOO);
        driveForwardDistance(DRIVE_POWER, FOO);
        stopDriving();*/
    }
}
