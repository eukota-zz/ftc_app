package org.swerverobotics.ftc6220.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Created by Mridula on 1/15/2016.
 */
@Autonomous(name = "AUTO Blue2 -> Park", group = "Swerve Examples")
@Disabled

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
        driveStraight(112, Constants.BACKWARDS, false);
        turnTo(0);
        driveStraight(100, Constants.BACKWARDS, false);
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
