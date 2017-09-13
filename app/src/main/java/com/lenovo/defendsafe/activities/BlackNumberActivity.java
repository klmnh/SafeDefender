package com.lenovo.defendsafe.activities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.db.dao.BlackNumberDao;
import com.lenovo.defendsafe.db.domain.BlackNumberInfo;
import com.lenovo.defendsafe.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.lenovo.defendsafe.R.id.tv_blackPhoneNumber;

/**
 * Created by Lenovo on 2017/9/9 009.
 */

public class BlackNumberActivity extends AppCompatActivity {

    private ListView lv_blackNumber;
    private List<BlackNumberInfo> listBlackNumber = null;
    private int mode = 1;
    private MyAdapter myAdapter;
    private int mCount = -1;
    private boolean mIsLoad = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blacknumber);

        InitUI();
        InitData(0);
    }

    private void InitUI() {
        Button btn_blackNumberAdd = (Button) findViewById(R.id.btn_blackNumberAdd);

        btn_blackNumberAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog(-1);
            }
        });

        lv_blackNumber = (ListView) findViewById(R.id.lv_blackNumber);

        lv_blackNumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                AbsListView.OnScrollListener.SCROLL_STATE_FLING
//                AbsListView.OnScrollListener.SCROLL_STATE_IDLE
//                AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL

                if (listBlackNumber != null) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                            lv_blackNumber.getLastVisiblePosition() >= listBlackNumber.size() - 1 && !mIsLoad) {
                        mIsLoad = true;
                        if (mCount == -1) {
                            mCount = BlackNumberDao.getInstance(getApplicationContext()).getCount();
                        }
                        if (mCount > listBlackNumber.size()) {
                            InitData(listBlackNumber.size());
                        } else {
                            mIsLoad = false;
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void ShowDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BlackNumberActivity.this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getApplicationContext(), R.layout.dialog_blacknumber, null);
        dialog.setView(view, 0, 0, 0, 0);

        final EditText et_blackNumber = (EditText) view.findViewById(R.id.et_blackNumber);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
        if (position >= 0) {
            et_blackNumber.setText(listBlackNumber.get(position).getPhoneNumber());
            mode = listBlackNumber.get(position).getMode();
            switch (mode) {
                case 1:
                    rg_group.check(R.id.rb_sms);
                    break;
                case 2:
                    rg_group.check(R.id.rb_sms);
                    break;
                case 3:
                    rg_group.check(R.id.rb_sms);
                    break;
            }
        }

        Button btn_blackNumberSumbit = (Button) view.findViewById(R.id.btn_blackNumberSumbit);
        Button btn_blackNumberCancel = (Button) view.findViewById(R.id.btn_blackNumberCancel);

        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sms:
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        mode = 2;
                        break;
                    case R.id.rb_smsPhone:
                        mode = 3;
                        break;
                }
            }
        });

        btn_blackNumberSumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = et_blackNumber.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    BlackNumberDao.getInstance(getApplicationContext()).insert(phoneNumber, mode + "");
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.setPhoneNumber(phoneNumber);
                    blackNumberInfo.setMode(mode);
                    listBlackNumber.add(0, blackNumberInfo);
                    if (myAdapter != null) {
                        myAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                } else {
                    CommonUtils.ShowToastInfo(getApplicationContext(), "请输入拦截号码");
                }
            }
        });

        btn_blackNumberCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void InitData(final int pageIndex) {

        new Thread() {
            @Override
            public void run() {
                if (listBlackNumber == null) {
                    listBlackNumber = new ArrayList<BlackNumberInfo>();
                }
                List<BlackNumberInfo> listInfo = BlackNumberDao.getInstance(getApplicationContext()).query(pageIndex);
                listBlackNumber.addAll(listInfo);
                if (listBlackNumber != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (myAdapter == null) {
                                myAdapter = new MyAdapter();
                                lv_blackNumber.setAdapter(myAdapter);
                            } else {
                                myAdapter.notifyDataSetChanged();
                            }
                            mIsLoad = false;
                        }
                    });
                }
            }
        }.start();

    }

    /**
     * 黑名单适配器
     */
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listBlackNumber.size();
        }

        @Override
        public Object getItem(int position) {
            return listBlackNumber.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view;
            ViewHolder viewHolder;
            if (convertView != null) {
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.adapter_blacknumber, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_blackPhoneNumber = (TextView) view.findViewById(tv_blackPhoneNumber);
                viewHolder.tv_blackPhoneMode = (TextView) view.findViewById(R.id.tv_blackPhoneMode);
                viewHolder.ib_phoneNumberDelete = (ImageButton) view.findViewById(R.id.ib_phoneNumberDelete);

                view.setTag(viewHolder);
            }

            viewHolder.ib_phoneNumberDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BlackNumberDao.getInstance(getApplicationContext()).delete(listBlackNumber.get(position).getPhoneNumber());
                    listBlackNumber.remove(position);
                    if (myAdapter != null) {
                        myAdapter.notifyDataSetChanged();
                    }
                }
            });

            viewHolder.tv_blackPhoneNumber.setText(listBlackNumber.get(position).getPhoneNumber());
            switch (listBlackNumber.get(position).getMode()) {
                case 1:
                    viewHolder.tv_blackPhoneMode.setText("拦截短信");
                    break;
                case 2:
                    viewHolder.tv_blackPhoneMode.setText("拦截电话");
                    break;
                case 3:
                    viewHolder.tv_blackPhoneMode.setText("拦截所有");
                    break;
            }
            return view;
        }
    }

    static class ViewHolder {
        TextView tv_blackPhoneNumber;
        TextView tv_blackPhoneMode;
        ImageButton ib_phoneNumberDelete;
    }
}
