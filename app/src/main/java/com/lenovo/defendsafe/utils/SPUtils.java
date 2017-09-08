package com.lenovo.defendsafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lenovo.defendsafe.activities.SettingsActivity;

/**
 * Created by Lenovo on 2017/9/3 003.
 */

public class SPUtils {

    private static SharedPreferences sharedPreferences;

    /**
     * 写入boolean类型值到sp中
     * @param context 上下文环境
     * @param key 节点名称
     * @param value 节点值
     */
    public static void putBoolean(Context context, String key, boolean value){
        if (sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("config", context.MODE_PRIVATE);
        }

        sharedPreferences.edit().putBoolean(key, value).commit();
    }
    /**
     * 从sp中获取boolean类型值
     * @param context 上下文环境
     * @param key 节点名称
     * @param defValue 节点不存在时的默认值
     */
    public static boolean getBoolean(Context context, String key, boolean defValue){
        if (sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("config", context.MODE_PRIVATE);
        }

        return sharedPreferences.getBoolean(key, defValue);
    }

    /**
     * 写入String类型值到sp中
     * @param context 上下文环境
     * @param key 节点名称
     * @param value 节点值
     */
    public static void putString(Context context, String key, String value){
        if (sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("config", context.MODE_PRIVATE);
        }

        sharedPreferences.edit().putString(key, value).commit();
    }
    /**
     * 从sp中获取String类型值
     * @param context 上下文环境
     * @param key 节点名称
     * @param defValue 节点不存在时的默认值
     */
    public static String getString(Context context, String key, String defValue){
        if (sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("config", context.MODE_PRIVATE);
        }

        return sharedPreferences.getString(key, defValue);
    }

    /**
     * 从sp中移除节点
     * @param context 上下文环境
     * @param key 节点名称
     */
    public static void Remove(Context context, String key) {
        if (sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("config", context.MODE_PRIVATE);
        }

        sharedPreferences.edit().remove(key).commit();
    }

    /**
     * 写入Int类型值到sp中
     * @param context 上下文环境
     * @param key 节点名称
     * @param value 节点值
     */
    public static void putInt(Context context, String key, int value){
        if (sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("config", context.MODE_PRIVATE);
        }

        sharedPreferences.edit().putInt(key, value).commit();
    }
    /**
     * 从sp中获取Int类型值
     * @param context 上下文环境
     * @param key 节点名称
     * @param defValue 节点不存在时的默认值
     */
    public static int getInt(Context context, String key, int defValue) {

        if (sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("config", context.MODE_PRIVATE);
        }

        return sharedPreferences.getInt(key, defValue);
    }
}
