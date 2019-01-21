package com.hanrx.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanrx.mobilesafe.R;

public class HomeActivity extends AppCompatActivity {

    private GridView gv_home;
    private String [] mTitleStrs;
    private int[] mDrawableIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        //初始化数据
        initData();
    }

    private void initUI() {
        gv_home = findViewById(R.id.gv_home);
    }

    private void initData() {
        //准备数据(文字和图片)
        mTitleStrs = new String[] {
                "手机防盗","通信卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级工具",
                "设置中心"
        };
        mDrawableIds = new int[] {
                R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps,
                R.drawable.home_taskmanager,R.drawable.home_netmanager,R.drawable.home_trojan,
                R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings
        };
        //九宫格控件设置数据适配器
        gv_home.setAdapter(new MyAdapter());
        //注册九宫格单个条目点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:

                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            //条目的总数
            return mTitleStrs.length;
        }

        @Override
        public Object getItem(int i) {
            return mTitleStrs[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = View.inflate(getApplicationContext(),R.layout.gridview_item,null);
            TextView tv_title = view.findViewById(R.id.tv_title);
            ImageView iv_icon = view.findViewById(R.id.iv_icon);
            tv_title.setText(mTitleStrs[i]);
            iv_icon.setBackgroundResource(mDrawableIds[i]);
            return view;
        }
    }
}
