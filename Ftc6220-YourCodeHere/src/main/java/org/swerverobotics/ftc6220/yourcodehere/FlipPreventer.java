package org.swerverobotics.ftc6220.yourcodehere;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.swerverobotics.library.SynchronousOpMode;

/**
 * Created by Cole on 4/7/2016.
 */
public class FlipPreventer
{
    MasterOpMode masterOpMode;
    boolean hanging = false;

    public FlipPreventer(MasterOpMode master)
    {
        this.masterOpMode = master;
    }

    double hangerEncoderValue;

    public void checkForFlip() throws InterruptedException
    {
        hangerEncoderValue = -1 * masterOpMode.getTapePosition();

        masterOpMode.telemetry.addData("hanger encoder value: ", hangerEncoderValue);
        masterOpMode.telemetry.update();

        if(hangerEncoderValue > 2240)
        {
            hanging = true;
        }

        masterOpMode.angles = masterOpMode.imu.getAngularOrientation();

        if((masterOpMode.angles.roll < -45) && (hanging == false))
        {
            masterOpMode.driveAllMotors(-1,-1);
            masterOpMode.pause(400);
            masterOpMode.stopAllMotors();
        }

    }

    public void checkForReversedFlip() throws InterruptedException
    {
        hangerEncoderValue = -1 * masterOpMode.getTapePosition();

        masterOpMode.telemetry.addData("hanger encoder value: ", hangerEncoderValue);
        masterOpMode.telemetry.update();

        if(hangerEncoderValue > 2240)
        {
            hanging = true;
        }

        masterOpMode.angles = masterOpMode.imu.getAngularOrientation();

        if((masterOpMode.angles.roll > 45) && (hanging == false))
        {
            masterOpMode.driveAllMotors(-1,-1);
            masterOpMode.pause(400);
            masterOpMode.stopAllMotors();
        }

    }
}
