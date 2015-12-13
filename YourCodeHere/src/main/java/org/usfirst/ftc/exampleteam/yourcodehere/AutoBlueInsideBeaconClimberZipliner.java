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

        servoClimberDump.setPosition(CLIMBER_RETURN_POSITION);
        servoCollectorHinge.setPosition(COLLECTOR_HINGE_UP);
        servoLeftZipline.setPosition(ZIPLINE_LEFT_UP);
        servoRightZipline.setPosition(ZIPLINE_RIGHT_UP);

        driveBackwardDistance(DRIVE_POWER, 12000);
        driveForward(-DRIVE_POWER / 4);
        lightSensorBack.enableLed(true);
        lightSensorFront.enableLed(true);
        while(lightSensorBack.getLightDetected() > 0.6)
        {
            // Wait until back light sensor detects line
            telemetry.update();
            idle();
        }
        turnRight(DRIVE_POWER / 2);
        while(lightSensorFront.getLightDetected() > 0.6)
        {
            // Wait until front light sensor detects line
            telemetry.update();
            idle();
        }
        while(lightSensorFront.getLightDetected() < 0.6)
        {
            // Wait until front light sensor detects line
            telemetry.update();
            idle();
        }
        driveBackwardDistance(DRIVE_POWER / 4, 400);
        stopDriving();
        dumpClimbers();
        int distance = (int) ultrasonicSensor.getUltrasonicLevel();
        driveForwardDistance(DRIVE_POWER, 500);
        stopDriving();
        /*
        followLine();
        driveBackwardDistance(DRIVE_POWER, FOO);
        stopDriving();
        pressBeaconButton();
        dumpClimbers();
        driveForwardDistance(-DRIVE_POWER, FOO);
        turnRightDistance(DRIVE_POWER, FOO);
        driveForwardDistance(DRIVE_POWER, FOO);
        stopDriving();*/
    }
}
