package org.usfirst.ftc.exampleteam.yourcodehere;


import org.swerverobotics.library.interfaces.Autonomous;

/*
	Autonomous program used to test encoder driving
	Drives 150cm straight forward
*/
@Autonomous(name = "AUTO Enc. Test", group = "Swerve Examples")
public class EncoderTest extends  DupupodAutoOpMode{

    @Override
    protected void main() throws InterruptedException{

        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();

        //drive forward 150 centimeters
        driveDistance(150,1);
    }
}
