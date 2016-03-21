package org.swerverobotics.ftc6220.yourcodehere;

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

        pause(100);
        driveStraight(130, Constants.BACKWARDS, false);
        turnTo(180);
        driveStraight(105, Constants.BACKWARDS, false);
        turnTo(225);
        driveStraight(56, Constants.BACKWARDS, false);
        pause(500);
        HikerDropper.slowToggle();
        pause(3000);
        driveStraight(56, Constants.FORWARDS * 0.4, false);
        HikerDropper.slowToggle();
        driveStraight(56, Constants.BACKWARDS * 0.4, false);
    }
}
