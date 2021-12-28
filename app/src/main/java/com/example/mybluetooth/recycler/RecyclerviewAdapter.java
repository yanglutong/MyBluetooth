package com.example.mybluetooth.recycler;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybluetooth.R;
import com.example.mybluetooth.bean.BluetoothDeviceItem;

import java.util.ArrayList;
import java.util.List;

public class RecyclerviewAdapter<T> extends RecyclerView.Adapter implements View.OnClickListener {
 
    private Context context;
    private List<T> data;
    private int type;
    /**
     * @param context 上下文
     * @param data   数据源
     * @param type   有无列表数据标识

     * @time 2021/12/15 17:30
     */

    public RecyclerviewAdapter(Context context, ArrayList<T> data,int type){
        this.context = context;
        this.data = data;
        this.type = type;
    }
 
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        RecyclerView.ViewHolder viewHolder = holder;
        if(type==1){// 有数据列表的集合
            viewHolder.itemView.setTag(position);//设置下标标识
            ArrayList<BluetoothDevice> items= (ArrayList<BluetoothDevice>) data;

            TextView tv_name = viewHolder.itemView.findViewById(R.id.tv_name);
            tv_name.setText(items.get(position).getName());

            TextView tv_mac = viewHolder.itemView.findViewById(R.id.tv_mac);
            tv_mac.setText(items.get(position).getAddress());
        }else if(type==0){//为0传入没有匹配列表或者搜索到列表的集合
            viewHolder.itemView.setTag(position);//设置下标标识
            ArrayList<BluetoothDeviceItem> items= (ArrayList<BluetoothDeviceItem>) data;

            TextView tv_name = viewHolder.itemView.findViewById(R.id.tv_name);
            tv_name.setText(items.get(position).getName());

            TextView tv_mac = viewHolder.itemView.findViewById(R.id.tv_mac);
            tv_mac.setText(items.get(position).getAddress());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View view) {
        if(itemOnClick!=null){
            itemOnClick.onItemClick(view, (Integer) view.getTag());//获取下标标识
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
 
        private TextView tv_name;
        private TextView tv_mac;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_mac = itemView.findViewById(R.id.tv_mac);

        }
    }

    public interface RecyclerOnItemOnClick{
        void onItemClick(View v, int position);
    }
    RecyclerOnItemOnClick itemOnClick;

    public void setItemOnClick(RecyclerOnItemOnClick itemOnClick) {
        this.itemOnClick = itemOnClick;
    }
}