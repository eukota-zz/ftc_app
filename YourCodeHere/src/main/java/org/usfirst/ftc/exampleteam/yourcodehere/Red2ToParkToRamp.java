package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Created by Cole on 1/14/2016.
 */
@Autonomous(name = "AUTO Red2 -> Park -> Ramp", group = "Swerve Examples")
@Disabled
public class Red2ToParkToRamp extends MasterAutonomous
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

        wait(100);
        driveStraight(125, Constants.BACKWARDS, false);
        turnTo(180);
        driveStraight(85, Constants.BACKWARDS, false);
        turnTo(225);
        driveStraight(65, Constants.BACKWARDS, false);
        wait(500);
        HikerDropper.deploy();
        wait(2000);
        driveStraight(65, Constants.FORWARDS * 0.4, false);
        HikerDropper.retract();
        turnTo(135);
        driveStraight(74, Constants.FORWARDS, false);
        turnTo(90);
        driveStraight(60, Constants.FORWARDS, false);
        driveStraight(65, Constants.FORWARDS, true);
    }
}
