package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * An example of a synchronous opmode that implements a simple drive-a-bot.
 */
@TeleOp(name = "Synch6220TeleOp", group = "Swerve Examples")
public class Synch6220TeleOp extends SynchronousOpMode {
    // All hardware variables can only be initialized inside the main() function,
    // not here at their member variable declarations.
    DcMotor MotorRightBack = null;
    DcMotor MotorLeftBack = null;
    DcMotor MotorLeftTriangle = null;
    DcMotor MotorLeftClimber = null;
    DcMotor MotorRightClimber = null;
    DcMotor MotorRightTriangle = null;
    //Servo CollectorServo = null;

    boolean driveClimbMode = false;
    double dirFactor = 1.0;
    boolean aJustPressed   = false;


    @Override
    protected void main() throws InterruptedException {
        // Initialize our hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names you assigned during the robot configuration
        // step you did in the FTC Robot Controller app on the phone.
        this.MotorRightBack = this.hardwareMap.dcMotor.get("MotorRightBack");
        this.MotorLeftBack = this.hardwareMap.dcMotor.get("MotorLeftBack");
        this.MotorLeftTriangle = this.hardwareMap.dcMotor.get("MotorLeftTriangle");
        this.MotorLeftClimber = this.hardwareMap.dcMotor.get("MotorLeftClimber");
        this.MotorRightClimber = this.hardwareMap.dcMotor.get("MotorRightClimber");
        this.MotorRightTriangle = this.hardwareMap.dcMotor.get("MotorRightTriangle");

        //this.CollectorServo = this.hardwareMap.servo.get("CollectorServo");

        // Configure the knobs of the hardware according to how you've wired your
        // robot. Here, we assume that there are no encoders connected to the motors,
        // so we inform the motor objects of that fact.
        this.MotorRightBack.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.MotorLeftBack.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.MotorLeftTriangle.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorLeftClimber.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorRightClimber.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.MotorRightTriangle.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        // One of the two motors (here, the left) should be set to reversed direction
        // so that it can take the same power level values as the other motor.
        this.MotorRightBack.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftTriangle.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftClimber.setDirection(DcMotor.Direction.REVERSE);


        this.MotorRightBack.setPower(0);
        this.MotorRightClimber.setPower(0);
        this.MotorRightTriangle.setPower(0);
        this.MotorLeftBack.setPower(0);
        this.MotorLeftTriangle.setPower(0);
        this.MotorLeftClimber.setPower(0);


        // Configure the dashboard however we want it
        this.configureDashboard();

        // Wait until we've been given the ok to go
        this.waitForStart();

        // Enter a loop processing all the input we receive
        while (this.opModeIsActive())
        {


            if (this.updateGamepads())
            {

                // There is (likely) new gamepad input available.
                // Do something with that! Here, we just drive.
                this.doManualDrivingControl(this.gamepad1);
            }

            // Emit telemetry with the freshest possible values
            this.telemetry.update();

            // Let the rest of the system run until there's a stimulus from the robot controller runtime.
            this.idle();
        }
    }

    /**
     * Implement a simple two-motor driving logic using the left and right
     * right joysticks on the indicated game pad.
     */
    void doManualDrivingControl(Gamepad pad) throws InterruptedException {
        // Remember that the gamepad sticks range from -1 to +1, and that the motor
        // power levels range over the same amount
        /*float ctlPower = pad.left_stick_y;
        float ctlSteering = pad.right_stick_x;
        */
        double WHEEL_CLIIMB_FACT = 0.5;
        double WHEEL_DRIVE_FACT = 0.5;
        double wheelPowerLeft = 0.0;
        double wheelPowerRight = 0.0;
        double wheelClimberLeft = 0.0;
        double wheelClimberRight = 0.0;
        double trianglePowerLeft = 0.0;
        double trianglePowerRight = 0.0;
        double trianglePowerFactor = 0.0;
        double climberPowerFactor = 0.0;
        double wheelPowerFactor = 0.0;
        //false= sticks for tank  /// true = sticks for climbing

        //toggle collector mode
        if (pad.a /*&& !aJustPressed*/) {
            driveClimbMode = false;
            dirFactor = 1.0;
        }
        //toggle "ready" mode for getting ready to climb the ramp
        else if (pad.b /*&& !aJustPressed*/) {
            driveClimbMode = false;
            dirFactor = -1.0;
        }
        //toggle drive climb mode
        else if (pad.y /*&& !aJustPressed*/) {
            driveClimbMode = true;
            dirFactor = -1.0;
        }


        double  sty2 = deadZoneShift(pad.left_stick_y);
        double  sty1 = deadZoneShift(pad.right_stick_y);

        trianglePowerFactor = deadZoneShift(pad.left_trigger);
        wheelPowerFactor = (float) deadZoneShift(pad.left_trigger * WHEEL_CLIIMB_FACT);
        climberPowerFactor = deadZoneShift(pad.right_trigger);
        //check for reversal modifier and set the power factor for climbing
        if (pad.left_bumper){
            trianglePowerFactor *= -1.0;
            wheelPowerFactor    *= -1.0;
            //pad.left_bumper.
        }
        if (pad.x){
            climberPowerFactor *= -1.0;
        }

        //slowmode
        if (pad.right_bumper){
            if ( (dirFactor == 1.0) || (dirFactor == -1.0) ){
                dirFactor = getSign(dirFactor) * 0.3;
            }
        }
        else{
            if ( (dirFactor == 0.3) || (dirFactor == -0.3) ){
                dirFactor = getSign(dirFactor);
            }
        }
        //collector mode
        if ((!driveClimbMode)&(dirFactor>0)) {
            //on-field driving
            wheelPowerLeft = sty1 * WHEEL_DRIVE_FACT * dirFactor;
            wheelPowerRight = sty2 * WHEEL_DRIVE_FACT * dirFactor;

            trianglePowerLeft = sty1 * dirFactor;
            trianglePowerRight = sty2* dirFactor;

            wheelClimberLeft = climberPowerFactor * dirFactor;
            wheelClimberRight = climberPowerFactor * dirFactor;
        }
        //"ready" mode for getting ready to climb the ramp
        else if ((!driveClimbMode)&(dirFactor<0)){
            //mountain driving
            wheelPowerLeft = sty2 * WHEEL_DRIVE_FACT * dirFactor;
            wheelPowerRight = sty1 * WHEEL_DRIVE_FACT * dirFactor;

            trianglePowerLeft = sty2 * dirFactor;
            trianglePowerRight = sty1 * dirFactor;

            wheelClimberLeft =  climberPowerFactor * dirFactor;
            wheelClimberRight = climberPowerFactor * dirFactor;
        }
        //drive climb mode
        else if ((driveClimbMode)&(dirFactor<0)){
            //mountain driving
            wheelPowerLeft = sty2 * WHEEL_DRIVE_FACT * dirFactor;
            wheelPowerRight = sty1 * WHEEL_DRIVE_FACT * dirFactor;

            trianglePowerLeft = sty2 * dirFactor;
            trianglePowerRight = sty1 * dirFactor;

            wheelClimberLeft =  sty2 * dirFactor;
            wheelClimberRight = sty1 * dirFactor;
        }

        //if(pad.a == true)
        //{
            //boolean aIsPressed = true;
        //}



        MotorRightBack.setPower(wheelPowerRight + wheelPowerFactor);
        MotorLeftBack.setPower( wheelPowerLeft  + wheelPowerFactor);
        MotorRightTriangle.setPower(trianglePowerRight + trianglePowerFactor);
        MotorLeftTriangle.setPower( trianglePowerLeft  + trianglePowerFactor);
        MotorLeftClimber.setPower(  wheelClimberLeft );
        MotorRightClimber.setPower( wheelClimberRight );
        // We're going to assume that the deadzone processing has been taken care of for us
        // already by the underlying system (that appears to be the intent). Were that not
        // the case, then we would here process ctlPower and ctlSteering to be exactly zero
        // within the deadzone.

        // Map the power and steering to have more oomph at low values (optional)
        /*
        ctlPower = this.xformDrivingPowerLevels(ctlPower);
        ctlSteering = this.xformDrivingPowerLevels(ctlSteering);

        // Dampen power to avoid clipping so we can still effectively steer even
        // under heavy throttle.
        //
        // We want
        //      -1 <= ctlPower - ctlSteering <= 1
        //      -1 <= ctlPower + ctlSteering <= 1
        // i.e
        //      ctlSteering -1 <= ctlPower <=  ctlSteering + 1
        //     -ctlSteering -1 <= ctlPower <= -ctlSteering + 1
        ctlPower = Range.clip(ctlPower, ctlSteering - 1, ctlSteering + 1);
        ctlPower = Range.clip(ctlPower, -ctlSteering - 1, -ctlSteering + 1);

        // Figure out how much power to send to each motor. Be sure
        // not to ask for too much, or the motor will throw an exception.
        float powerRightBack = Range.clip(ctlPower - ctlSteering, -1f, 1f);
        float powerLeftBack = Range.clip(ctlPower + ctlSteering, -1f, 1f);
        climberPowerRight = Range.clip(climberPowerRight, -1f, 1f);
        climberPowerLeft = Range.clip(climberPowerLeft, -1f, 1f);

        if (this.gamepad1.a)
        {
            CollectorServo.setPosition(1);
        }
        else CollectorServo.setPosition(0.5);

        if (pad.left_bumper)
        {
            MotorLeftClimber.setPower(-.7);
        }
        else
        {
            MotorLeftClimber.setPower(climberPowerLeft);
        }

        if (pad.right_bumper)
        {
            MotorRightClimber.setPower(-.7);
        }
        else
        {
            MotorRightClimber.setPower(climberPowerRight);
        }


        // Tell the motors
        this.MotorRightBack.setPower(powerRightBack);
        this.MotorRightTriangle.setPower(powerRightBack);
        this.MotorLeftBack.setPower(powerLeftBack);
        this.MotorLeftTriangle.setPower(powerLeftBack);
        */

    }

    float xformDrivingPowerLevels(float level)
    // A useful thing to do in some robots is to map the power levels so that
    // low power levels have more power than they otherwise would. This sometimes
    // help give better driveability.
    {
        // We use a log function here as a simple way to transform the levels.
        // You might want to try something different: perhaps construct a
        // manually specified function using a table of values over which
        // you interpolate.
        float zeroToOne = Math.abs(level);
        float oneToTen = zeroToOne * 9 + 1;
        return (float) (Math.log10(oneToTen) * Math.signum(level));
    }
    double getSign(double value)
    {
        return (Math.abs(value) / value);
    }

    double deadZoneShift(double value){
        double deadZone = 0.05;
        double newSlope = (1.0 - deadZone);
        double output = 0.0;

        if (Math.abs(value) > deadZone) {
            output = newSlope * (value + getSign(value) * deadZone);
        }
        return output;
    }

    void configureDashboard() {


        this.telemetry.log.setDisplayOldToNew(false);   // And we show the log in new to old order, just because we want to
        this.telemetry.log.setCapacity(10);             // We can control the number of lines used by the log

        // Configure the dashboard. Here, it will have one line, which will contain three items
        this.telemetry.addLine
                (
                        this.telemetry.item("left:", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return format(MotorRightBack.getPower());
                            }
                        }),
                        this.telemetry.item("right: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return format(MotorRightBack.getPower());
                            }
                        }),
                        this.telemetry.item("mode: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return MotorRightBack.getChannelMode();
                            }
                        })
                );
    }



    // Handy functions for formatting data for the dashboard
    String format(double d) {
        return String.format("%.1f", d);
    }
}
