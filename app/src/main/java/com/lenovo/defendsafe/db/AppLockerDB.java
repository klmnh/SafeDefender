package com.lenovo.defendsafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lenovo on 2017/9/9 009.
 */

public class AppLockerDB extends SQLiteOpenHelper {

    private String createTable_appLocker = "create table appLocker" +
            "(id Integer Primary Key autoincrement, packageName varchar(100) not null)";

    public AppLockerDB(Context context) {
        super(context, "appLocker.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable_appLocker);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
