package org.swerverobotics.ftc417.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.interfaces.EulerAngles;
import org.swerverobotics.library.interfaces.IBNO055IMU;

/**
 * 417 master opmode
 */
public abstract class MasterAuto extends MasterOpMode
{
    public Transform autoStartPosition = new Transform(0.0,0.0,-90.0);
    //IMU variable declaration
    ElapsedTime elapsed = new ElapsedTime();
    IBNO055IMU.Parameters parameters = new IBNO055IMU.Parameters();
    EulerAngles angles;

    void initialize()
    {
        super.initialize();

        // Configure the knobs of the hardware according to how you've wired your
        // robot. Here, we assume that there are no encoders connected to the motors,
        // so we inform the motor objects of that fact.
        this.motorFrontLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorFrontRight.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorBackLeft.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorBackRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        this.motorDeliverySlider.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.motorCollector.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        //this.motorHook.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        //this.motorLift.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorDeliverySlider.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        //IMU initialization
        parameters.angleUnit = IBNO055IMU.ANGLEUNIT.DEGREES;
        parameters.accelUnit = IBNO055IMU.ACCELUNIT.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = true;
        parameters.loggingTag = "BNO055";
        imu = ClassFactory.createAdaFruitBNO055IMU(hardwareMap.i2cDevice.get("imu"), parameters);

    }

    void driveTo(double power, int position , boolean resetEncoders) throws InterruptedException
    {
        telemetry.log.add("starting driveto");

        if(resetEncoders)
        {
            telemetry.log.add("try reset backright");
            this.motorBackRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }
        telemetry.log.add("done reset");

        this.motorBackRight.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        telemetry.log.add("done set mode");

        this.motorBackRight.setTargetPosition(position);
        telemetry.log.add("done set target");

        driveForward(power);

        telemetry.log.add("done set power");
        while ( this.motorBackRight.isBusy() )
        {
            telemetry.addData("backRight", this.motorBackRight.getCurrentPosition());
            telemetry.update();
            this.idle();
        }
        telemetry.log.add("done moving");

        driveStop();

        telemetry.addData("backRight", this.motorBackRight.getCurrentPosition());
        telemetry.update();
    }

    /////// IMU code
    public void setAutoStartPosition (double startingAngle) throws InterruptedException
    {
        autoStartPosition.orientation = startingAngle;
    }





    //returns the average of the left/right encoders, giving a distance in CM
    private double getDistanceTraveled() // in CM
    {
        double CORRECTION_FACTOR = 4; ///< @todo needs calibrated
        //double avgTick = (MotorLeftBack.getCurrentPosition() + MotorRightBack.getCurrentPosition())/2;
        double tick=this.motorBackLeft.getCurrentPosition();
        double distanceTraveledPerTick = Constants.BACK_LEFT_WHEEL_DIAMETER * Math.PI / Constants.TETRIX_ENC_TICKS;
        return tick*distanceTraveledPerTick/CORRECTION_FACTOR;
    }

    public void driveForwardDistanceIMU(double power, int distance) throws InterruptedException
    {  /*
        motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        */


        double calibratedHeading = imu.getAngularOrientation().heading;
        double currentHeading = imu.getAngularOrientation().heading - calibratedHeading;
        double offsetMultiplier = 0.2;

        while(Math.abs(getDistanceTraveled()) < Math.abs(distance))
        {
            // Use IMU to keep us driving straight
            currentHeading = imu.getAngularOrientation().heading - calibratedHeading;
            if(currentHeading > 180)
                currentHeading = currentHeading - 360;
            driveLeft(power + currentHeading * offsetMultiplier);
            driveRight(power - currentHeading * offsetMultiplier);

            // Wait until distance is reached
            telemetry.update();
            idle();
        }

     driveForward(0);
    }

    public void driveBackwardDistanceIMU(double power, int distance) throws InterruptedException
    {
        driveForwardDistanceIMU(-power, distance);
    }

    public void turnRightDegrees(double power, int angle) throws InterruptedException
    {
        double calibratedHeading = imu.getAngularOrientation().heading;
        double currentHeading = imu.getAngularOrientation().heading - calibratedHeading;

        turnRight(power);

        while(Math.abs(currentHeading) < Math.abs(angle))
        {
            currentHeading = imu.getAngularOrientation().heading - calibratedHeading;
            telemetry.log.add("current" + currentHeading);
            if(currentHeading > 180)
                currentHeading = currentHeading - 360;
            // Wait until we've reached our target angle
            telemetry.log.add("current" + currentHeading + " actual" + formatNumber(imu.getAngularOrientation().heading));
            telemetry.update();
            idle();
        }

        driveForward(0);
    }

    public void turnLeftDegrees(double power, int angle) throws InterruptedException
    {
        turnRightDegrees(-power, -angle);
    }


    public void turnLeft(double power)
    {
        driveLeft(-power);
        driveRight(power);
    }

    public void turnRight(double power)
    {
        turnLeft(-power);
    }



    //return the global orientation of the robot, accounting for autonomous start
    public double getCurrentGlobalOrientation()
    {
        return ((getCurrentLocalOrientation() + autoStartPosition.orientation));
    }
    //return the local orientation of the robot, relative to it's start
    public double getCurrentLocalOrientation()
    {
        angles = imu.getAngularOrientation();
        return 360-angles.heading;
    }




}
