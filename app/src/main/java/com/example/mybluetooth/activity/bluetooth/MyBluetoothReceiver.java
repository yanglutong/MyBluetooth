package com.example.mybluetooth.activity.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mybluetooth.MainActivity;

//import com.example.mybluetooth.MainActivity;

/**
 * 广播接收器
 * 当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
 * @author: 小杨同志
 * @date: 2021/12/13
 */
public class MyBluetoothReceiver extends BroadcastReceiver {
    private static String TAG = "MyBluetoothReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action){
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    MainActivity.callBack.onScanStarted();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    MainActivity.callBack.onScanFinished();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    MainActivity.callBack.onScanning(device);
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    MainActivity.callBack.onStateChanged(device);
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    //蓝牙连接被服务器主动切断
                    MainActivity.callBack.onSocketServerBreak(device);
                    break;
            }
    }
}
