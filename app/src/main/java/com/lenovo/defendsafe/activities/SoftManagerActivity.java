package com.lenovo.defendsafe.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.db.domain.AppInfo;
import com.lenovo.defendsafe.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/9/10 010.
 */

public class SoftManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private List<AppInfo> listSystemAppInfo;
    private List<AppInfo> listUserAppInfo;
    private MyAdapter myAdapter;
    private PopupWindow popupWindow;
    private AppInfo appInfo;
    private TextView tv_desc;
    private ListView lv_softMgr;
    private AnimationSet animationSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_softmanager);

        InitTitle();

        InitUI();
    }

    private void InitUI() {
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        lv_softMgr = (ListView) findViewById(R.id.lv_softMgr);
        lv_softMgr.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listUserAppInfo != null && listSystemAppInfo != null) {
                    if (firstVisibleItem > listUserAppInfo.size()) {
                        tv_desc.setText("系统应用(" + listSystemAppInfo.size() + ")");
                    } else {
                        tv_desc.setText("用户应用(" + listUserAppInfo.size() + ")");
                    }
                }
            }
        });
        lv_softMgr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (myAdapter != null) {
                    appInfo = myAdapter.getItem(position);
                    if (appInfo != null) {
                        View view1 = View.inflate(getApplicationContext(), R.layout.popwindow_softmgr, null);

                        TextView tv_popWindowUninstall = (TextView) view1.findViewById(R.id.tv_popWindowUninstall);
                        TextView tv_popWindowStart = (TextView) view1.findViewById(R.id.tv_popWindowStart);
                        TextView tv_popWindowShare = (TextView) view1.findViewById(R.id.tv_popWindowShare);

                        tv_popWindowUninstall.setOnClickListener(SoftManagerActivity.this);
                        tv_popWindowStart.setOnClickListener(SoftManagerActivity.this);
                        tv_popWindowShare.setOnClickListener(SoftManagerActivity.this);

                        if (animationSet == null) {
                            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                            alphaAnimation.setDuration(1000);
                            alphaAnimation.setFillAfter(true);
                            ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                            scaleAnimation.setDuration(1000);
                            scaleAnimation.setFillAfter(true);
                            animationSet = new AnimationSet(true);
                            animationSet.addAnimation(alphaAnimation);
                            animationSet.addAnimation(scaleAnimation);
                        }
                        popupWindow = new PopupWindow(view1, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        popupWindow.setBackgroundDrawable(new ColorDrawable());
                        popupWindow.showAsDropDown(view, 50, -view.getHeight());
                        view1.startAnimation(animationSet);
                    }
                }
            }
        });

        listSystemAppInfo = new ArrayList<AppInfo>();
        listUserAppInfo = new ArrayList<AppInfo>();
    }

    private void GetPackageInstalled() {
        new Thread() {
            @Override
            public void run() {
                listSystemAppInfo.clear();
                listUserAppInfo.clear();
                List<AppInfo> listAppInfo = CommonUtils.getAppInfoList(getApplicationContext());
                for (AppInfo appInfo : listAppInfo) {
                    if (appInfo.isSystem) {
                        listSystemAppInfo.add(appInfo);
                    } else {
                        listUserAppInfo.add(appInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (myAdapter == null) {
                            myAdapter = new MyAdapter();
                        }
                        lv_softMgr.setAdapter(myAdapter);
                        tv_desc.setText("用户应用(" + listUserAppInfo.size() + ")");
                    }
                });
            }
        }.start();
    }

    private void InitTitle() {

        String path = Environment.getDataDirectory().getAbsolutePath();
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String size = CommonUtils.GetAvailableSpace(getApplicationContext(), path);
        String sdSize = CommonUtils.GetAvailableSpace(getApplicationContext(), sdPath);

        TextView tv_internal_memory = (TextView) findViewById(R.id.tv_internal_memory);
        TextView tv_sdCard_memory = (TextView) findViewById(R.id.tv_sdCard_memory);

        tv_internal_memory.setText("磁盘可用:" + size);
        tv_sdCard_memory.setText("SD卡可用:" + sdSize);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetPackageInstalled();
    }

    @Override
    public void onClick(View v) {
        if(appInfo != null) {
            switch (v.getId()) {
                case R.id.tv_popWindowUninstall:
                    if (appInfo.isSystem) {
                        CommonUtils.ShowToastInfo(getApplicationContext(), "此应用为系统应用，不能卸载");
                    } else {
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(Uri.parse("package:" + appInfo.packageName));
                        startActivity(intent);
                    }
                    break;
                case R.id.tv_popWindowStart:
                    PackageManager packageManager = getPackageManager();
                    Intent intent1 = packageManager.getLaunchIntentForPackage(appInfo.packageName);
                    if (intent1 != null) {
                        startActivity(intent1);
                    } else {
                        CommonUtils.ShowToastInfo(getApplicationContext(), "此应用不能被开启");
                    }
                    break;
                case R.id.tv_popWindowShare:
                    Intent intent2 = new Intent(Intent.ACTION_SEND);
                    intent2.putExtra(Intent.EXTRA_TEXT, "分享一个应用，应用名称为" + appInfo.Name);
                    intent2.setType("text/plain");
                    startActivity(intent2);
                    break;
            }
        }
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listSystemAppInfo.size() + listUserAppInfo.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 || position == listUserAppInfo.size() + 1) {
                return null;
            } else if (position < listUserAppInfo.size() + 1) {
                return listUserAppInfo.get(position - 1);
            } else {
                return listSystemAppInfo.get(position - listUserAppInfo.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == listUserAppInfo.size() + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view;
            int type = getItemViewType(position);
            if (type == 0) {
                ViewTitleHolder viewTitleHolder;
                if (convertView != null) {
                    view = convertView;
                    viewTitleHolder = (ViewTitleHolder) convertView.getTag();
                } else {
                    view = View.inflate(getApplicationContext(), R.layout.adapter_soft_title, null);
                    viewTitleHolder = new ViewTitleHolder();
                    viewTitleHolder.tv_desc = (TextView) view.findViewById(R.id.tv_softDesc);
                    view.setTag(viewTitleHolder);
                }
                if (position == 0) {
                    viewTitleHolder.tv_desc.setText("用户应用(" + listUserAppInfo.size() + ")");
                } else {
                    viewTitleHolder.tv_desc.setText("系统应用(" + listSystemAppInfo.size() + ")");
                }

            } else {
                ViewHolder viewHolder;
                if (convertView != null) {
                    view = convertView;
                    viewHolder = (ViewHolder) convertView.getTag();
                } else {
                    view = View.inflate(getApplicationContext(), R.layout.adapter_softmgr, null);
                    viewHolder = new ViewHolder();
                    viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_softIcon);
                    viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_softName);
                    viewHolder.tv_path = (TextView) view.findViewById(R.id.tv_softPath);

                    view.setTag(viewHolder);
                }
                AppInfo appInfo = getItem(position);
                viewHolder.iv_icon.setBackground(appInfo.icon);
                viewHolder.tv_name.setText(appInfo.Name);
                viewHolder.tv_path.setText(appInfo.isSDcard ? "sd卡应用" : "手机应用");
            }

            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_path;
    }

    static class ViewTitleHolder {
        TextView tv_desc;
    }
}
