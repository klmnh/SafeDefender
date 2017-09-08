package com.lenovo.defendsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.CommonUtils;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;
import com.lenovo.defendsafe.views.SettingsItemView;

/**
 * Created by Lenovo on 2017/9/4 004.
 */

public class PhoneBak_Nav2 extends BasePhoneBak {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phonebak_nav2);


        Button btn_pre = (Button) findViewById(R.id.btn_pre);
        Button btn_next = (Button) findViewById(R.id.btn_next);

        btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPrePage();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowNextPage();
            }
        });

        final SettingsItemView siv_sim = (SettingsItemView) findViewById(R.id.sivSIM);
        String sim_number = SPUtils.getString(getApplicationContext(), ConstantValue.SIM_NUMBER, "");
        siv_sim.setChecked(!TextUtils.isEmpty(sim_number));

        siv_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = siv_sim.isChecked();
                siv_sim.setChecked(!isChecked);
                if (!isChecked){
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = telephonyManager.getSimSerialNumber();

                    SPUtils.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);

                }else {

                    SPUtils.Remove(getApplicationContext(), ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

    @Override
    protected void ShowPrePage() {
        Intent intent = new Intent(PhoneBak_Nav2.this, PhoneBak_Nav1.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_pre_in, R.anim.anim_pre_out);
    }

    @Override
    protected void ShowNextPage() {
        if(!TextUtils.isEmpty(SPUtils.getString(getApplicationContext(), ConstantValue.SIM_NUMBER, ""))) {
            Intent intent = new Intent(PhoneBak_Nav2.this, PhoneBak_Nav3.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.anim_next_in, R.anim.anim_next_out);
        }else {
            CommonUtils.ShowToastInfo(getApplicationContext(), "请绑定SIM卡");
        }
    }

}
