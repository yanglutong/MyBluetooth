package com.example.mybluetooth.bean;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import java.util.Arrays;

/**蓝牙设备列表
 * @author: 小杨同志
 * @date: 2021/12/13
 */
public class BluetoothDeviceItem {
    private String name;
    private String address;
    private int bondState;
    private int type;
    private ParcelUuid[] uuids;
    private  Class<? extends BluetoothDevice> aClass;

    public BluetoothDeviceItem(String name, String address, int bondState, int type, ParcelUuid[] uuids, Class<? extends BluetoothDevice> aClass) {
        this.name = name;
        this.address = address;
        this.bondState = bondState;
        this.type = type;
        this.uuids = uuids;
        this.aClass = aClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBondState() {
        return bondState;
    }

    public void setBondState(int bondState) {
        this.bondState = bondState;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ParcelUuid[] getUuids() {
        return uuids;
    }

    public void setUuids(ParcelUuid[] uuids) {
        this.uuids = uuids;
    }

    public Class<? extends BluetoothDevice> getaClass() {
        return aClass;
    }

    public void setaClass(Class<? extends BluetoothDevice> aClass) {
        this.aClass = aClass;
    }

    @Override
    public String toString() {
        return "BluetoothDeviceItem{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", bondState=" + bondState +
                ", type=" + type +
                ", uuids=" + Arrays.toString(uuids) +
                ", aClass=" + aClass +
                '}';
    }
}
