package org.swerverobotics.ftc6220.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Created by Cole on 1/14/2016.
 */
@Autonomous(name = "AUTO WAIT_Red1 -> Park -> Ramp", group = "Swerve Examples")
@Disabled
public class WAIT_Red1ToParkToRamp extends MasterAutonomous
{
    @Override
    protected void main() throws InterruptedException
    {
        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();

        initializeServoPositions();

        setAutoStartPosition(90);

        wait(8000);
        driveStraight(270, Constants.BACKWARDS, false);
        turnTo(-45);
        driveStraight(78, Constants.BACKWARDS, false);
        wait(500);
        HikerDropper.deploy();
        wait(2000);
        driveStraight(78, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        turnTo(225);
        driveStraight(74, Constants.FORWARDS, false);
        turnTo(180);
        driveStraight(60, Constants.FORWARDS, false);
        driveStraight(65, Constants.FORWARDS, true);
    }
}
