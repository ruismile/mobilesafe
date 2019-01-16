package com.hanrx.mobilesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hanrx.mobilesafe.R;
import com.hanrx.mobilesafe.utils.StreamUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private TextView tv_version_name;
    private int mLocalVersionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除当前activity头title
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        //初始化UI
        initUI();
        //初始化数据
        initData();
    }

    /**
     * 初始化UI方法  alt+shift+j
     */
    private void initUI() {
        tv_version_name = findViewById(R.id.tv_version_name);
    }

    /**
     * 获取数据
     */
    private void initData() {
        //1.应用版本名称
        tv_version_name.setText("版本名称：" + getVersionName());
        //检测(本地版本号和服务器版本号比对)是否有更新,如有更新提示用户下载
        //2.获取本地版本号
        mLocalVersionCode = getVersionCode();
        //3.获取服务器版本号(客户端发请求,服务端给相应,(json,xml))
        //http://www.oxxx.com/update74.jsom?key=value 返回200请求成功，流的方式将读取下来
        //json中内容包含：
        /* 更新版本的版本名称
         * 新版本的描述信息
         * 服务器版本号
         * 新版本apk下载地址
         */
        checkVersion();
    }

    /**
     * 检查版本号
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //发送请求获取数据
                    //封装url地址
                    //URL url = new URL("http://192.168.103.166/update.json");
                    //仅限模拟器访问电脑tomcat
                    URL url = new URL("http://10.0.2.2/update.json");
                    //开启一个链接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //设置常见请求参数(请求头)
                    //请求超时
                    connection.setConnectTimeout(2000);
                    //读取超时
                    connection.setReadTimeout(2000);
                    //请求方式 默认GET请求
                    //connection.setRequestMethod("GET");
                    //获取响应码
                    if(connection.getResponseCode() == 200) {
                        //以流的方式，将数据获取下来
                        InputStream is = connection.getInputStream();
                        //将流转换成字符串(工具类封装)
                        String json = StreamUtil.streamToString(is);
                        Log.i(TAG, json);
                        //JSON解析
                        JSONObject jsonObject = new JSONObject(json);
                        String versionName = jsonObject.getString("versionname");
                        String versionDes = jsonObject.getString("versionDes");
                        String versionCode = jsonObject.getString("versionCode");
                        String downloadUrl = jsonObject.getString("downloadUrl");

                        Log.i(TAG, versionName);
                        Log.i(TAG, versionDes);
                        Log.i(TAG, versionCode);
                        Log.i(TAG, downloadUrl);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        /*new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });*/
    }

    /** 返回版本号
     * @return 非0 则代表获取成功
     */
    private int getVersionCode() {
        //1.包管理者对象packageManager
        PackageManager pm = getPackageManager();
        //2.从包管理对象中获取版本信息,0代表基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //3.获取版本名称
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本名称
     * @return 应用版本名称 返回null代表异常
     */
    private String getVersionName() {
        //1.包管理者对象packageManager
        PackageManager pm = getPackageManager();
        //2.从包管理对象中获取版本信息,0代表基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //3.获取版本名称
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
