package org.usfirst.ftc.exampleteam.yourcodehere;

import org.swerverobotics.library.interfaces.IFunc;

/*
 * Skeleton program to be used for specific autonomous programs
 * Drives to beacon repair zone
 * Presses beacon button
 * Dumps climbers into basket
 * Drives into floor goal and triggers low zipliner
 */
public class BeaconClimberZiplinerSkeleton extends Master8923Autonomous
{
    // TODO Change this
    int FOO = 1;

    @Override public void main() throws InterruptedException {}

    public void initHardware() throws InterruptedException
    {
        robotInit();
        configureTelemtry();
    }

    public void configureTelemtry()
    {
        // Left drive motor info
        telemetry.addLine
                (
                        this.telemetry.item("Left Power:", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return motorLeft.getPower();
                            }
                        }),
                        this.telemetry.item("Left Position: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return motorLeft.getCurrentPosition();
                            }
                        })
                );

        // Right drive motor info
        telemetry.addLine
                (
                        this.telemetry.item("Right Power: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return motorRight.getPower();
                            }
                        }),
                        this.telemetry.item("Right Position: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return motorRight.getCurrentPosition();
                            }
                        })
                );

        // Light sensor info
        telemetry.addLine
                (
                        this.telemetry.item("Front light sensor: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return lightSensorFront.getLightDetected();
                            }
                        }),
                        this.telemetry.item("Back light sensor: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return lightSensorBack.getLightDetected();
                            }
                        })
                );

        telemetry.addLine
                (
                        this.telemetry.item("Ultrasonic: ", new IFunc<Object>() {
                            @Override
                            public Object value() {
                                return ultrasonicSensor.getUltrasonicLevel();
                            }
                        })
                );
    }
    
}
