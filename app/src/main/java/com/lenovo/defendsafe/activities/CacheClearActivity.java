package com.lenovo.defendsafe.activities;

import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lenovo.defendsafe.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/9/13 013.
 */

public class CacheClearActivity extends AppCompatActivity {

    private TextView tv_cacheClear;
    private Button btn_cacheClear;
    private ProgressBar pb_cacheClear;
    private LinearLayout ll_cacheClear;
    private List<String> listCacheExsit;
    private PackageManager packageMgr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cacheclear);

        InitUI();

        CheckCache();
    }

    private void CheckCache() {
        new Thread() {
            @Override
            public void run() {
                packageMgr = getPackageManager();
                List<PackageInfo> listPackageInfo = packageMgr.getInstalledPackages(0);
                pb_cacheClear.setMax(listPackageInfo.size());
                listCacheExsit = new ArrayList<String>();
                final int[] count = {0};
                for (final PackageInfo packageInfo :
                        listPackageInfo) {
                    final String packageName = packageInfo.packageName;
                    final long cacheSize = getPackageCache(packageInfo, packageName);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_cacheClear.setText(packageInfo.applicationInfo.loadLabel(packageMgr).toString());

                            count[0]++;
                            pb_cacheClear.setProgress(count[0]);
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
                        if (listCacheExsit.size() > 0) {
                            tv_cacheClear.setText("扫描完成");
                        } else {
                            tv_cacheClear.setText("扫描完成,您的系统很干净");
                        }
                    }
                });
            }
        }.start();
    }

    private long getPackageCache(PackageInfo packageinfo, final String packageName) {
        final long[] cacheSize = {0};
        try {

            final PackageInfo packageInfo = packageinfo;
            // 获取到getPackageSizeInfo。调用getPackageSizeInfo方法需要在清单文件中配置权限信息：<uses-permission
            // android:name="android.permission.GET_PACKAGE_SIZE"/>
            Method method = packageMgr.getClass().getMethod(
                    "getPackageSizeInfo",
                    new Class[]{String.class, IPackageStatsObserver.class});
            // 执行getPackageSizeInfo方法
            method.invoke(packageMgr, packageName,
                    new IPackageStatsObserver.Stub() {
                        @Override
                        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {

                            cacheSize[0] = pStats.cacheSize;
                            if (cacheSize[0] > 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        View view = View.inflate(getApplicationContext(), R.layout.adapter_cacheclear, null);

                                        ImageView iv_cacheIcon = (ImageView) view.findViewById(R.id.iv_cacheIcon);
                                        TextView tv_cacheName = (TextView) view.findViewById(R.id.tv_cacheName);
                                        TextView tv_cacheSize = (TextView) view.findViewById(R.id.tv_cacheSize);
                                        ImageView iv_cacheDelete = (ImageView) view.findViewById(R.id.iv_cacheDelete);

                                        iv_cacheIcon.setBackground(packageInfo.applicationInfo.loadIcon(packageMgr));
                                        tv_cacheName.setText(packageInfo.applicationInfo.loadLabel(packageMgr).toString());
                                        tv_cacheSize.setText(android.text.format.Formatter.formatFileSize(getApplicationContext(), cacheSize[0]));

                                        iv_cacheDelete.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent();
                                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                                intent.setData(Uri.parse("package:" + packageName));
                                                startActivity(intent);
                                            }
                                        });

                                        ll_cacheClear.addView(view, 0);

                                        listCacheExsit.add(packageInfo.packageName);
                                    }
                                });

                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cacheSize[0];
    }

    private void InitUI() {

        tv_cacheClear = (TextView) findViewById(R.id.tv_cacheClear);
        btn_cacheClear = (Button) findViewById(R.id.btn_cacheClear);
        pb_cacheClear = (ProgressBar) findViewById(R.id.pb_cacheClear);
        ll_cacheClear = (LinearLayout) findViewById(R.id.ll_cacheClear);

        btn_cacheClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Method method = packageMgr.getClass().getMethod(
                            "freeStorageAndNotify",
                            new Class[]{long.class, IPackageDataObserver.class});
                    // 执行getPackageSizeInfo方法
                    method.invoke(packageMgr, Long.MAX_VALUE,
                            new IPackageDataObserver.Stub() {
                                @Override
                                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ll_cacheClear.removeAllViews();
                                        }
                                    });
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
