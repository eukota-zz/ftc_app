package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IBNO055IMU;

import java.util.ArrayList;
import java.util.List;

/*
    Skeleton Op Mode that holds all initialization and low level methods for our robot in particular
     All skeletal op modes should inherit from this
 */
public class DupupodOpMode extends SynchronousOpMode {

    //default loop inherited from SynchOpMode
    //empty until this is extended by a teleOp or auto class
    @Override
    protected void main() throws InterruptedException {}

    //         constants that require changing             //
    final boolean LEFT = false;
    final boolean RIGHT = !LEFT;

    final boolean DEPLOY = true;
    final boolean RETRACT = !DEPLOY;

    //              Constants for control                  //

    //IMU and encoder constants
    final int ANDYMRK_ENC_TICKS = 1120;
    final int TETRIX_ENC_TICKS = 1440;

    //max power to output to a motor in the drive
    final double MAX_DRIVE_SPEED = 1.0;

    //drive physical characteristics
    final double TRIANGLE_GEAR_RATIO = 56 / 36;
    final double TRIANGLE_WHEEL_DIAMETER = 7.62;//cm
    final double REAR_GEAR_RATIO = 1 / 1;
    final double REAR_WHEEEL_DIAMETER = 10.16;//cm
    //calculate the proper factor to apply to the rear wheel power in order to reduce skidding
    final double REAR_WHEEL_POWER_FACTOR = (TRIANGLE_GEAR_RATIO / REAR_GEAR_RATIO) * (TRIANGLE_WHEEL_DIAMETER/REAR_WHEEEL_DIAMETER);

    //constants for controlling servos
    final double MAX_SERVO_VALUE = 1.0;
    final double MIN_SERVO_VALUE = -1.0;
    final double CR_STOP_VALUE = 0.0;

    //should these be static?
    static final double ZIPLINE_HITTER_NOTDEPLOYED = -1.0;
    static final double ZIPLINE_HITTER_DEPLOYED = 0.6;
    static final double HIKER_DROPPER_NOTDEPLOYED = -1.0;
    static final double HIKER_DROPPER_DEPLOYED = 0.95;

    //untested, and the holders aren't on the robot yet
    /*
    static final double HOLDER_SERVO_LEFT_NOTDEPLOYED = -1.0;
    static final double HOLDER_SERVO_LEFT_DEPLOYED = 1.0;
    static final double HOLDER_SERVO_RIGHT_NOTDEPLOYED = 1.0;
    static final double HOLDER_SERVO_RIGHT_DEPLOYED = -1.0;
    */

    //              Declare hardware modules              //


        //motors
    DcMotor MotorRightBack = null;
    DcMotor MotorLeftBack = null;
    DcMotor MotorLeftTriangle = null;
    DcMotor MotorLeftClimber = null;
    DcMotor MotorRightClimber = null;
    DcMotor MotorRightTriangle = null;
    DcMotor MotorHanger = null;


        //servos
    //Servo CollectorServo = null;//not on the robot yet
    Servo LeftZiplineHitter = null;
    Servo RightZiplineHitter = null;
    Servo HikerDropper = null;
    Servo HangerServo = null;
    Servo HolderServoLeft = null;
    Servo HolderServoRight = null;

        //lists
    List<DcMotor> motors = new ArrayList<>();
    List<Servo> servos = new ArrayList<>();

        //sensors
    IBNO055IMU imu = null;

    //hardware initialization
    protected void initializeHardware()
    {
        //dynamically load all servos
        for (String curServoName : Servo6220.GetNames())
        {
            servos.add(this.hardwareMap.servo.get(curServoName));
        }
        //initialize old servo references for backwards compatibility
        this.LeftZiplineHitter = servos.get(Servo6220.LeftZiplineHitter.ordinal());
        this.RightZiplineHitter = servos.get(Servo6220.RightZiplineHitter.ordinal());
        this.HikerDropper = servos.get(Servo6220.HikerDropper.ordinal());
        this.HangerServo = servos.get(Servo6220.HangerServo.ordinal());
        this.HolderServoLeft = servos.get(Servo6220.HolderServoLeft.ordinal());
        this.HolderServoRight = servos.get(Servo6220.HolderServoRight.ordinal());

        //dynamically load all motors
        for (String curMotorName : Motor6220.GetNames())
        {
            motors.add(this.hardwareMap.dcMotor.get(curMotorName));
        }
        //initialize old motor references for backwards compatibility
        this.MotorRightBack = motors.get(Motor6220.RightBack.ordinal());
        this.MotorLeftBack = motors.get(Motor6220.LeftBack.ordinal());
        this.MotorLeftTriangle = motors.get(Motor6220.RightTriangle.ordinal());
        this.MotorLeftClimber = motors.get(Motor6220.LeftTriangle.ordinal());
        this.MotorRightClimber = motors.get(Motor6220.RightClimber.ordinal());
        this.MotorRightTriangle = motors.get(Motor6220.LeftClimber.ordinal());
        this.MotorHanger = motors.get(Motor6220.MotorHanger.ordinal());

        //the correct motors should be reversed, since they are on the opposite side of the robot.
        this.MotorRightBack.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftTriangle.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftClimber.setDirection(DcMotor.Direction.REVERSE);



        RightZiplineHitter.setDirection(Servo.Direction.REVERSE);

    }

    //not sure if we want this, ...?
    enum Side{
        LEFT,
        RIGHT
    }
    Side robotSide;

    //for keeping track of all the actuators
    enum ActuatorState{
        RETRACTED,
        EXTENDED,
        IN_TRANSITION
    }

    //          DRIVE              //

    //drive the small wheels, but not the climbers
    public void driveSmallWheels(double leftPower, double rightPower){

        leftPower  *= MAX_DRIVE_SPEED;
        rightPower *= MAX_DRIVE_SPEED;

        MotorLeftTriangle.setPower(                          leftPower );
        MotorRightTriangle.setPower(                         rightPower);
        MotorLeftBack.setPower( REAR_WHEEL_POWER_FACTOR * leftPower );
        MotorRightBack.setPower(REAR_WHEEL_POWER_FACTOR * rightPower);
    }

    //drive the small wheels equally on left/right
    public void driveSmallWheels(double power){
        power *= MAX_DRIVE_SPEED;
        driveSmallWheels(power, power);
    }


    //drive the climbers
    public void driveClimbers(double leftPower, double rightPower) {
        leftPower  *= MAX_DRIVE_SPEED;
        rightPower *= MAX_DRIVE_SPEED;

        MotorLeftClimber.setPower( leftPower );
        MotorRightClimber.setPower(rightPower);
    }
    public void driveClimbers(double power){
        power *= MAX_DRIVE_SPEED;
        driveClimbers(power, power);
    }

    //stop any action in the drive
    public void stopDrive(){
        driveSmallWheels(0);
        driveClimbers(0);
    }

    //          ZIPLINE HITTERS            //

    //general retract/deploy zipline hitter
    private void setZiplineHitter(boolean side, boolean deploy){
        Servo servo = null;
        //if left
        if (side == LEFT){
            servo = LeftZiplineHitter;
        }
        else if (side == RIGHT){
            servo = RightZiplineHitter;
        }

        //retract or deploy?
        if (deploy){
            servo.setPosition(ZIPLINE_HITTER_DEPLOYED);
        }
        else{
            servo.setPosition(ZIPLINE_HITTER_NOTDEPLOYED);
        }

    }
    //delpoy/retract zipline hitter
    public void deployLeftZiplineHitter(){   setZiplineHitter(LEFT, DEPLOY); }
    public void deployRightZiplineHitter(){  setZiplineHitter(RIGHT, DEPLOY); }
    public void retractLeftZiplineHitter(){  setZiplineHitter(LEFT, RETRACT); }
    public void retractRightZiplineHitter(){ setZiplineHitter( RIGHT , RETRACT); }

    //              HIKER/CLIMBER DUMPER          //

    //general hiker dumper setting
    private void setHikerDropper(boolean deploy){
        if (deploy){
            HikerDropper.setPosition(HIKER_DROPPER_DEPLOYED);
        }
        else{
            HikerDropper.setPosition(HIKER_DROPPER_NOTDEPLOYED);
        }
    }
    //deploy/retract hiker dropper
    public void deployHikerDropper(){  setHikerDropper( DEPLOY  ); }
    public void retractHikerDropper(){ setHikerDropper( RETRACT ); }


    //             HOLDER HOOKS               //
    /*

        placeholder

    */

    //               HANGER                   //
    /*

        placeholder

     */

    //            UTILITITY FUNCTIONS        ///
    //these should move elsewhere, eventually

    public double getSign(double value){
        double output = 0;
        if (value == 0){
            output = 0;
        }
        else{
            output = value / Math.abs(value);
        }
        return output;
    }

}
