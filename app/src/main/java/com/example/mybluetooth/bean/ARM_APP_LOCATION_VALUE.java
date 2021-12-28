package com.example.mybluetooth.bean;

/**ARM_APP_LOCATION_VALUE
 * @author: 小杨同志
 * @date: 2021/12/15
 */
public class ARM_APP_LOCATION_VALUE {
    private String MSG_TYPE;//1字节 消息类型，此处为0xa2
    private String Lock_index;//2字节 锁定指示 0：未锁定  1：锁定
    private String Location_value;//3字节 定位数值
    private String Reserved;// 4-7字节  0xff
    private String Volt;//  第8字节 电压百分比指示,取值为enum(100,80,60,40,20)

    public ARM_APP_LOCATION_VALUE(String MSG_TYPE, String lock_index, String location_value, String reserved, String volt) {
        this.MSG_TYPE = MSG_TYPE;
        Lock_index = lock_index;
        Location_value = location_value;
        Reserved = reserved;
        Volt = volt;
    }

    @Override
    public String toString() {
        return "ARM_APP_LOCATION_VALUE{" +
                "MSG_TYPE='" + MSG_TYPE + '\'' +
                ", Lock_index='" + Lock_index + '\'' +
                ", Location_value='" + Location_value + '\'' +
                ", Reserved='" + Reserved + '\'' +
                ", Volt='" + Volt + '\'' +
                '}';
    }

    public String getMSG_TYPE() {
        return MSG_TYPE;
    }

    public void setMSG_TYPE(String MSG_TYPE) {
        this.MSG_TYPE = MSG_TYPE;
    }

    public String getLock_index() {
        return Lock_index;
    }

    public void setLock_index(String lock_index) {
        Lock_index = lock_index;
    }

    public String getLocation_value() {
        return Location_value;
    }

    public void setLocation_value(String location_value) {
        Location_value = location_value;
    }

    public String getReserved() {
        return Reserved;
    }

    public void setReserved(String reserved) {
        Reserved = reserved;
    }

    public String getVolt() {
        return Volt;
    }

    public void setVolt(String volt) {
        Volt = volt;
    }
}
