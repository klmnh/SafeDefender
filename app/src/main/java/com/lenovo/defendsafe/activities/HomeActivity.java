package com.lenovo.defendsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.CommonUtils;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;

/**
 * Created by Lenovo on 2017/9/3 003.
 */

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private GridView gv_home;
    private String[] mFunsTitle;
    private int[] mFunsDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        InitUI();

        InitData();
    }

    /**
     * 初始化UI控件
     */
    private void InitUI() {

        gv_home = (GridView) findViewById(R.id.gv_home);

    }

    /**
     * 初始化九宫格
     */
    private void InitData() {
        mFunsTitle = new String[]{
                getString(R.string.PhoneBak), getString(R.string.SIMGuard), getString(R.string.SoftManager),
                getString(R.string.ProcessManager), getString(R.string.MobileCounter), getString(R.string.MobileSecurity),
                getString(R.string.MobileCleaner), getString(R.string.AdvencedTools), getString(R.string.Settings)
        };

        mFunsDrawable = new int[]{
                R.drawable.phonebak, R.drawable.simguard, R.drawable.softmanager,
                R.drawable.processmanager, R.drawable.mobilecounter, R.drawable.mobilesecurity,
                R.drawable.mobilecleaner, R.drawable.advencedtools, R.drawable.settings
        };

        gv_home.setAdapter(new MyAdapter());
        gv_home.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        FunctionModule currModdule = FunctionModule.values()[position];
        switch (currModdule) {
            case PhoneBak:
                String pwd = SPUtils.getString(getApplicationContext(), ConstantValue.SETTINGS_PHONEBAK_PASSWORD, "");
                if (TextUtils.isEmpty(pwd)) {
                    ShowSetPwdDialog();
                } else {
                    ShowConfimPwdDialog(pwd);
                }
                break;
            case SIMGuard:
                break;
            case SoftManager:
                break;
            case ProcessManager:
                break;
            case MobileCounter:
                break;
            case MobileSecurity:
                break;
            case MobileCleaner:
                break;
            case AdvencedTools:
                Intent intent1 = new Intent(HomeActivity.this, AdvancedToolsActivity.class);
                startActivity(intent1);
                break;
            case Settings:
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 设置密码对话框
     */
    private void ShowSetPwdDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_set_pwd, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();

        final Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        final Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText et_firstPwd = (EditText) view.findViewById(R.id.et_firstPwd);
                EditText et_secondPwd = (EditText) view.findViewById(R.id.et_secondPwd);

                String firstPwd = et_firstPwd.getText().toString();
                String secondPwd = et_secondPwd.getText().toString();

                if (!TextUtils.isEmpty(firstPwd) && !TextUtils.isEmpty(secondPwd)) {
                    if (firstPwd.equals(secondPwd)) {

                        Intent intent = new Intent(HomeActivity.this, PhoneBak_Nav1.class);
                        startActivity(intent);
                        SPUtils.putString(getApplicationContext(), ConstantValue.SETTINGS_PHONEBAK_PASSWORD, CommonUtils.MD5Encoder(firstPwd));

                        dialog.dismiss();
                    } else {
                        CommonUtils.ShowToastInfo(getApplicationContext(), "两次密码输入不一致");
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
    }

    /**
     * 确认密码对话框
     */
    private void ShowConfimPwdDialog(final String pwd) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_confirm_pwd, null);
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
                    if (confirmPwd.equals(pwd)) {
                        boolean PhoneBak_SetCompleted = SPUtils.getBoolean(getApplicationContext(), ConstantValue.PHONE_BAK_SET_COMPLETED, false);
                        if(PhoneBak_SetCompleted){
                            Intent intent = new Intent(HomeActivity.this, PhoneBak.class);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(HomeActivity.this, PhoneBak_Nav1.class);
                            startActivity(intent);
                        }
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
    }

    /**
     * 功能模块对应的枚举类型
     */
    public enum FunctionModule {
        PhoneBak, SIMGuard, SoftManager,
        ProcessManager, MobileCounter, MobileSecurity,
        MobileCleaner, AdvencedTools, Settings
    }

    /**
     * 功能模块对应的九宫格适配器
     */
    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFunsTitle.length;
        }

        @Override
        public Object getItem(int position) {
            return mFunsTitle[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(getApplicationContext(), R.layout.adapter_home, null);
            }
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);

            iv_icon.setBackgroundResource(mFunsDrawable[position]);
            tv_title.setText(mFunsTitle[position]);

            return view;
        }
    }

}
