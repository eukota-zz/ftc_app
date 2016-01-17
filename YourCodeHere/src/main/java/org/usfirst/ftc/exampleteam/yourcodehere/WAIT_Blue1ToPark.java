package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Created by Mridula on 1/15/2016.
 */
@Autonomous(name = "AUTO WAIT_Blue1 -> Park", group = "Swerve Examples")
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

        wait(8000);
        driveStraight(250, Constants.BACKWARDS, false);
        turnTo(45);
        driveStraight(77, Constants.BACKWARDS, false);
        wait(500);
        HikerDropper.deploy();
        wait(2000);
        driveStraight(77, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        driveStraight(77, Constants.BACKWARDS * 0.4, false);
    }
}
