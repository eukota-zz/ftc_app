package org.swerverobotics.ftc6220.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Created by Cole on 1/14/2016.
 */
@Disabled
@Autonomous(name = "AUTO WAIT_Red2 -> Park", group = "Swerve Examples")

public class WAIT_Red2ToPark extends MasterAutonomous
{
    @Override
    protected void main() throws InterruptedException
    {
        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();

        initializeServoPositions();

        setAutoStartPosition(135);

        pause(8000);
        driveStraight(125, Constants.BACKWARDS, false);
        turnTo(180);
        driveStraight(93, Constants.BACKWARDS, false);
        turnTo(225);
        driveStraight(73, Constants.BACKWARDS, false);
        pause(500);
        HikerDropper.deploy();
        pause(2000);
        driveStraight(73, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        driveStraight(73, Constants.BACKWARDS * 0.4, false);
    }
}
