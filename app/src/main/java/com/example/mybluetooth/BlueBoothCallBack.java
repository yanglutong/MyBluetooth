package com.example.mybluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * @author: 小杨同志
 * @date: 2021/12/7
 */
public interface BlueBoothCallBack {
    void onScanStarted();//开始扫描
    void onScanFinished();//结束扫描
    void onScanning(BluetoothDevice device);//发现设备
    void onStateChanged(BluetoothDevice device);//设备绑定状态改变
    void onSocketServerBreak(BluetoothDevice device);//蓝牙设备 服务端断开监听
}
