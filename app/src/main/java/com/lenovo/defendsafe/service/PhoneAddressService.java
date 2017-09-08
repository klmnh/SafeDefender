package com.lenovo.defendsafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.CommonUtils;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;

/**
 * Created by Lenovo on 2017/9/7 007.
 */

public class PhoneAddressService extends Service {

    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;
    private WindowManager windowManager;
    private View view_toast;
    private WindowManager.LayoutParams params;
    private boolean isPhoneState = false;
    private String address;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    String phoneNumber = (String) msg.obj;
                    if (tv_toast != null) {
                        tv_toast.setText(phoneNumber);
                    }
                    break;
            }
        }
    };
    private TextView tv_toast;
    private int[] styleDrawable;
    private int width;
    private int height;
    private OutGoingReceiver outGoingReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        width = windowManager.getDefaultDisplay().getWidth();
        height = windowManager.getDefaultDisplay().getHeight();

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

        outGoingReceiver = new OutGoingReceiver();
        registerReceiver(outGoingReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (telephonyManager != null && myPhoneStateListener != null) {
            telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (outGoingReceiver != null){
            unregisterReceiver(outGoingReceiver);
        }
        super.onDestroy();
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (isPhoneState && windowManager != null && view_toast != null) {
                        windowManager.removeView(view_toast);
                    }
                    isPhoneState = false;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    showCustomToast(incomingNumber);
                    break;
            }
        }

        private void showCustomToast(String incomingNumber) {
            if (params == null) {
                params = new WindowManager.LayoutParams();
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                params.format = PixelFormat.TRANSLUCENT;
                params.type = WindowManager.LayoutParams.TYPE_PHONE;
                params.setTitle("Toast");
                params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                params.gravity = Gravity.LEFT | Gravity.TOP;
            }

            if (view_toast == null) {
                view_toast = View.inflate(getApplicationContext(), R.layout.view_toast, null);
                tv_toast = (TextView) view_toast.findViewById(R.id.tv_toast);
                styleDrawable = new int[]{R.drawable.transparent, R.drawable.orange, R.drawable.blue, R.drawable.gray, R.drawable.green};
                view_toast.setOnTouchListener(new View.OnTouchListener() {
                    private int startX, startY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                startX = (int) event.getRawX();
                                startY = (int) event.getRawY();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                int moveX = (int) event.getRawX();
                                int moveY = (int) event.getRawY();

                                int disX = (moveX - startX);
                                int disY = (moveY - startY);

                                params.x = params.x + disX;
                                params.y = params.y + disY;

                                if (params.x < 0) {
                                    params.x = 0;
                                }
                                if (params.y < 0) {
                                    params.y = 0;
                                }
                                if (params.x > width - view_toast.getWidth()) {
                                    params.x = width - view_toast.getWidth();
                                }
                                if (params.y > height - 40 - view_toast.getHeight()) {
                                    params.y = height - 40 - view_toast.getHeight();
                                }

                                windowManager.updateViewLayout(view_toast, params);

                                startX = moveX;
                                startY = moveY;
                                break;
                            case MotionEvent.ACTION_UP:
                                SPUtils.putInt(getApplicationContext(), ConstantValue.PHONE_POSITION_X, params.x);
                                SPUtils.putInt(getApplicationContext(), ConstantValue.PHONE_POSITION_Y, params.y);
                                break;
                        }
                        return true;
                    }
                });
            }
            tv_toast.setBackgroundResource(styleDrawable[SPUtils.getInt(getApplicationContext(), ConstantValue.SETTINGS_PHONE_Style, 0)]);
            params.x = SPUtils.getInt(getApplicationContext(), ConstantValue.PHONE_POSITION_X, 0);
            params.y = SPUtils.getInt(getApplicationContext(), ConstantValue.PHONE_POSITION_Y, 0);
            windowManager.addView(view_toast, params);
            isPhoneState = true;
            final String phoneNumber = incomingNumber;
            new Thread() {
                @Override
                public void run() {
                    address = CommonUtils.GetPhoneAddress(getApplicationContext(), phoneNumber);
                    Message message = Message.obtain();
                    message.what = 100;
                    message.obj = address;
                    handler.sendMessage(message);
                }
            }.start();
        }
    }

    private class OutGoingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String phoneNumber = getResultData();
            if(myPhoneStateListener != null) {
                myPhoneStateListener.showCustomToast(phoneNumber);
            }
        }
    }
}
