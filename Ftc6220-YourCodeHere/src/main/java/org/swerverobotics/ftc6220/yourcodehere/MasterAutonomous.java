package org.swerverobotics.ftc6220.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.interfaces.EulerAngles;
import org.swerverobotics.library.interfaces.IBNO055IMU;


/*
    Skeleton Autonomous1 Op Mode that holds all initialization and general methods
     All auto op modes should inherit from this
 */
public abstract class MasterAutonomous extends MasterOpMode
{
    //auto start position info
    public Transform autoStartPosition = new Transform(0.0,0.0,-90.0);



    PIDFilter turnFilter = new PIDFilter( 0.04, 0.00001, 0.01 );


    protected void initialize()
    {

        super.initialize();

        //zero encoders
        MotorLeftBack.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        MotorRightBack.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        MotorLeftBack.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        MotorRightBack.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);



    }

    public void setAutoStartPosition (double startingAngle) throws InterruptedException
    {
        autoStartPosition.orientation = startingAngle;
    }


    //turn the robot to face a global direction
    public void turnTo(double targetAngle) throws InterruptedException
    {
        double offset = 0;
        double angleDiff;
        double power;
        boolean isTurnCompleted = false;
        double currentOrientation;
        double[] lasts = {0,0};
        double sensorDiff;
        double satisfactionCounter = 0;

        while (!isTurnCompleted)
        {
            turnFilter.update();

            currentOrientation = getCurrentGlobalOrientation();
            angleDiff = targetAngle - currentOrientation;
            //roll sensor difference. we do this to maintain the PID filter's unawareness of the transition
            lasts[1] = lasts[0];
            lasts[0] = angleDiff;
            sensorDiff = lasts[0]-lasts[1];
            //check 360-0 case
            if (Math.abs(sensorDiff) > 350)
            {
                offset -= Math.signum(sensorDiff) * 360;
            }
            angleDiff += offset;
            //check suboptimal direction case
            //should only resolve once
            if (Math.abs(angleDiff) > 180)
            {
                offset -= Math.signum(angleDiff) * 360;
            }
            turnFilter.roll(angleDiff);

            //set filtered motor powers; we added a constant to increase turning power
            power = Constants.TURNING_POW_ADJUSTMENT * turnFilter.getFilteredValue();
            //cap power at 1 magnitude
            if (Math.abs(power) > 1)
            {
                power = Math.signum(power);
            }
            driveWheels(-power, power);

            telemetry.addData("power:", power);
            telemetry.addData("dA:", angleDiff);



            //check if the turn is finished and the robot is settled

            if (Math.abs(angleDiff) < 3.0)
            {
                satisfactionCounter++;
            }
            else if (Math.abs(angleDiff) < 5.0)
            {
                satisfactionCounter+= 0.6;
            }
            else
            {
                satisfactionCounter += 0.15;
            }


            if (satisfactionCounter > 60)
            {
                isTurnCompleted = true;
            }

            telemetry.addData("satisfaction:", satisfactionCounter);
            telemetry.update();

            pause(1);
            idle();
        }
        stopAllMotors();

    }

    public void driveStraight(double distance, double direction, boolean climbers) throws InterruptedException
    {
        double offset = 0;
        double angleDiff;
        double power;
        double leftpower;
        double rightpower;
        boolean isDriveCompleted = false;
        double currentOrientation;
        double[] lasts = {0,0};
        double sensorDiff;
        double timeFactor = 0.5;

        double targetAngle = getCurrentGlobalOrientation();
        double startDistance = getDistanceTraveled();

        while (!isDriveCompleted)
        {
            turnFilter.update();

            if (timeFactor < 1)
            {
                timeFactor += .005;
            }

            currentOrientation = getCurrentGlobalOrientation();
            angleDiff = targetAngle - currentOrientation;
            //roll sensor difference. we do this to maintain the PID filter's unawareness of the transition
            lasts[1] = lasts[0];
            lasts[0] = angleDiff;
            sensorDiff = lasts[0]-lasts[1];
            //check 360-0 case
            if (Math.abs(sensorDiff) > 350)
            {
                offset -= Math.signum(sensorDiff) * 360;
            }
            angleDiff += offset;
            //check suboptimal direction case
            //should only resolve once
            if (Math.abs(angleDiff) > 180)
            {
                offset -= Math.signum(angleDiff) * 360;
            }
            turnFilter.roll(angleDiff);

            //set filtered motor powers
            power = turnFilter.getFilteredValue();
            //cap power at 1 magnitude
            if (Math.abs(power) > 1)
            {
                power = 1*Math.signum(power);
            }

            leftpower = 0.9 + power;
            rightpower = 0.9 - power;

            telemetry.addData("power:", power);
            telemetry.update();
            //cap power at 1 magnitude
            if (Math.abs(leftpower) > 1)
            {
                leftpower = Math.signum(leftpower);
            }

            //cap power at 1 magnitude
            if (Math.abs(rightpower) > 1)
            {
                rightpower = Math.signum(rightpower);
            }

            if(climbers)
            {
                driveClimbers(leftpower * direction * timeFactor, rightpower * direction * timeFactor);
            }
            driveWheels(leftpower * direction * timeFactor, rightpower * direction * timeFactor);

            //check if the robot should stop
            if (Math.abs(getDistanceTraveled() - startDistance) >= Math.abs(distance))
            {
                isDriveCompleted = true;
            }

            pause(1);

            flipPreventor.checkForFlip();

            idle();
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
        return ((getCurrentLocalOrientation() + autoStartPosition.orientation));
    }
    //return the local orientation of the robot, relative to it's start
    public double getCurrentLocalOrientation()
    {
        angles = imu.getAngularOrientation();
        return 360-angles.heading;
    }

}
