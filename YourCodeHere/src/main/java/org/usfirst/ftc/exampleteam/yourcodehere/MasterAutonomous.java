package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotorController;

/*
 * Robot attributes used in autonomous programs
 */
public class MasterAutonomous extends Master
{
    public void servoStartingPositions()
    {
        servoClimberDumper.setPosition(CLIMBER_RETURN_POSITION);
        servoCollectorHinge.setPosition(COLLECTOR_HINGE_UP);
        servoLeftZipline.setPosition(ZIPLINE_LEFT_UP);
        servoRightZipline.setPosition(ZIPLINE_RIGHT_UP);
    }

    public void lightSensorLEDs (boolean state)
    {
        lightSensorBack.enableLed(state);
        lightSensorFront.enableLed(state);
    }

    public void driveForward(double power)
    {
        motorLeft.setPower(power);
        motorRight.setPower(power);
    }

    public void driveForwardDistance(double power, int distance) throws InterruptedException
    {
        motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        motorLeft.setTargetPosition(distance);
        motorRight.setTargetPosition(distance);

        motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        driveForward(power);

        while(Math.abs(motorLeft.getCurrentPosition()) < Math.abs(distance) && Math.abs(motorRight.getCurrentPosition()) < Math.abs(distance))
        {
            // Wait until distance is reached
            telemetry.update();
            idle();
        }

        stopDriving();

        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    public void driveBackwardDistance(double power, int distance) throws InterruptedException
    {
        driveForwardDistance(-power, -distance);
    }

    public void turnLeft(double power)
    {
        motorLeft.setPower(-power);
        motorRight.setPower(power);
    }

    public void turnRight(double power)
    {
        turnLeft(-power);
    }

    public void stopDriving()
    {
        driveForward(0);
    }

    public void turnLeftDistance(double power, int distance) throws InterruptedException
    {
        motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        motorLeft.setTargetPosition(-distance);
        motorRight.setTargetPosition(distance);

        motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        turnLeft(power);

        while(Math.abs(motorLeft.getCurrentPosition()) < Math.abs(distance) && Math.abs(motorRight.getCurrentPosition()) < Math.abs(distance))
        {
            // Wait until distance is reached
            telemetry.update();
            idle();
        }

        stopDriving();

        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    public void turnRightDistance(double power, int distance) throws InterruptedException
    {
        turnLeftDistance(-power, distance);
    }

    public void turnRightDegrees(double power, int angle) throws InterruptedException
    {
        double calibratedHeading = imu.getAngularOrientation().heading;
        double currentHeading = imu.getAngularOrientation().heading - calibratedHeading;

        turnRight(power);

        while(Math.abs(currentHeading) < Math.abs(angle))
        {
            currentHeading = imu.getAngularOrientation().heading - calibratedHeading;
            // Wait until we've reached our target angle
            telemetry.update();
            idle();
        }

        stopDriving();
    }

    public void turnLeftDegrees(double power, int angle) throws InterruptedException
    {
       turnRightDegrees(-power, -angle);
    }

    public void dumpClimbers() throws InterruptedException
    {
        servoClimberDumper.setPosition(CLIMBER_DUMP_POSITION);
    }

    public void setRightZiplineOut() throws InterruptedException
    {
        servoRightZipline.setPosition(ZIPLINE_RIGHT_OUT);
    }

    public void setRightZiplineUp() throws InterruptedException
    {
        servoRightZipline.setPosition(ZIPLINE_RIGHT_UP);
    }

    public void setLeftZiplineOut() throws InterruptedException
    {
        servoLeftZipline.setPosition(ZIPLINE_LEFT_OUT);
    }

    public void setLeftZiplineUp() throws InterruptedException
    {
        servoLeftZipline.setPosition(ZIPLINE_LEFT_UP);
    }


    public void pressBeaconButton() throws InterruptedException
    {
        // Check for a range of blue
        if(colorSensorBeacon.blue() <= calibratedBlue + 50 && colorSensorBeacon.blue() < calibratedBlue - 50)
        {
            // Press Blue
            servoPressBeaconButton.setPosition(0.8);
            Thread.sleep(500);
            servoPressBeaconButton.setPosition(0.5);
        }
        else
        {
            // Otherwise press Red
            servoPressBeaconButton.setPosition(0.2);
            Thread.sleep(500);
            servoPressBeaconButton.setPosition(0.5);
        }

    }

    public void followLine() throws InterruptedException
    {
        while(lightSensorBack.getLightDetected() > 0.5)
        {
            driveForward(DRIVE_POWER);
        }
        stopDriving();
        while (lightSensorFront.getLightDetected() > 0.5)
        {
            motorRight.setPower(DRIVE_POWER);
        }
        stopDriving();
    }

    public void allignWithBlueSideWhiteLine() throws InterruptedException
    {
        driveForward(-DRIVE_POWER / 2);
        lightSensorLEDs(ON);
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
        /*
        while(lightSensorFront.getLightDetected() < 0.6)
        {
            // Wait until front light sensor detects line
            telemetry.update();
            idle();
        }
        */
    }

    public void alignWithRedSideWhiteLine() throws InterruptedException
    {
        driveForward(-DRIVE_POWER / 2);
        lightSensorLEDs(ON);
        while(lightSensorBack.getLightDetected() > 0.6)
        {
            // Wait until back light sensor detects line
            telemetry.update();
            idle();
        }
        turnLeft(DRIVE_POWER / 2);
        while(lightSensorFront.getLightDetected() > 0.6)
        {
            // Wait until front light sensor detects line
            telemetry.update();
            idle();
        }
        /*
        while(lightSensorFront.getLightDetected() < 0.6)
        {
            // Wait until front light sensor detects line
            telemetry.update();
            idle();
        }
        */
    }

    public double getDistance()
    {
        return ultrasonicSensor.getUltrasonicLevel();
    }
}
