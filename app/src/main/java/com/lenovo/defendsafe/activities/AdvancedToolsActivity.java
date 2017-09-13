package com.lenovo.defendsafe.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.CommonUtils;

/**
 * Created by Lenovo on 2017/9/7 007.
 */

public class AdvancedToolsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_advancedtools);

        InitPhoneAdress();

        InitSMSBackup();

        InitCommonNumber();

        InitProgramLocker();
    }

    private void InitProgramLocker() {

        TextView tv_programLocker = (TextView) findViewById(R.id.tv_programLocker);
        tv_programLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdvancedToolsActivity.this, AppLockerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void InitCommonNumber() {

        TextView tv_queryPhoneNumber = (TextView) findViewById(R.id.tv_queryPhoneNumber);
        tv_queryPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdvancedToolsActivity.this, CommonNumberActivity.class);
                startActivity(intent);
            }
        });
    }

    private void InitPhoneAdress() {
        TextView tv_queryPhoneAddress = (TextView) findViewById(R.id.tv_queryPhoneAddress);
        tv_queryPhoneAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdvancedToolsActivity.this, PhoneAddressQueryActivity.class);
                startActivity(intent);
            }
        });

    }

    private void InitSMSBackup() {

        TextView tv_SMSBackUp = (TextView) findViewById(R.id.tv_SMSBackUp);
        tv_SMSBackUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(AdvancedToolsActivity.this);
                progressDialog.setTitle("短信备份");
                progressDialog.setIcon(R.mipmap.ic_launcher);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.show();
                new Thread(){
                    @Override
                    public void run() {
                        CommonUtils.SMSBackup(getApplicationContext(), new CommonUtils.ISMSBackup() {
                            @Override
                            public void setMax(int count) {
                                progressDialog.setMax(count);
                            }

                            @Override
                            public void SetProgress(int progress) {
                                progressDialog.setProgress(progress);
                            }
                        });
                        progressDialog.dismiss();
                    }
                }.start();

            }
        });
    }
}
