package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.EulerAngles;
import org.swerverobotics.library.interfaces.IBNO055IMU;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.Position;
import org.swerverobotics.library.interfaces.TeleOp;

import java.util.concurrent.TimeUnit;

/**
 * An example of a synchronous opmode that implements a simple drive-a-bot.
 */
@TeleOp(name = "BlueSideAuto6220", group = "Swerve Examples")
//TODO make variables to implement a red and blue side auto in the same program and simplify cluttered functions
public class BlueSideAuto6220 extends Master6220OpMode
{
    public BlueSideAuto6220()
    {
        super();
    }
    // Our sensors, motors, and other devices go here, along with other long term state
    IBNO055IMU imu;
    ElapsedTime elapsed = new ElapsedTime();
    IBNO055IMU.Parameters parameters = new IBNO055IMU.Parameters();

    // Here we have state we use for updating the dashboard. The first of these is important
    // to read only once per update, as its acquisition is expensive. The remainder, though,
    // could probably be read once per item, at only a small loss in display accuracy.
    EulerAngles angles;
    Position position;
    int loopCycles;
    int i2cCycles;
    double ms; // milliseconds?


    @Override
    protected void main() throws InterruptedException
    {
        // We are expecting the IMU to be attached to an I2C port on  a core device interface
        // module and named "imu". Retrieve that raw I2cDevice and then wrap it in an object that
        // semantically understands this particular kind of sensor.
        parameters.angleunit = IBNO055IMU.ANGLEUNIT.DEGREES;
        parameters.accelunit = IBNO055IMU.ACCELUNIT.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = true;
        parameters.loggingTag = "BNO055";
        imu = ClassFactory.createAdaFruitBNO055IMU(hardwareMap.i2cDevice.get("imu"), parameters);

        // Enable reporting of position using the naive integrator
        //imu.startAccelerationIntegration(new Position(), new Velocity());


        // Configure the dashboard however we want it
        this.configureDashboard();

        // Wait until we've been given the ok to go
        this.waitForStart();

        //autonomous code starts here
        Thread.sleep(100);
        driveStraight(1);
        Thread.sleep(2000);
        this.stopAllMotors();
        turnRight(45, .5);
        this.stopAllMotors();
        driveStraight(1);
        Thread.sleep(2000);
        this.stopAllMotors();

        /*
        driveWithIMU(90,2000,0.02);
        turnBotTo(135, false);
        driveWithIMU(135,2000,0.02);
        */
    }

    void driveStraight(double power)
    {
        //use previous function to simplify driving in a straight line
        driveForwards(power, power, power, power);
    }

    // unused function - consider removing?
    double normalizeAngle180(double angle) {
        while (angle > 180) angle -= 360;  // unnecessary - use modulus
        while (angle <= -180) angle += 360; // use modulus
        return angle;
    }

    // @todo change to use modulus. The percent symbol is modulus (ie: 365%360 = 5) Could do "return abs(angle%360)"
    // @todo needs description of how to use
    double normalizeAngle360(double angle)
    {
        while (angle >= 360) angle -= 360;
        while (angle < 0) angle += 360;
        return angle;
    }

    double getCurrentHeading()
    {
        angles = imu.getAngularOrientation();

        // Emit telemetry with the freshest possible values
        this.telemetry.update();

        return angles.heading;
    }

    //NOT NECESSARY, deprecated  @todo replace usage with java sign function
    int sign(double value)
    {
        if(value == 0)
        {
            return 0;
        }
        else
        {
            return (int) (Math.abs(value)/value);
        }
    }
    // @todo we can't understand what inputs are allowed for faceAngle
    void driveWithIMU(double faceAngle, int time, double reactFactor) throws InterruptedException
    {
        int sinceStart = 0;
        double delta;
        while (sinceStart < time)
        {
            delta = faceAngle - getCurrentHeading(); // angle is in degrees
            if (delta < 0)
            {
                driveForwards(1 + delta*reactFactor, 1, 1 + delta*reactFactor, 1);
                // drive power is -1.0 to 1.0. But delta here is in degrees so this is going to be odd
            }
            else if (delta > 0)
            {
                driveForwards(1, 1 - delta*reactFactor, 1, 1 - delta*reactFactor);
            }
            sinceStart++;
            Thread.sleep(1);
        }
        this.stopAllMotors();
    }
    //turn the robot to an angle relative to it's starting position before this is called
    //@miyu you could read the init heading at the start so that you know what direction is forward.
    //TODO this method is full of unorganised code, confusing functions, and magic numbers; it needs to be more understandable
    void turnBotTo(double faceAngle, boolean pid){
        //array of last 10 diffs
        double[] delta = {0,0,0,0,0,0,0,0,0,0};
        //continuous summation
        double sum = 0;
        //which way? 1=left, 2=right
        //@todo use enum and not a magic number
        int direction = 1;

        //for check to cross 360-0
        double lastHeading = getCurrentHeading();
        int spinOffset = 0;

        delta[0] = faceAngle - getCurrentHeading();
        while (Math.abs(delta[0]) > 5){
            //set to avoid 360-0 boundary issues
            if (Math.abs(getCurrentHeading() - 360) < 1 && Math.abs(lastHeading) < 1){
                spinOffset += 360;
            }
            else if (Math.abs(getCurrentHeading()) < 1 && Math.abs(lastHeading-360) < 1){
                spinOffset -= 360;
            }
            //move the age stack down
            for (int i = 0; i < 9; i++){
                delta[i + 1] = delta[i];
            }
            delta[0] = faceAngle - getCurrentHeading() - spinOffset;
            sum += delta[0];
            direction = sign(delta[0]);
            driveForwards(-1 * direction, direction, -1 * direction, direction);
        }
        this.stopAllMotors();
    }

    void turnRight(double delta, double power)
    {
        turnLeft(-delta, power);
    }
    void turnLeft(double delta, double power) {
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

    void TurnUpTo270Degrees(double delta, double power) {
        //ASSERT(delta<=270)
        double start_heading = getCurrentHeading();
        double cur = start_heading;
        double dest = cur + delta;

        //normalize dest to between 0..360
        dest = normalizeAngle360(dest);

        boolean goLeft = (delta < 0 ? true : false);

        //int watchdog = 0;

        double GUARD = 40; //protection region against jolts that make the robot jump past the end point or before the start point
        double offset = 0; //amount to offset angles to ensure we don't cross over the 359.99..0 discontinuity during the turn

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

            driveForwards(power, -power, power, -power);

            while ((cur > dest))
            {
                //going left = --
                cur = normalizeAngle360(getCurrentHeading() + offset);
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

            driveForwards(-power, power, -power, power);

            //now compass 0 doesn't fall between the current heading and the destination,
            //so we can use a < comparison to wait for our target
            while ((cur < dest)) {
                //result += cur.ToString() + "\n";
                //going left = --
                cur = normalizeAngle360(getCurrentHeading() + offset);
                //watchdog++;
            }
        }

        //result += "dest reached: " + cur.ToString() + "\n";
        this.stopAllMotors();
    }
}
