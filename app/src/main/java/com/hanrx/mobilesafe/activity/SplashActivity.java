package com.hanrx.mobilesafe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.hanrx.mobilesafe.R;
import com.hanrx.mobilesafe.utils.StreamUtil;
import com.hanrx.mobilesafe.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    /**
     * 更新新版本的状态码
     */
    private static final int UPDATE_VERSION = 100;
    /**
     * 进入主界面状态吗码
     */
    private static final int ENTER_HOME = 101;
    /**
     * URL地址出错状态码
     */
    private static final int URL_ERROR = 102;
    /**
     * IO出错状态码
     */
    private static final int IO_ERROR = 103;
    /**
     * JSON出错状态码
     */
    private static final int JSON_ERROR = 104;
    private TextView tv_version_name;
    private int mLocalVersionCode = 0;
    private String mVersionDes;
    private String mDownloadUrl;

    private  Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    //弹出对话框，提示用户更新
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    //进入应用程序主界面
                    enterHome();
                    break;
                case URL_ERROR:
                    ToastUtil.show(SplashActivity.this, "url异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(SplashActivity.this, "读取异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(SplashActivity.this, "json解析异常");
                    enterHome();
                    break;
            }
        }
    };
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
     * 弹出对话框，提示用户更新
     */
    protected void showUpdateDialog() {
        //对话框是依赖于activity存在的
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("版本更新");
        builder.setMessage(mVersionDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //下载apk,apk链接地址,downloadUrl
                downloadApk();
            }
        });
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //取消对话框, 进入主界面
                enterHome();
            }
        });
        //点击取消事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //即使用户点击取消也让用户进入主界面
                enterHome();
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    protected void downloadApk() {
        //apk下载链接地址，放置apk的所在路径
        //1.判断sd卡是否可用，是否挂载上
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //2.获取sd卡路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "mobilesafe.apk";
            //3.发送请求获取apk，并且放置到指定路径
            HttpUtils httpUtils = new HttpUtils();
            //4.发送请求传递参数(下载地址，下载应用放置的位置)
            httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功(下载成功放在sd卡中的apk)
                    Log.i(TAG, "下载成功");
                    File file = responseInfo.result;
                    //提示用户安装
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    //下载失败

                }
                //刚刚开始下载
                @Override
                public void onStart() {
                    Log.i(TAG, "刚刚开始下载");
                    super.onStart();
                }
                //下载中的方法(下载apk的大小，当前下载的位置，是否正在下载)
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    Log.i(TAG, "下载中.....");
                    Log.i(TAG, "total = " + total);
                    Log.i(TAG, "current = " + current);
                    Log.i(TAG, "isUplaoding = " + isUploading);
                    super.onLoading(total, current, isUploading);
                }
            });
        }
    }

    /**
     *  安装对应apk
     * @param file 安装文件
     */
    protected void installApk(File file) {
        //系统应用姐买你，源码，安装apk入口
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        /*//文件作为数据源
        intent.setData(Uri.fromFile(file));
        //设置安装的类型
        intent.setType("application/vnd.android.package-archive");*/
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }
    //开启一个activity后，返回结果调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 今日应用程序主界面
     */
    protected void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //在开启一个新的界面后,将导航界面关闭(导航界面只可见一次)
        finish();
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
        Log.i(TAG, "mLocalVersionCode = " + mLocalVersionCode);
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
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    //发送请求获取数据
                    //封装url地址
                    //URL url = new URL("http://192.168.103.166/update.json");
                    URL url = new URL("http://192.168.1.103/update.json");
                    //仅限模拟器访问电脑tomcat
                    //URL url = new URL("http://10.0.2.2/update.json");
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
                        String versionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDes");
                        String versionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");

                        Log.i(TAG, versionName);
                        Log.i(TAG, mVersionDes);
                        Log.i(TAG, versionCode);
                        Log.i(TAG, mDownloadUrl);

                        //比对版本号(服务器版本号)本地版本号,提示用户更新
                        Log.i(TAG, "mLocalVersionCode = " + mLocalVersionCode);
                        Log.i(TAG, "versionCode = " + Integer.parseInt(versionCode));
                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            Log.i(TAG, "enterDialog");
                            //提示用户更新,弹出对话框
                            msg.what = UPDATE_VERSION;
                        } else {
                            //进入应用程序主界面
                            Log.i(TAG, "enterHome");
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                    //指定睡眠时间,请求网络的时长超过四秒则不做处理
                    //请求网络的时长小于四秒,强制其睡眠满四秒
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 4000) {
                        try {
                            Thread.sleep(4000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
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
