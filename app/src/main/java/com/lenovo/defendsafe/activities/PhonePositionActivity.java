package com.lenovo.defendsafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by Lenovo on 2017/9/8 008.
 */

public class PhonePositionActivity extends Activity {

    private WindowManager windowManager;
    private long[] mHits = new long[2];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnoneposition);

        InitUI();
    }

    private void InitUI() {

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        final int width = windowManager.getDefaultDisplay().getWidth();
        final int height = windowManager.getDefaultDisplay().getHeight();

        final TextView tv_phonePositionDrag = (TextView) findViewById(R.id.tv_phonePositionDrag);
        final Button btn_phonePositionTipTop = (Button) findViewById(R.id.btn_phonePositionTipTop);
        final Button btn_phonePositionTipBottom = (Button) findViewById(R.id.btn_phonePositionTipBottom);

        int locationX = SPUtils.getInt(getApplicationContext(), ConstantValue.PHONE_POSITION_X, 0);
        int locationY = SPUtils.getInt(getApplicationContext(), ConstantValue.PHONE_POSITION_Y, 0);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = locationX;
        params.topMargin = locationY;
        tv_phonePositionDrag.setLayoutParams(params);
        if (locationY > (height - 40) / 2){
            btn_phonePositionTipTop.setVisibility(View.VISIBLE);
            btn_phonePositionTipBottom.setVisibility(View.INVISIBLE);
        }else {
            btn_phonePositionTipTop.setVisibility(View.INVISIBLE);
            btn_phonePositionTipBottom.setVisibility(View.VISIBLE);
        }

        tv_phonePositionDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[mHits.length - 1] - mHits[0] < 500){
                    int left = (width - tv_phonePositionDrag.getWidth()) / 2;
                    int top = ((height - 40) - tv_phonePositionDrag.getHeight()) / 2;
                    int right = (width + tv_phonePositionDrag.getWidth()) / 2;
                    int bottom = ((height - 40) + tv_phonePositionDrag.getHeight()) / 2;

                    tv_phonePositionDrag.layout(left, top, right, bottom);
                    if (top > (height - 40) / 2){
                        btn_phonePositionTipTop.setVisibility(View.VISIBLE);
                        btn_phonePositionTipBottom.setVisibility(View.INVISIBLE);
                    }else {
                        btn_phonePositionTipTop.setVisibility(View.INVISIBLE);
                        btn_phonePositionTipBottom.setVisibility(View.VISIBLE);
                    }

                    SPUtils.putInt(getApplicationContext(), ConstantValue.PHONE_POSITION_X, tv_phonePositionDrag.getLeft());
                    SPUtils.putInt(getApplicationContext(), ConstantValue.PHONE_POSITION_Y, tv_phonePositionDrag.getTop());
                }
            }
        });

        tv_phonePositionDrag.setOnTouchListener(new View.OnTouchListener() {
            private int startX = 0, startY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = (moveX - startX);
                        int disY = (moveY - startY);

                        int Left = (tv_phonePositionDrag.getLeft() + disX);
                        int Top = (tv_phonePositionDrag.getTop() + disY);
                        int Right = (tv_phonePositionDrag.getRight() + disX);
                        int Bottom = (tv_phonePositionDrag.getBottom() + disY);

                        if (Left < 0 || Top < 0 || Right > width || Bottom > height - 40) {
                            return true;
                        }

                        if (Top > (height - 40) / 2){
                            btn_phonePositionTipTop.setVisibility(View.VISIBLE);
                            btn_phonePositionTipBottom.setVisibility(View.INVISIBLE);
                        }else {
                            btn_phonePositionTipTop.setVisibility(View.INVISIBLE);
                            btn_phonePositionTipBottom.setVisibility(View.VISIBLE);
                        }

                        tv_phonePositionDrag.layout(Left, Top, Right, Bottom);

                        startX = moveX;
                        startY = moveY;
                        break;
                    case MotionEvent.ACTION_UP:
                        SPUtils.putInt(getApplicationContext(), ConstantValue.PHONE_POSITION_X, tv_phonePositionDrag.getLeft());
                        SPUtils.putInt(getApplicationContext(), ConstantValue.PHONE_POSITION_Y, tv_phonePositionDrag.getTop());
                        break;
                }
                return false;
            }
        });
    }
}
