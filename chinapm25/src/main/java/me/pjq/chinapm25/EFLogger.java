package me.pjq.chinapm25;

import android.util.Log;

/**
 * Created by pjq on 1/21/15.
 */
public class EFLogger {
    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void w(String tag, String onLowMemory) {
        EFLogger.i(tag, onLowMemory);
    }
}
