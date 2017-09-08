package com.lenovo.defendsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;

/**
 * Created by Lenovo on 2017/9/4 004.
 */

public class PhoneBak extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phonebak);

        TextView tv_securityPhone = (TextView) findViewById(R.id.tv_securityPhone);
        tv_securityPhone.setText(SPUtils.getString(getApplicationContext(), ConstantValue.PHONE_NUMBER, ""));

        Button btn_resetPhoneBak = (Button) findViewById(R.id.btn_ResetPhoneBak);

        btn_resetPhoneBak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneBak.this, PhoneBak_Nav1.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
