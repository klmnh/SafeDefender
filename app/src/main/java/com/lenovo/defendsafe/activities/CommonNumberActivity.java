package com.lenovo.defendsafe.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.content.Intent;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.CommonUtils;

import java.util.List;

/**
 * Created by Lenovo on 2017/9/11 011.
 */

public class CommonNumberActivity extends AppCompatActivity {

    private List<CommonUtils.NumberGroup> numberGroupList;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonnumber);

        InitCommonNumber();
    }

    private void InitCommonNumber() {
        final ExpandableListView elv_commonNumber = (ExpandableListView) findViewById(R.id.elv_commonNumber);
        elv_commonNumber.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + numberGroupList.get(groupPosition).childList.get(childPosition).number));
                if (ActivityCompat.checkSelfPermission(CommonNumberActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return false;
                }
                startActivity(intent);

                return false;
            }
        });

        new Thread(){
            @Override
            public void run() {
                numberGroupList = CommonUtils.GetCommonNumber(getApplicationContext());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(myAdapter == null) {
                            myAdapter = new MyAdapter();
                            elv_commonNumber.setAdapter(myAdapter);
                        }else {
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }.start();
    }

    private class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return numberGroupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return numberGroupList.get(groupPosition).childList.size();
        }

        @Override
        public CommonUtils.NumberGroup getGroup(int groupPosition) {
            return numberGroupList.get(groupPosition);
        }

        @Override
        public CommonUtils.NumberChild getChild(int groupPosition, int childPosition) {
            return numberGroupList.get(groupPosition).childList.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder viewHolder = null;
            if (convertView != null){
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
                viewHolder.tv_view.setText("          " + getGroup(groupPosition).name);
            }else {
                TextView textView = new TextView(CommonNumberActivity.this);
                textView.setTextColor(Color.RED);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                textView.setText("          " + getGroup(groupPosition).name);

                viewHolder = new ViewHolder();
                viewHolder.tv_view = textView;
                textView.setTag(viewHolder);
                view = textView;
            }

            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            View view;
            ViewChildHolder viewChildHolder;
            if (convertView != null) {
                view = convertView;
                viewChildHolder = (ViewChildHolder) convertView.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.adapter_commonnumber, null);
                viewChildHolder = new ViewChildHolder();
                viewChildHolder.tv_viewNumber = (TextView) view.findViewById(R.id.tv_commonNumber);
                viewChildHolder.tv_viewName = (TextView) view.findViewById(R.id.tv_commonName);

                view.setTag(viewChildHolder);
            }
            CommonUtils.NumberChild numberChild = getChild(groupPosition, childPosition);
            viewChildHolder.tv_viewNumber.setText("     " +numberChild.number);
            viewChildHolder.tv_viewName.setText("     " +numberChild.name);

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    static class ViewHolder{

        TextView tv_view;
    }

    static class ViewChildHolder{

        TextView tv_viewNumber;
        TextView tv_viewName;
    }
}
