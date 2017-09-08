package com.lenovo.defendsafe.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lenovo.defendsafe.activities.SettingsActivity;
import com.lenovo.defendsafe.service.PhoneAddressService;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;
import com.lenovo.defendsafe.utils.ServiceUtils;

/**
 * Created by Lenovo on 2017/9/5 005.
 */

public class CheckPhoneBakBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String simSerialNumber = telephonyManager.getSimSerialNumber();

        String sim_number = SPUtils.getString(context, ConstantValue.SIM_NUMBER, "");

        if (SPUtils.getBoolean(context, ConstantValue.OPEN_PROTECT, false)) {
            if (!sim_number.equals(simSerialNumber)) {
                SmsManager smsManager = SmsManager.getDefault();
                String security_number = SPUtils.getString(context, ConstantValue.PHONE_NUMBER, "");
                if (!TextUtils.isEmpty(security_number)) {
                    smsManager.sendTextMessage(security_number, null, "sim changed!", null, null);
                }
            }
        }

        if (SPUtils.getBoolean(context, ConstantValue.SETTINGS_PHONE_ADDRESS, false)) {
            boolean isRunningService1 = ServiceUtils.isRunning(context, PhoneAddressService.class.getName());
            if(!isRunningService1) {
                Intent intent1 = new Intent(context, PhoneAddressService.class);
                context.startService(intent1);
            }
        }
    }
}
