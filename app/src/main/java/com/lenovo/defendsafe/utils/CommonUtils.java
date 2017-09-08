package com.lenovo.defendsafe.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Lenovo on 2017/9/3 003.
 */

public class CommonUtils {

    private static MessageDigest messageDigest;

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

    /**
     * @param strPwd 要加密的密码
     * @return 加密后的字符串
     */
    public static String MD5Encoder(String strPwd) {

        try {
            if(messageDigest == null) {
                messageDigest = MessageDigest.getInstance("MD5");
            }

            strPwd += "qweasdzxcqazwsxedc";

            byte[] bytes = messageDigest.digest(strPwd.getBytes());

            StringBuilder stringBuilder = new StringBuilder();
            for (byte bt:
                 bytes) {

                int i = bt & 0xff;
                String strHex = Integer.toHexString(i);
                if (strHex.length() < 2){
                    strHex = "0" + strHex;
                }

                stringBuilder.append(strHex);
            }

            return stringBuilder.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String GetPhoneAddress(Context context, String phoneNumber) {
        String regularExpression = "^1[3-8]\\d{9}$";
        String address = "未知号码";

        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir().getAbsolutePath() + "/telocation.db", null, SQLiteDatabase.OPEN_READONLY);
        if (phoneNumber.matches(regularExpression)) {
            phoneNumber = phoneNumber.substring(0, 7);

            Cursor cursor = db.query("mob_location", new String[]{"location"}, "_id = ?", new String[]{phoneNumber}, null, null, null);
            if (cursor.moveToNext()) {
                address = cursor.getString(0);
            }
        } else {
            switch (phoneNumber.length()) {
                case 3:
                    address = "报警电话";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "服务电话";
                    break;
                case 7:
                    address = "本地电话";
                    break;
                case 8:
                    address = "本地电话";
                    break;
                case 11:
                    String area = phoneNumber.substring(1, 3);
                    Cursor cursor = db.query("tel_location", new String[]{"location"}, "_id = ?", new String[]{area}, null, null, null);
                    if (cursor.moveToNext()) {
                        address = cursor.getString(0);
                    } else {
                        address = "未知号码";
                    }
                    break;
                case 12:
                    String area1 = phoneNumber.substring(1, 4);
                    Cursor cursor1 = db.query("tel_location", new String[]{"location"}, "_id = ?", new String[]{area1}, null, null, null);
                    if (cursor1.moveToNext()) {
                        address = cursor1.getString(0);
                    } else {
                        address = "未知号码";
                    }
                    break;
            }
        }

        return address;
    }
}
