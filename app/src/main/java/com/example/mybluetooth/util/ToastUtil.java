package com.example.mybluetooth.util;

import android.view.Gravity;
import android.widget.Toast;

import com.example.mybluetooth.App;

/**
 * @author: 小杨同志
 * @date: 2021/12/6
 */
public class ToastUtil {
    public static void showToast(String showing_devices) {
        Toast mToast = Toast.makeText(App.getContext(), null, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0 , 0);
        mToast.setText(showing_devices);
        mToast.show();
    }
}
