package org.usfirst.ftc.exampleteam.yourcodehere;


import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.interfaces.EulerAngles;
import org.swerverobotics.library.interfaces.IBNO055IMU;
import org.swerverobotics.library.interfaces.Position;


/*
    Skeleton Autonomous1 Op Mode that holds all initialization and general methods
     All auto op modes should inherit from this
 */
public abstract class MasterAutonomous extends MasterOpMode
{
    //auto start position info
    public Transform autoStartPosition = new Transform(0.0,0.0,0.0);
    //IMU variable declaration
    ElapsedTime elapsed = new ElapsedTime();
    IBNO055IMU.Parameters parameters = new IBNO055IMU.Parameters();

    EulerAngles angles;



    PIDFilter filter = new PIDFilter( 0.1, 0.0, 0.01 );


    protected void initialize()
    {

        super.initialize();

        //zero encoders
        MotorLeftBack.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        MotorRightBack.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        MotorLeftBack.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        MotorRightBack.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        //IMU initialization
        parameters.angleunit = IBNO055IMU.ANGLEUNIT.DEGREES;
        parameters.accelunit = IBNO055IMU.ACCELUNIT.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = true;
        parameters.loggingTag = "BNO055";
        imu = ClassFactory.createAdaFruitBNO055IMU(hardwareMap.i2cDevice.get("imu"), parameters);

        //TODO FINISH
        //imu.write8(IBNO055IMU.REGISTER.OPR_MODE, );

    }

    //turn the robot to face a global direction
    public void turnTo(double targetAngle) throws InterruptedException
    {
        double offset = 0;
        double Δϴ = 0;
        double power = 0;
        boolean isTurnCompleted = false;
        
        while (!isTurnCompleted)
        {
            //filter.update();
            Δϴ = targetAngle - getCurrentGlobalOrientation();

            //check 360-0 case
            /*
            if (Math.abs(filter.dV) > 180)
            {
                offset -= Math.signum(filter.dV) * 360;
            }
            Δϴ += offset;
            */
            //set filtered motor powers
            //power = filter.getFilteredValue();
            power = 0.1 * Δϴ;
            if (Math.abs(power) > 1)
            {
                power = Math.signum(power);
            }
            driveWheels(-power, power);

            //roll records
            //filter.roll(Δϴ);

            //check if the turn is finished and the robot is settled

            /*
            if (Math.abs(Δϴ) < 1 && Math.abs(filter.dV) < 0.1)
            {
                isTurnCompleted = true;
                stopDriveMotors();
            }*/

            idle();
        }

    }


    //drive to a distance in CM
    public void driveDistance(double distance, double direction) throws InterruptedException
    {
        double startDist = getDistanceTraveled();
        double netDist = startDist + direction*distance;
        driveWheels(direction, direction);
        //until passed target
        if (direction < 0)
        {
            while (getDistanceTraveled() > netDist)
            {
                idle();
            }
        }
        else{
            while (getDistanceTraveled() < netDist)
            {
                idle();
            }
        }
        stopDriveMotors();
    }

    //returns the average of the left/right encoders, giving a distance in CM
    //tested to be within 0.67% of actual, at 150cm
    private double getDistanceTraveled()
    {
        double CORRECTION_FACTOR = 1.0026;
        double avgTick = (MotorLeftBack.getCurrentPosition() + MotorRightBack.getCurrentPosition())/2;
        double realityFactor = Constants.REAR_WHEEL_DIAMETER * Math.PI / Constants.ANDYMARK_ENC_TICKS;
        return avgTick*realityFactor/CORRECTION_FACTOR;
    }

    //return the global orientation of the robot, accounting for autonomous start
    public double getCurrentGlobalOrientation()
    {
        return (-1 * (getCurrentLocalOrientation() + autoStartPosition.orientation) + 360);
    }
    //return the local orientation of the robot, relative to it's start
    public double getCurrentLocalOrientation()
    {
        angles = imu.getAngularOrientation();
        return angles.heading;
    }


    //wait a number of milliseconds
    public void wait(int t) throws InterruptedException
    {
        //convert
        t*=1000*1000;
        //we don't use System.currentTimeMillis() because it can be inconsistent
        long initialTime = System.nanoTime();
        while(System.nanoTime() - initialTime < t){
            idle();
        }
    }


}
