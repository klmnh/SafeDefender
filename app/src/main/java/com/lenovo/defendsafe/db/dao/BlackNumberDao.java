package com.lenovo.defendsafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lenovo.defendsafe.db.BlackNumberDB;
import com.lenovo.defendsafe.db.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/9/9 009.
 */

public class BlackNumberDao {
    private static BlackNumberDao blackNumberDao;
   private BlackNumberDB blackNumberDB;
    private List<BlackNumberInfo> blackNumberInfoArrayList = new ArrayList<BlackNumberInfo>();

    private BlackNumberDao(Context context){
        blackNumberDB = new BlackNumberDB(context);
    }

    /**
     * @param context 上下文环境
     * @return
     */
    public static BlackNumberDao getInstance(Context context){
        if (blackNumberDao == null){
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    /**
     * 插入黑名单
     * @param phoneNumber 要拦截的手机号码
     * @param mode 拦截类型（1短信 2 电话 3短信和电话）
     */
    public void insert(String phoneNumber, String mode){
        SQLiteDatabase db = blackNumberDB.getWritableDatabase();

        ContentValues cobtentValues = new ContentValues();
        cobtentValues.put("phoneNumber", phoneNumber);
        cobtentValues.put("mode", mode);

        db.insert("blackNumber", null, cobtentValues);
        db.close();
    }

    /**
     * 删除黑名单
     * @param phoneNumber 要拦截的手机号码
     */
    public void delete(String phoneNumber){
        SQLiteDatabase db = blackNumberDB.getWritableDatabase();

        db.delete("blackNumber", "phoneNumber = ?", new String[]{phoneNumber});
        db.close();
    }

    /**
     * 更新黑名单
     * @param phoneNumber 要拦截的手机号码
     * @param mode 拦截类型（1短信 2 电话 3短信和电话）
     */
    public void update(String phoneNumber, String mode){
        SQLiteDatabase db = blackNumberDB.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("phoneNumber", phoneNumber);
        contentValues.put("mode", mode);

        db.update("blackNumber", contentValues, "phoneNumber = ?", new String[]{phoneNumber});
        db.close();
    }

    /**
     * 查询全部黑名单
     */
    public List<BlackNumberInfo> queryAll(){
        SQLiteDatabase db = blackNumberDB.getReadableDatabase();

        Cursor cursor = db.query("blackNumber", new String[]{"phoneNumber", "mode"}, null,null,null,null, "id desc");
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setPhoneNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getInt(1));
            blackNumberInfoArrayList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberInfoArrayList;
    }

    /**
     * 查询pageIndex页的黑名单
     */
    public List<BlackNumberInfo> query(int pageIndex){
        SQLiteDatabase db = blackNumberDB.getReadableDatabase();

        Cursor cursor = db.rawQuery("select phoneNumber, mode from blackNumber order by id desc limit ?, ?", new String[] { pageIndex + "", (pageIndex + 11) + ""});
        blackNumberInfoArrayList.clear();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setPhoneNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getInt(1));
            blackNumberInfoArrayList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberInfoArrayList;
    }

    /**
     * 查询黑名单的总个数
     */
    public int getCount(){
        SQLiteDatabase db = blackNumberDB.getReadableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from blackNumber",null);
        if (cursor.moveToNext()){
            count = (cursor.getInt(0));
        }
        cursor.close();
        db.close();

        return count;
    }

    /**
     * 查询黑名单的类型
     */
    public int getMode(String phoneNumber){
        SQLiteDatabase db = blackNumberDB.getReadableDatabase();
        int mode = 0;
        Cursor cursor = db.query("blackNumber", new String[]{"mode"}, "phoneNumber = ?",new String[]{phoneNumber},null,null, "id desc");
        if (cursor.moveToNext()){
            mode = (cursor.getInt(0));
        }
        cursor.close();
        db.close();

        return mode;
    }
}
