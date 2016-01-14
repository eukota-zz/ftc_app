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
        driveDistance(270,Constants.BACKWARDS);
        turnTo(225);
        driveDistance(65,Constants.BACKWARDS);
        wait(1000);
        HikerDropper.deploy();
        wait(1000);
        driveDistance(65, Constants.FORWARDS);
        HikerDropper.retract();
        driveDistance(65,Constants.BACKWARDS);
    }
}
