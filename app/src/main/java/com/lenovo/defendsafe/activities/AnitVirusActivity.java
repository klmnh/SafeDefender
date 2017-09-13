package com.lenovo.defendsafe.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/9/13 013.
 */

public class AnitVirusActivity extends AppCompatActivity {

    private ImageView iv_antivirusScan;
    private TextView tv_antivirusScan;
    private ProgressBar pb_antivirusScan;
    private LinearLayout ll_antivirusScan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anitvirus);

        InitUI();

        InitAnimation();

        CheckAnitVirus();
    }

    private void CheckAnitVirus() {
        new Thread() {
            @Override
            public void run() {
                final PackageManager packageMgr = getPackageManager();
                List<PackageInfo> listPackageInfo = packageMgr.getInstalledPackages(PackageManager.GET_SIGNATURES | PackageManager.MATCH_UNINSTALLED_PACKAGES);
                pb_antivirusScan.setMax(listPackageInfo.size());
                List<String> listVirus = CommonUtils.GetAntiVirusList(getApplicationContext());
                final List<String> listScanVirus = new ArrayList<String>();
                final int[] count = {0};
                for (final PackageInfo packageInfo :
                        listPackageInfo) {
                    Signature signature = packageInfo.signatures[0];
                    String md5 = CommonUtils.MD5Encoder(signature.toCharsString());
                    final boolean isVirus = listVirus.contains(md5);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = new TextView(getApplicationContext());
                            textView.setText(isVirus ? "发现病毒:" : "扫描安全:" + packageInfo.applicationInfo.loadLabel(packageMgr).toString());
                            textView.setTextColor(isVirus ? Color.RED : Color.BLACK);

                            ll_antivirusScan.addView(textView, 0);

                            tv_antivirusScan.setText(packageInfo.applicationInfo.loadLabel(packageMgr).toString());

                            count[0]++;
                            pb_antivirusScan.setProgress(count[0]);

                            if(isVirus) {
                                listScanVirus.add(packageInfo.packageName);
                            }
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_antivirusScan.setText("扫描完成");
                        iv_antivirusScan.clearAnimation();

                        for (String packageName:
                             listScanVirus) {
                            Intent intent = new Intent(Intent.ACTION_DELETE);
                            intent.setData(Uri.parse("package:" + packageName));
                            startActivity(intent);
                        }
                    }
                });
            }
        }.start();

    }

    private void InitAnimation() {

        RotateAnimation rotateAnim = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 1, RotateAnimation.RELATIVE_TO_SELF, 1);
        rotateAnim.setDuration(1000);
        rotateAnim.setRepeatCount(RotateAnimation.INFINITE);

        iv_antivirusScan.startAnimation(rotateAnim);
    }

    private void InitUI() {

        iv_antivirusScan = (ImageView) findViewById(R.id.iv_antivirusScan);
        tv_antivirusScan = (TextView) findViewById(R.id.tv_antivirusScan);
        pb_antivirusScan = (ProgressBar) findViewById(R.id.pb_antivirusScan);
        ll_antivirusScan = (LinearLayout) findViewById(R.id.ll_antivirusScan);
    }
}
