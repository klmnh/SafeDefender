package com.lenovo.defendsafe.activities;

import android.content.ComponentName;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.broadcastReceiver.MyDeviceAdminReceiver;
import com.lenovo.defendsafe.service.PhoneAddressService;
import com.lenovo.defendsafe.utils.CommonUtils;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;
import com.lenovo.defendsafe.utils.ServiceUtils;
import com.lenovo.defendsafe.views.SettingsClickItemView;
import com.lenovo.defendsafe.views.SettingsItemView;

/**
 * Created by Lenovo on 2017/9/3 003.
 */

public class SettingsActivity extends AppCompatActivity {

    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private SettingsItemView siv_phoneBak;
    private SettingsClickItemView siv_phoneStyle;
    private String[] listStyleDesc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, MyDeviceAdminReceiver.class);

        InitUpdate();

        InitPhoneBak();

        InitPhoneAddress();

        InitPhoneStyle();

        InitPhonePosition();
    }

    private void InitUpdate() {
        final SettingsItemView siv_Update = (SettingsItemView) findViewById(R.id.sivUpdate);
        boolean open_update = SPUtils.getBoolean(this, ConstantValue.SETTINGS_UPDATE, false);
        siv_Update.setChecked(open_update);

        siv_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = siv_Update.isChecked();
                siv_Update.setChecked(!isChecked);

                SPUtils.putBoolean(getApplicationContext(), ConstantValue.SETTINGS_UPDATE, !isChecked);
            }
        });
    }

    private void InitPhoneBak() {
        siv_phoneBak = (SettingsItemView) findViewById(R.id.sivPhoneBak);
        siv_phoneBak.setChecked(devicePolicyManager.isAdminActive(componentName));

        siv_phoneBak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final boolean isChecked = siv_phoneBak.isChecked();
                if (!isChecked) {
                    if (!devicePolicyManager.isAdminActive(componentName)) {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                "防盗管理器");
                        startActivityForResult(intent, 1);
                    } else {
                        siv_phoneBak.setChecked(!isChecked);
                    }
                } else {
                    String pwd = SPUtils.getString(getApplicationContext(), ConstantValue.SETTINGS_PHONEBAK_PASSWORD, "");
                    if (!TextUtils.isEmpty(pwd)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        final AlertDialog dialog = builder.create();
                        final View view = View.inflate(SettingsActivity.this, R.layout.dialog_confirm_pwd, null);
                        dialog.setView(view, 0, 0, 0, 0);
                        dialog.show();

                        final Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
                        final Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
                        btn_confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                EditText et_confirmPwd = (EditText) view.findViewById(R.id.et_confirmPwd);

                                String confirmPwd = et_confirmPwd.getText().toString();

                                if (!TextUtils.isEmpty(confirmPwd)) {
                                    confirmPwd = CommonUtils.MD5Encoder(confirmPwd);
                                    String pwd = SPUtils.getString(getApplicationContext(), ConstantValue.SETTINGS_PHONEBAK_PASSWORD, "");
                                    if (confirmPwd.equals(pwd)) {
                                        siv_phoneBak.setChecked(!isChecked);
                                        devicePolicyManager.removeActiveAdmin(componentName);

                                        dialog.dismiss();

                                    } else {
                                        CommonUtils.ShowToastInfo(getApplicationContext(), "密码输入不正确");
                                    }
                                } else {
                                    CommonUtils.ShowToastInfo(getApplicationContext(), "请输入密码");
                                }
                            }
                        });
                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        siv_phoneBak.setChecked(!isChecked);
                        devicePolicyManager.removeActiveAdmin(componentName);
                    }
                }
            }
        });
    }

    private void InitPhoneAddress() {
        final SettingsItemView siv_PhoneAddress = (SettingsItemView) findViewById(R.id.sivPhoneAddress);
        boolean open_update = SPUtils.getBoolean(this, ConstantValue.SETTINGS_PHONE_ADDRESS, false);
        boolean isRunningService = ServiceUtils.isRunning(getApplicationContext(), PhoneAddressService.class.getName());
        siv_PhoneAddress.setChecked(open_update && isRunningService);

        siv_PhoneAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = siv_PhoneAddress.isChecked();
                siv_PhoneAddress.setChecked(!isChecked);

                boolean isRunningService1 = ServiceUtils.isRunning(getApplicationContext(), PhoneAddressService.class.getName());
                Intent intent = new Intent(SettingsActivity.this, PhoneAddressService.class);
                if (!isChecked) {
                    if(!isRunningService1) {
                        startService(intent);
                    }
                } else {
                    if(isRunningService1) {
                        stopService(intent);
                    }
                }

                SPUtils.putBoolean(getApplicationContext(), ConstantValue.SETTINGS_PHONE_ADDRESS, !isChecked);
            }
        });
    }

    private void InitPhoneStyle() {
        listStyleDesc = new String[]{"透明", "橙色", "蓝色", "灰色", "绿色"};
        siv_phoneStyle = (SettingsClickItemView) findViewById(R.id.scivPhoneStyle);
        int phoneStyle = SPUtils.getInt(this, ConstantValue.SETTINGS_PHONE_Style, 0);
        siv_phoneStyle.SetStyleDesc(listStyleDesc[phoneStyle]);



        siv_phoneStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int phoneStyle = SPUtils.getInt(SettingsActivity.this, ConstantValue.SETTINGS_PHONE_Style, 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("请选择归属地样式");
                builder.setIcon(R.drawable.headright);
                builder.setSingleChoiceItems(listStyleDesc, phoneStyle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        siv_phoneStyle.SetStyleDesc(listStyleDesc[which]);
                        SPUtils.putInt(getApplicationContext(), ConstantValue.SETTINGS_PHONE_Style, which);

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

    }

    private void InitPhonePosition() {

        SettingsClickItemView siv_phonePosition = (SettingsClickItemView) findViewById(R.id.scivPhonePosition);

        siv_phonePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PhonePositionActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (devicePolicyManager.isAdminActive(componentName)) {
                    siv_phoneBak.setChecked(true);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
