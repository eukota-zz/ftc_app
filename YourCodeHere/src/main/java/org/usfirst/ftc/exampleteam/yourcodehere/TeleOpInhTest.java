package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

import java.util.HashMap;


/*
    This is a teleop program uses the new inheritance structure recommended by Darrel
	It should be operationally identical to "Synch6220TeleOp", with the exception of real toggles,
	AS WELL AS adjusted driver 2 controls
	
	CURRENT CONTROLS:
		PAD1-
			A::Field Mode           [Set]
			B::PreRampMode			[Set]
			Y::RampMode				[Set]
			L_Stick::Left Drive
			R_Stick::Right Drive
			R_Trigger::Both Climbers
		PAD2-
			X::Left Zipline Hitter  [Toggle]
			B::Right Zipline Hitter [Toggle]
			Y::Hiker Dropper        [Toggle]
 */
@TeleOp(name = "TELE 6220 TeleOp (INH)", group = "Swerve Examples")
public class TeleOpInhTest extends DupupodTeleOpMode{

    @Override
    protected void main() throws InterruptedException
    {
        //temporary variables until we make the manipulator classes
        boolean[] zipLineDeployed = {false,false};//left,right
        boolean[] toggleBtnLCA = {false,false,false};//b,x,y
        boolean hikerDropperDeployed = false;
		
        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();

        // Enter a loop processing all the input we receive
        while (opModeIsActive()) {
            if (updateGamepads()) {

                //allow the deriver to move the robot
                driveRobot(gamepad1, currentDriveMode, gamepad1.right_bumper);

                //        MANIPULATOR CONTROL        //

                //toggles

                //hiker dropper
                if (gamepad2.y && !toggleBtnLCA[2]){
                    hikerDropperDeployed = !hikerDropperDeployed;
                }
                toggleBtnLCA[2] = gamepad2.y;

                if (hikerDropperDeployed){deployHikerDropper();}
                else{retractHikerDropper();}

                //zipliners
                if (gamepad2.b && !toggleBtnLCA[1]){
                    zipLineDeployed[1] = !zipLineDeployed[1];
                }
                toggleBtnLCA[1] = gamepad2.b;
                if (gamepad2.x && !toggleBtnLCA[0]){
                    zipLineDeployed[0] = !zipLineDeployed[0];
                }
                toggleBtnLCA[0] = gamepad2.x;

                if (zipLineDeployed[0]){ deployLeftZiplineHitter(); }
                else{ retractLeftZiplineHitter(); }
                if (zipLineDeployed[1]){deployRightZiplineHitter();}
                else{retractRightZiplineHitter();}

            }
        }
        idle();

    }
}
