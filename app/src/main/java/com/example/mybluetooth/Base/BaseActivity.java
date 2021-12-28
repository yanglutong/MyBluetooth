package com.example.mybluetooth.Base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybluetooth.util.ToastUtil;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;

import java.util.ArrayList;

/**
 * @author: 小杨同志
 * @date: 2021/12/10
 */
public abstract class BaseActivity  extends AppCompatActivity {
    private static String TAG = "BaseActivity";
    protected static Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutXml());//由子类实现返回布局
        mContext=this;//获取上下文
        baseFindView();//先查找控件
        baseData();//初始化资源
    }


    protected interface PermissionsResult {//权限结果返回
        void   permissionsOk(boolean b,String str);//检查结果返回 b true 代表全部授权 false则没有
        void   permissionsRequest(boolean b,Permission[] allPermissions);//权限申请结果返回 b true 代表权限申请成功 false则失败
    }
    private ArrayList<Permission> list;
    protected void requestPermissions(String [] permissions, final PermissionsResult permissionsResult){
                SoulPermission.setDebug(true);//设置debug模式(看日志打印)  SoulPermission
        //先检查是否有此权限
        //you can also use checkPermissions() for a series of permissions
        Permission[] per = SoulPermission.getInstance().checkPermissions(permissions);
        if(per.length>0){
            list = new ArrayList<>();
            for (Permission permission : per) {
                if(!permission.isGranted()){//如果有未申请的权限就添加到集合里
                    list.add(permission);
                }
            }
        }
        if(list != null && list.size()>0){//有未申请的权限
            permissionsResult.permissionsOk(false,"有未申请的权限");
            SoulPermission.getInstance().checkAndRequestPermissions(
                        Permissions.build(permissions),
                        //if you want do noting or no need all the callbacks you may use SimplePermissionsAdapter instead
                        new CheckRequestPermissionsListener() {
                            @Override
                            public void onAllPermissionOk(Permission[] allPermissions) {
                                permissionsResult.permissionsRequest(true, allPermissions);
                            }

                            @Override
                            public void onPermissionDenied(Permission[] refusedPermissions) {
                                permissionsResult.permissionsRequest(false, refusedPermissions);
                            }
                        });
        }else{
            permissionsResult.permissionsOk(true,"已有全部权限");
        }
    }

    protected abstract void baseData();

    protected abstract void baseFindView();

    protected abstract int getLayoutXml();
    /*程序退出界面时*/
    private long mPressedTime = 0;
    @Override
    public void onBackPressed() {//重写返回键方法
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if ((mNowTime - mPressedTime) > 2000) {//比较两次按键时间差
            ToastUtil.showToast("再按一次退出程序");
            mPressedTime = mNowTime;
        } else {//退出程序
            this.finish();
            System.exit(0);//正常退出程序
        }
    }
}
