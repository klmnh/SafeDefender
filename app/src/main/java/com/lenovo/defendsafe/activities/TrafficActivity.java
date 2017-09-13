package com.lenovo.defendsafe.activities;

import android.net.TrafficStats;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.widget.TextView;

import com.lenovo.defendsafe.R;

/**
 * Created by Lenovo on 2017/9/14 014.
 */

public class TrafficActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);

        TextView tv_traffic = (TextView) findViewById(R.id.tv_traffic);

        String recPhone = Formatter.formatFileSize(getApplicationContext(),TrafficStats.getMobileRxBytes());
        String recPhoneTotal = Formatter.formatFileSize(getApplicationContext(),TrafficStats.getMobileTxBytes());

        String allPhone = Formatter.formatFileSize(getApplicationContext(),TrafficStats.getTotalRxBytes());
        String allPhoneTotal = Formatter.formatFileSize(getApplicationContext(),TrafficStats.getTotalTxBytes());

        tv_traffic.setText(recPhone + "    " + recPhoneTotal + "    " + allPhone + "    " + allPhoneTotal);
    }
}
