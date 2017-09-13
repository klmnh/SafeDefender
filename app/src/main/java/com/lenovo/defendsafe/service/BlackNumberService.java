package com.lenovo.defendsafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.lenovo.defendsafe.db.dao.BlackNumberDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Lenovo on 2017/9/7 007.
 */

public class BlackNumberService extends Service {

    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;
    private BlackNumberSMSReceiver blackNumberSMSReceiver;
    private MyContentObserver myContentObserver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);
        blackNumberSMSReceiver = new BlackNumberSMSReceiver();
        registerReceiver(blackNumberSMSReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (telephonyManager != null && myPhoneStateListener != null) {
            telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (blackNumberSMSReceiver != null){
            unregisterReceiver(blackNumberSMSReceiver);
        }
        if (myContentObserver != null){
            getContentResolver().unregisterContentObserver(myContentObserver);
        }
        super.onDestroy();
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    int mode = BlackNumberDao.getInstance(getApplicationContext()).getMode(incomingNumber);
                    if (mode == 2 || mode == 3) {
                        try {
                            Class<?> clazz = Class.forName("android.os.ServiceManager");
                            Method method = clazz.getMethod("getService", String.class);
                            IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                            iTelephony.endCall();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        //getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{incomingNumber});
                        myContentObserver = new MyContentObserver(incomingNumber, null);
                        getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"),true, myContentObserver);
                    }
                    break;
            }
        }
    }

    private class BlackNumberSMSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object obj:
                    pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                String originalAddr = sms.getOriginatingAddress();

                int mode = BlackNumberDao.getInstance(getApplicationContext()).getMode(originalAddr);
                if (mode == 1 || mode == 3){
                    abortBroadcast();
//                    Intent intent1 = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//                    intent1.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
//                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent1);
                }
            }
        }
    }

    private class MyContentObserver extends ContentObserver{
        private final String mPhoneNumber;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(String phoneNumber, Handler handler) {
            super(handler);
            this.mPhoneNumber = phoneNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{mPhoneNumber});
        }


    }
}
