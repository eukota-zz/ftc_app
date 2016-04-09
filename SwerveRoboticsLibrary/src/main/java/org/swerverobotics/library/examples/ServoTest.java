package org.swerverobotics.library.examples;

import com.qualcomm.robotcore.hardware.Servo;

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
    Servo servo = null;

    @Override
    public void main() throws InterruptedException
    {
        // The configuration file needs to have a servo named "servo"
        servo = hardwareMap.servo.get("servo");

        waitForStart();

        while (this.opModeIsActive())
        {
            if (updateGamepads())
            {
                // This converts the joystick range of
                // [-1.0, 1.0] to the servo range of [0.0, 1.0]
                double pos = (gamepad1.left_stick_y) / 2.0;
                servo.setPosition(pos);
            }

            telemetry.update();
            idle();
        }
    }

    void composeDashboard()
    {
        telemetry.addLine
                (
                        this.telemetry.item("Position: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return formatNumber(servo.getPosition());
                            }
                        })
                );

    }

    public String formatNumber(double number)
    {
        return String.format("%.3f", number);
    }
}