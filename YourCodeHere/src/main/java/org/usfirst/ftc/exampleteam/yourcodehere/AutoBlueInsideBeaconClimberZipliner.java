package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Robot starts on blue side, goes to beacon,
 * presses beacon button, and scores climber,
 * parks in floor goal
 */
@Autonomous(name="AutoBlueInsideBeaconClimberZipliner")
public class AutoBlueInsideBeaconClimberZipliner extends Master8923Autonomous
{
    @Override public void main() throws InterruptedException
    {
        robotInit();
        configureTelemtry();

        waitForStart();

        // This is a hack to keep us inside the 18" limit
        // None of the servos move if none are set to a position
        // If any servo is set to a position, they all go
        servoClimberDumper.setPosition(CLIMBER_RETURN_POSITION);
        servoCollectorHinge.setPosition(COLLECTOR_HINGE_UP);
        servoLeftZipline.setPosition(ZIPLINE_LEFT_UP);
        servoRightZipline.setPosition(ZIPLINE_RIGHT_UP);

        driveBackwardDistance(DRIVE_POWER, 12000);
        driveForward(-DRIVE_POWER / 2);
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
        turnLeftDistance(DRIVE_POWER, 300);
        int distance = (int) (ultrasonicSensor.getUltrasonicLevel() - 25) * 50;
        double tempPower = (distance > 0) ? (DRIVE_POWER / 2) : (-DRIVE_POWER / 2);
        driveBackwardDistance(tempPower, distance);
        stopDriving();
        dumpClimbers();
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
