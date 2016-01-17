package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Created by Cole on 1/14/2016.
 */
@Disabled
@Autonomous(name = "AUTO WAIT_Red1 -> Park", group = "Swerve Examples")

public class WAIT_Red1ToPark extends MasterAutonomous
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
        turnTo(135);
        driveStraight(77, Constants.BACKWARDS, false);
        wait(500);
        HikerDropper.deploy();
        wait(2000);
        driveStraight(77, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        driveStraight(77, Constants.BACKWARDS * 0.4, false);
    }
}
