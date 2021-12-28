package com.example.mybluetooth.util;

import android.util.Log;

/**
 * @author: 小杨同志
 * @date: 2021/12/6
 */
public class LogUtil {
    public static void e (String TAG,String ms){
        Log.e(TAG, "e: "+ms);
    }
    public static void i (String TAG,String ms){
        Log.i(TAG, "i: "+ms);
    }
}
