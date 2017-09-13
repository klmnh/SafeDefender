package com.lenovo.defendsafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lenovo.defendsafe.db.AppLockerDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/9/9 009.
 */

public class AppLockerDao {
    private static AppLockerDao appLockerDao;
    private AppLockerDB appLockerDB;
    private Context context;

    private AppLockerDao(Context context) {
        appLockerDB = new AppLockerDB(context);
        this.context = context;
    }

    /**
     * @param context 上下文环境
     * @return
     */
    public static AppLockerDao getInstance(Context context) {
        if (appLockerDao == null) {
            appLockerDao = new AppLockerDao(context);
        }
        return appLockerDao;
    }

    public void insert(String packageName) {
        SQLiteDatabase db = appLockerDB.getWritableDatabase();

        ContentValues cobtentValues = new ContentValues();
        cobtentValues.put("packageName", packageName);

        db.insert("appLocker", null, cobtentValues);
        db.close();
        context.getContentResolver().notifyChange(Uri.parse("content://appLocker/change"), null);
    }

    public void delete(String packageName) {
        SQLiteDatabase db = appLockerDB.getWritableDatabase();

        db.delete("appLocker", "packageName = ?", new String[]{packageName});
        db.close();
        context.getContentResolver().notifyChange(Uri.parse("content://appLocker/change"), null);
    }

    public void update(String packageName) {
        SQLiteDatabase db = appLockerDB.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("phoneNumber", packageName);

        db.update("appLocker", contentValues, "packageName = ?", new String[]{packageName});
        db.close();
    }

    /**
     * 查询全部黑名单
     */
    public List<String> queryAll() {
        SQLiteDatabase db = appLockerDB.getReadableDatabase();

        List<String> list = new ArrayList<>();
        Cursor cursor = db.query("appLocker", new String[]{"packageName"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        cursor.close();
        db.close();

        return list;
    }

    public int getCount() {
        SQLiteDatabase db = appLockerDB.getReadableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from appLocker", null);
        if (cursor.moveToNext()) {
            count = (cursor.getInt(0));
        }
        cursor.close();
        db.close();

        return count;
    }
}
