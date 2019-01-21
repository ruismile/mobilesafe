package com.hanrx.mobilesafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hanrx.mobilesafe.R;
import com.hanrx.mobilesafe.view.SettingItemView;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUpdate();
    }

    private void initUpdate() {
        final SettingItemView siv_update = findViewById(R.id.siv_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果之前是选中，点击后变为未选中，之前是未选中点击后为选中
                //获取之前的选中状态
                boolean isCheck = siv_update.isCheck();
                siv_update.setCheck(!isCheck);
            }
        });
    }

}
