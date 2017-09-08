package com.lenovo.defendsafe.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenovo.defendsafe.R;

/**
 * Created by Lenovo on 2017/9/3 003.
 */

public class SettingsItemView extends RelativeLayout {

    public static final String SettingsItemUri = "http://schemas.android.com/apk/res/com.lenovo.defendsafe";
    private TextView tv_settingsTitle;
    private TextView tv_settingsDes;
    private CheckBox cb_settingsChecked;
    private String settingsTitle;
    private String settingDescOff;
    private String settingDescOn;

    public SettingsItemView(Context context) {
        this(context, null);
    }

    public SettingsItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //xml---->view
        View.inflate(context, R.layout.view_settings_item, this);

        tv_settingsTitle = (TextView) findViewById(R.id.tv_settingsTitle);
        tv_settingsDes = (TextView) findViewById(R.id.tv_settingsDes);
        cb_settingsChecked = (CheckBox) findViewById(R.id.cb_settingsChecked);

        //获取自定义及原生属性的操作写在此处
        settingsTitle = attrs.getAttributeValue(SettingsItemUri, "settingsTitle");
        settingDescOff = attrs.getAttributeValue(SettingsItemUri, "settingsDescOff");
        settingDescOn = attrs.getAttributeValue(SettingsItemUri, "settingsDescOn");

        tv_settingsTitle.setText(settingsTitle);
        setChecked(isChecked());
    }

    public boolean isChecked() {

        return cb_settingsChecked.isChecked();
    }

    public void setChecked(boolean isChecked) {
        cb_settingsChecked.setChecked(isChecked);
        if (isChecked) {
            tv_settingsDes.setText(settingDescOn);
        } else {
            tv_settingsDes.setText(settingDescOff);
        }
    }

}
