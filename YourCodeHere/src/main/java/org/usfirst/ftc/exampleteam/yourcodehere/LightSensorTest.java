package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.LightSensor;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.Disabled;
import org.swerverobotics.library.interfaces.IFunc;

/**
 * Robot starts on blue side, goes to beacon,
 * presses beacon button, and scores climber
 */
@Autonomous(name="LightSensorTest")

public class LightSensorTest extends SynchronousOpMode
{
    LightSensor lightSensorFront = null;
    LightSensor lightSensorBack = null;

    @Override public void main() throws InterruptedException
    {
        lightSensorFront = hardwareMap.lightSensor.get("lightSensorFront");
        lightSensorBack = hardwareMap.lightSensor.get("lightSensorBack");

        // Set up our dashboard computations
        composeDashboard();

        waitForStart();

        while(opModeIsActive())
        {
            telemetry.update();

            idle();
        }
    }

    void composeDashboard() {
        telemetry.addLine(
                telemetry.item("Back: ", new IFunc<Object>() {
                    @Override
                    public Object value() {
                        return lightSensorBack.getLightDetected();
                    }
                }));
        telemetry.addLine(
                telemetry.item("Front: ", new IFunc<Object>() {
                    @Override
                    public Object value() {
                        return lightSensorFront.getLightDetected();
                    }
                }));

    }

}
