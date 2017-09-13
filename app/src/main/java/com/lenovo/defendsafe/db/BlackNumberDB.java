package com.lenovo.defendsafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lenovo on 2017/9/9 009.
 */

public class BlackNumberDB extends SQLiteOpenHelper {

    private String createTable_BlackNumber = "create table blackNumber" +
            "(id Integer Primary Key autoincrement, phoneNumber varchar(20) not null, mode varchar(5))";

    public BlackNumberDB(Context context) {
        super(context, "blackNumber.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable_BlackNumber);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
