package com.lenovo.defendsafe.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.CommonUtils;

/**
 * Created by Lenovo on 2017/9/12 012.
 */

public class watchDogActivity extends AppCompatActivity {

    private Button btn_watchDogUnlock;
    private EditText et_watchPwd;
    private ImageView iv_watchDogIcon;
    private TextView tv_watchDogName;
    private String packageName;
    private PackageManager packageManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_watchdog);

        packageName = getIntent().getStringExtra("packageName");

        InitUI();

    }

    private void InitUI() {

        tv_watchDogName = (TextView) findViewById(R.id.tv_watchDogName);
        iv_watchDogIcon = (ImageView) findViewById(R.id.iv_watchDogIcon);
        et_watchPwd = (EditText) findViewById(R.id.et_watchPwd);
        btn_watchDogUnlock = (Button) findViewById(R.id.btn_watchDogUnlock);

        packageManager = getPackageManager();
        try {
            ApplicationInfo applicationInfo =packageManager.getApplicationInfo(packageName, 0);
            tv_watchDogName.setText(applicationInfo.loadLabel(packageManager).toString());
            iv_watchDogIcon.setBackground(applicationInfo.loadIcon(packageManager));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        btn_watchDogUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_watchPwd.getText().toString();
                if (!TextUtils.isEmpty(pwd)){
                    if (pwd.equals("123")){
                        Intent intent = new Intent("com.lenovo.defendersafe.SKIP");
                        intent.putExtra("packageName", packageName);
                        sendBroadcast(intent);

                        finish();

                    }else {
                        CommonUtils.ShowToastInfo(getApplicationContext(), "密码输入不正确");
                    }
                }else{
                    CommonUtils.ShowToastInfo(getApplicationContext(), "请输入密码");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //跳转到桌面
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
