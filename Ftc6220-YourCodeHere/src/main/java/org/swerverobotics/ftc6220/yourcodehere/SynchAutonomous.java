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

        if (autoWaitAtStart)
        {
            pause(8000);
        }
        else
        {
            pause(100);
        }

        //Red 1
        if ((autoStartingPlace == Constants.START_POSITION_1) && (Constants.RED == autoSide))
        {
            setAutoStartPosition(90);

            driveStraight(272, Constants.BACKWARDS, false);
            turnTo(135);
            driveStraight(70, Constants.BACKWARDS, false);
            pause(100);
            HikerDropper.slowToggle();
            pause(1000);
            driveStraight(70, Constants.FORWARDS * 0.4, false);
            HikerDropper.slowToggle();
            driveStraight(70, Constants.BACKWARDS * 0.4, false);
        }
        //Red 2
        else if ((autoStartingPlace == Constants.START_POSITION_2) && (Constants.RED == autoSide))
        {
            setAutoStartPosition(135);

            driveStraight(110, Constants.BACKWARDS, false);
            turnTo(180);
            driveStraight(91, Constants.BACKWARDS, false);
            turnTo(220);
            driveStraight(59, Constants.BACKWARDS, false);
            pause(400);
            HikerDropper.slowToggle();
            pause(400);
            HikerDropper.slowToggle();
            driveStraight(59, Constants.FORWARDS, false);
            //driveStraight(56, Constants.BACKWARDS * 0.4, false);
            turnTo(135);
            pause(500);
            driveStraight(65, Constants.FORWARDS, false);
            turnTo(88);
            pause(1400);
            driveStraight(60, Constants.FORWARDS, false);
            driveStraight(65, Constants.FORWARDS, true);
        }
        //Blue 1
        else if ((autoStartingPlace == Constants.START_POSITION_1) && (Constants.BLUE == autoSide))
        {
            setAutoStartPosition(90);

            driveStraight(260, Constants.BACKWARDS, false);
            turnTo(45);
            driveStraight(70, Constants.BACKWARDS, false);
            pause(100);
            HikerDropper.slowToggle();
            pause(1000);
            driveStraight(70, Constants.FORWARDS * 0.4, false);
            HikerDropper.slowToggle();
            driveStraight(70, Constants.BACKWARDS * 0.4, false);
        }
        //Blue 2
        else if ((autoStartingPlace == Constants.START_POSITION_2) && (Constants.BLUE == autoSide))
        {
            setAutoStartPosition(45);

            driveStraight(112, Constants.BACKWARDS, false);
            turnTo(0);
            driveStraight(100, Constants.BACKWARDS, false);
            turnTo(-45);
            driveStraight(56, Constants.BACKWARDS, false);
            pause(1400);
            HikerDropper.slowToggle();
            pause(1000);
            driveStraight(56, Constants.FORWARDS * 0.4, false);
            HikerDropper.slowToggle();
            driveStraight(56, Constants.BACKWARDS * 0.4, false);
        }

    }
}
