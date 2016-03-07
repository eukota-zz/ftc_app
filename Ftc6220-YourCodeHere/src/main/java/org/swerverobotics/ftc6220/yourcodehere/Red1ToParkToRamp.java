package org.swerverobotics.ftc6220.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;

import org.swerverobotics.library.interfaces.Disabled;

/*
	Autonomous program turns 90 degrees.
*/
@Autonomous(name = "AUTO Red1 -> Park -> Ramp", group = "Swerve Examples")
@Disabled
public class Red1ToParkToRamp extends MasterAutonomous
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

        pause(100);
        driveStraight(270, Constants.BACKWARDS, false);
        turnTo(-45);
        driveStraight(78, Constants.BACKWARDS, false);
        pause(500);
        HikerDropper.deploy();
        pause(2000);
        driveStraight(78, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        turnTo(225);
        driveStraight(74, Constants.FORWARDS, false);
        turnTo(180);
        driveStraight(60, Constants.FORWARDS, false);
        driveStraight(65, Constants.FORWARDS, true);
    }
}
