package org.swerverobotics.library.internal;

import com.qualcomm.robotcore.hardware.Engagable;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;

import org.swerverobotics.library.interfaces.Multiplexable;
import org.swerverobotics.library.interfaces.TCA9548A;

/**
 * Created by Steve on 6/5/2016.
 */
public class I2cMultiplexedDeviceSync implements Engagable, I2cDeviceSynch, Multiplexable {
    private I2cDeviceSynch deviceContext;
    private TCA9548A multiplexer = null;
    private TCA9548A.MULTIPLEXER_CHANNEL channel;


    public I2cMultiplexedDeviceSync(I2cDevice i2cDevice, int i2cAddr8Bit, boolean isI2cDeviceOwned)
    {
        deviceContext = new I2cDeviceSynchImpl(i2cDevice, i2cAddr8Bit, isI2cDeviceOwned);
    }

    public void attachToMultiplexer(TCA9548A mux, TCA9548A.MULTIPLEXER_CHANNEL chan)
    {
        this.multiplexer = mux;
        this.channel = chan;
    }

    /*
     * Wrap methods of I2cDeviceSyncImpl. We can't extend it because it's a final class.
     */

    /*
     * I2cDeviceSyncImpl methods that need adjusting when a multiplexer is in use
     */
    public byte read8(int ireg)
    {
        //If we are multiplexed, switch to channel before trying to access the device
        if (this.multiplexer!= null)
        {
            this.multiplexer.switchToChannel(this.channel);
        }
        return deviceContext.read8(ireg);
    }

    public byte[] read(int ireg, int creg)
    {
        //If we are multiplexed, switch to channel before trying to access the device
        if (this.multiplexer!= null)
        {
            this.multiplexer.switchToChannel(this.channel);
        }
        return deviceContext.read(ireg, creg);
    }

    public TimestampedData readTimeStamped(int ireg, int creg)
    {
        //todo Not sure if we need to switch channel before this? Probably doesn't hurt though.
        //If we are multiplexed, switch to channel before trying to access the device
        if (this.multiplexer!= null)
        {
            this.multiplexer.switchToChannel(this.channel);
        }
        return deviceContext.readTimeStamped(ireg, creg);
    }

    public TimestampedData readTimeStamped(int ireg, int creg, ReadWindow readWindowNeeded, ReadWindow readWindowSet)
    {
        //todo Not sure if we need to switch channel before this? Probably doesn't hurt, though.
        //If we are multiplexed, switch to channel before trying to access the device
        if (this.multiplexer!= null)
        {
            this.multiplexer.switchToChannel(this.channel);
        }
        return deviceContext.readTimeStamped(ireg, creg, readWindowNeeded, readWindowSet);
    }

    public void write8(int ireg, int data)
    {
        //If we are multiplexed, switch to channel before trying to access the device
        if (this.multiplexer!= null)
        {
            this.multiplexer.switchToChannel(this.channel);
        }
        deviceContext.write8(ireg, data);
    }

    public void write8(int ireg, int data, boolean waitforCompletion)
    {
        //If we are multiplexed, switch to channel before trying to access the device
        if (this.multiplexer!= null)
        {
            this.multiplexer.switchToChannel(this.channel);
        }
        deviceContext.write8(ireg, data, waitforCompletion);
    }

    public void write(int ireg, byte[] data)
    {
        //If we are multiplexed, switch to channel before trying to access the device
        if (this.multiplexer!= null)
        {
            this.multiplexer.switchToChannel(this.channel);
        }
        deviceContext.write(ireg, data);
    }

    public void write(int ireg, byte[] data, boolean waitForCompletion)
    {
        //If we are multiplexed, switch to channel before trying to access the device
        if (this.multiplexer!= null)
        {
            this.multiplexer.switchToChannel(this.channel);
        }
        deviceContext.write(ireg, data, waitForCompletion);
    }

    /*
     * Methods that don't need adjusting when a multiplexer is in use.
     */
    public void setI2cAddr(int i2cAddr8Bit)  { deviceContext.setI2cAddr(i2cAddr8Bit);  }
    public int getI2cAddr() { return deviceContext.getI2cAddr();  }
    public void engage() { deviceContext.engage(); }
    public boolean isEngaged() { return deviceContext.isEngaged(); }
    public boolean isArmed() { return deviceContext.isArmed(); }
    public void disengage() { deviceContext.disengage(); }
    public String getDeviceName() { return deviceContext.getDeviceName(); }
    public String getConnectionInfo() { return deviceContext.getConnectionInfo(); }
    public int getVersion() { return deviceContext.getVersion(); }
    public void close() { deviceContext.close(); }
    public void setReadWindow(ReadWindow newWindow) { deviceContext.setReadWindow(newWindow); }
    public ReadWindow getReadWindow() { return deviceContext.getReadWindow(); }
    public void ensureReadWindow(ReadWindow windowNeeded, ReadWindow windowToSet) { deviceContext.ensureReadWindow(windowNeeded, windowToSet); }
    public void waitForWriteCompletions() { deviceContext.waitForWriteCompletions(); }
    public void enableWriteCoalescing(boolean enable) { deviceContext.enableWriteCoalescing(enable); }
    public boolean isWriteCoalescingEnabled() { return deviceContext.isWriteCoalescingEnabled(); }
    public void setLogging(boolean enabled) { deviceContext.setLogging(enabled); }
    public void setLoggingTag(String loggingTag) { deviceContext.setLoggingTag(loggingTag); }
    public int getHeartbeatInterval() { return deviceContext.getHeartbeatInterval(); }
    public void setHeartbeatInterval(int msHeartbeatInterval) { deviceContext.setHeartbeatInterval(msHeartbeatInterval); }
    public void setHeartbeatAction(HeartbeatAction action) { deviceContext.setHeartbeatAction(action); }
    public HeartbeatAction getHeartbeatAction() { return deviceContext.getHeartbeatAction(); }

}
