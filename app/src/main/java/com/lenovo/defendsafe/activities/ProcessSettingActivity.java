package com.lenovo.defendsafe.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.content.Intent;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.service.LockScreenClearService;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;

/**
 * Created by Lenovo on 2017/9/11 011.
 */

public class ProcessSettingActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processsetting);

        InitSystemShow();

        InitLockClear();
    }

    private void InitSystemShow() {

        final CheckBox cb_showSystem = (CheckBox) findViewById(R.id.cb_showSystem);
        boolean isChecked = SPUtils.getBoolean(getApplicationContext(),ConstantValue.PROCESS_SHOW_SYSTEM, false);
        cb_showSystem.setChecked(isChecked);
        if (isChecked){
            cb_showSystem.setText("显示系统进程");
        }else {
            cb_showSystem.setText("隐藏系统进程");
        }

        cb_showSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_showSystem.setText("显示系统进程");
                }else {
                    cb_showSystem.setText("隐藏系统进程");
                }
                SPUtils.putBoolean(getApplicationContext(), ConstantValue.PROCESS_SHOW_SYSTEM, isChecked);
            }
        });
    }

    private void InitLockClear() {

        final CheckBox cb_lockClear = (CheckBox) findViewById(R.id.cb_lockClear);
        boolean isChecked = SPUtils.getBoolean(getApplicationContext(), ConstantValue.PROCESS_LOCK_CLEAR, false);
        cb_lockClear.setChecked(isChecked);
        if (isChecked){
            cb_lockClear.setText("锁屏清理已开启");
        }else {
            cb_lockClear.setText("锁屏清理已关闭");
        }

        cb_lockClear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_lockClear.setText("锁屏清理已开启");
                    Intent intent = new Intent(ProcessSettingActivity.this, LockScreenClearService.class);
                    startService(intent);
                }else {
                    cb_lockClear.setText("锁屏清理已关闭");
                    Intent intent = new Intent(ProcessSettingActivity.this, LockScreenClearService.class);
                    stopService(intent);
                }
                SPUtils.putBoolean(getApplicationContext(), ConstantValue.PROCESS_LOCK_CLEAR, isChecked);
            }
        });
    }
}
