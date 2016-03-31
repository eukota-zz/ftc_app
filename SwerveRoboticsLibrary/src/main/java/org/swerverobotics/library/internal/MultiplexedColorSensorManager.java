package org.swerverobotics.library.internal;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;

import org.swerverobotics.library.ClassFactory;
import org.swerverobotics.library.interfaces.TCA9548A;
import org.swerverobotics.library.interfaces.TCS34725;


/**
 * Created by Steve on 3/30/2016.
 */
public class MultiplexedColorSensorManager
{
    //the multiplexer we're controlling
    private TCA9548A multiplexer = null;
    private static final int NUM_CHANNELS = 8; //the multiplexer has 8 channels
    public static enum MULTIPLEXER_CHANNEL
    {
        CHANNEL0(0x00),
        CHANNEL1(0x01),
        CHANNEL2(0x02),
        CHANNEL3(0x03),
        CHANNEL4(0x04),
        CHANNEL5(0x05),
        CHANNEL6(0x06),
        CHANNEL7(0x07);

        //------------------------------------------------------------------------------------------
        public final int iVal;

        MULTIPLEXER_CHANNEL(int i) {
            this.iVal = i;
        }
    }

    //an array to hold up our color sensors.
    //each slot in the array corresponds to a channel in the multiplexer
    private TCS34725 colorSensor[] = null;

    public MultiplexedColorSensorManager(TCA9548A multi)
    {
        this.multiplexer = multi;

        colorSensor = new TCS34725[NUM_CHANNELS];
    }

    public synchronized void createColorSensorInChannel(I2cDevice i2cDevice, TCS34725.Parameters parameters, MULTIPLEXER_CHANNEL channel)
    {
        multiplexer.switchToChannel(channel.iVal);
        TCS34725 sensor = ClassFactory.createAdaFruitTCS34725(i2cDevice, parameters);
        colorSensor[channel.iVal] = sensor;
        //don't return sensor! we can't read from it without switching the multiplexer.
    }

    public synchronized int red(MULTIPLEXER_CHANNEL channel)
    {
        multiplexer.switchToChannel(channel.iVal);
        return colorSensor[channel.iVal].red();
    }

    public synchronized int green(MULTIPLEXER_CHANNEL channel)
    {
        multiplexer.switchToChannel(channel.iVal);
        return colorSensor[channel.iVal].green();
    }


    public synchronized int blue(MULTIPLEXER_CHANNEL channel)
    {
        multiplexer.switchToChannel(channel.iVal);
        return colorSensor[channel.iVal].blue();
    }

    public synchronized int alpha(MULTIPLEXER_CHANNEL channel)
    {
        multiplexer.switchToChannel(channel.iVal);
        return colorSensor[channel.iVal].alpha();
    }

}
