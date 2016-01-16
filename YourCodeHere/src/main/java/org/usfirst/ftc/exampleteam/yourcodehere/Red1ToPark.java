package org.usfirst.ftc.exampleteam.yourcodehere;
import org.swerverobotics.library.interfaces.Autonomous;
/*
	Autonomous program turns 90 degrees.
*/
@Autonomous(name = "AUTO Red1 -> Park", group = "Swerve Examples")
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

        wait(100);
        driveStraight(270, Constants.BACKWARDS, false);
        turnTo(135);
        driveStraight(77, Constants.BACKWARDS, false);
        wait(500);
        HikerDropper.deploy();
        wait(2000);
        driveStraight(77, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        driveStraight(77, Constants.BACKWARDS * 0.4, false);
    }
}
