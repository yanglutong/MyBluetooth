package com.example.mybluetooth.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mybluetooth.R;
import com.example.mybluetooth.util.ToastUtil;

import java.util.List;

public class DemoActivity extends AppCompatActivity {

    private Spinner city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Spinner spinner = (Spinner) findViewById(R.id.field_item_spinner_content);

        //资源转[]
        String meinv[] = getResources().getStringArray(R.array.meinv);
        //构造ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.layout_spinner_item_drop, meinv);
        //设置下拉样式以后显示的样式
        adapter.setDropDownViewResource(R.layout.my_drop_down_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //选择列表项的操作
                int position = adapterView.getSelectedItemPosition();
                ToastUtil.showToast(        spinner.getSelectedItem().toString()
                +"-----------"+position);
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