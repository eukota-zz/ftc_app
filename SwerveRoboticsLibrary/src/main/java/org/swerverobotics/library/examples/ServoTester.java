package org.swerverobotics.library.examples;

import com.qualcomm.robotcore.hardware.Servo;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Disabled;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

/*
 * This file will test 1 servo based on joystick input
 */
@TeleOp(name="DrywServo", group="Swerve Examples")
public class ServoTester extends SynchronousOpMode
{
    // Declare servo
    Servo servo1 = null;

    @Override protected void main() throws InterruptedException
    {
        // Initialize servo
        this.servo1 = this.hardwareMap.servo.get("servo1");

        // Configure dashboard
        this.telemetry.addLine
                (
                        this.telemetry.item("Servo1:", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return servo1.getPosition();
                            }
                        })
                );
        
        // Wait until we've been given the ok to go
        this.waitForStart();
        
        // Enter a loop processing all the input we receive
        while (this.opModeIsActive())
        {
            if (this.updateGamepads()) {
                if (this.gamepad1.a) {
                    servo1.setPosition(0);
                    this.telemetry.update();
                }

                if (this.gamepad1.b) {
                    servo1.setPosition(127);
                    this.telemetry.update();
                }

                if (this.gamepad1.x) {
                    servo1.setPosition(.5);
                    this.telemetry.update();
                }
            }

            /*servo1.setPosition(0);
            this.telemetry.update();
            Thread.sleep(1000);
            servo1.setPosition(255);
            this.telemetry.update();
            Thread.sleep(1000);
            servo1.setPosition(0);
            this.telemetry.update();
            Thread.sleep(1000);
            servo1.setPosition(-255);
            this.telemetry.update();
            Thread.sleep(1000);*/


            // Emit the latest telemetry and wait, letting other things run
            this.telemetry.update();
            this.idle();
        }
    }
}
