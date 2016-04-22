package org.swerverobotics.ftc6220.yourcodehere;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.SynchronousOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.interfaces.EulerAngles;
import org.swerverobotics.library.interfaces.IBNO055IMU;
import org.swerverobotics.library.interfaces.IFunc;
import java.util.ArrayList;
import java.util.List;

/*
    Contains initialization related code as well as some other hardware methods
*/
public abstract class MasterOpMode extends SynchronousOpMode
{
    enum Servo6220
    {
        LeftZiplineHitter,
        RightZiplineHitter,
        HikerDropper,
        HangerServo,
        HolderServoLeft,
        HolderServoRight;

        public static String[] GetNames()
        {
            return new String[] {"ServoLeftZiplineHitter", "ServoRightZiplineHitter", "HikerDropper", "HangerServo", "HolderServoLeft", "HolderServoRight"};
        }
    }

    enum Motor6220
    {
        RightBack,
        LeftBack,
        RightTriangle,
        LeftTriangle,
        RightClimber,
        LeftClimber,
        LeftMotorHanger,
        RightMotorHanger;

        public static String[] GetNames()
        {
            return new String[] {"MotorRightBack", "MotorLeftBack", "MotorRightTriangle", "MotorLeftTriangle", "MotorRightClimber", "MotorLeftClimber", "LeftMotorHanger", "RightMotorHanger"};
        }
    }

    static double currentDrivePowerFactor = Constants.FULL_POWER;

    //sensors
    IBNO055IMU imu = null;
    //IMU variable declaration
    ElapsedTime elapsed = new ElapsedTime();
    IBNO055IMU.Parameters parameters = new IBNO055IMU.Parameters();

    EulerAngles angles;


    //Declare motors
    DcMotor MotorRightBack = null;
    DcMotor MotorLeftBack = null;
    DcMotor MotorRightTriangle = null;
    DcMotor MotorLeftTriangle = null;
    DcMotor MotorRightClimber = null;
    DcMotor MotorLeftClimber = null;
    public DcMotor LeftMotorHanger = null;
    public DcMotor RightMotorHanger = null;

    // Declare servos
    Servo ServoLeftZiplineHitter = null;
    Servo ServoRightZiplineHitter = null;
    Servo ServoHikerDropper = null;
    Servo HangerServo = null;
    Servo HolderServoLeft = null;
    Servo HolderServoRight = null;
    List<DcMotor> motors = new ArrayList<>();
    List<Servo> servos = new ArrayList<>();

    ServoToggler LeftZiplineHitter;
    ServoToggler RightZiplineHitter;
    ServoToggler LeftHolder;
    ServoToggler RightHolder;
    ServoToggler HikerDropper;

    boolean autoWaitAtStart = false;
    boolean autoStartingPlace = Constants.START_POSITION_1;
    boolean autoSide = Constants.RED;

    DriverLEDManager ledManager;

    FlipPreventor flipPreventor;

    //drive the wheels
    public void driveWheels(double leftPower, double rightPower)
    {
        MotorLeftTriangle.setPower(Constants.LEFT_ASSEMBLY_DIFF * leftPower );
        MotorRightTriangle.setPower(rightPower);
        MotorLeftBack.setPower(Constants.LEFT_ASSEMBLY_DIFF * Constants.REAR_WHEEL_POWER_FACTOR * leftPower );
        MotorRightBack.setPower(Constants.REAR_WHEEL_POWER_FACTOR * rightPower);
    }

    //drive the climbers
    public void driveClimbers(double leftPower, double rightPower)
    {
        MotorLeftClimber.setPower(leftPower);
        MotorRightClimber.setPower(rightPower);
    }

    public void driveAllMotors(double leftPower, double rightPower)
    {
        MotorLeftTriangle.setPower(Constants.LEFT_ASSEMBLY_DIFF * leftPower );
        MotorRightTriangle.setPower(rightPower);
        MotorLeftBack.setPower(Constants.LEFT_ASSEMBLY_DIFF * Constants.REAR_WHEEL_POWER_FACTOR * leftPower );
        MotorRightBack.setPower(Constants.REAR_WHEEL_POWER_FACTOR * rightPower);
        MotorLeftClimber.setPower(leftPower);
        MotorRightClimber.setPower(rightPower);
    }

    protected void initialize()
    {
        //IMU initialization
        parameters.angleUnit = IBNO055IMU.ANGLEUNIT.DEGREES;
        parameters.accelUnit = IBNO055IMU.ACCELUNIT.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = true;
        parameters.loggingTag = "BNO055";
        imu = ClassFactory.createAdaFruitBNO055IMU(hardwareMap.i2cDevice.get("imu"), parameters);

        //TODO adjust imu frequency dampening
        //imu.write8(IBNO055IMU.REGISTER.OPR_MODE, );

        //dynamically load all servos
        for (String curServoName : Servo6220.GetNames())
        {
            servos.add(this.hardwareMap.servo.get(curServoName));
        }
        //initialize old servo references for backwards compatibility

        //dynamically load all motors
        for (String curMotorName : Motor6220.GetNames())
        {
            motors.add(this.hardwareMap.dcMotor.get(curMotorName));
        }

        //initialize old motor references for backwards compatibility
        this.HolderServoLeft = servos.get(Servo6220.HolderServoLeft.ordinal());
        this.LeftMotorHanger = motors.get(Motor6220.LeftMotorHanger.ordinal());
        this.RightMotorHanger = motors.get(Motor6220.RightMotorHanger.ordinal());
        this.ServoLeftZiplineHitter = servos.get(Servo6220.LeftZiplineHitter.ordinal());
        this.ServoHikerDropper = servos.get(Servo6220.HikerDropper.ordinal());
        this.MotorRightTriangle = motors.get(Motor6220.RightTriangle.ordinal());
        this.ServoRightZiplineHitter = servos.get(Servo6220.RightZiplineHitter.ordinal());
        this.MotorLeftClimber = motors.get(Motor6220.LeftClimber.ordinal());
        this.MotorLeftTriangle = motors.get(Motor6220.LeftTriangle.ordinal());
        this.MotorLeftBack = motors.get(Motor6220.LeftBack.ordinal());
        this.MotorRightClimber = motors.get(Motor6220.RightClimber.ordinal());
        this.HangerServo = servos.get(Servo6220.HangerServo.ordinal());
        this.MotorRightBack = motors.get(Motor6220.RightBack.ordinal());
        this.HolderServoRight = servos.get(Servo6220.HolderServoRight.ordinal());

        // Configure the knobs of the hardware according to how you've wired your
        // robot. Here, we assume that there are no encoders connected to the motors,
        // so we inform the motor objects of that fact (we might use encoders later.)
        for (DcMotor curMotor : motors)
        {
            curMotor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        }

        //the correct motors should be reversed, since they are on the opposite side of the robot.
        this.MotorRightBack.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftTriangle.setDirection(DcMotor.Direction.REVERSE);
        this.MotorLeftClimber.setDirection(DcMotor.Direction.REVERSE);

        //the left side hanger motor moves the opposite direction of the right side hanger.
        this.LeftMotorHanger.setDirection(DcMotor.Direction.REVERSE);
        this.LeftMotorHanger.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.RightMotorHanger.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        this.initializeAutonomousParmeters();

        this.ledManager = new DriverLEDManager(this);

        this.flipPreventor = new FlipPreventor(this);

        stopAllMotors();
    }

    public void initializeAutonomousParmeters()
    {
        DigitalChannel wait = hardwareMap.digitalChannel.get("SwitchWait");
        wait.setMode(DigitalChannelController.Mode.INPUT);
        autoWaitAtStart = wait.getState();
        telemetry.log.add("Wait is " + autoWaitAtStart);

        DigitalChannel startPosition = hardwareMap.digitalChannel.get("SwitchStartPosition");
        startPosition.setMode(DigitalChannelController.Mode.INPUT);
        autoStartingPlace = startPosition.getState();
        telemetry.log.add("Start is " + (autoStartingPlace ? "1" : "2"));


        DigitalChannel redOrBlue = hardwareMap.digitalChannel.get("RedorBlue");
        redOrBlue.setMode(DigitalChannelController.Mode.INPUT);
        autoSide = redOrBlue.getState();
        telemetry.log.add("Side is " + (autoSide ? "red" : "blue"));

    }


    //this is so we can be able to initialize servos after pause for start (robot needs to fit in sizing box)
    public void initializeServoPositions()
    {
        this.ServoRightZiplineHitter.setDirection(Servo.Direction.REVERSE);

        //changed zipline hitters from ServoTogglers to cr servos
        this.ServoLeftZiplineHitter.setPosition(0.5);
        this.ServoRightZiplineHitter.setPosition(0.5);

        //this.LeftZiplineHitter = new ServoToggler(ServoLeftZiplineHitter, Constants.ZIPLINEHITTER_NOTDEPLOYED, Constants.ZIPLINEHITTER_DEPLOYED, this);
        //this.RightZiplineHitter = new ServoToggler(ServoRightZiplineHitter, Constants.ZIPLINEHITTER_NOTDEPLOYED, Constants.ZIPLINEHITTER_DEPLOYED, this);

        this.HolderServoLeft.setDirection(Servo.Direction.REVERSE);

        this.LeftHolder = new ServoToggler(HolderServoLeft, Constants.HOLDER_SERVO_NOTDEPLOYED, Constants.HOLDER_SERVO_DEPLOYED, this);
        this.RightHolder = new ServoToggler(HolderServoRight, Constants.HOLDER_SERVO_NOTDEPLOYED, Constants.HOLDER_SERVO_DEPLOYED, this);

        this.HikerDropper = new ServoToggler(ServoHikerDropper, Constants.HIKER_DROPPER_NOTDEPLOYED, Constants.HIKER_DROPPER_DEPLOYED, this);

        this.HangerServo.setPosition(Constants.HANGER_SERVO_STOP);
    }

    public int getTapePosition()
    {
        return this.LeftMotorHanger.getCurrentPosition();
    }

    protected void stopDriveMotors()
    {
        MotorLeftBack.setPower(0);
        MotorRightBack.setPower(0);
        MotorLeftTriangle.setPower(0);
        MotorRightTriangle.setPower(0);
        MotorLeftClimber.setPower(0);
        MotorRightClimber.setPower(0);

    }

    protected void stopAllMotors()
    {
        for (DcMotor curMotor: this.motors)
        {
            curMotor.setPower(0.0);
        }
    }

    //pause a number of milliseconds
    public void pause(int t) throws InterruptedException
    {
        //we don't use System.currentTimeMillis() because it can be inconsistent
        long initialTime = System.nanoTime();
        while((System.nanoTime() - initialTime)/1000/1000 < t)
        {
            idle();
        }
    }


    void configureDashboard()
    {

        this.telemetry.log.setDisplayOldToNew(false);   // And we show the log in new to old order, just because we want to
        this.telemetry.log.setCapacity(10);             // We can control the number of lines used by the log

        // Configure the dashboard. Here, it will have one line, which will contain three items
        this.telemetry.addLine
                (
                        this.telemetry.item("left back:", new IFunc<Object>()
                        {
                            @Override
                            public Object value()
                            {
                                return format(MotorLeftBack.getPower());
                            }
                        }),
                        this.telemetry.item("right back: ", new IFunc<Object>()
                        {
                            @Override
                            public Object value()
                            {
                                return format(MotorRightBack.getPower());
                            }
                        })
                );
    }

    // Handy functions for formatting data for the dashboard
    String format(double d)
    {
        return String.format("%.1f", d);
    }

}
