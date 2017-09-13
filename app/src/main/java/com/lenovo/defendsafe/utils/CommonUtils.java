package com.lenovo.defendsafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Debug;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Xml;
import android.widget.Toast;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.db.domain.AppInfo;
import com.lenovo.defendsafe.db.domain.ProcessInfo;

import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/9/3 003.
 */

public class CommonUtils {

    private static MessageDigest messageDigest;
    private Cursor cursor;

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
        } finally {
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
     *
     * @param context 上下文
     * @param str     显示字符串
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
            if (messageDigest == null) {
                messageDigest = MessageDigest.getInstance("MD5");
            }

            strPwd += "qweasdzxcqazwsxedc";

            byte[] bytes = messageDigest.digest(strPwd.getBytes());

            StringBuilder stringBuilder = new StringBuilder();
            for (byte bt :
                    bytes) {

                int i = bt & 0xff;
                String strHex = Integer.toHexString(i);
                if (strHex.length() < 2) {
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
            cursor.close();
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
                    cursor.close();
                    break;
                case 12:
                    String area1 = phoneNumber.substring(1, 4);
                    Cursor cursor1 = db.query("tel_location", new String[]{"location"}, "_id = ?", new String[]{area1}, null, null, null);
                    if (cursor1.moveToNext()) {
                        address = cursor1.getString(0);
                    } else {
                        address = "未知号码";
                    }
                    cursor1.close();
                    break;
            }
        }
        db.close();
        return address;
    }

    public static List<NumberGroup> GetCommonNumber(Context context) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir().getAbsolutePath() + "/commonnum.db", null, SQLiteDatabase.OPEN_READONLY);

        List<NumberGroup> numberGroupList = new ArrayList<NumberGroup>();
        Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            NumberGroup numberGroup = new NumberGroup();
            numberGroup.name = cursor.getString(0);
            numberGroup.idx = cursor.getString(1);
            numberGroup.childList = GetCommonNumber(context, numberGroup.idx);

            numberGroupList.add(numberGroup);
        }
        cursor.close();
        db.close();

        return numberGroupList;
    }

    public static List<NumberChild> GetCommonNumber(Context context, String idx) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir().getAbsolutePath() + "/commonnum.db", null, SQLiteDatabase.OPEN_READONLY);

        List<NumberChild> numberChildList = new ArrayList<NumberChild>();
        Cursor cursor = db.query("table" + idx, new String[]{"_id", "number", "name"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            NumberChild numberChild = new NumberChild();
            numberChild.id = cursor.getString(0);
            numberChild.number = cursor.getString(1);
            numberChild.name = cursor.getString(2);

            numberChildList.add(numberChild);
        }
        cursor.close();
        db.close();

        return numberChildList;
    }

    public static List<String> GetAntiVirusList(Context context) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir().getAbsolutePath() + "/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);

        List<String> list = new ArrayList<String>();
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        while (cursor.moveToNext()) {

            list.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 备份短信
     *
     * @param context   上下文环境
     * @param smsBackup
     */
    public static void SMSBackup(Context context, ISMSBackup smsBackup) {

        Cursor cursor = null;
        FileOutputStream fos = null;
        File file = new File(context.getFilesDir(), "smsBackup.xml");
        try {
            fos = new FileOutputStream(file);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            xmlSerializer.setOutput(fos, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null, "smss");

            int index = 0;
            cursor = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "date", "type", "body"}, null, null, null);
            if (smsBackup != null) {
                smsBackup.setMax(cursor.getCount());
            }
            while (cursor.moveToNext()) {
                xmlSerializer.startTag(null, "sms");

                xmlSerializer.startTag(null, "address");
                xmlSerializer.text(cursor.getString(0));
                xmlSerializer.endTag(null, "address");

                xmlSerializer.startTag(null, "date");
                xmlSerializer.text(cursor.getString(1));
                xmlSerializer.endTag(null, "date");

                xmlSerializer.startTag(null, "type");
                xmlSerializer.text(cursor.getString(2));
                xmlSerializer.endTag(null, "type");

                xmlSerializer.startTag(null, "body");
                xmlSerializer.text(cursor.getString(3));
                xmlSerializer.endTag(null, "body");

                xmlSerializer.endTag(null, "sms");

                index++;
                Thread.sleep(500);
                if (smsBackup != null) {
                    smsBackup.SetProgress(index);
                }
            }

            xmlSerializer.endTag(null, "smss");
            xmlSerializer.endDocument();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取磁盘可用空间
     *
     * @param context
     * @param path
     * @return
     */
    public static String GetAvailableSpace(Context context, String path) {
        StatFs statFs = new StatFs(path);
        long totalSize = (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
        return Formatter.formatFileSize(context.getApplicationContext(), totalSize);
    }

    /**
     * 获取进程总数
     *
     * @param context
     * @return
     */
    public static int GetProcessCount(Context context) {
        ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listProcess = activityMgr.getRunningAppProcesses();
        return listProcess.size();
    }

    public static String GetProcessMemory(Context context) {
        ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityMgr.getMemoryInfo(memoryInfo);

        return Formatter.formatFileSize(context, memoryInfo.availMem) + "/" + Formatter.formatFileSize(context, memoryInfo.totalMem);
    }

    public static List<ProcessInfo> GetProcessInfoList(Context context) {
        List<ProcessInfo> listProcessInfo = new ArrayList<ProcessInfo>();
        ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager packageMgr = context.getPackageManager();

        List<ActivityManager.RunningAppProcessInfo> listProcess = activityMgr.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo :
                listProcess) {
            ProcessInfo proInfo = new ProcessInfo();
            proInfo.packageName = processInfo.processName;
            Debug.MemoryInfo memoryIInfo = activityMgr.getProcessMemoryInfo(new int[]{processInfo.pid})[0];
            proInfo.memSize = memoryIInfo.getTotalPrivateDirty() * 1024;

            try {
                ApplicationInfo applicationInfo = packageMgr.getApplicationInfo(proInfo.packageName, 0);
                proInfo.Name = applicationInfo.loadLabel(packageMgr).toString();
                proInfo.icon = applicationInfo.loadIcon(packageMgr);
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    proInfo.isSystem = true;
                } else {
                    proInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                proInfo.Name = processInfo.processName;
                proInfo.icon = context.getResources().getDrawable(R.mipmap.ic_launcher);
                proInfo.isSystem = true;
            }

            listProcessInfo.add(proInfo);
        }

        return listProcessInfo;
    }

    /**
     * @param context 上下文环境
     * @return 手机安装应用的相关信息
     */
    public static List<AppInfo> getAppInfoList(Context context) {
        List<AppInfo> listAppInfo = new ArrayList<AppInfo>();
        PackageManager packageManager = context.getPackageManager();

        for (PackageInfo packageInfo : packageManager.getInstalledPackages(0)) {
            AppInfo appInfo = new AppInfo();
            appInfo.packageName = packageInfo.packageName;
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            appInfo.Name = applicationInfo.loadLabel(packageManager).toString();
            appInfo.icon = applicationInfo.loadIcon(packageManager);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                appInfo.isSystem = true;
            } else {
                appInfo.isSystem = false;
            }
            if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                appInfo.isSDcard = true;
            } else {
                appInfo.isSDcard = false;
            }
            listAppInfo.add(appInfo);
        }
        return listAppInfo;
    }

    /**
     * 杀死进程
     *
     * @param context
     * @param processInfo
     */
    public static void KillProcess(Context context, ProcessInfo processInfo) {
        ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityMgr.killBackgroundProcesses(processInfo.packageName);
    }

    public static void KillAllProcess(Context context) {

        ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> listProcess = activityMgr.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo :
                listProcess) {
            if (!processInfo.processName.equals(context.getPackageName())) {
                activityMgr.killBackgroundProcesses(processInfo.processName);
            }
        }
    }

    public interface ISMSBackup {
        public void setMax(int count);

        public void SetProgress(int progress);
    }

    public static class NumberGroup {
        public String name;
        public String idx;
        public List<NumberChild> childList;
    }

    public static class NumberChild {
        public String id;
        public String number;
        public String name;
    }
}
