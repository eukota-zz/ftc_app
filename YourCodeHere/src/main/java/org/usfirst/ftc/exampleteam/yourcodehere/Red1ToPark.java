package org.usfirst.ftc.exampleteam.yourcodehere;
import org.swerverobotics.library.interfaces.Autonomous;
/*
	Autonomous program turns 90 degrees.
*/
@Autonomous(name = "AUTO Red 1 -> Park", group = "Swerve Examples")
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

        wait(2000);
        driveStraight(270, Constants.BACKWARDS, false);
        turnTo(135);
        driveStraight(73, Constants.BACKWARDS, false);
        wait(500);
        HikerDropper.deploy();
        wait(4000);
        driveStraight(73, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        driveStraight(73, Constants.BACKWARDS * 0.4, false);

    }
}
