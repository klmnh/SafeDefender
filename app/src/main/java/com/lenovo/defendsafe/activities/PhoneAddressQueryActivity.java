package com.lenovo.defendsafe.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.CommonUtils;

/**
 * Created by Lenovo on 2017/9/7 007.
 */

public class PhoneAddressQueryActivity extends AppCompatActivity {

    private TextView tv_phoneAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phoneaddress);

        final EditText et_phoneNumber = (EditText) findViewById(R.id.et_phoneNumber);
        et_phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = et_phoneNumber.getText().toString().trim();
                QueryAddress(phoneNumber);

            }
        });
        tv_phoneAddress = (TextView) findViewById(R.id.tv_phoneAddress);

        Button btn_queryAddress = (Button) findViewById(R.id.btn_queryAddress);
        btn_queryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phoneNumber = et_phoneNumber.getText().toString().trim();
                if(!TextUtils.isEmpty(phoneNumber)) {
                    new Thread() {
                        @Override
                        public void run() {
                            QueryAddress(phoneNumber);
                        }
                    }.start();
                }else {
                    //抖动动画
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                    et_phoneNumber.startAnimation(shake);

                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(new long[]{500, 1000}, -1);
                }
            }
        });

    }

    private void QueryAddress(String phoneNumber) {
        final String address = CommonUtils.GetPhoneAddress(getApplicationContext(), phoneNumber);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_phoneAddress.setText(address);
            }
        });
    }

}
