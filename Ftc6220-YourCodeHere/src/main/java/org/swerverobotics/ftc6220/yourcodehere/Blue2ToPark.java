package org.swerverobotics.ftc6220.yourcodehere;

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

        pause(100);
        driveStraight(125, Constants.BACKWARDS, false);
        turnTo(0);
        driveStraight(105, Constants.BACKWARDS, false);
        turnTo(-45);
        driveStraight(56, Constants.BACKWARDS, false);
        pause(500);
        HikerDropper.slowToggle();
        pause(3000);
        driveStraight(56, Constants.FORWARDS * 0.4, false);
        HikerDropper.slowToggle();
        driveStraight(56, Constants.BACKWARDS * 0.4, false);
    }
}
