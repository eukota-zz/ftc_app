package org.usfirst.ftc.exampleteam.yourcodehere;
import org.swerverobotics.library.interfaces.Autonomous;
/*
	Autonomous program turns 90 degrees.
*/
@Autonomous(name = "AUTO Testing", group = "Swerve Examples")
public class AutoTest extends MasterAutonomous
{
    @Override
    protected void main() throws InterruptedException
    {
        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();

        initializeServoPositions();

        wait(1000);
        driveStraight(272, Constants.BACKWARDS);
        turnTo(225);
        driveStraight(77, Constants.BACKWARDS);
        wait(2000);
        HikerDropper.deploy();
        wait(500);
        driveStraight(1, Constants.SLOW_FORWARDS);
        wait(500);
        driveStraight(1, Constants.SLOW_BACKWARDS);
        wait(2000);
        driveStraight(77, Constants.FORWARDS);
        HikerDropper.retract();
        driveStraight(77, Constants.BACKWARDS);
    }
}
