package org.swerverobotics.ftc6220.yourcodehere;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.LED;

/**
 * Created by Mridula on 4/11/2016.
 */
public class DriverLEDManager
{
    LED greenLight;
    LED blueLight;
    LED yellowLight;

    public DriverLEDManager(OpMode opMode)
    {
        greenLight = opMode.hardwareMap.led.get("GreenLight");

        blueLight = opMode.hardwareMap.led.get("BlueLight");

        yellowLight = opMode.hardwareMap.led.get("YellowLight");
    }

    public void turnOnGreenLight()
    {
        greenLight.enable(true);
        blueLight.enable(false);
        yellowLight.enable(false);
    }

    public void turnOnBlueLight()
    {
        greenLight.enable(false);
        blueLight.enable(true);
        yellowLight.enable(false);
    }

    public void turnOnYellowLight()
    {
        greenLight.enable(false);
        blueLight.enable(false);
        yellowLight.enable(true);
    }
}
