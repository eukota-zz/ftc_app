package org.swerverobotics.library.examples;

import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

/*
 * Monitors the voltage of a battery
 * when plugged into a motor controller
 * with no load per Sig's request
 */
@TeleOp(name = "No Load Motor Controller Test")
public class NoLoadMotorControllerTest extends SynchronousOpMode
{
    VoltageSensor voltageSensor = null;

    @Override
    public void main() throws InterruptedException
    {
        voltageSensor = hardwareMap.voltageSensor.iterator().next();

        composeDashboard();

        waitForStart();

        while (this.opModeIsActive())
        {
            telemetry.update();
            idle();
        }
    }

    void composeDashboard()
    {
        telemetry.addLine
                (
                        this.telemetry.item("Voltage: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(voltageSensor.getVoltage());
                            }
                        })
                );

    }

    public String formatNumber(double number)
    {
        return String.format("%.3f", number);
    }
}