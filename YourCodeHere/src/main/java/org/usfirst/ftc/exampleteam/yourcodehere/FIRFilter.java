package org.usfirst.ftc.exampleteam.yourcodehere;

/*
    Finite Impulse Response
    Calculates a weighted average based on age
*/
//TODO move sum and weighted average elsewere
public class FIRFilter implements Filter
{
    public double[] values;
    private double[] weights;

    public FIRFilter(double[] w)
    {
        weights = w;
    }

    private double weightedAverage(double[] list, double[] w)
    {
        double total = 0;
        for (int i=0; i<list.length; i++ ){
            total += list[i]*w[i];
        }
        return total / sum(w);
    }

    private double sum(double[] list)
    {
        double total = 0;
        for (int i=0; i<list.length; i++ ){
            total += list[i];
        }
        return total;
    }

    public void roll(double newValue)
    {
        for (int i=0; i<(values.length-1); i++ ){
            values[i+1] = values[i];
        }
        values[0] = newValue;
    }

    public double getFilteredValue()
    {
        return weightedAverage(values, weights);
    }
}
