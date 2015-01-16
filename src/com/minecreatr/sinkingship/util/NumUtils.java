package com.minecreatr.sinkingship.util;

import java.util.Random;

/**
 * Number utility class
 *
 * @author minecreatr
 */
public class NumUtils {

    private static Random random;

    /**
     * Gets a random number in the range
     * @param num1 Number 1
     * @param num2 Number 2
     * @return The Random Number
     */
    public static int randomInRange(int num1, int num2){
        if (num1==num2){
            return num1;
        }
        int max = getBigger(num1, num2);
        int min = getSmaller(num1, num2);
        int ii = -min + (int) (Math.random() * ((max - (-min)) + 1));
        return ii;
    }

    /**
     * Gets a instance of {@link java.util.Random}
     * @return Random
     */
    public static Random getRandom(){
        if (random==null){
            random=new Random();
        }
        return random;
    }

    /**
     * Gets the biggest number passed in
     * @param args Numbers
     * @return The Biggest
     */
    public static int getBigger(int... args){
        int curBiggest = 0;
        for (int num : args){
            if (num>curBiggest){
                curBiggest=num;
            }
        }
        return curBiggest;
    }

    /**
     * Gets the smallest number passed in
     * @param args Numbers
     * @return The Smallest
     */
    public static int getSmaller(int... args){
        int curSmallest = Integer.MAX_VALUE;
        for (int num : args){
            if (num<curSmallest){
                curSmallest=num;
            }
        }
        return curSmallest;
    }
}
