package org.usfirst.ftc.exampleteam.yourcodehere;

import android.content.SharedPreferences;

import com.qualcomm.robotcore.hardware.ColorSensor;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * A skeletal example of a do-nothing first OpMode. Go ahead and change this code
 * to suit your needs, or create sibling OpModes adjacent to this one in the same
 * Java package.
 */
@Autonomous(name="colorSensorCalibration")
public class colorSensorCalibration extends SynchronousOpMode {

    ColorSensor colorSensorBeacon = null;
    int blue;
    int red;

    @Override
    public void main() throws InterruptedException
    {
        calibrateColorSensor();
    }

    public void calibrateColorSensor() {
        try
        {
            boolean Toggle = true;
            while (Toggle)
            {
                if (this.updateGamepads())
                {
                    if (gamepad1.x)
                    {
                        blue = colorSensorBeacon.blue();
                        telemetry.clearDashboard();
                        telemetry.addLine(
                                telemetry.item("Blue: ", new IFunc<Object>() {
                                    @Override
                                    public Object value() {
                                        return blue;
                                    }
                                }));
                        Toggle = false;
                    }


                }
                if (gamepad1.b)
                {
                    red = colorSensorBeacon.red();
                    telemetry.clearDashboard();
                    telemetry.addLine(
                            telemetry.item("Red: ", new IFunc<Object>() {
                                @Override
                                public Object value() {
                                    return red;
                                }
                            }));
                    Toggle = false;
                }
            }
        }
        catch (NullPointerException Not)
        {

        }
    }
}