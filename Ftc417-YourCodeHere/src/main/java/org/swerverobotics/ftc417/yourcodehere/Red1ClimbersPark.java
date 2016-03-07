package org.swerverobotics.ftc417.yourcodehere;

import org.swerverobotics.library.interfaces.Autonomous;

@Autonomous(name = "AUTO Red 1 Dump Climbers -> Park", group = "417")
public class Red1ClimbersPark extends MasterAuto
{
    @Override
    protected void main() throws InterruptedException
    {
        //deploy blocker

        driveBackwardDistanceIMU(1, 200);

        turnRightDegrees(.5, 30);

        driveBackwardDistanceIMU(1, 15);

        //deploy climber servo

        driveForwardDistanceIMU(1, 15);

        //retract climber servo

        turnRightDegrees(.5, 90);

        driveBackwardDistanceIMU(1, 30);
    }
}