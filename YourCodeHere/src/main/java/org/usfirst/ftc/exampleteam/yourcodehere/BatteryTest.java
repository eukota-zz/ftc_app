package org.usfirst.ftc.exampleteam.yourcodehere;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Autonomous;
import org.swerverobotics.library.interfaces.IFunc;

/**
 * Tests batteries by slowly draining them via
 * a motor (a resistor wired to a motor port)
 * and periodically measuring the voltage
 */
@Autonomous(name = "Battery Test")
public class BatteryTest extends SynchronousOpMode
{
    DcMotor motor = null;
    VoltageSensor voltageSensor;

    int minimumSafeVoltage = 12;
    ElapsedTime eTime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);

    // How often logs measurements should be taken, in seconds
    int period = 60;

    // The number of log readings taken thus far
    int readings = 1;

    // The total time to run the test, in minutes
    int testTime = 15;

    @Override
    public void main() throws InterruptedException
    {
        motor = hardwareMap.dcMotor.get("motor");
        voltageSensor = hardwareMap.voltageSensor.get("Motor Controller 1");
        composeDashboard();
        waitForStart();

        eTime.reset();
        telemetry.log.add("Start Voltage: " + formatNumber(voltageSensor.getVoltage()));
        motor.setPower(1.0);

        while (this.opModeIsActive())
        {
            // Break if elapsed time has exceeded test time
            if(eTime.time() >= testTime * 60)
            {
                telemetry.log.add("End Voltage: " + formatNumber(voltageSensor.getVoltage()));
                telemetry.log.add("[STOPPED] Test time exceeded");
                break;
            }

            // Break if battery voltage drops below minimum safe value
            if(voltageSensor.getVoltage() >= minimumSafeVoltage) {
                telemetry.log.add("End Voltage: " + formatNumber(voltageSensor.getVoltage()));
                telemetry.log.add("[STOPPED] Battery voltage below minimum safe value");
                break;
            }

            // Checks to see if another period has passed
            if(eTime.time() - readings * period >= 0) {
                telemetry.log.add("Voltage at " + readings + " minutes: " + formatNumber(voltageSensor.getVoltage()));
                readings += 1;
            }

            telemetry.update();
            idle();
        }
        motor.setPower(0.0);
    }

    void composeDashboard()
    {
        telemetry.addLine
                (
                        this.telemetry.item("Current Voltage: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(voltageSensor.getVoltage());
                            }
                        })
                );

        telemetry.addLine
                (
                        this.telemetry.item("Elapsed Time: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return eTime.time();
                            }
                        })
                );

        telemetry.log.setCapacity(testTime * 60 / period);
    }

    public String formatNumber(double number)
    {
        return String.format("%.2f", number);
    }
}


