package org.swerverobotics.ftc8923.yourcodehere;

import org.json.JSONException;
import org.json.JSONObject;
import org.usfirst.ftc.exampleteam.yourcodehere.Master;

public class JSONTest extends Master
{
    JSONObject colorSensorValueStorage = new JSONObject();

    public void setCalibratedBlue() throws JSONException
    {
        colorSensorValueStorage.put("Calibrated Blue", colorSensorBeacon.blue());
        calibratedBlue = colorSensorBeacon.blue();
    }

    public int receiveCalibratedBlue() throws JSONException
    {
        return colorSensorValueStorage.getInt("Calibrated Blue");
    }
}
