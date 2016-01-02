package org.usfirst.ftc.exampleteam.yourcodehere;
import org.swerverobotics.library.interfaces.Autonomous;
/*
	Autonomous program starts at the corner point at the parking zone
*/
//TODO For now, this program is purposed for the red side.  We will change it to include other game configuration options later.
@Autonomous(name = "AUTORedToPark", group = "Swerve Examples")
public class Autonomous1 extends MasterAutonomous
{

    @Override
    protected void main() throws InterruptedException{

        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();

        driveDistance(255, Constants.FORWARDS);
        turnLeft(229,1.0);
        driveDistance(90, Constants.BACKWARDS);
        this.HikerDropper.setPosition(Constants.HIKER_DROPPER_DEPLOYED);
        wait(2300);
        driveDistance(40, Constants.FORWARDS);
        this.HikerDropper.setPosition(Constants.HIKER_DROPPER_NOTDEPLOYED);
        wait(1000);
        driveDistance(40, Constants.BACKWARDS);
    }
}
