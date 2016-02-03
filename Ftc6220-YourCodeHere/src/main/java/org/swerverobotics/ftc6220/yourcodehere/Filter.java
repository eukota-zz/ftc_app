package org.swerverobotics.ftc6220.yourcodehere;

/*
    This is implimented by any control filter classes.
    roll() and update() are kept separate due to properties of certain potential filters
 */
public interface Filter
{
    void roll(double newValue);

    double getFilteredValue();

}
