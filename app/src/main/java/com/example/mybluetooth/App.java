package com.example.mybluetooth;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.mybluetooth.util.LogUtil;

public class App extends Application {
    private static final Handler sHandler = new Handler();
    private static Toast sToast; // 单例Toast,避免重复创建，显示时间过长
    private static Toast sToasts; // 单例Toast,避免重复创建，显示时间过长
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        sToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        sToasts = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }
    public static Context getContext(){
        return mContext;
    }
    public static void toast(String txt, int duration) {
        sToast.setText(txt);
        sToast.setDuration(duration);
        sToast.show();
    }
    public static void toastS(String txt, int duration) {
        LogUtil.e("TAG", "toastS: " );
        sToasts.setText(txt);
        sToasts.setDuration(duration);
        sToasts.show();
    }

    public static void runUi(Runnable runnable) {
        sHandler.post(runnable);
    }
}
