package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/**
 * Created by Mridula on 1/15/2016.
 */
    @Autonomous(name = "AUTO WAIT_Red2 -> Park -> Ramp", group = "Swerve Examples")
    @Disabled
    public class WAIT_Red2ToParkToRamp extends MasterAutonomous
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

            wait(10000);
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

