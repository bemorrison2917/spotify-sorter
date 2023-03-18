package com.basil.spotify.sorter.util;

public class Wait {
    public static void waitInMillis(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
