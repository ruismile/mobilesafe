package com.hanrx.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanrx.mobilesafe.R;

public class SettingItemView extends RelativeLayout {

    private CheckBox cb_box;
    private TextView tv_des;
    private TextView tv_title;

    public SettingItemView(Context context) {
        this(context,null);
    }
    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //xml --> view将设置界面的一个条目转换成view对象
        View.inflate(context, R.layout.setting_item_view,this);
        tv_title = findViewById(R.id.tv_title);
        tv_des = findViewById(R.id.tv_des);
        cb_box = findViewById(R.id.cb_box);
    }

    /**
     *  判断是否开启的方法
     * @return 返回当前SettingItemView是否选中状态， true开启， false关闭
     */
    public boolean isCheck() {
        return  cb_box.isChecked();
    }

    /**
     * @param isCheck 是否作为开启的变量， 由点击过程中去传递
     */
    public void setCheck(boolean isCheck) {
        //当前条目在选择的过程中，cb_box选中状态也在跟着变化
        cb_box.setChecked(isCheck);
        if (isCheck) {
            //开启
            tv_des.setText("自动更新已开启");
        } else {
            //关闭
            tv_des.setText("自动更新已关闭");
        }
    }
}
