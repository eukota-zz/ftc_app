package org.swerverobotics.ftc6220.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Created by Mridula on 1/15/2016.
 */
@Autonomous(name = "AUTO Blue1 -> Park", group = "Swerve Examples")

public class Blue1ToPark extends MasterAutonomous
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

        wait(2000);
        driveStraight(250, Constants.BACKWARDS, false);
        turnTo(45);
        driveStraight(73, Constants.BACKWARDS, false);
        wait(500);
        HikerDropper.deploy();
        wait(4000);
        driveStraight(73, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        driveStraight(73, Constants.BACKWARDS * 0.4, false);
    }
}
