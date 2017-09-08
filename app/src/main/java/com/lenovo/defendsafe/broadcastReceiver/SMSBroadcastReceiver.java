package com.lenovo.defendsafe.broadcastReceiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.service.LocationService;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;

/**
 * Created by Lenovo on 2017/9/6 006.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (SPUtils.getBoolean(context, ConstantValue.OPEN_PROTECT, false)){

            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object obj:
                 pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])obj);
                String originalAddr = sms.getOriginatingAddress();
                String body = sms.getMessageBody();
                if (body.contains("#*alarm*#")){
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.thz);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }

                if (body.contains("#*location*#")){

                    Intent intent1 = new Intent(context, LocationService.class);
                    context.startService(intent1);
                }

                if (body.contains("#*wipedata*#")){

                    DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
                    ComponentName componentName = new ComponentName(context, MyDeviceAdminReceiver.class);
                    if (devicePolicyManager.isAdminActive(componentName)){
                        devicePolicyManager.wipeData(0);
                    }
                }

                if (body.contains("#*lockscreen*#")){

                    DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
                    ComponentName componentName = new ComponentName(context, MyDeviceAdminReceiver.class);
                    if (devicePolicyManager.isAdminActive(componentName)){
                        devicePolicyManager.lockNow();
                        devicePolicyManager.resetPassword("123", 0);
                    }
                }
            }
        }
    }
}
