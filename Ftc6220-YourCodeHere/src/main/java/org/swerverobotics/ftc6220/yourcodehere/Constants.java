package org.swerverobotics.ftc6220.yourcodehere;

/**
 * Created by Cole on 12/19/2015.
 */
public class Constants
{
    //TODO change this to an xml file to eliminate having to call a variable by saying "Constants.xxx"
    //these are the constants for SynchTeleOp.  They control motor power to support normal speed and slow speed driving.
    public static final double FULL_POWER = 1.0;
    public static final double LOW_POWER = 0.3;
    public static final double STOP = 0.0;
    //servo constants
    public static final double ZIPLINEHITTER_NOTDEPLOYED = -0.85;
    public static final double ZIPLINEHITTER_DEPLOYED = 0.6;
    public static final double HIKER_DROPPER_NOTDEPLOYED = 0.11;
    public static final double HIKER_DROPPER_DEPLOYED = 0.69;
    public static final double HANGER_SERVO_NOTDEPLOYED = 0.0;
    public static final double HANGER_SERVO_STOP = 0.5;
    public static final double HANGER_SERVO_DEPLOYED = 1.0;
    public static final double HOLDER_SERVO_NOTDEPLOYED = 0.8;
    public static final double HOLDER_SERVO_DEPLOYED = -1.0;

    //deadzone constant
    public static final float JOYSTICK_DEADZONE = 0.05f;

    //drive physical characteristics
    public static final double TRIANGLE_GEAR_RATIO = 56 / 36;
    public static final double TRIANGLE_WHEEL_DIAMETER = 7.62;//cm
    public static final double REAR_GEAR_RATIO = 1 / 1;
    public static final double REAR_WHEEL_DIAMETER = 10.16;//cm
    //calculate the proper factor to apply to the rear wheel power in order to reduce skidding
    public static final double REAR_WHEEL_POWER_FACTOR = (TRIANGLE_GEAR_RATIO / REAR_GEAR_RATIO) * (TRIANGLE_WHEEL_DIAMETER/REAR_WHEEL_DIAMETER);

    //motion variable declaration
    public static final double FORWARDS = 1.0;
    public static final double BACKWARDS = -FORWARDS;
    public static final double SLOW_FORWARDS = 0.3 * FORWARDS;
    public static final double SLOW_BACKWARDS = 0.3 * BACKWARDS;

    //IMU and encoder constants
    public static final int ANDYMARK_ENC_TICKS = 1120;
    public static final int TETRIX_ENC_TICKS = 1440;

    //this accounts for the small difference between the wheel assemblies on the left and right side of the robot during driving
    public static final double LEFT_ASSEMBLY_DIFF = 0.84;
}
