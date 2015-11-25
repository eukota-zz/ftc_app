package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Robot starts on blue side, goes to beacon,
 * presses beacon button, and scores climber
 */
@Autonomous(name="RightMotorTest")
public class RightMotorTest extends SynchronousOpMode
{
    // Declare motors
    DcMotor motorRight = null;

    @Override public void main() throws InterruptedException
    {
        // Initialize motors
        motorRight = hardwareMap.dcMotor.get("motorRight");
        motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        motorRight.setDirection(DcMotor.Direction.REVERSE);
        waitForStart();

        while(opModeIsActive())
        {
            if(updateGamepads())
            {
                motorRight.setPower(gamepad1.right_stick_y);
            }
        }
    }

}
