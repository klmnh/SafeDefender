package com.lenovo.defendsafe.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenovo.defendsafe.R;

/**
 * Created by Lenovo on 2017/9/3 003.
 */

public class SettingsClickItemView extends RelativeLayout {

    public static final String SettingsItemUri = "http://schemas.android.com/apk/res/com.lenovo.defendsafe";
    private TextView tv_settingsTitle;
    private TextView tv_settingsDes;
    private String settingDescOn;

    public SettingsClickItemView(Context context) {
        this(context, null);
    }

    public SettingsClickItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsClickItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //xml---->view
        View.inflate(context, R.layout.view_settings_click_item, this);

        tv_settingsTitle = (TextView) findViewById(R.id.tv_settingsTitle);
        tv_settingsDes = (TextView) findViewById(R.id.tv_settingsDes);

        //获取自定义及原生属性的操作写在此处
        String settingsTitle = attrs.getAttributeValue(SettingsItemUri, "settingsTitle");
        settingDescOn = attrs.getAttributeValue(SettingsItemUri, "settingsDescOn");

        tv_settingsTitle.setText(settingsTitle);
        tv_settingsDes.setText(settingDescOn);
    }

    public String GetStyleDesc(){
        return settingDescOn;
    }

    public void SetStyleDesc(String styleDesc){
        tv_settingsDes.setText(styleDesc);
        settingDescOn = styleDesc;
    }

}
