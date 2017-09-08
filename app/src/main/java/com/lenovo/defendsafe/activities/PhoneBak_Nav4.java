package com.lenovo.defendsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.CommonUtils;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;

/**
 * Created by Lenovo on 2017/9/4 004.
 */

public class PhoneBak_Nav4 extends BasePhoneBak {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phonebak_nav4);

        final CheckBox cb_Protect = (CheckBox) findViewById(R.id.cb_Protect);
        final boolean open_protect = SPUtils.getBoolean(getApplicationContext(), ConstantValue.OPEN_PROTECT, false);
        cb_Protect.setChecked(open_protect);
        if (open_protect){
            cb_Protect.setText("安全设置已开启");
        }else {
            cb_Protect.setText("安全设置已关闭");
        }

        cb_Protect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_PROTECT, isChecked);
                if (isChecked){
                    cb_Protect.setText("安全设置已开启");
                }else {
                    cb_Protect.setText("安全设置已关闭");
                }
            }
        });

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
    }

    @Override
    protected void ShowPrePage() {
        Intent intent = new Intent(PhoneBak_Nav4.this, PhoneBak_Nav3.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_pre_in, R.anim.anim_pre_out);
    }

    @Override
    protected void ShowNextPage() {
        boolean open_protect = SPUtils.getBoolean(getApplicationContext(), ConstantValue.OPEN_PROTECT, false);
        if (open_protect){
            Intent intent = new Intent(PhoneBak_Nav4.this, PhoneBak.class);
            startActivity(intent);

            SPUtils.putBoolean(getApplicationContext(), ConstantValue.PHONE_BAK_SET_COMPLETED, true);
            finish();
            overridePendingTransition(R.anim.anim_next_in, R.anim.anim_next_out);
        }
        else {
            CommonUtils.ShowToastInfo(getApplicationContext(), "请勾选复选框，开启防盗保护");
        }
    }
}
