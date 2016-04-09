package org.swerverobotics.library.examples;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

/*
 * Tests servos to check functionality
 * A single servo is controlled by a joystick
 */
@TeleOp(name = "Servo Test")
public class ServoTest extends SynchronousOpMode
{
    @Override
    public void main() throws InterruptedException
    {
        waitForStart();

        while (this.opModeIsActive())
        {
            if (updateGamepads())
            {

            }

            telemetry.update();
            idle();
        }
    }

    void composeDashboard()
    {
        telemetry.addLine
                (
                        this.telemetry.item("Foo: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return 0;
                            }
                        }),
                        this.telemetry.item("Bar: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return 0;
                            }
                        })
                );

    }

    String formatConfig (int config) { return String.format("0x%04X", config); }

    public String formatNumber(double number)
    {
        return String.format("%.2f", number);
    }
}