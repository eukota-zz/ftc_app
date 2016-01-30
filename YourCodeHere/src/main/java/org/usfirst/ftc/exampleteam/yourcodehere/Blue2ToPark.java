package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Created by Mridula on 1/15/2016.
 */
@Autonomous(name = "AUTO Blue2 -> Park", group = "Swerve Examples")
public class Blue2ToPark extends MasterAutonomous
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

        wait(100);
        driveStraight(115, Constants.BACKWARDS, false);
        turnTo(0);
        driveStraight(98, Constants.BACKWARDS, false);
        turnTo(-45);
        driveStraight(62, Constants.BACKWARDS, false);
        wait(500);
        HikerDropper.deploy();
        wait(4000);
        driveStraight(62, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        driveStraight(62, Constants.BACKWARDS * 0.4, false);
    }
}
