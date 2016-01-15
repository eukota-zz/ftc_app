    package org.swerverobotics.library.examples;

    import com.qualcomm.robotcore.hardware.Servo;
    import org.swerverobotics.library.SynchronousOpMode;
    import org.swerverobotics.library.interfaces.*;

    /*
     * This file will test 1 servo based on joystick input
     */
    @TeleOp(name="DrywServo", group="Swerve Examples")
    @Disabled
    public class ServoTester extends SynchronousOpMode {
        // Declare servo
        Servo CollectorServo = null;
        Servo LeftZiplineHitter = null;
        Servo RightZiplineHitter = null;
        double RightServoPostion = 0;


        @Override
        protected void main() throws InterruptedException {
            // Initialize servo
            this.CollectorServo = this.hardwareMap.servo.get("CollectorServo");
            this.LeftZiplineHitter = this.hardwareMap.servo.get("LeftZiplineHitter");
            this.RightZiplineHitter = this.hardwareMap.servo.get("RightZiplineHitter");
            RightZiplineHitter.setDirection(Servo.Direction.REVERSE);
            RightZiplineHitter.setPosition(RightServoPostion);

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
            this.telemetry.addLine
                    (
                            this.telemetry.item("RightZiplineHitter:", new IFunc<Object>() {
                                @Override
                                public Object value() {
                                    return RightZiplineHitter.getPosition();
                                }
                            })
                    );

            // Wait until we've been given the ok to go
            this.waitForStart();

            // Enter a loop processing all the input we receive
            while (this.opModeIsActive()) {
                if (this.updateGamepads()) {
                    if (this.gamepad2.a)
                    {
                        CollectorServo.setPosition(0.0);
                        this.telemetry.update();
                    }
                    else
                    {
                        CollectorServo.setPosition(0.5);
                        this.telemetry.update();
                    }

                    if (this.gamepad2.left_bumper)
                    {
                       LeftZiplineHitter.setPosition(90);
                       this.telemetry.update();
                    }

                    if ((this.gamepad2.left_trigger > 0.2))
                    {
                        LeftZiplineHitter.setPosition(-90);
                        this.telemetry.update();
                    }
                    //The RightZiplineHitter reads from (0-1), which is different than the LeftZiplineHitter(0-360)
                    if (this.gamepad2.right_bumper)
                    {
                        RightZiplineHitter.setPosition(0.75);
                        this.telemetry.update();
                    }

                    if ((this.gamepad2.right_trigger > 0.2))
                    {
                        RightZiplineHitter.setPosition(-0.75);
                        this.telemetry.update();
                    }
                    /*if (this.gamepad1.x)
                    {
                        RightServoPostion += 0.05;
                        RightZiplineHitter.setPosition(RightServoPostion);
                        this.telemetry.update();
                    }

                    if ((this.gamepad1.y))
                    {
                        RightServoPostion -= 0.05;
                        RightZiplineHitter.setPosition(RightServoPostion);
                        this.telemetry.update();
                    }*/

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