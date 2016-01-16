package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Created by Cole on 1/14/2016.
 */
@Autonomous(name = "AUTO Red2 -> Park", group = "Swerve Examples")
public class Red2ToPark extends MasterAutonomous
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

        wait(100);
        driveStraight(125, Constants.BACKWARDS, false);
        turnTo(180);
        driveStraight(85, Constants.BACKWARDS, false);
        turnTo(225);
        driveStraight(73, Constants.BACKWARDS, false);
        wait(500);
        HikerDropper.deploy();
        wait(2000);
        driveStraight(73, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        driveStraight(73, Constants.BACKWARDS * 0.4, false);
    }
}
