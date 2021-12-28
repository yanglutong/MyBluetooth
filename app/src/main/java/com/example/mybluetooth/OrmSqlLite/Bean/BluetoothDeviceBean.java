package com.example.mybluetooth.OrmSqlLite.Bean;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import android.text.style.StrikethroughSpan;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Arrays;

/**
 * @author: 小杨同志
 * @date: 2021/12/20
 */
@DatabaseTable(tableName = "bluetoothbean")
public class BluetoothDeviceBean {
    @DatabaseField(generatedId = true) //generatedId = true 表示自增长的主键
    private int id;
    @DatabaseField //只有添加这个注释，才能把此属性添加到表中的字段
    private String name;
    @DatabaseField //只有添加这个注释，才能把此属性添加到表中的字段
    private String address;
    @DatabaseField //只有添加这个注释，才能把此属性添加到表中的字段
    private String bondState;
    @DatabaseField //只有添加这个注释，才能把此属性添加到表中的字段
    private String type;

    public BluetoothDeviceBean(String name, String address, String bondState, String type) {
        this.name = name;
        this.address = address;
        this.bondState = bondState;
        this.type = type;
    }

    public BluetoothDeviceBean() {
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

    public String getBondState() {
        return bondState;
    }

    public void setBondState(String bondState) {
        this.bondState = bondState;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    @Override
    public String toString() {
        return "BluetoothDeviceBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", bondState=" + bondState +
                ", type=" + type +
                '}';
    }
}
