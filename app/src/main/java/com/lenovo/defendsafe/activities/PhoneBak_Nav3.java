package com.lenovo.defendsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.CommonUtils;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;

/**
 * Created by Lenovo on 2017/9/4 004.
 */

public class PhoneBak_Nav3 extends BasePhoneBak {

    private EditText et_securitySIM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phonebak_nav3);

        et_securitySIM = (EditText) findViewById(R.id.et_securitySIM);
        String phone = SPUtils.getString(getApplicationContext(), ConstantValue.PHONE_NUMBER, "");
        et_securitySIM.setText(phone);

        Button btn_selectPhone = (Button) findViewById(R.id.btn_selectPhone);
        btn_selectPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneBak_Nav3.this, ContactListActivity.class);
                startActivityForResult(intent, 1);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            if (requestCode == 1) {
                String phone = data.getStringExtra("phone");
                phone = phone.replace("-", "").replace(" ", "").trim();
                et_securitySIM.setText(phone);
                SPUtils.putString(getApplicationContext(), ConstantValue.PHONE_NUMBER, phone);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void ShowPrePage() {
        Intent intent = new Intent(PhoneBak_Nav3.this, PhoneBak_Nav2.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_pre_in, R.anim.anim_pre_out);
    }

    @Override
    protected void ShowNextPage() {
        String phone = et_securitySIM.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(PhoneBak_Nav3.this, PhoneBak_Nav4.class);
            startActivity(intent);
            SPUtils.putString(getApplicationContext(), ConstantValue.PHONE_NUMBER, phone);
            finish();
            overridePendingTransition(R.anim.anim_next_in, R.anim.anim_next_out);
        } else {
            CommonUtils.ShowToastInfo(getApplicationContext(), "请输入安全号码");
        }
    }
}
