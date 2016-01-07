package org.usfirst.ftc.exampleteam.yourcodehere;
import org.swerverobotics.library.interfaces.Autonomous;
/*
	Autonomous program turns 90 degrees.
*/
//This is a static turning loop testing program
@Autonomous(name = "AUTO Testing", group = "Swerve Examples")
public class AutoTest extends MasterAutonomous
{

    double targetAngle = 90;
    double offset = 0;
    double Δϴ = 0;
    //WHOA! A ϴ!!!!!!!
    double power = 0;

    PIDFilter filter = new PIDFilter( 0.8, 0.1, 0.0 );
    
    @Override
    protected void main() throws InterruptedException{

        //Initialize our hardware
        initialize();

        // Wait until we've been given the ok to go
        waitForStart();

        ///TODO Encapsulate and add a termination condition
        while(true)
        {
            
            filter.update();
            Δϴ = targetAngle - getCurrentGlobalOrientation();

            //check 360-0 case
            if (Math.abs(filter.dV) > 180)
            {
                offset -= Math.signum(filter.dV) * 360;
            }
            Δϴ += offset;
            
            //set filtered motor powers
            power = filter.getFilteredValue();
            driveWheels(-power, power);

            //roll records
            filter.roll(Δϴ);

            idle();
        }

    }
}
