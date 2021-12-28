package com.example.mybluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.mybluetooth.util.DigitalTrans;
import com.example.mybluetooth.util.JK;
import com.example.mybluetooth.util.LogUtil;
import com.example.mybluetooth.util.Util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.UUID;

/**
 * 客户端和服务端的基类，用于管理socket长连接
 */
public class BtBase {
    private static Handler handler;
    private static String TAG = "BtBase";
    static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bluetooth/";
    private static final int FLAG_MSG = 0;  //消息标记
    private static final int FLAG_FILE = 1; //文件标记

    public static BluetoothSocket mSocket;
    private DataOutputStream mOut;
    private static Listener mListener;
    public static boolean isRead;
    private boolean isSending;
    private InputStream inputStream;
    private OutputStream outputStream;
    private int type = 0;//是哪个发的数据
    private String isConnect = null;
    private OutputStreamWriter outputStreamWriter;
    BtBase(Listener listener, Handler handler) {
        mListener = listener;
        this.handler = handler;
    }
    /**
     * 循环读取对方数据(若没有数据，则阻塞等待)
     */
    void loopRead(BluetoothSocket socket) {
        mSocket = socket;
        try {
            if (!mSocket.isConnected())
                mSocket.connect();
            notifyUI(Listener.CONNECTED, mSocket.getRemoteDevice());
            inputStream = mSocket.getInputStream();
            outputStream = mSocket.getOutputStream();



            isRead = true;

            StringBuffer bfA1 = new StringBuffer();//用于拼接两次的数据
            StringBuffer bfA2 = new StringBuffer();//用于拼接两次的数据
            while (isRead) { //死循环读取
                int count = 0;
                while (count == 0) {
                    count = inputStream.available();//获取字节流的长度
                }
                byte[] b = new byte[count];
                Message message = Message.obtain();
                if ((inputStream.read(b)) != -1) {//读取一条
                    String s = DigitalTrans.byte2hex(b);
                    LogUtil.e("BtBase接收", s.length() + "----" + count);
                    if (count != 1 && count != 7) {//不为需要拼接的字符直接发送
                        //1.发送的字符为空串 2.发送的字符为8字节的整串
                        if (s.length() == 16 && !s.equals("0000000000000000")) {//发送的字符为8字节的整串
                            message.obj = s;
                        } else {
                            //发送空字符
                            message.obj = "";
                        }
                        //将activity的handler对象给到BtBase 蓝牙里获取到的数据返回给handler
                        message.what = 12;
                        handler.sendMessage(message);
                    } else {
                        if (s.equals("A2") && count == 1) {//需要拼接
                            bfA2.setLength(0);//设置长度为0
                            bfA2.append(s);
//                            LogUtil.e("BtBase接收bf2", "需要拼接   "+bfA2 + "----");
                            type = 2;
                        }else if (s.equals("A1") && count == 1) {//需要拼接
                            bfA1.setLength(0);//设置长度为0
                            bfA1.append(s);
                            type = 1;
                        } else if (type == 2) {
                            bfA2.append(s);
                            if(bfA2.toString().length() == 16){
                                message.obj = bfA2.toString();
                                bfA2.setLength(0);
                            }
                            message.what = 12;
                            handler.sendMessage(message);
                            LogUtil.e("BtBase接收bf2", "type   "+bfA2 + "----");
                        } else if (type == 1) {
                            bfA1.append(s);
                            if(bfA1.toString().length() == 16){
                                message.obj = bfA1.toString();
                                bfA1.setLength(0);
                            }
                            message.what = 12;
                            handler.sendMessage(message);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            LogUtil.e("TAGloopRead", "loopRead: "+e.getMessage() );
            close();
        }
    }

    /**
     * 发送短消息
     */
    public void sendMsg(String msg) {
        if (checkSend()) return;
        isSending = true;
        try {
            LogUtil.e("outputStreammsg", JK.conver16HexStr(JK.conver16HexToByte(msg)));
            LogUtil.e("outputStream", JK.conver16HexStr(JK.conver16HexToByte(msg)));
            outputStream.write(JK.conver16HexToByte(msg));
//            mOut.writeInt(FLAG_MSG); //消息标记
//            mOut.writeUTF(msg);
            outputStream.flush();
            notifyUI(Listener.MSG, "发送短消息：" + msg);
        } catch (Throwable e) {
            close();
        }
        isSending = false;
    }

    /**
     * 发送文件
     */
    public void sendFile(final String filePath) {
        if (checkSend()) return;
        isSending = true;
        Util.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream in = new FileInputStream(filePath);
                    File file = new File(filePath);
                    mOut.writeInt(FLAG_FILE); //文件标记
                    mOut.writeUTF(file.getName()); //文件名
                    mOut.writeLong(file.length()); //文件长度
                    int r;
                    byte[] b = new byte[4 * 1024];
                    notifyUI(Listener.MSG, "正在发送文件(" + filePath + "),请稍后...");
                    while ((r = in.read(b)) != -1)
                        mOut.write(b, 0, r);
                    mOut.flush();
                    notifyUI(Listener.MSG, "文件发送完成.");
                } catch (Throwable e) {
                    close();
                }
                isSending = false;
            }
        });
    }

    /**
     * 释放监听引用(例如释放对Activity引用，避免内存泄漏)
     */
    public void unListener() {
        mListener = null;
    }

    /**
     * 关闭Socket连接
     */
    public void close() {
        try {
            isRead = false;
            mSocket.close();
            notifyUI(Listener.DISCONNECTED, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前设备与指定设备是否连接
     */
    public boolean isConnected(BluetoothDevice dev) {
        boolean connected = (mSocket != null && mSocket.isConnected());
        if (dev == null)
            return connected;
        return connected && mSocket.getRemoteDevice().equals(dev);
    }

    // ============================================通知UI===========================================================
    private boolean checkSend() {
        if (isSending) {
            App.toast("正在发送其它数据,请稍后再发...", 0);
            return true;
        }
        return false;
    }

    public static void notifyUI(final int state, final Object obj) {
        App.runUi(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mListener != null)
                        mListener.socketNotify(state, obj);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface Listener {
        int DISCONNECTED = 0;//连接断开
        int CONNECTED = 1;//连接上
        int MSG = 2;//消息

        void socketNotify(int state, Object obj);
    }
}
