package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

/*
 *
 */
public class Master8923Autonomous extends Master8923
{
    public void driveForward(double power) {
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

    public void dumpClimbers() throws InterruptedException
    {
        servoClimberDumper.setPosition(CLIMBER_DUMP_POSITION);
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

    public double getDistance()
    {
        return ultrasonicSensor.getUltrasonicLevel();
    }
}
