package org.swerverobotics.ftc6220.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;

/*
	Autonomous program turns 90 degrees.
*/
@Autonomous(name = "AUTO Everything", group = "Swerve Examples")

public class SynchAutonomous extends MasterAutonomous
{
    @Override
    protected void main() throws InterruptedException
    {
        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();

        initializeServoPositions();

        //Red 1
        if ((autoStartingPlace == Constants.START_POSITION_1) && (Constants.RED == autoSide))
        {
            setAutoStartPosition(90);

            if (autoWaitAtStart)
            {
                pause(10000);
            }
            else
            {
                pause(2000);
            }

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
        //Red 2
        else if ((autoStartingPlace == Constants.START_POSITION_2) && (Constants.RED == autoSide))
        {
            setAutoStartPosition(135);

            if (autoWaitAtStart)
            {
                pause(10000);
            }
            else
            {
                pause(100);
            }

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
        //Blue 1
        else if ((autoStartingPlace == Constants.START_POSITION_1) && (Constants.BLUE == autoSide))
        {
            setAutoStartPosition(90);

            if (autoWaitAtStart)
            {
                pause(10000);
            }
            else
            {
                pause(2000);
            }

            driveStraight(259, Constants.BACKWARDS, false);
            turnTo(45);
            driveStraight(75, Constants.BACKWARDS, false);
            pause(500);
            HikerDropper.slowToggle();
            pause(4000);
            driveStraight(75, Constants.FORWARDS * 0.4, false);
            HikerDropper.slowToggle();
            driveStraight(75, Constants.BACKWARDS * 0.4, false);
        }
        //Blue 2
        else if ((autoStartingPlace == Constants.START_POSITION_2) && (Constants.BLUE == autoSide))
        {
            setAutoStartPosition(45);

            if (autoWaitAtStart)
            {
                pause(10000);
            }
            else
            {
                pause(100);
            }

            driveStraight(112, Constants.BACKWARDS, false);
            turnTo(0);
            driveStraight(100, Constants.BACKWARDS, false);
            turnTo(-45);
            driveStraight(56, Constants.BACKWARDS, false);
            pause(500);
            HikerDropper.slowToggle();
            pause(3000);
            driveStraight(56, Constants.FORWARDS * 0.4, false);
            HikerDropper.slowToggle();
            driveStraight(56, Constants.BACKWARDS * 0.4, false);
        }

    }
}
