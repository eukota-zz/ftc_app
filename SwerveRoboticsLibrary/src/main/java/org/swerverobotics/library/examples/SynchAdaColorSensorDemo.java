package org.swerverobotics.library.examples;

import com.qualcomm.hardware.AdafruitI2cColorSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.LED;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.Disabled;
import org.swerverobotics.library.interfaces.IFunc;
import org.swerverobotics.library.interfaces.TeleOp;

/**
 * SynchLEDDemo is a short demo on using the Adafruit Color Sensor module.
 * It assumes that:
 * an Adafruit Color Sensor is connected to a Core Device Interface I2C port and called "color"
 * the led for the AdaFruit is connected to a Core Device Interface digital port and called "led"
 */
@TeleOp(name = "AdaColorSensor", group = "Swerve Examples")
public class SynchAdaColorSensorDemo extends SynchronousOpMode {

    ColorSensor sensorRGB = null;
    DigitalChannel sensorLED = null;

    final boolean LED_ON = true;
    final boolean LED_OFF = false;

    @Override
    public void main() throws InterruptedException {

        // get a reference to our ColorSensor object.
        sensorRGB = hardwareMap.colorSensor.get("color");

        // get a reference to the LED on the color sensor board
        sensorLED = hardwareMap.digitalChannel.get("led");
        sensorLED.setMode(DigitalChannelController.Mode.OUTPUT);
        sensorLED.setState(LED_ON);

        // Set up our dashboard computations
        composeDashboard();

        // Wait until we're told to go
        waitForStart();

        // Loop and update the dashboard
        while (this.opModeIsActive()) {

            if (this.updateGamepads()) {
                if (this.gamepad1.a) {
                    //toggle LED state
                    sensorLED.setState( !sensorLED.getState() );
                }
            }

            telemetry.update();

            idle();
        }
    }

    void composeDashboard() {
        telemetry.addLine(
                telemetry.item("loop count: ", new IFunc<Object>() {
                    @Override
                    public Object value() {
                        return getLoopCount();
                    }
                }));
        telemetry.addLine(
                telemetry.item("color: ", new IFunc<Object>() {
                    @Override
                    public Object value() {
                        return "r: " + sensorRGB.red() + " g: " + sensorRGB.green() + " b: " + sensorRGB.blue();
                    }
                })
        );
        telemetry.addLine(
                telemetry.item("led: ", new IFunc<Object>() {
                    @Override
                    public Object value() {
                        return sensorLED.getState();
                    }
                })
        );

    }

}
