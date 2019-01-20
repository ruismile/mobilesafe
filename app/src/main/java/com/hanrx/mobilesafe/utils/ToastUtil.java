package com.hanrx.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    /**
     * @param context 上下文环境
     * @param msg 打印内容
     */
    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, 0).show();
    }
}
