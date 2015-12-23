package org.usfirst.ftc.exampleteam.yourcodehere;

/**
	Type for handling future autonomous tasks
 */
public class Transform{
    private final double[] values;
    double xPosition;
    double yPosition;
    double orientation;

    //constructors
    public Transform(double[] values){
        this.values = values;
        this.xPosition = values[0];
        this.yPosition = values[1];
        this.orientation = values[2];
    }

    public Transform(double xI, double yI, double zI){
        this.values = new double[] {xI,yI,zI};
        this.xPosition = values[0];
        this.yPosition = values[1];
        this.orientation = values[2];
    }


    //add to the transform, either with another transform or a double array
    public void add(Transform adder){
        this.xPosition += adder.xPosition;
        this.yPosition += adder.yPosition;
        this.orientation += adder.orientation;
    }

    public void add(double[] adder){
        this.add(new Transform(adder));
    }

    //return a transform that holds the components of a relative position as well as the angle to
    public Transform getVecTo(Transform target){
        double x = target.xPosition - this.xPosition;
        double y = target.yPosition - this.yPosition;
        double angle = Math.atan2(y,x);
        double[] output = {x, y, angle};
        return new Transform(output);
    }

    //return an array that holds the attributes of the transform
    public double[] getComponents(){
        double[] output = {this.xPosition, this.yPosition, this.orientation};
        return output;
    }

    //return the distance to a target
    public double getDistanceTo(Transform target){
        double[] vec = this.getVecTo(target).getComponents();
        double distance = Math.sqrt( Math.pow(vec[0],2) + Math.pow(vec[1],2) );
        return distance;
    }


}
