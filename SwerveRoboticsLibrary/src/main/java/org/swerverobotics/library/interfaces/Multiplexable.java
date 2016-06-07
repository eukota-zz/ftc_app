package org.swerverobotics.library.interfaces;

import com.qualcomm.robotcore.hardware.I2cDeviceSynch;

import org.swerverobotics.library.internal.I2cMultiplexedDeviceSync;

/**
 * Created by Steve on 6/5/2016.
 */
public interface Multiplexable {
    void attachToMultiplexer(TCA9548A mux, TCA9548A.MULTIPLEXER_CHANNEL channel);
}
