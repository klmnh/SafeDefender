package com.lenovo.defendsafe.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.defendsafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lenovo on 2017/9/5 005.
 */

public class ContactListActivity extends AppCompatActivity {

    private ListView lv_contactList;
    private List<HashMap<String, String>> listMap = new ArrayList<HashMap<String, String>>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contactlist);

        lv_contactList = (ListView) findViewById(R.id.lv_contactList);

        InitData();

        lv_contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (listMap != null){
                    HashMap<String, String> hashMap = listMap.get(position);
                    String phone = hashMap.get("phone");
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);
                    setResult(0, intent);

                    finish();
                }

            }
        });

    }

    /**
     * content://com.android.contacts/raw_contacts
     * content://com.android.contacts/data
     * 初始化数据 （raw_contacts[contact_id], data[raw_contact_id, mimetype_id],
     * mime_types[vnd.android.cursor.item/name,vnd.android.cursor.item/phone_v2]）
     */
    private void InitData() {
        new Thread(){
            @Override
            public void run() {
                listMap.clear();
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},
                        null,null,null);
                while (cursor.moveToNext()){
                    String id = cursor.getString(0);

                    Cursor dataCursor = contentResolver.query(Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"},
                            "raw_contact_id = ?", new String[]{id},null);
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    while (dataCursor.moveToNext()){
                        String data = dataCursor.getString(0);
                        String mimetype = dataCursor.getString(1);


                            if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                                if(!TextUtils.isEmpty(data)) {
                                    hashMap.put("phone", data);

                                    listMap.add(hashMap);
                                }
                            } else if (mimetype.equals("vnd.android.cursor.item/name")) {
                                hashMap.put("name", data);
                            }
                    }
                    dataCursor.close();
                }
                cursor.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        lv_contactList.setAdapter(new ContactAdapter());
                    }
                });
            }
        }.start();

    }

    private class ContactAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listMap.size();
        }

        @Override
        public Object getItem(int position) {
            return listMap.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(getApplicationContext(), R.layout.adapter_contactlist, null);
            }
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);

            tv_name.setText(listMap.get(position).get("name"));
            tv_phone.setText(listMap.get(position).get("phone"));

            return view;
        }
    }
}
