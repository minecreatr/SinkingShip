package com.minecreatr.sinkingship.util;

import com.minecreatr.sinkingship.arena.Arena;

/**
 * Some Miscellaneous utilities
 *
 * @author minecreatr
 */
public class MiscUtils {

    public static void main(String[] args){
//        Arena arena = Arena.createArena("cheese", 0, 0, 0, 0, 0, 0, 0, 0);
//        System.out.println(arena.getName());
    }

    /**
     * Gets the object that called the function that called this
     * @return
     */
    public static String getCaller(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[3].toString();
    }
}
