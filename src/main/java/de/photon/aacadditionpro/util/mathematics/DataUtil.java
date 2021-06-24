package de.photon.aacadditionpro.util.mathematics;

import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class DataUtil
{
    public static int sum(int[] data)
    {
        int sum = 0;
        for (int datum : data) sum += datum;
        return sum;
    }

    public static long sum(long[] data)
    {
        long sum = 0;
        for (long datum : data) sum += datum;
        return sum;
    }

    public static double sum(double[] data)
    {
        double sum = 0;
        for (double datum : data) sum += datum;
        return sum;
    }

    public static double average(int[] data)
    {
        return sum(data) / (double) data.length;
    }

    public static double average(long[] data)
    {
        return sum(data) / (double) data.length;
    }

    public static double average(double[] data)
    {
        return sum(data) / data.length;
    }

    /**
     * Shortcut for calculating the squared error of a certain value.
     */
    public static double squaredError(final double reference, final double value)
    {
        val error = value - reference;
        return error * error;
    }

    /**
     * Calculates the summed square error from the reference value.
     */
    public static double squaredError(double reference, int[] data)
    {
        int sum = 0;
        for (int datum : data) sum += squaredError(reference, datum);
        return sum;
    }

    /**
     * Calculates the summed square error from the reference value.
     */
    public static double squaredError(double reference, long[] data)
    {
        long sum = 0;
        for (long datum : data) sum += squaredError(reference, datum);
        return sum;
    }

    /**
     * Calculates the summed square error from the reference value.
     */
    public static double squaredError(double reference, double[] data)
    {
        double sum = 0;
        for (double datum : data) sum += squaredError(reference, datum);
        return sum;
    }

}
