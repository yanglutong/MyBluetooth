package com.example.mybluetooth.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.mybluetooth.MainActivity;
import com.example.mybluetooth.R;
import com.example.mybluetooth.activity.DemoActivity;


/**
 * PopWindow弹窗
 * @author  dlong
 * created at 2019/3/14 11:05 AM
 */
public class DLPopupWindow<T> extends PopupWindow {

    /**
     * 定义一个接口
     */
    public interface OnItemClickListener{
        void OnClick(View  position);
    }

    /** 实例化 */
    private OnItemClickListener onItemClickListener = null;

    /**
     * 设置点击回调
     * @param on
     */
    public void setOnItemClickListener(OnItemClickListener on){
        this.onItemClickListener = on;
    }

    /** 微信样式 */
    public static final int STYLE_WEIXIN = 1;
    /** 默认样式 */
    public static final int STYLE_DEF = 2;

    /** 上下文 */
    private Context mContext;
    private LayoutInflater mInflater;
    private View mContentView;
    private int layout;
    /**
     * @param context 上下文
     * @param layout 布局
     * @param style 弹出样式
     * @param mainActivity 类名对象
     * @param popView 接口返回
     * @author lutong
     * @time 2021/12/14 16:15
     */

    public DLPopupWindow(Context context, int layout, int style, final MainActivity mainActivity, PopView popView){
        this.mContext = context;
        this.layout = layout;
        // 打气筒
        mInflater = LayoutInflater.from(mContext);
        // 打气
        mContentView = mInflater.inflate(layout,null, false);
        // 设置View
        setContentView(mContentView);
        // 设置宽与高
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置可以获取集点
        setFocusable(true);
        // 设置背景只有设置了这个才可以点击外边和BACK消失
        setBackgroundDrawable(new ColorDrawable());
        // 设置点击外边可以消失
        setOutsideTouchable(true);
        //进入退出动画
        setAnimationStyle(style);


        /**
         * 点击popupWindow让背景变暗
         */
        final WindowManager.LayoutParams lp = mainActivity.getWindow().getAttributes();
        lp.alpha = 0.3f;//代表透明程度，范围为0 - 1.0f
        mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mainActivity.getWindow().setAttributes(lp);

        /**
         * 退出popupWindow时取消暗背景
         */
        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                final WindowManager.LayoutParams lp = mainActivity.getWindow().getAttributes();
                lp.alpha = 1.0f;
                mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mainActivity.getWindow().setAttributes(lp);
            }
        });
        //通过接口将PopWindow布局返回到调用的activity
        popView.OnView(mContentView);
    }
    public interface PopView {
        void OnView(View view);
    }
}
