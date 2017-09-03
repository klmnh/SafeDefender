package com.lenovo.defendsafe.utils;

import android.content.Context;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Lenovo on 2017/9/3 003.
 */

public class CommonUtils {


    /**
     * @param inputStream 输入字节流
     * @return Json字符串
     */
    public static String StreamToString(InputStream inputStream) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] byteBuffer = new byte[1024];

        try {
            int len;
            while ((len = inputStream.read(byteBuffer)) != -1) {

                bos.write(byteBuffer, 0, len);
            }
            return bos.toString("gbk");
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 显示提示信息
     * @param context 上下文
     * @param str 显示字符串
     */
    public static void ShowToastInfo(Context context, String str) {

        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
