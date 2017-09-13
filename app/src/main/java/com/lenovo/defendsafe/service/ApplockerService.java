package com.lenovo.defendsafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.lenovo.defendsafe.db.dao.AppLockerDao;

import java.util.List;

/**
 * Created by Lenovo on 2017/9/12 012.
 */

public class ApplockerService extends Service{

    private boolean isWatch = true;
    private ActivityManager activityManager;
    private List<String> listAppInfo;
    private MyContentObserver myContentObserver;
    private MySkipReceiver mySkipReceiver;
    private String skippackageName;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        myContentObserver = new MyContentObserver(new Handler());
        getApplicationContext().getContentResolver().registerContentObserver(Uri.parse("content://appLocker/change"), true, myContentObserver);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.lenovo.defendersafe.SKIP");

        mySkipReceiver = new MySkipReceiver();
        registerReceiver(mySkipReceiver, intentFilter);

        watchDog();
    }

    private void watchDog() {
        new Thread(){
            @Override
            public void run() {
                listAppInfo = AppLockerDao.getInstance(getApplicationContext()).queryAll();
                while (isWatch){
                    if(listAppInfo != null) {
                        ActivityManager.RunningTaskInfo runningTaskInfo = activityManager.getRunningTasks(1).get(0);
                        String packageName = runningTaskInfo.topActivity.getPackageName();
                        if (listAppInfo.contains(packageName)) {
                            if(!packageName.equals(skippackageName)) {
                                skippackageName = "";
                                Intent intent = new Intent();
                                intent.setAction("com.lenovo.defendsafe.watchDog");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("packageName", packageName);
                                startActivity(intent);
                            }
                        }
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isWatch = false;
        if(mySkipReceiver != null) {
            unregisterReceiver(mySkipReceiver);
        }
        if (myContentObserver != null){
            getContentResolver().unregisterContentObserver(myContentObserver);
        }
    }

    private class MyContentObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new Thread(){
                @Override
                public void run() {
                    listAppInfo = AppLockerDao.getInstance(getApplicationContext()).queryAll();
                }
            }.start();
        }
    }

    private class MySkipReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            skippackageName = intent.getStringExtra("packageName");
        }
    }
}
