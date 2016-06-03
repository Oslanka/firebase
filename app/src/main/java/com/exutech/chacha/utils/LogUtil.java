package com.exutech.chacha.utils;

import android.util.Log;

/**
 * Created by EXUTech on 16/6/3.
 */
public class LogUtil {
    private boolean isLog = true;
    public static LogUtil logUtil = null;

    public static LogUtil getInstance() {
        if (null == logUtil) logUtil = new LogUtil();
        return logUtil;
    }

    public void d(String a) {
        if (isLog) {
            Log.d("-------->", "" + a);
        }
    }

    public void d(Double a) {
        if (isLog) {
            Log.d("-------->", "" + a);
        }
    }

    public void d(float a) {
        if (isLog) {
            Log.d("-------->", "" + a);
        }
    }

    public void d(int a) {
        if (isLog) {
            Log.d("-------->", "" + a);
        }
    }

    public void d(boolean a) {
        if (isLog) {
            Log.d("-------->", "" + a);
        }
    }

    public void d(Object a) {
        if (isLog) {
            Log.d("-------->", "" + a.toString());
        }
    }
}
