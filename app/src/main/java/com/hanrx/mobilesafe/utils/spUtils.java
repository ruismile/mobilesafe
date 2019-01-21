package com.hanrx.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class spUtils {
    private static SharedPreferences sp;

    /**
     *  写入boolean至sp
     * @param ctx 上下文环境
     * @param key 存储节点名称
     * @param value 存储节点值
     */
    public static void putBoolean(Context ctx, String key, boolean value) {
        //存储节点文件名称，读写方式
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     *  从sp中读取值
     * @param ctx 上下文环境
     * @param key 存储节点名称
     * @param defValue 没有此节点默认值
     * @return 默认值或存储节点的值
     */
    public static boolean getBoolean(Context ctx, String key, boolean defValue) {
        //存储节点文件名称，读写方式
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }
}
