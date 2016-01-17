package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Created by Mridula on 1/15/2016.
 */
@Autonomous(name = "AUTO WAIT_Blue2 -> Park", group = "Swerve Examples")
public class WAIT_Blue2ToPark extends MasterAutonomous
{

    @Override
    protected void main() throws InterruptedException
    {
        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();

        initializeServoPositions();

        setAutoStartPosition(45);

        wait(8000);
        driveStraight(125, Constants.BACKWARDS, false);
        turnTo(0);
        driveStraight(76, Constants.BACKWARDS, false);
        turnTo(-45);
        driveStraight(73, Constants.BACKWARDS, false);
        wait(500);
        HikerDropper.deploy();
        wait(2000);
        driveStraight(73, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        driveStraight(73, Constants.BACKWARDS * 0.4, false);
    }
}
