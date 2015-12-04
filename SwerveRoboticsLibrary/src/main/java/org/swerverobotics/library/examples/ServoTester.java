    package org.swerverobotics.library.examples;

    import com.qualcomm.robotcore.hardware.Servo;
    import org.swerverobotics.library.SynchronousOpMode;
    import org.swerverobotics.library.interfaces.IFunc;
    import org.swerverobotics.library.interfaces.TeleOp;

    /*
     * This file will test 1 servo based on joystick input
     */
    @TeleOp(name="DrywServo", group="Swerve Examples")
    public class ServoTester extends SynchronousOpMode {
        // Declare servo
        Servo CollectorServo = null;
        Servo LeftZiplineHitter = null;


        @Override
        protected void main() throws InterruptedException {
            // Initialize servo
            this.CollectorServo = this.hardwareMap.servo.get("CollectorServo");
            this.LeftZiplineHitter = this.hardwareMap.servo.get("LeftZiplineHitter");

            // Configure dashboard
            this.telemetry.addLine
                    (
                            this.telemetry.item("CollectorServo:", new IFunc<Object>() {
                                @Override
                                public Object value() {
                                    return CollectorServo.getPosition();
                                }
                            })
                    );
            this.telemetry.addLine
                    (
                            this.telemetry.item("LeftZiplineHitter:", new IFunc<Object>() {
                                @Override
                                public Object value() {
                                    return LeftZiplineHitter.getPosition();
                                }
                            })
                    );

            // Wait until we've been given the ok to go
            this.waitForStart();

            // Enter a loop processing all the input we receive
            while (this.opModeIsActive()) {
                if (this.updateGamepads()) {
                    if (this.gamepad1.a)
                    {
                        CollectorServo.setPosition(0.0);
                        this.telemetry.update();
                    }
                    else
                    {
                        CollectorServo.setPosition(0.5);
                        this.telemetry.update();
                    }

                    if (this.gamepad1.right_bumper)
                    {
                       LeftZiplineHitter.setPosition(90);
                       this.telemetry.update();
                    }

                    if (this.gamepad1.left_bumper)
                    {
                        LeftZiplineHitter.setPosition(-90);
                        this.telemetry.update();
                    }
                }

                /*CollectorServo.setPosition(0);
                this.telemetry.update();
                Thread.sleep(1000);
                CollectorServo.setPosition(255);
                this.telemetry.update();
                Thread.sleep(1000);
                CollectorServo.setPosition(0);
                this.telemetry.update();
                Thread.sleep(1000);
                CollectorServo.setPosition(-255);
                this.telemetry.update();
                Thread.sleep(1000);*/


                // Emit the latest telemetry and wait, letting other things run
                this.telemetry.update();
                this.idle();
            }
        }

    }