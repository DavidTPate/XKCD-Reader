package com.davidtpate.xkcd.util;

import java.util.Random;

public class MathUtil {
    private static final Random mRandom = new Random();
    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * See http://stackoverflow.com/a/363692/1076683 for details.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = mRandom.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
