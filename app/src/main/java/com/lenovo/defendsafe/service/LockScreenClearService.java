package com.lenovo.defendsafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.lenovo.defendsafe.utils.CommonUtils;

/**
 * Created by Lenovo on 2017/9/11 011.
 */

public class LockScreenClearService extends Service{

    private LockScreenReceiver lockScreenReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        lockScreenReceiver = new LockScreenReceiver();
        registerReceiver(lockScreenReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lockScreenReceiver != null){
            unregisterReceiver(lockScreenReceiver);
        }
    }

    private class LockScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            CommonUtils.KillAllProcess(getApplicationContext());
        }
    }
}
