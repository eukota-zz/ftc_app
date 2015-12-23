package org.usfirst.ftc.exampleteam.yourcodehere;


import org.swerverobotics.library.interfaces.Autonomous;

/*
	Autonomous program starts at the corner pointe at the parking zone
*/
@Autonomous(name = "AUTO Red -> Park", group = "Swerve Examples")
public class RedToPark extends  DupupodAutoOpMode{

    @Override
    protected void main() throws InterruptedException{

        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();

        retractHikerDropper();
        retractRightZiplineHitter();
        retractLeftZiplineHitter();
        driveDistance(255, FORWARDS);
        turnLeft(229,1.0);
        driveDistance(90, BACKWARDS);
        deployHikerDropper();
        wait(2300);
        driveDistance(40, FORWARDS);
        retractHikerDropper();
        wait(1000);
        driveDistance(40, BACKWARDS);
    }
}