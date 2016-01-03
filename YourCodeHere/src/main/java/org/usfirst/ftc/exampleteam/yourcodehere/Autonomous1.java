package org.usfirst.ftc.exampleteam.yourcodehere;
import org.swerverobotics.library.interfaces.Autonomous;
/*
	Autonomous program starts at the corner point at the parking zone
*/
//This is a static turning loop testing program
@Autonomous(name = "TEST Static Loop", group = "Swerve Examples")
public class Autonomous1 extends MasterAutonomous
{

    double angle = 90;
    double offset = 0;
    double diff = 0;
    double lastDiffs[] = {0.0,0.0};
    double dV = 0;
    double power = 0;
    double anglePowerSwitch = 10;

    @Override
    protected void main() throws InterruptedException{

        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();


        while(true) {
            diff = angle - getCurrentOrientation();
            dV = diff - lastDiffs[0];

            //check 360-0 case
            if (Math.abs(dV) > 180) {
                offset -= Math.signum(dV) * 360;
            }
            diff += offset;


            //turn the correct direction
            int direction = (int) Math.signum(diff);
            //
            if (diff < -anglePowerSwitch) {
                power = direction;
            } else if (diff > anglePowerSwitch) {
                power = -direction;
            } else {
                power = -1 * diff / anglePowerSwitch;
            }

            driveSmallWheels(-power, power);

            //roll records
            lastDiffs[1] = lastDiffs[0];
            lastDiffs[0] = diff;

            idle();
        }

        //redto park old code
         /*driveDistance(255, Constants.FORWARDS);
        turnLeft(229,1.0);
        driveDistance(90, Constants.BACKWARDS);
        this.HikerDropper.setPosition(Constants.HIKER_DROPPER_DEPLOYED);
        wait(2300);
        driveDistance(40, Constants.FORWARDS);
        this.HikerDropper.setPosition(Constants.HIKER_DROPPER_NOTDEPLOYED);
        wait(1000);
        driveDistance(40, Constants.BACKWARDS);*/

    }
}
