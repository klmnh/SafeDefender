package com.lenovo.defendsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lenovo.defendsafe.R;

/**
 * Created by Lenovo on 2017/9/7 007.
 */

public class AdvancedToolsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_advancedtools);

        InitPhoneAdress();
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
}
