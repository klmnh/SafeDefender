package com.lenovo.defendsafe.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.db.dao.AppLockerDao;
import com.lenovo.defendsafe.db.domain.AppInfo;
import com.lenovo.defendsafe.utils.CommonUtils;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/9/12 012.
 */

public class AppLockerActivity extends AppCompatActivity {

    private Button btn_appUnLocker;
    private Button btn_appLocker;
    private LinearLayout ll_appUnLocker;
    private LinearLayout ll_appLocker;
    private TextView tv_appUnLocker;
    private TextView tv_appLocker;
    private ListView lv_appUnLocker;
    private ListView lv_appLocker;
    private List<AppInfo> listUnLockAppInfo;
    private List<AppInfo> listLockAppInfo;
    private MyAdapter myAdapterLocker;
    private MyAdapter myAdapterUnLocker;
    private TranslateAnimation translateAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applocker);

        InitUI();

        InitData();

        InitAnimation();
    }

    private void InitUI() {

        btn_appUnLocker = (Button) findViewById(R.id.btn_appUnLocker);
        btn_appLocker = (Button) findViewById(R.id.btn_appLocker);
        btn_appUnLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myAdapterUnLocker == null) {
                    myAdapterUnLocker = new MyAdapter(false);
                    lv_appUnLocker.setAdapter(myAdapterUnLocker);
                } else {
                    myAdapterUnLocker.notifyDataSetChanged();
                }
                btn_appUnLocker.setBackgroundResource(R.drawable.button_bg_green_on);
                btn_appLocker.setBackgroundResource(R.drawable.button_bg_green);
                ll_appUnLocker.setVisibility(View.VISIBLE);
                ll_appLocker.setVisibility(View.GONE);
                tv_appUnLocker.setText("未加锁应用:" + myAdapterUnLocker.getCount());

                SPUtils.putBoolean(getApplicationContext(), ConstantValue.IS_APP_LOCK, false);
            }
        });
        btn_appLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myAdapterLocker == null) {
                    myAdapterLocker = new MyAdapter(true);
                    lv_appLocker.setAdapter(myAdapterLocker);
                } else {
                    myAdapterLocker.notifyDataSetChanged();
                }
                btn_appLocker.setBackgroundResource(R.drawable.button_bg_green_on);
                btn_appUnLocker.setBackgroundResource(R.drawable.button_bg_green);
                ll_appLocker.setVisibility(View.VISIBLE);
                ll_appUnLocker.setVisibility(View.GONE);
                tv_appLocker.setText("已加锁应用：" + myAdapterLocker.getCount());

                SPUtils.putBoolean(getApplicationContext(), ConstantValue.IS_APP_LOCK, true);
            }
        });

        ll_appUnLocker = (LinearLayout) findViewById(R.id.ll_appUnLocker);
        ll_appLocker = (LinearLayout) findViewById(R.id.ll_appLocker);

        tv_appUnLocker = (TextView) findViewById(R.id.tv_appUnLocker);
        tv_appLocker = (TextView) findViewById(R.id.tv_appLocker);

        lv_appUnLocker = (ListView) findViewById(R.id.lv_appUnLocker);
        lv_appLocker = (ListView) findViewById(R.id.lv_appLocker);
    }

    private void InitData() {
        new Thread() {

            @Override
            public void run() {
                List<AppInfo> listAppInfo = CommonUtils.getAppInfoList(getApplicationContext());
                if (listUnLockAppInfo == null) {
                    listUnLockAppInfo = new ArrayList<AppInfo>();
                } else {
                    listUnLockAppInfo.clear();
                }
                if (listLockAppInfo == null) {
                    listLockAppInfo = new ArrayList<AppInfo>();
                } else {
                    listLockAppInfo.clear();
                }
                List<String> listLockApp = AppLockerDao.getInstance(getApplicationContext()).queryAll();
                for (AppInfo appInfo :
                        listAppInfo) {
                    if (listLockApp.contains(appInfo.packageName)) {
                        listLockAppInfo.add(appInfo);
                    } else {
                        listUnLockAppInfo.add(appInfo);
                    }
                }
                boolean isAppLock = SPUtils.getBoolean(getApplicationContext(), ConstantValue.IS_APP_LOCK, false);
                if (isAppLock) {
                    if (myAdapterLocker == null) {
                        myAdapterLocker = new MyAdapter(true);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_appLocker.setBackgroundResource(R.drawable.button_bg_green_on);
                                btn_appUnLocker.setBackgroundResource(R.drawable.button_bg_green);
                                ll_appLocker.setVisibility(View.VISIBLE);
                                ll_appUnLocker.setVisibility(View.GONE);
                                tv_appLocker.setText("已加锁应用：" + myAdapterLocker.getCount());
                                lv_appLocker.setAdapter(myAdapterLocker);
                            }
                        });
                    } else {
                        myAdapterLocker.notifyDataSetChanged();
                    }
                } else {
                    if (myAdapterUnLocker == null) {
                        myAdapterUnLocker = new MyAdapter(false);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_appUnLocker.setBackgroundResource(R.drawable.button_bg_green_on);
                                btn_appLocker.setBackgroundResource(R.drawable.button_bg_green);
                                ll_appUnLocker.setVisibility(View.VISIBLE);
                                ll_appLocker.setVisibility(View.GONE);
                                tv_appUnLocker.setText("未加锁应用:" + myAdapterUnLocker.getCount());
                                lv_appUnLocker.setAdapter(myAdapterUnLocker);
                            }
                        });
                    } else {
                        myAdapterUnLocker.notifyDataSetChanged();
                    }
                }
            }
        }.start();
    }

    private void InitAnimation() {

        if(translateAnimation == null) {
            translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            translateAnimation.setDuration(1000);
        }
    }

    class MyAdapter extends BaseAdapter {
        private boolean isLock = false;

        public MyAdapter(boolean isLock) {
            super();
            this.isLock = isLock;
        }

        @Override
        public int getCount() {
            if (isLock) {
                return listLockAppInfo.size();
            } else {
                return listUnLockAppInfo.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (isLock) {
                return listLockAppInfo.get(position);
            } else {
                return listUnLockAppInfo.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            ViewHolder viewHolder;
            if (convertView != null) {
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.adapter_applocker, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_appLockerIcon);
                viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_appLockerName);
                viewHolder.iv_lock = (ImageView) view.findViewById(R.id.iv_appLockerLock);

                view.setTag(viewHolder);
            }
            final AppInfo appInfo = getItem(position);
            viewHolder.iv_icon.setBackground(appInfo.icon);
            viewHolder.tv_name.setText(appInfo.Name);
            viewHolder.iv_lock.setBackgroundResource(isLock ? R.drawable.lock : R.drawable.unlock);
            viewHolder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.startAnimation(translateAnimation);
                    translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isLock){
                                listLockAppInfo.remove(appInfo);
                                listUnLockAppInfo.add(appInfo);
                                AppLockerDao.getInstance(getApplicationContext()).delete(appInfo.packageName);
                                myAdapterLocker.notifyDataSetChanged();
                                tv_appLocker.setText("已加锁应用:" + getCount());
                            }else {
                                listUnLockAppInfo.remove(appInfo);
                                listLockAppInfo.add(appInfo);
                                AppLockerDao.getInstance(getApplicationContext()).insert(appInfo.packageName);
                                myAdapterUnLocker.notifyDataSetChanged();
                                tv_appLocker.setText("未加锁应用:" + getCount());
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });

            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }
}
