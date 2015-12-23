package org.usfirst.ftc.exampleteam.yourcodehere;

/**
 	another autonomous type for later use
	keeps track of the robot's position and related
 */

public class BotFieldState extends Transform{
    public double[] AUTO_START_POSITION = {0,0,0};
    private boolean teamIsRed;

    //constructors
    public BotFieldState(double[] initialPosition) { super(initialPosition);   }
    public BotFieldState(double xI, double yI, double zI) {
        super(xI,yI,zI);
    }

    //estimate the time until a destination is reached
    public double estimateETA(Transform target){
        double distance = this.getDistanceTo(target);

        // MAGIC NUMBER!
        double speed = 5.0;
        return distance / speed;
    }
}
