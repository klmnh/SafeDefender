package com.lenovo.defendsafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Lenovo on 2017/9/7 007.
 */

public class ServiceUtils {

    public static boolean isRunning(Context context, String ServiceName){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = activityManager.getRunningServices(100);

        for (ActivityManager.RunningServiceInfo runningServiceInfo:
             runningServiceInfos) {
            if (ServiceName.equals(runningServiceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
