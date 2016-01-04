package org.usfirst.ftc.exampleteam.yourcodehere;


import com.qualcomm.robotcore.hardware.DcMotorController;
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
    final Transform AUTO_START_POSITION = new Transform(0.0,0.0,0.0);
    //IMU variable declaration
    ElapsedTime elapsed = new ElapsedTime();
    IBNO055IMU.Parameters parameters = new IBNO055IMU.Parameters();

    EulerAngles angles;
    Position position;
    int loopCycles;
    int i2cCycles;
    double ms; // milliseconds?


    protected void initialize()
    {

        initializeHardware();

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

    public double getCurrentOrientation()
    {

        angles = imu.getAngularOrientation();
        return angles.heading;
    }

    //turn the bot to face a direction (Field relative)
    public void turnBotTo(double angle, double finishThreshold) throws InterruptedException
    {
        double startAngle = getCurrentOrientation();
        //turn the correct direction
        int direction = (int) Math.signum(angle - startAngle);
        driveWheels(-0.88 * direction, 1.0 * direction);
        //wait for achieved turn
        while (Math.abs(angle - getCurrentOrientation()) > finishThreshold)
        {
            idle();
        }
        stopDriveMotors();
    }

    //turn the robot (bot initial state relative)
    public void turnBot(double angle, double finishThreshold) throws  InterruptedException
    {
        double trueTarget = getCurrentOrientation() + angle;
        turnBotTo(trueTarget, finishThreshold);
    }

    //wait a number of milliseconds
    public void wait(int t)
    {
        double e = 0;
        while(e < t){
            e += 0.00001;
        }
    }
    double normalizeAngle360(double angle)
    {
        while (angle >= 360) angle -= 360;
        while (angle < 0) angle += 360;
        return angle;
    }

    void turnRight(double delta, double power)
    {
        //richTextBoxResult.Text = "";

        int full270s = (int) Math.abs(delta / 270);
        if (delta >= 0) {
            for (int i = 0; i < full270s; i++)
                TurnUpTo270Degrees(270, power);
        } else {
            for (int i = 0; i < full270s; i++) TurnUpTo270Degrees(-270, power);
        }

        double remainder = delta % 270;
        TurnUpTo270Degrees(remainder, power);
    }

    void turnLeft(double delta, double power)
    {
        turnRight(-delta, power);
    }

    void TurnUpTo270Degrees(double delta, double power)
    {

        /*
        These constants are only used here, so they are declared here.
         */

        //protection region against jolts that make the robot jump past the end point or before the start point
        double GUARD = 40;
        //amount to offset angles to ensure we don't cross over the 359.99..0 discontinuity during the turn
        double offset = 0;

        //ASSERT(delta<=270)
        double start_heading = getCurrentOrientation();
        double cur = start_heading;
        double dest = cur + delta;

        //normalize dest to between 0..360
        dest = normalizeAngle360(dest);

        boolean goLeft = (delta < 0 ? true : false);

        //int watchdog = 0;

        /*
        String result = "";

        result += "start: " + start_heading.ToString() + "\n";
        result += "cur: " + cur.ToString() + "\n";
        result += "dest: " + dest.ToString() + "\n";
        result += "direction: " + (goLeft ? "left" : "right") + "\n";
        result += "\n\n";
        */

        if (goLeft) //go left: moving in negative direction around compass
        {
            //if the dest is normalized to a value larger than the current heading,
            //it means that the compass will pass 0 (going in the negative direction) on the way to the dest.
            //When that happens, the current heading will jump from 0 to 359.
            //We will offset our angles to ensure that we don't have that problem.
            if (cur < dest)
            {
                offset = (360 - dest) + GUARD; //put dest at GUARD, move cur by the same amount
            }
            //We won't cross 0 in this turn, but we still want to put some space between
            //the start/end angles and the discontinuity at 0/360
            else
            {
                offset = (GUARD - dest); //put dest at GUARD, move cur by the same amount
            }

            //result += "offset: " + offset.ToString() + "\n";

            cur = normalizeAngle360(cur + offset);
            dest = normalizeAngle360(dest + offset);

            driveWheels(power, -power);

            while ((cur > dest))
            {
                //going left = --
                cur = normalizeAngle360(getCurrentOrientation() + offset);
                //watchdog++;
            }
        } else //go right: moving in positive direction around compass
        {
            /*if the dest is normalized to a value less than than the current heading,
            it means that the compass will pass 0 on the way to the dest.
            When that happens, the current heading will jump from 359 to 0.
            We will offset our angles to ensure that we don't have that problem.*/
            if (cur > dest) {
                offset = (360 - cur) + GUARD; //put cur at GUARD, move dest by the same amount
            } else {
                offset = (GUARD - cur); //put cur at GUARD, move dest by the same amount
            }

            cur = normalizeAngle360(cur + offset);
            dest = normalizeAngle360(dest + offset);

            driveWheels(-power, power);

            //now compass 0 doesn't fall between the current heading and the destination,
            //so we can use a < comparison to wait for our target
            while ((cur < dest)) {
                //result += cur.ToString() + "\n";
                //going left = --
                cur = normalizeAngle360(getCurrentOrientation() + offset);
                //watchdog++;
            }
        }

        //result += "dest reached: " + cur.ToString() + "\n";
        stopDriveMotors();
    }


}
