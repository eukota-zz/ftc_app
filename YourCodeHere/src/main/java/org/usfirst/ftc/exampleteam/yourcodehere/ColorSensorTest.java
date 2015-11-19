package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;

/**
 * Robot starts on blue side, goes to beacon,
 * presses beacon button, and scores climber
 */
@Autonomous(name="ColorSensorTest")
public class ColorSensorTest extends SynchronousOpMode
{
    // Declare sensors
    ColorSensor colorSensorBeacon = null;
    DigitalChannel LED = null;

    @Override public void main() throws InterruptedException
    {
        // Initialize sensors
        colorSensorBeacon = hardwareMap.colorSensor.get("colorSensorBeacon");
        LED = hardwareMap.digitalChannel.get("LED");


        waitForStart();



        while(this.opModeIsActive())
        {
            FollowLine();
        }
    }

    public void FollowLine() throws InterruptedException
    {


        while (opModeIsActive())
        {
            this.LED.setState(false);
            telemetry.addData("red", this.colorSensorBeacon.red());
            telemetry.addData("green", this.colorSensorBeacon.green());
            telemetry.addData("blue",  this.colorSensorBeacon.blue());
            telemetry.addData("alpha", this.colorSensorBeacon.alpha());
            telemetry.update();
            this.idle();
        }
    }
}
