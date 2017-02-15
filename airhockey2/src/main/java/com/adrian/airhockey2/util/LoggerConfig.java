package com.adrian.airhockey2.util;

import android.util.Log;

/**
 * Created by adrian on 17-1-13.
 */

public class LoggerConfig {
    public static final boolean ON = true;

    public static void LogW(String tag, String msg) {
        if (ON) Log.w(tag, msg);
    }

    public static void LogE(String tag, String msg) {
        if (ON) Log.e(tag, msg);
    }

    public static void LogI(String tag, String msg) {
        if (ON) Log.i(tag, msg);
    }

    public static void LogD(String tag, String msg) {
        if (ON) Log.d(tag, msg);
    }

    public static void LogV(String tag, String msg) {
        if (ON) Log.v(tag, msg);
    }
}
