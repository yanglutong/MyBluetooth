package com.example.mybluetooth.util;

import android.bluetooth.BluetoothDevice;

import com.example.mybluetooth.bean.BluetoothDeviceItem;

import java.util.List;

/**
 * @author: 小杨同志
 * @date: 2021/12/14
 */
public class Utils {
    public static List<BluetoothDevice> removeD(List<BluetoothDevice> list) {
// 从list中索引为0开始往后遍历
        for (int i = 0; i < list.size() - 1; i++) {
            // 从list中索引为 list.size()-1 开始往前遍历
            for (int j = list.size() - 1; j > i; j--) {
                // 进行比较
                if (list.get(j).getAddress().equals(list.get(i).getAddress())) {
                    // 去重
                    list.remove(j);
                }
            }
        }
        return list;
    }

    /*
     * btye[]字节数组转换成16进制的字符串
     */
    public static String toHexString1(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; ++i) {
            buffer.append(toHexString1(b[i]));
        }
        return buffer.toString();
    }

    public static String toHexString1(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }

    /**
     * 输入频点是否匹配
     *
     * @param pd 频点
     * @author lutong
     * @time 2021/12/16 15:58
     */

    public static boolean isArFcn(int pd) {
        boolean b = false;
        if (pd >= 0 && pd <= 599) {
            //（1）BAND1
            b = true;
        } else if (pd >= 1200 && pd <= 1949) {
            //（2）BAND3
            b = true;
        } else if (pd >= 2400 && pd <= 2649) {
            //（3）BAND5
            b = true;
        } else if (pd >= 3450 && pd <= 3799) {
            //（3）（4）BAND8
            b = true;
        } else if (pd >= 36200 && pd <= 36349) {
            //（5）BAND34
            b = true;
        } else if (pd >= 37750 && pd <= 38249) {
            //（6）BAND38
            b = true;
        } else if (pd >= 38250 && pd <= 38649) {
            //（7）BAND39
            b = true;
        } else if (pd >= 38650 && pd <= 39649) {
            //（8）BAND40
            b = true;
        } else if (pd >= 39650 && pd <= 41589) {
            //（9）BAND41
            b = true;
        }
        return b;
    }

    public static int TDD(int str) {//根据频点获取制式TDD 0  FDD 1
        int df = 0;
        if (str >= 0 && str <= 599) {
            df = 1;
        }
        if (str >= 1200 && str <= 1949) {
            df = 1;
        }
        if (str >= 2400 && str <= 2649) {
            df = 1;
        }
        if (str >= 3450 && str <= 3799) {
            df = 1;
        }
        if (str >= 36200 && str <= 36349) {
            df = 0;
        }
        if (str >= 37750 && str <= 38249) {
            df = 0;
        }
        if (str >= 38250 && str <= 38649) {
            df = 0;
        }
        if (str >= 38650 && str <= 39649) {
            df = 0;
        }
        if (str >= 39650 && str <= 41589) {
            df = 0;
        }

        if (str >= 0 && str <= 599) {
            df = 1;
        }
        if (str >= 1200 && str <= 1949) {
            df = 1;
        }
        if (str >= 2400 && str <= 2649) {
            df = 1;
        }
        if (str >= 3450 && str <= 3799) {
            df = 1;
        }
        if (str >= 36200 && str <= 36349) {
            df = 0;
        }
        if (str >= 37750 && str <= 38249) {
            df = 0;
        }
        if (str >= 38250 && str <= 38649) {
            df = 0;
        }
        if (str >= 38650 && str <= 39649) {
            df = 0;
        }
        if (str >= 39650 && str <= 41589) {
            df = 0;
        }
        if (str >= 0 && str <= 599) {
            df = 1;
        }
        if (str >= 1200 && str <= 1949) {
            df = 1;
        }
        if (str >= 2400 && str <= 2649) {
            df = 1;
        }
        if (str >= 3450 && str <= 3799) {
            df = 1;
        }
        if (str >= 36200 && str <= 36349) {
            df = 0;
        }
        if (str >= 37750 && str <= 38249) {
            df = 0;
        }
        if (str >= 38250 && str <= 38649) {
            df = 0;
        }
        if (str >= 38650 && str <= 39649) {
            df = 0;
        }
        if (str >= 39650 && str <= 41589) {
            df = 0;
        }
        return df;
    }
}
