package org.swerverobotics.ftc6220.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/*
	Autonomous program turns 90 degrees.
*/
@Autonomous(name = "AUTO Red 1 -> Park", group = "Swerve Examples")
@Disabled
public class Red1ToPark extends MasterAutonomous
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

        pause(2000);
        driveStraight(279, Constants.BACKWARDS, false);
        turnTo(135);
        driveStraight(75, Constants.BACKWARDS, false);
        pause(500);
        HikerDropper.slowToggle();
        pause(4000);
        driveStraight(75, Constants.FORWARDS * 0.4, false);
        HikerDropper.slowToggle();
        driveStraight(75, Constants.BACKWARDS * 0.4, false);

    }
}
