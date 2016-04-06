package org.swerverobotics.ftc6220.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Created by Mridula on 1/15/2016.
 */
@Autonomous(name = "AUTO WAIT_Blue1 -> Park", group = "Swerve Examples")
@Disabled
public class WAIT_Blue1ToPark extends MasterAutonomous
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

        pause(8000);
        driveStraight(250, Constants.BACKWARDS, false);
        turnTo(45);
        driveStraight(77, Constants.BACKWARDS, false);
        pause(500);
        HikerDropper.deploy();
        pause(2000);
        driveStraight(77, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        driveStraight(77, Constants.BACKWARDS * 0.4, false);
    }
}
