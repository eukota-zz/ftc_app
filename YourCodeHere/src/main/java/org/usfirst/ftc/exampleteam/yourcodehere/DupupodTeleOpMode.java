package org.usfirst.ftc.exampleteam.yourcodehere;


import com.qualcomm.robotcore.hardware.Gamepad;

/*
    Skeleton Tele-Op Mode that holds all initialization and general methods
     All teleop modes should inherit from this
 */
public class DupupodTeleOpMode extends DupupodOpMode {

    //       constants for driver control     //
    final float HARD_JOYSTICK_DEADZONE = 0.05f;
    final double SLOW_MODE_FACTOR = 0.3;


    //should the super method "initializeHardware()" be this, and have the method extended instead?
    //for clarity: we might have a method "DupupodOpMode.initialize()" that does the hardware,
    // and then extend it for either teleOp/auto?
    protected void initialize() {

        initializeHardware();

        //set the hard deadzone for the joysticks
        this.gamepad1.setJoystickDeadzone(HARD_JOYSTICK_DEADZONE);
        this.gamepad2.setJoystickDeadzone(HARD_JOYSTICK_DEADZONE);
    }

    //convert a linear stick behaviour to a super-egg/circular curve
    //  rho = 2 :: squarish
    //  rho = 1 :: circle
    //  rho = 0 :: linear
    protected double stickCurve(double value, double rho){
        double output;
        if (value > 0){
            output =  Math.pow(1- Math.pow((1-value),(rho+1)),1/(rho+1));
        }
        else if (value < 0){
            output = Math.pow(1- Math.pow((1-value),rho),1/rho);
        }
        else{
            output = 0.0;
        }
        return Math.abs(output) * getSign(value);
    }

    /*The three drive modes for teleOp:
        1. Field     - Front of the robot is opposite the climbers, and only the small wheels turn on the sticks.
                *This is for the main phase of the game, where we are scoring blocks and dumping climbers
                *In this mode, all drive motors receive a -1x multiplier and the left/right sticks are flipped
        2. PreRamp   - Front of the robot is on the same end as the climbers, and only the small wheels on the sticks.
                *This is for the last bit of the main phase where we are aiming ourselves for climbing the ramp
                *Formerly, this was called "Backwards", but since our hardware naming scheme directly contradicted this, it was renamed
                *In this mode, the left stick applies to the left drive etc., and there is a 1x multiplier to power
        3. Ramp      - Identical to PreRamp, but the climbers are now bound to the sticks as well.
                *This is for climbing the ramp and subsequent churros, collectively the mountain
                *We may want to rename it to mountain mode/pre-mountain mode, but so far the name seems fine
    */
    enum DriveMode
    {
        Field,
        PreRamp,
        Ramp
    };

    //we expect the robot will always start like this, but it may be better to initialize it another way
    public DriveMode currentDriveMode = DriveMode.Field;

    public void setCurrentDriveMode(DriveMode newMode){
        currentDriveMode = newMode;
    }

    //regular dual stick driving for a drive mode
    //maybe we want to change this toallow adjustment of the controls on the triggers?
    public void driveRobot(Gamepad pad, DriveMode mode, boolean slow) throws InterruptedException{
        //control for climbers from triggers
        double climberSecondaryPower = 0.0;

        //if the right trigger is pulled, the climber go forward. if the left is pulled, it reverses
        //this needs to be a little more elegant
        climberSecondaryPower = pad.right_trigger;

        //handle slowmode
        double slowFactor = 1.0;
        if (slow){
            slowFactor = SLOW_MODE_FACTOR;
        }

        //capture and adjust stick input with slowmode
        double y1Stick = stickCurve(pad.left_stick_y , -0.1) * slowFactor;
        double y2Stick = stickCurve(pad.right_stick_y, -0.1) * slowFactor;

        //mode cases
        if       ( mode == DriveMode.Field  ){
            driveSmallWheels(y2Stick, y1Stick);
            driveClimbers(climberSecondaryPower);
        }
        else if ( mode == DriveMode.PreRamp ){
            driveSmallWheels(-1*y1Stick, -1*y2Stick);
            driveClimbers(climberSecondaryPower);
        }
        else if ( mode == DriveMode.Ramp    ){
            driveSmallWheels(-1*y1Stick, -1*y2Stick);
            driveClimbers(   -1*y1Stick+climberSecondaryPower, -1*y2Stick+climberSecondaryPower);
        }

        //drive mode switching
        if (pad.a){
            setCurrentDriveMode(DriveMode.Field);
        }
        else if (pad.b){
            setCurrentDriveMode(DriveMode.PreRamp);
        }
        else if (pad.y){
            setCurrentDriveMode(DriveMode.Ramp);
        }
    }





}
