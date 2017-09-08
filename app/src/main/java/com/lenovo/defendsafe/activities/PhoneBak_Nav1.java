package com.lenovo.defendsafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.lenovo.defendsafe.R;

/**
 * Created by Lenovo on 2017/9/4 004.
 */

public class PhoneBak_Nav1 extends BasePhoneBak {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phonebak_nav1);

        Button btn_next = (Button) findViewById(R.id.btn_next);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowNextPage();
            }
        });
    }

    @Override
    protected void ShowPrePage() {

    }

    @Override
    protected void ShowNextPage() {
        Intent intent = new Intent(PhoneBak_Nav1.this, PhoneBak_Nav2.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_next_in, R.anim.anim_next_out);
    }
}
