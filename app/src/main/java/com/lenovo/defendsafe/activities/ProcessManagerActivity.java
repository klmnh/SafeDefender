package com.lenovo.defendsafe.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.db.domain.ProcessInfo;
import com.lenovo.defendsafe.utils.CommonUtils;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/9/10 010.
 */

public class ProcessManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_processDesc;
    private ListView lv_processMgr;
    private Button btn_processSelectAll;
    private Button btn_processUnSelectAll;
    private Button btn_processClearAll;
    private Button btn_processSetting;
    private List<ProcessInfo> listSystemProcessInfo;
    private List<ProcessInfo> listUserProcessInfo;
    private MyAdapter myAdapter;
    private TextView tv_processCount;
    private TextView tv_memoryInfo;
    private long totalReleaseSpace = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processmgr);

        InitTitle();

        InitUI();

        InstallShortCut();
    }

    private void InstallShortCut() {
        if (!SPUtils.getBoolean(getApplicationContext(), ConstantValue.PROCESS_HAS_SHORTCUT, false)) {
            Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "safeDefender");
            Intent startIntent = new Intent();
            startIntent.setAction("com.lenovo.process.shortcut");
            startIntent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, startIntent);
            sendBroadcast(intent);
            SPUtils.putBoolean(getApplicationContext(), ConstantValue.PROCESS_HAS_SHORTCUT, true);
        }
    }

    private void InitUI() {
        tv_processDesc = (TextView) findViewById(R.id.tv_processDesc);
        lv_processMgr = (ListView) findViewById(R.id.lv_processMgr);
        lv_processMgr.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listUserProcessInfo != null && listSystemProcessInfo != null) {
                    if (firstVisibleItem > listUserProcessInfo.size()) {
                        tv_processDesc.setText("系统进程(" + listSystemProcessInfo.size() + ")");
                    } else {
                        tv_processDesc.setText("用户进程(" + listUserProcessInfo.size() + ")");
                    }
                }
            }
        });
        lv_processMgr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (myAdapter != null) {
                    ProcessInfo processInfo = myAdapter.getItem(position);
                    if (processInfo != null && !processInfo.packageName.equals(getPackageName())) {
                        processInfo.isChecked = !processInfo.isChecked;
                        ViewHolder viewHolder = (ViewHolder) view.getTag();
                        if (viewHolder != null) {
                            viewHolder.cb_check.setChecked(processInfo.isChecked);
                        }
                    }
                }
            }
        });

        btn_processSelectAll = (Button) findViewById(R.id.btn_processSelectAll);
        btn_processUnSelectAll = (Button) findViewById(R.id.btn_processUnSelectAll);
        btn_processClearAll = (Button) findViewById(R.id.btn_processClearAll);
        btn_processSetting = (Button) findViewById(R.id.btn_processSetting);

        btn_processSelectAll.setOnClickListener(ProcessManagerActivity.this);
        btn_processUnSelectAll.setOnClickListener(ProcessManagerActivity.this);
        btn_processClearAll.setOnClickListener(ProcessManagerActivity.this);
        btn_processSetting.setOnClickListener(ProcessManagerActivity.this);

        listSystemProcessInfo = new ArrayList<ProcessInfo>();
        listUserProcessInfo = new ArrayList<ProcessInfo>();
    }

    private void GetProcessList() {
        new Thread() {
            @Override
            public void run() {
                listSystemProcessInfo.clear();
                listUserProcessInfo.clear();
                List<ProcessInfo> listProcessInfo = CommonUtils.GetProcessInfoList(getApplicationContext());
                for (ProcessInfo proInfo : listProcessInfo) {
                    if (proInfo.isSystem) {
                        listSystemProcessInfo.add(proInfo);
                    } else {
                        listUserProcessInfo.add(proInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (myAdapter == null) {
                            myAdapter = new MyAdapter();
                            lv_processMgr.setAdapter(myAdapter);
                        } else {
                            myAdapter.notifyDataSetChanged();
                        }
                        tv_processDesc.setText("用户进程(" + listUserProcessInfo.size() + ")");
                    }
                });
            }
        }.start();
    }

    private void InitTitle() {

        int processCount = CommonUtils.GetProcessCount(getApplicationContext());
        String processMemory = CommonUtils.GetProcessMemory(getApplicationContext());

        tv_processCount = (TextView) findViewById(R.id.tv_processCount);
        tv_memoryInfo = (TextView) findViewById(R.id.tv_memoryInfo);

        tv_processCount.setText("进程总数:" + processCount);
        tv_memoryInfo.setText("剩余/总共:" + processMemory);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetProcessList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_processSelectAll:
                for (ProcessInfo proInfo :
                        listUserProcessInfo) {
                    if (!proInfo.packageName.equals(getPackageName())) {
                        proInfo.isChecked = true;
                    }
                }
                for (ProcessInfo proInfo :
                        listSystemProcessInfo) {
                    proInfo.isChecked = true;
                }

                if (myAdapter != null) {
                    myAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_processUnSelectAll:
                for (ProcessInfo proInfo :
                        listUserProcessInfo) {
                    if (!proInfo.packageName.equals(getPackageName())) {
                        proInfo.isChecked = false;
                    }
                }
                for (ProcessInfo proInfo :
                        listSystemProcessInfo) {
                    proInfo.isChecked = false;
                }

                if (myAdapter != null) {
                    myAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_processClearAll:
                totalReleaseSpace = 0;
                int totalProcessCount = listUserProcessInfo.size() + listSystemProcessInfo.size();
                List<ProcessInfo> killProcess = new ArrayList<>();
                for (ProcessInfo proInfo :
                        listUserProcessInfo) {
                    if (!proInfo.packageName.equals(getPackageName())) {
                        killProcess.add(proInfo);
                    }
                }
                for (ProcessInfo proInfo :
                        listSystemProcessInfo) {
                    killProcess.add(proInfo);
                }

                for (ProcessInfo proInfo :
                        killProcess) {
                    if (listUserProcessInfo.contains(proInfo)) {
                        listUserProcessInfo.remove(proInfo);
                        totalProcessCount--;
                    }
                    if (listSystemProcessInfo.contains(proInfo)) {
                        listSystemProcessInfo.remove(proInfo);
                        totalProcessCount--;
                    }

                    CommonUtils.KillProcess(getApplicationContext(), proInfo);
                    totalReleaseSpace += proInfo.memSize;
                }
                tv_processCount.setText("进程总数:" + totalProcessCount);
                String processMemory = CommonUtils.GetProcessMemory(getApplicationContext());
                tv_memoryInfo.setText(processMemory);
                CommonUtils.ShowToastInfo(getApplicationContext(), "杀死了" + killProcess.size() + "个进程,释放了" + Formatter.formatFileSize(getApplicationContext(), totalReleaseSpace) + "空间");

                if (myAdapter != null) {
                    myAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_processSetting:
                Intent intent = new Intent(ProcessManagerActivity.this, ProcessSettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (SPUtils.getBoolean(getApplicationContext(), ConstantValue.PROCESS_SHOW_SYSTEM, false)) {
                return listSystemProcessInfo.size() + listUserProcessInfo.size() + 2;
            } else {
                return listUserProcessInfo.size() + 1;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position == 0 || position == listUserProcessInfo.size() + 1) {
                return null;
            } else if (position < listUserProcessInfo.size() + 1) {
                return listUserProcessInfo.get(position - 1);
            } else {
                return listSystemProcessInfo.get(position - listUserProcessInfo.size() - 2);
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
            if (position == 0 || position == listUserProcessInfo.size() + 1) {
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
                    viewTitleHolder.tv_desc.setText("用户进程(" + listUserProcessInfo.size() + ")");
                } else {
                    viewTitleHolder.tv_desc.setText("系统进程(" + listSystemProcessInfo.size() + ")");
                }

            } else {
                ViewHolder viewHolder;
                if (convertView != null) {
                    view = convertView;
                    viewHolder = (ViewHolder) convertView.getTag();
                } else {
                    view = View.inflate(getApplicationContext(), R.layout.adapter_processmgr, null);
                    viewHolder = new ViewHolder();
                    viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_processIcon);
                    viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_processName);
                    viewHolder.tv_path = (TextView) view.findViewById(R.id.tv_processPath);
                    viewHolder.cb_check = (CheckBox) view.findViewById(R.id.cb_processCheck);

                    view.setTag(viewHolder);
                }
                ProcessInfo proInfo = getItem(position);
                viewHolder.iv_icon.setBackground(proInfo.icon);
                viewHolder.tv_name.setText(proInfo.Name);
                viewHolder.tv_path.setText(proInfo.isSystem ? "系统进程" : "应用进程");
                viewHolder.cb_check.setChecked(proInfo.isChecked);
            }

            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_path;
        CheckBox cb_check;
    }

    static class ViewTitleHolder {
        TextView tv_desc;
    }
}
