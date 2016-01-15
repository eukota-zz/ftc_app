package org.usfirst.ftc.exampleteam.yourcodehere;
import org.swerverobotics.library.interfaces.Autonomous;
/*
	Autonomous program turns 90 degrees.
*/
@Autonomous(name = "AUTO Red -> Park -> Ramp", group = "Swerve Examples")
public class RedToParkRamp extends MasterAutonomous
{
    @Override
    protected void main() throws InterruptedException
    {
        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();

        initializeServoPositions();

        wait(100);
        driveStraight(273, Constants.BACKWARDS, false);
        turnTo(-45);
        driveStraight(77, Constants.BACKWARDS, false);
        wait(500);
        HikerDropper.deploy();
        wait(2000);
        driveStraight(77, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        turnTo(180);
        driveStraight(88, Constants.FORWARDS, false);
        turnTo(0);
        driveStraight(45, Constants.FORWARDS, false);
        driveStraight(80, Constants.FORWARDS, true);
    }
}
