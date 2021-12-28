package com.example.mybluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybluetooth.Base.BaseActivity;
import com.example.mybluetooth.Base.Constants;
import com.example.mybluetooth.OrmSqlLite.Bean.BluetoothDeviceBean;
import com.example.mybluetooth.OrmSqlLite.DBManager;
import com.example.mybluetooth.OrmSqlLite.DBManagerMessage;
import com.example.mybluetooth.activity.bluetooth.MyBluetoothReceiver;
import com.example.mybluetooth.bean.APP_ARM_PARAM_CFG_REQ;
import com.example.mybluetooth.bean.ARM_APP_LOCATION_VALUE;
import com.example.mybluetooth.bean.ARM_APP_PARAM_CFG_RESP;
import com.example.mybluetooth.bean.BluetoothDeviceItem;
import com.example.mybluetooth.OrmSqlLite.Bean.MessageModel;
import com.example.mybluetooth.linechart.Bean;
import com.example.mybluetooth.linechart.ChartView;
import com.example.mybluetooth.popwindow.DLPopupWindow;
import com.example.mybluetooth.recycler.RecyclerviewAdapter;
import com.example.mybluetooth.tts.SystemTTS;
import com.example.mybluetooth.util.JK;
import com.example.mybluetooth.util.LogUtil;
import com.example.mybluetooth.util.ToastUtil;
import com.example.mybluetooth.util.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qw.soul.permission.bean.Permission;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.mybluetooth.Base.Constants.State;
import static com.example.mybluetooth.BtBase.mSocket;
import static com.example.mybluetooth.BtBase.notifyUI;

public class MainActivity extends BaseActivity implements View.OnClickListener, BtBase.Listener {
    private static boolean isSelectSpinner = false;
    private BtClient mClient = new BtClient(this, mHandler);//初始化客户端蓝牙连接
    private String[] permissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION
            , ACCESS_FINE_LOCATION
    };
    public static String TAG = "MainActivity";
    private LineChart chartView;//折线图
    private static ARM_APP_PARAM_CFG_RESP locationValueA1 = null;//下发指令后 收到的A1回复
    private static ARM_APP_LOCATION_VALUE locationValueA2 = null;//下发指令后 收到的A2内容
    private static Handler mHandler = new Handler(
            new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    Log.e(TAG, "handleMessage: " + State);
                    if (!State) {//蓝牙没有连接
                        iv_cell_state.setImageResource(R.mipmap.cell);//初始化电量置空
                    }
                    if (msg.what == 2) {//广播接收蓝牙状态
                        if (msg.arg1 == 1) {//正在配对

                        } else if (msg.arg1 == 2) {//配对完成
                            BluetoothDevice device = (BluetoothDevice) msg.obj;
                            if (itemSearch.size() > 0) {//搜索到的条目点击配对并且配对完成后删除该条目显示
                                Iterator<BluetoothDevice> it = itemSearch.iterator();//删除集合元素必须使用迭代器删除，其他方式会出错
                                while (it.hasNext()) {
                                    BluetoothDevice next = it.next();
                                    if (next.getName().equals(device.getName()) && next.getAddress().equals(device.getAddress())) {
                                        it.remove();
                                        if (items != null) {
                                            items.add(next);//将新配对成功的添加到已配对列表
                                        }
                                    }
                                }
                            }
                            //将适配器重新设置为1类型数据源
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            adapter = new RecyclerviewAdapter(mContext, items, 1);
                            recyclerView.setAdapter(adapter);
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                            if (adapterSearch != null) {
                                adapterSearch.notifyDataSetChanged();//刷新适配器
                            }
                        } else if (msg.arg1 == 3) {//取消配对
                            BluetoothDevice device = (BluetoothDevice) msg.obj;
                            if (items.size() > 0) {//已存在设备取消配对后从列表移除
                                Iterator<BluetoothDevice> it = items.iterator();//删除集合元素必须使用迭代器删除，其他方式会出错
                                while (it.hasNext()) {
                                    BluetoothDevice next = it.next();
                                    if (next.getName().equals(device.getName()) && next.getAddress().equals(device.getAddress())) {
                                        it.remove();
                                    }
                                }
                            }
                            //将适配器重新设置为1类型数据源
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            adapter = new RecyclerviewAdapter(mContext, items, 1);
                            recyclerView.setAdapter(adapter);
                        }
                        Log.e(TAG, "handleMessage: " + msg.obj);
                    }
                    if (msg.what == 12) {//接收蓝牙数据
                        String o = (String) msg.obj;
                        LogUtil.e("handler接收蓝牙数据", o);
                        if (o != null && !o.equals("")) {//有数据 进行拆分
                            String type = o.substring(0, 2);
                            if (type.equals("A2")) {//消息内容
                                LogUtil.e("接收", o);
                                ARM_APP_LOCATION_VALUE locationValue = new ARM_APP_LOCATION_VALUE(type, JK.hex16To10(o.substring(2, 4)), JK.hex16To10(o.substring(4, 6)), JK.hex16To10(o.substring(6, 14)), JK.hex16To10(o.substring(14, 16)));
                                if (null != locationValue) {
                                    locationValueA2 = locationValue;
                                }
                                if(!Constants.sendLastState){//下发指令时不会显示数据
                                    //下发指令
                                    tv_Location_value.setText("同步中");
                                    //曲线图值为空
                                    ChartView.clearList(mContext);
                                }else{
                                    if (null != locationValue.getVolt()) {//电量字段有数据
                                        //将板卡对应电量显示到界面上
                                        if (locationValue.getVolt().equals("100")) {
                                            iv_cell_state.setImageResource(R.mipmap.cell_100);
                                        } else if (locationValue.getVolt().equals("80")) {
                                            iv_cell_state.setImageResource(R.mipmap.cell_80);
                                        } else if (locationValue.getVolt().equals("60")) {
                                            iv_cell_state.setImageResource(R.mipmap.cell_60);
                                        } else if (locationValue.getVolt().equals("40")) {
                                            iv_cell_state.setImageResource(R.mipmap.cell_40);
                                        } else if (locationValue.getVolt().equals("20")) {
                                            iv_cell_state.setImageResource(R.mipmap.cell_20);
                                        } else {
                                            iv_cell_state.setImageResource(R.mipmap.cell);
                                        }
                                    } else {
                                        iv_cell_state.setImageResource(R.mipmap.cell);
                                    }
                                    if (null != locationValue.getLocation_value()) {//定位数值字段有数据
                                        int i = Integer.parseInt(locationValue.getLocation_value()) * 10;//将能量值放大十倍
//                                        if(!locationValue.getLocation_value().equals("0")){
                                            tv_Location_value.setText(i+"");//曲线图上的能量值
//                                        }
                                        //曲线图
                                        ChartView.initChart(mContext);
                                        ChartView.list0.remove(0);
                                        ChartView.list0.add(new Bean("", Float.parseFloat(i + "")));
                                        if (ChartView.list0 != null && ChartView.list0.size() > 0) {
                                            ChartView.showLineChart(ChartView.list0, "", mContext.getResources().getColor(R.color.color_3853e8));
                                        }

                                        //语音播放
                                        if(null != locationValue.getLocation_value()&& !locationValue.getLocation_value().equals("0")){
                                            if(SystemTTS.isOpen){
                                                SystemTTS.getInstance(mContext).playText(i+"");//播放语音
                                            }
                                        }
                                    }
                                    LogUtil.e(TAG, locationValue.toString());
                                }
                            } else if (type.equals("A1")) {//参数配置结果
                                ARM_APP_PARAM_CFG_RESP locationValue = new ARM_APP_PARAM_CFG_RESP(type, JK.hex16To10(o.substring(2, 4)), JK.hex16To10(o.substring(4, 6)), JK.hex16To10(o.substring(6, 16)));
                                if (null != locationValue) {
                                    locationValueA1 = locationValue;
                                }
                                LogUtil.e("m123456789", locationValueA1.toString());
                            }
                        }
                    }
                    if (msg.what == 200) {//改变连接状态
                        if(tv_connect != null){
                            tv_connect.setText("" + Constants.bluetoothState);
                        }
                        if (State) {
                            iv_connect.setImageResource(R.mipmap.connect);
                        }else{
                            iv_connect.setImageResource(R.mipmap.disconnect);
                        }
                    }
                    return true;
                }
            }
    );
    private static ImageView iv_settings, iv_audio, iv_connect, iv_cell_state;
    private Button bt_send;
    private MyBluetoothReceiver receiver;
    private static ArrayList<BluetoothDevice> items = new ArrayList<>();//获取已配对设备列表
    private static ArrayList<BluetoothDevice> itemSearch = new ArrayList<>();//获取未匹配搜索后的设备列表
    private static DLPopupWindow popupWindow;//蓝牙连接配置窗口
    private static RecyclerView recyclerSearch;//蓝牙连接配置窗口 搜索条目
    private static RecyclerView recyclerView;//蓝牙连接配置窗口 已配对条目
    private static RecyclerviewAdapter adapterSearch;
    private static RecyclerviewAdapter adapter;
    private EditText et_pd, et_pci;
    private static TextView tv_connect, tv_device_state , tv_Location_value;
    private static TextView tv_qt;
    private Spinner sp,sp_pci;
    private ImageView iv_sp;

    @Override
    protected void baseData() {//onCreate
        registerReceiver();//动态注册蓝牙广播监听
        //权限申请 由子类实现 将需要的权限交由父类进行处理 将结果回调到子类
        requestPermissions(permissions, new PermissionsResult() {
            @Override
            public void permissionsOk(boolean b, String str) {//检查权限结果
                if (b) {
//                    ToastUtil.showToast("所有权限已经授权");
                } else {
                    ToastUtil.showToast("有权限未授权");
                }
            }

            @Override
            public void permissionsRequest(boolean b, Permission[] allPermissions) {//申请权限结果
                if (b) {
                    ToastUtil.showToast("权限申请成功");
                } else {
                    ToastUtil.showToast("权限申请失败");
//                    ToastUtil.showToast("申请"+allPermissions[0]+"失败");
                }
            }
        });
        //初始化下拉框
        initSpinner();
        //初始化曲线图
        ChartView.initData(chartView, this);
        //初始化蓝牙
        blueBoothData();
        //初始化蓝牙连接
        initBlueConnect();
    }

    private void initSpinner() {
        sp = (Spinner) findViewById(R.id.sp);//下拉框
        sp_pci = (Spinner) findViewById(R.id.sp_pci);//下拉框pci
        //资源转[]
//        String meinv[] = getResources().getStringArray(R.array.meinv);
        DBManagerMessage dbManagerMessage;
        List<MessageModel> list = null;
        try {
            dbManagerMessage = new DBManagerMessage(mContext);
            list = dbManagerMessage.getdemoBeanList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list != null && list.size()>0){
            ArrayList<String> strings = new ArrayList<>();
            ArrayList<String> stringsPci = new ArrayList<>();
            LogUtil.e("spinner", ""+list.toString());
            for (MessageModel messageModel : list) {
                strings.add(messageModel.getName());
                stringsPci.add(messageModel.getTitle());
            }
            //构造ArrayAdapter
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                    R.layout.layout_spinner_item_drop, strings);
            //设置下拉样式以后显示的样式
            adapter.setDropDownViewResource(R.layout.my_drop_down_item);//下拉条

            sp.setAdapter(adapter);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String item = adapter.getItem(i);
                    //选择列表项的操作
//                    int position = adapterView.getSelectedItemPosition();
//                    ToastUtil.showToast(item);
                    //将选中的频点显示
                        if(et_pd != null){
                            et_pd.setText(""+sp.getSelectedItem().toString());
                        }
                    Log.e("qnzq",i+"332");

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    //未选中时候的操作
                    ToastUtil.showToast(
                            "未选中时候的操作");
                }
            });


            //构造ArrayAdapter
            ArrayAdapter<String> adapterss = new ArrayAdapter<>(mContext,
                    R.layout.layout_spinner_item_drop, stringsPci);
            //设置下拉样式以后显示的样式
            adapterss.setDropDownViewResource(R.layout.my_drop_down_item);//下拉条
            sp_pci.setAdapter(adapterss);
            sp_pci.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String item = adapterss.getItem(i);
                    //选择列表项的操作
//                    int position = adapterView.getSelectedItemPosition();
//                    ToastUtil.showToast(item);
                    //将选中的频点显示
                        if(et_pci != null){
                            et_pci.setText(""+sp_pci.getSelectedItem().toString());
                        }
                    Log.e("qnzq",i+"332");

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    //未选中时候的操作
                    ToastUtil.showToast(
                            "未选中时候的操作");

                }
            });

        }

    }

    private void initBlueConnect() {
        //默认进来创建定时器每一秒监听一次连接状态  每一秒获取一次本地配对的蓝牙设备 保存的数据库蓝牙设备 将两个进行匹配有的话连接
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //蓝牙实时监听连接状态
                isBlueConnect();
                Message message = Message.obtain();
                message.what = 200;//蓝牙连接状态
                mHandler.sendMessage(message);
                mHandler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    private void isBlueConnect() {
        //1.获取已配对列表 2.查看已配对列表中是否有本地连接过的设备  3.本地蓝牙名称不能为空
        BluetoothDeviceBean bluetoothDevice = null;
        DBManager device = null;
        try {
            device = new DBManager(this);
            List<BluetoothDeviceBean> list = device.getBluetoothDeviceBeanList();
//            Log.e(TAG, "initBlueConnect: " + list.size() + list.toString());

            if (list.size() > 0) {//有存储数据
                bluetoothDevice = list.get(list.size() - 1);
            } else {
//                ToastUtil.showToast("本地没有存储蓝牙名称");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (null == bluetoothDevice) {
            return;
        }
        if (mBluetoothAdapter != null) {
            BluetoothDevice dev = null;
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();//已配对列表
            if (devices.size() > 0) {
                for (BluetoothDevice device1 : devices) {
                    if (device1.getAddress().equals(bluetoothDevice.getAddress())) {//已配对和本地连接的进行匹配
                        dev = device1;
                    }
                }
            }
            if (dev != null) {
                //如果已配对蓝牙中包含连接过的蓝牙
//                ToastUtil.showToast("蓝牙可以进行连接");
                //连接蓝牙设备
                if (mClient != null) {
                    if (!mClient.isConnected(dev)) {
                        mClient.connect(dev);
                        //蓝牙未连接
                        State = false;
                        Constants.bluetoothState = "蓝牙连接：未连接";
                        LogUtil.e(TAG + "HJKz", "蓝牙未连接  mClient.connect(dev)");
                    } else {
                        //蓝牙已连接
                        State = true;
                        Constants.bluetoothState = "蓝牙连接：已连接";
                        LogUtil.e(TAG + "HJKz", "蓝牙已连接  mClient.connect(dev)");
                    }
                } else {
                    State = false;
                    Constants.bluetoothState = "蓝牙连接：未连接";
                    LogUtil.e(TAG + "HJKz", "蓝牙连接断开状态");
//                    ToastUtil.showToast("客户端未连接");
                }
            } else {//获取不到本地蓝牙代表本地蓝牙被关闭
                if (mClient != null) {
                    mClient.close();//蓝牙断开后关闭连接
                }
                State = false;
                Constants.bluetoothState = "蓝牙连接：未连接";
                LogUtil.e(TAG + "HJKz", "蓝牙未连接");
            }
        }
    }


    private void registerReceiver() {
        receiver = new MyBluetoothReceiver();
        //先注册广播获取搜索结果
        //搜索开始的过滤器
        IntentFilter filter1 = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//搜索结束的过滤器
        IntentFilter filter2 = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//寻找到设备的过滤器
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//绑定状态改变
        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//配对请求
        IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
//蓝牙被设备或服务端关闭监听
        IntentFilter disConnectedFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);


        registerReceiver(receiver, filter1);
        registerReceiver(receiver, filter2);
        registerReceiver(receiver, filter3);
        registerReceiver(receiver, filter4);
        registerReceiver(receiver, filter5);
        registerReceiver(receiver, disConnectedFilter);

    }


    private static final int REQUEST_ENABLE_BT = 200;
    private BluetoothAdapter mBluetoothAdapter;

    private void blueBoothData() {
        //1.进入界面打开蓝牙 失败退出程序 成功则进行下一步操作
        //获取本地蓝牙适配器
        //获取BluetoothAdapter对象
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断设备是否支持蓝牙，如果mBluetoothAdapter为空则不支持，否则支持
        if (mBluetoothAdapter == null) {
//            ToastUtil.showToast("这台设备不支持蓝牙");
            //不支持蓝牙则退出程序
            this.finish();
            System.exit(0);//正常退出程序
        } else {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
//            ToastUtil.showToast("这台设备支持蓝牙");
            //判断蓝牙是否开启，如果蓝牙没有打开则打开蓝牙
            if (!mBluetoothAdapter.isEnabled()) {
                //请求用户开启
                Intent enableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
//                ToastUtil.showToast("蓝牙已打开");
                //进来就是打开状态时，进行每秒连接上次蓝牙操作
//                showBoundDevices();
            }
        }
    }

    /**
     * 判断蓝牙请求后是否开启
     *
     * @return
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // bluetooth is opened
                //可以获取列表操作等
//                ToastUtil.showToast("蓝牙已打开");
                //和设备蓝牙进行连接 只连接一次
            } else {
                // bluetooth is not open
//                this.finish();
//                System.exit(0);//正常退出程序
                ToastUtil.showToast("请打开蓝牙");
            }
        }
    }


    @Override
    protected void baseFindView() {
        //折线图控件
        chartView = findViewById(R.id.chartview);
        iv_audio = findViewById(R.id.iv_audio);//声音
        iv_connect = findViewById(R.id.iv_connect);//蓝牙
        //频点
        et_pd = findViewById(R.id.et_pd);
        //PCI
        et_pci = findViewById(R.id.et_pci);
        //下发
        bt_send = findViewById(R.id.bt_send);

        //蓝牙连接状态
        tv_connect = findViewById(R.id.tv_connect);

        //蓝牙电池电量
        iv_cell_state = findViewById(R.id.iv_cell_state);

        //设备状态
        tv_device_state = findViewById(R.id.tv_device_state);

        //曲线图能量值
        tv_Location_value = findViewById(R.id.tv_Location_value);



        bt_send.setOnClickListener(this);
        iv_audio.setOnClickListener(this);
        iv_connect.setOnClickListener(this);
    }
    //布局
    @Override
    protected int getLayoutXml() {
        return R.layout.activity_main;
    }

    //监听事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_audio://声音
                if(SystemTTS.isOpen){
                    iv_audio.setImageResource(R.mipmap.noaudio);
                    SystemTTS.isOpen = false;
                }else {
                    iv_audio.setImageResource(R.mipmap.audio);
                    SystemTTS.isOpen = true;
                }
                break;
            case R.id.iv_connect://蓝牙
                //检测蓝牙是否打开
                isOpenBluetooth();
                break;
            case R.id.bt_send://下发
                String et_pd1 = et_pd.getText().toString();
                String et_pci1 = et_pci.getText().toString();
                //1.频点和PCI不能为空 2.频点和PCI限制为数字 3.PCI 设置为0~503 4.输入的频点是否匹配已有频点
                if (TextUtils.isEmpty(et_pd1)) {
                    ToastUtil.showToast("请输入频点");
                    return;
                }
                if (TextUtils.isEmpty(et_pci1)) {
                    ToastUtil.showToast("请输入PCI");
                    return;
                }
                if (Integer.parseInt(et_pci1) >= 0 && Integer.parseInt(et_pci1) <= 503) {
                } else {
                    ToastUtil.showToast("请输入有效PCI");
                    return;
                }
                if (!Utils.isArFcn(Integer.parseInt(et_pd1))) {
                    ToastUtil.showToast("频点输入错误");
                    return;
                }

                //根据频点划分TDD FDD 制式
                int tdd = Utils.TDD(Integer.parseInt(et_pd1));//0 TDD 1FDD


                //将要发送的数据转成16进制
                APP_ARM_PARAM_CFG_REQ paramCfgReq = null;
                String et_pd1d = JK.hex10To16(Integer.parseInt(et_pd1));
                if (et_pd1d.substring(0, 2).equals("00")) {
                    et_pd1d = et_pd1d.substring(2);
                }
                String et_pci1d = JK.hex10To16(Integer.parseInt(et_pci1));
                if (et_pci1d.substring(0, 4).equals("0000")) {
                    et_pci1d = et_pci1d.substring(4);
                }
                if (tdd == 0) {
                    paramCfgReq = new APP_ARM_PARAM_CFG_REQ("A0", "00", et_pd1d, et_pci1d, "FF");
                    paramCfgReq.getAllSendMsg();
                } else if (tdd == 1) {
                    paramCfgReq = new APP_ARM_PARAM_CFG_REQ("A0", "01", et_pd1d, et_pci1d, "FF");
                    paramCfgReq.getAllSendMsg();
                }
                //条件都满足下发送指令
                Log.e(TAG, "onClick: " + paramCfgReq.toString());
                Log.e(TAG, "onClick: " + paramCfgReq.getAllSendMsg());
                if (mClient != null) {
                    Log.e(TAG, "onClick: " + State);
                    if (State) {//连接状态才发送
                        //上次发送完成才可以重新发送
                        if (Constants.sendLastState) {
                            Constants.sendLastState = false;//正在下发参数
                            if (locationValueA1 != null) {//将上次下发的置为空
                                locationValueA1 = null;
                            }
                            if (locationValueA2 != null) {//将上次下发的置为空
                                locationValueA2 = null;
                            }
                            mClient.sendMsg(paramCfgReq.getAllSendMsg());
                            //下发指令
                            tv_Location_value.setText("同步中");
                            tv_device_state.setText("设备状态：配置中");
                            //曲线图值为空
                            ChartView.clearList(mContext);
                            //检测下发指令
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    //下发指令后延时5秒 看是否下发成功 1.
                                    if (locationValueA1 == null) {//1.下发后a1没有回来 下发指令失败
                                        Constants.sendState = false;
                                        //下发失败
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.showToast("配置失败"+0);
                                                tv_device_state.setText("设备状态：配置失败");
                                            }
                                        });
                                    } else if (locationValueA1 != null) {//2.下发后a1回来
                                        if (locationValueA1.getResult().equals("0")) {//回复0成功
                                            //成功后有数据回来则成功，没有数据回来就失败
                                            if (locationValueA2 != null) {
                                                //下发成功
                                                Constants.sendState = true;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //保存不一样的频点
                                                        ToastUtil.showToast("配置成功");
                                                        tv_device_state.setText("设备状态：配置成功");

                                                        //配置成功的保存此频点
                                                        setPd(et_pd1,et_pci1);


                                                    }
                                                });
                                            } else {
                                                //下发失败
                                                Constants.sendState = false;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastUtil.showToast("配置失败"+1);
                                                        tv_device_state.setText("设备状态：配置失败");
                                                    }
                                                });


                                            }
                                        } else if (locationValueA1.getResult().equals("1")) {//回复1失败
                                            //下发失败
                                            Constants.sendState = false;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.showToast("配置失败"+3);
                                                    tv_device_state.setText("设备状态：配置失败");
                                                }
                                            });
                                        }
                                    }
                                    Constants.sendLastState = true;//上次的参数下发完成
                                }
                            }, 15000);
                        } else {
                            ToastUtil.showToast("参数正在下发请稍后");
                        }
                    } else {
                        LogUtil.e(TAG, "onClick: 请先连接设备");
                        ToastUtil.showToast("请先连接设备");
                    }
                } else {
                    ToastUtil.showToast("设备未连接");
                }
                break;
        }
    }

    private void setPd(String etPd1, String et_pc) {//保存每次下发的频点和PCI 去重复
        boolean isxtpd=false;//相同频点
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<String> pci = new ArrayList<>();
        try {
            DBManagerMessage managerMessage = new DBManagerMessage(mContext);

            List<MessageModel> list = managerMessage.getdemoBeanList();

            if(list != null && list.size()>0){
                for (MessageModel messageModel : list) {
                    if(messageModel.getName().equals(etPd1)){//集合里没有此频点则插入
                        isxtpd = true;
                    }
                    LogUtil.e("LLL", messageModel.getName()+"---"+etPd1);
                }
                if(isxtpd){//是相同频点不加
                }else{
                    strings.add(etPd1);
                    pci.add(et_pc);
                }
                LogUtil.e("LLLs", strings.toString()+"---"+pci.toString());

                if(strings.size()>0 && pci.size()>0){
                    if(list.size()==5){
                        ArrayList<MessageModel> models = new ArrayList<>();
                        for (int i = 1; i < list.size(); i++) {
                            models.add(list.get(i));
                        }
                        MessageModel messageModel = new MessageModel();
                        messageModel.setName(etPd1);
                        messageModel.setTitle(et_pc);
                        models.add(messageModel);

                        if(models.size()>0){
                            //删除原来的数据
                            List<MessageModel> list1 = managerMessage.getdemoBeanList();
                            for (MessageModel model : list1) {
                                managerMessage.deletedemoBean(model);
                            }

                            //添加新数据
                            for (MessageModel model : models) {
                                managerMessage.insertdemoBean(model);
                            }
                        }
                    }else{
                        MessageModel messageModel = new MessageModel();
                        messageModel.setName(strings.get(0));
                        messageModel.setTitle(pci.get(0));
                        managerMessage.insertdemoBean(messageModel);
                    }
                }
            }else{
                MessageModel messageModel = new MessageModel();
                messageModel.setName(etPd1);
                messageModel.setTitle(et_pc);
                managerMessage.insertdemoBean(messageModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void isOpenBluetooth() {
        //判断设备是否支持蓝牙，如果mBluetoothAdapter为空则不支持，否则支持
        if (mBluetoothAdapter == null) {
//            ToastUtil.showToast("这台设备不支持蓝牙");
            //不支持蓝牙则退出程序
            this.finish();
            System.exit(0);//正常退出程序
        } else {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
//            ToastUtil.showToast("这台设备支持蓝牙");
            //判断蓝牙是否开启，如果蓝牙没有打开则打开蓝牙
            if (!mBluetoothAdapter.isEnabled()) {
                //请求用户开启
                Intent enableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
//清空搜索和匹配记录
                if (items.size() > 0) {
                    items.clear();
                }
                if (itemSearch.size() > 0) {
                    itemSearch.clear();
                }
                //用户手动进行蓝牙连接
                //1.弹出蓝牙框
                popupWindow = new DLPopupWindow(Objects.requireNonNull(this), Objects.requireNonNull(R.layout.pop_window), Objects.requireNonNull(R.style.showPopupAnimation), Objects.requireNonNull(MainActivity.this), new DLPopupWindow.PopView() {
                    @Override
                    public void OnView(View view) {
                        //已匹配设备
                        recyclerView = view.findViewById(R.id.recycler);
                        //已匹配设备
                        recyclerSearch = view.findViewById(R.id.recyclerSearch);

                        tv_qt = view.findViewById(R.id.tv_qt);//其他可用设备

                        Button bt_search = view.findViewById(R.id.bt_search);
                        TextView tv_device_connect = view.findViewById(R.id.tv_device_connect);//蓝牙标题状态
                        bt_search.setOnClickListener(view1 -> {//搜索蓝牙
                            ToastUtil.showToast("开始搜索");
                            doDiscovery();

                            bt_search.setVisibility(View.GONE);
                            view.findViewById(R.id.liner_qt).setVisibility(View.VISIBLE);//显示其他按钮
                            tv_device_connect.setText("搜索可用设备");//显示其他按钮
                        });


                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        adapter = new RecyclerviewAdapter(mContext, items, 1);
                        recyclerView.setAdapter(adapter);
                        adapter.setItemOnClick((v, position) -> {//已配对条目监听
                            if (items.size() > 0) {//获取匹配成功的蓝牙条目
                                if (mClient.isConnected(items.get(position))) {
                                    App.toast("已经连接了", 0);
                                    return;
                                } else {
                                    App.toast("开始连接了", 0);
                                }
                                mClient.connect(items.get(position));
                                App.toast("正在连接...", 0);
                            }
                        });
                        adapter.notifyDataSetChanged();


                        recyclerSearch.setLayoutManager(new LinearLayoutManager(mContext));
                        adapterSearch = new RecyclerviewAdapter(mContext, itemSearch, 1);
                        recyclerSearch.setAdapter(adapterSearch);
                        adapterSearch.setItemOnClick((v, position) -> {//搜索条目监听
                            if (itemSearch.size() > 0) {//未配对设备
                                pinTargetDevice(position);
                            }
                        });
                        adapterSearch.notifyDataSetChanged();
                    }
                });
                //显示popWindow
                popupWindow.showAtLocation(findViewById(R.id.base_liner), Gravity.CENTER, 0, 0);

                //popWindow退出时需要进行的操作
                popupWindow.setOnDismissListener(() -> {
                    final WindowManager.LayoutParams lp = MainActivity.this.getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    MainActivity.this.getWindow().setAttributes(lp);
                    //取消搜索
                    if (mBluetoothAdapter.isDiscovering()) {//判断是否正在查找设备
                        mBluetoothAdapter.cancelDiscovery();//取消查找
                    }
                });

                //2.点击蓝牙获取已匹配列表
                showBoundDevices();
            }
        }
    }


    /*
     *蓝牙获取已配对设备列表
     */
    private void showBoundDevices() {
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) { //存在已配对过的设备
            //利用for循环读取每一个设备的信息
            for (BluetoothDevice device : devices) {
                items.add(device);
            }

//            //已匹配的列表 //动态设置recycler的宽高
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
//            lp.height = 200;
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//            recyclerView.setLayoutParams(lp);
            Log.e(TAG, "showBoundDevices: " + items.size() + " ===" + items.toString());
        } else {
            //不存在已配对过的设备
            ArrayList<BluetoothDeviceItem> items = new ArrayList<>();
            items.add(new BluetoothDeviceItem("没有配对的设备", "", 0, 0, null, null));
            RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(mContext, items, 0);
            recyclerView.setAdapter(recyclerviewAdapter);
            recyclerviewAdapter.notifyDataSetChanged();
            //不存在已经配对的蓝牙设备
//            Toast.makeText(this, "不存在已经配对的蓝牙设备", Toast.LENGTH_SHORT).show();
            //搜索蓝牙
        }
    }

    /*搜索蓝牙*/
    private void doDiscovery() {
        new Thread(() -> {
            if (mBluetoothAdapter.isDiscovering()) {//判断是否正在查找设备
                mBluetoothAdapter.cancelDiscovery();//取消查找
            }
            mBluetoothAdapter.startDiscovery();//开始查找
        }).start();
    }

    /*搜索蓝牙回调*/
    public static BlueBoothCallBack callBack = new BlueBoothCallBack() {
        @Override
        public void onScanStarted() {
            LogUtil.e(TAG, "开始扫描...");
        }

        @Override
        public void onScanFinished() {
            LogUtil.e(TAG, "结束扫描...");
            //去重相同的蓝牙设备
            if (itemSearch.size() > 0) {
                itemSearch = (ArrayList<BluetoothDevice>) Utils.removeD(itemSearch);
            }
            if (itemSearch.size() > 0 && popupWindow != null) {
                if (popupWindow.isShowing()) {//显示在屏幕上的时候显示已经搜索到的蓝牙设备
                    adapterSearch.notifyDataSetChanged();//刷新适配器
                    recyclerSearch.setVisibility(View.VISIBLE);//将搜索列表显示
                }
            } else if (itemSearch.size() == 0) {//结束扫描无设备时
                if (recyclerSearch != null) {
                    //已匹配的列表 //动态设置recycler的宽高
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) recyclerSearch.getLayoutParams();
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;


                    ArrayList<BluetoothDeviceItem> items = new ArrayList<>();
                    items.add(new BluetoothDeviceItem("暂未搜索到可用设备", "", 0, 0, null, null));
                    RecyclerviewAdapter adapter = new RecyclerviewAdapter(mContext, items, 0);
                    recyclerSearch.setAdapter(adapter);
                    recyclerView.setLayoutParams(lp);//动态宽高
                    adapter.notifyDataSetChanged();//刷新适配器
                    recyclerSearch.setVisibility(View.VISIBLE);//将搜索列表显示
                }
            }

            if(tv_qt != null){
                tv_qt.setText("其他可用设备");
            }
            //当设备结束扫描时将所用搜索到的蓝牙条目显示
            ToastUtil.showToast("搜索完成");
        }

        @Override
        public void onScanning(BluetoothDevice device) {
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {//将没有配对的搜索结果显示
                //将搜索到的蓝牙数据添加
                if (device.getName() != null && device.getAddress() != null) {//不要没有名称的蓝牙设备
                    MainActivity.itemSearch.add(device);
                }
                if (itemSearch.size() > 0) {
                    LogUtil.e(TAG, itemSearch.toString());
                }
            }
            LogUtil.e(TAG, "发现设备...");
        }

        @Override
        public void onStateChanged(BluetoothDevice device) {
            String s = "";
            switch (device.getBondState()) {
                case BluetoothDevice.BOND_BONDING:
                    Log.w(TAG, "正在配对......" + device.getName());
                    s = "正在配对" + device.getName();
                    Message message = Message.obtain();
                    message.what = 2;
                    message.arg1 = 1;//正在配对
                    message.obj = device;
                    mHandler.sendMessage(message);
                    break;
                case BluetoothDevice.BOND_BONDED:
                    Log.w(TAG, "配对完成" + device.getName());
                    s = "配对完成" + device.getName();
                    Message message2 = Message.obtain();
                    message2.what = 2;
                    message2.obj = device;
                    message2.arg1 = 2;//配对完成
                    mHandler.sendMessage(message2);

                    break;
                case BluetoothDevice.BOND_NONE:
                    Log.w(TAG, "取消配对" + device.getName());
                    s = "取消配对" + device.getName();
                    Message message3 = Message.obtain();
                    message3.what = 2;
                    message3.arg1 = 3;//取消配对
                    message3.obj = device;
                    mHandler.sendMessage(message3);
                default:
                    break;
            }
            LogUtil.e(TAG, "设备绑定状态改变...");
            ToastUtil.showToast("设备绑定状态改变..." + s);
        }

        @Override
        public void onSocketServerBreak(BluetoothDevice device) {
            Log.e(TAG, "onSocketServerBreak: "+device.getName() );
            //服务端断开
            LogUtil.e("蓝牙连接被切断", "name" + device.getName());
            //释放socket
            try {
                BtBase.isRead = false;
                if(mSocket!=null){
                    mSocket.close();
                }
                notifyUI(BtBase.Listener.DISCONNECTED, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Constants.bluetoothState = "蓝牙连接：未连接";
            State = false;
            if (tv_connect != null) {
                tv_connect.setText(Constants.bluetoothState);
            }
            LogUtil.e(TAG + "HJKz", "服务端将蓝牙断开了");
        }
    };


    /**
     * 配对蓝牙设备
     */
    private void pinTargetDevice(int position) {
        //在配对之前，停止搜索
        mBluetoothAdapter.cancelDiscovery();
        //获取要匹配的BluetoothDevice对象，后边的deviceList是你本地存的所有对象
        BluetoothDevice device = itemSearch.get(position);
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {//没配对才配对
            try {
                LogUtil.e(TAG, "开始配对...");
                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                Boolean returnValue = (Boolean) createBondMethod.invoke(device);
                if (returnValue) {
                    LogUtil.e(TAG, "配对请求建立成功...");
                    ToastUtil.showToast("配对请求建立成功");
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 取消配对（取消配对成功与失败通过广播返回 也就是配对失败）
     *
     * @param device
     */
    public void cancelPinBule(BluetoothDevice device) {
        Log.d(TAG, "attemp to cancel bond:" + device.getName());
        try {
            Method removeBondMethod = device.getClass().getMethod("removeBond");
            Boolean returnValue = (Boolean) removeBondMethod.invoke(device);
            returnValue.booleanValue();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "attemp to cancel bond fail!");
        }
//        //如果此方法取消不了配对，就跳转到系统蓝牙设置手动取消
//        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁广播
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        //关闭蓝牙连接
        if (mClient != null) {
            mClient.close();
        }
    }

    @Override
    public void socketNotify(int state, Object obj) {//蓝牙连接回调
        if (isDestroyed())
            return;
        String msg = null;
        switch (state) {
            case BtBase.Listener.CONNECTED:
                BluetoothDevice dev = (BluetoothDevice) obj;
                msg = String.format("与%s(%s)连接成功", dev.getName(), dev.getAddress());
                Constants.bluetoothState = "蓝牙连接：已连接";
                try {
                    DBManager dbManager = new DBManager(this);
                    dbManager.insertBluetoothDeviceBean(new BluetoothDeviceBean(dev.getName(), dev.getAddress(), dev.getBondState() + "", dev.getType() + ""));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
//                App.toast(msg, 0);
                break;
            case BtBase.Listener.DISCONNECTED:
                Constants.bluetoothState = "蓝牙连接：未连接";
                msg = "连接断开";
//                App.toastS(msg, 0);
                break;
            case BtBase.Listener.MSG:
                msg = String.format("\n%s", obj);
                msg = "参数下发";
                App.toast(msg, 0);
                break;
        }
        LogUtil.e("socketNotify", "socketNotify: " + msg);
    }

    @Override
    public void onBackPressed() {//重写返回键方法
//        if(receiver != null){
//            unregisterReceiver(receiver);
//        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //如果进入后台声音则关闭
        SystemTTS.isOpen = false;
        if(iv_audio != null){
            iv_audio.setImageResource(R.mipmap.noaudio);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationValueA2 = null;
        new Timer().schedule(new TimerTask() {//检测上次是否下发指令配置成功
            @Override
            public void run() {
                if(locationValueA2 != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_device_state.setText("设备状态：配置成功");
                        }
                    });
                }else {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(locationValueA2 != null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_device_state.setText("设备状态：配置成功");
                                    }
                                });
                            }else {
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_device_state.setText("设备状态：未配置");
                                            }
                                        });
                                    }
                                }, 10000);

                            }
                        }
                    }, 10000);

                }
            }
        }, 10000);
    }
}