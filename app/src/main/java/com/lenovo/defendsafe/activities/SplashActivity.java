package com.lenovo.defendsafe.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenovo.defendsafe.R;
import com.lenovo.defendsafe.utils.CommonUtils;
import com.lenovo.defendsafe.utils.ConstantValue;
import com.lenovo.defendsafe.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private int mLocalVersionCode;
    private PackageInfo packageInfo;

    private final int UPDATE_VERSION = 100;
    private final int ENTER_HOME = 101;
    private final int ERROR_JSON = 102;
    private final int ERROR_IO = 103;
    private final int ERROR_URL = 104;

    private String versionDesc;
    private String downloadUrl;
    private int successNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        TextView textView = (TextView) findViewById(R.id.txtVersion);

        InitAnimation();

        InitData(textView);

        InitDB("telocation.db");

        InitDB("commonnum.db");

        InitDB("antivirus.db");
    }

    private void InitDB(String dbName) {

        InputStream is = null;
        FileOutputStream fos = null;
        File file = getFilesDir();
        File fileDB = new File(file, dbName);
        if (!fileDB.exists()) {
            try {
                is = getAssets().open(dbName);
                fos = new FileOutputStream(fileDB);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void InitAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setFillAfter(true);
        RelativeLayout rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        rl_root.startAnimation(alphaAnimation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1:
                EnterHomeActivity();
                break;
        }
    }

    /**
     * 进入主界面
     */
    private void EnterHomeActivity() {

        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 更新版本
     */
    private void UpdateVersion() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.splash);
        builder.setTitle("更新版本");
        builder.setMessage(versionDesc);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DownloadVersion();
            }
        });
        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                EnterHomeActivity();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                EnterHomeActivity();
            }
        });

        builder.show();
    }

    /**
     * 下载更新包
     */
    private void DownloadVersion() {
        new Thread() {
            @Override
            public void run() {

                URL url = null;
                try {
                    url = new URL(downloadUrl);
                    HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                    httpUrlConnection.setRequestMethod("GET");
                    httpUrlConnection.setConnectTimeout(1000);
                    if (httpUrlConnection.getResponseCode() == 200) {
                        int len = httpUrlConnection.getContentLength();
                        String path = "";
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                            path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        } else {

                            path = Environment.getDownloadCacheDirectory().getAbsolutePath();
                        }
                        path += ("/" + downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1));
                        RandomAccessFile raFile = new RandomAccessFile(path, "rw");
                        raFile.setLength(len);

                        int threadNum = 3;
                        int blockLength = len / threadNum;
                        for (int i = 0; i < threadNum; i++) {

                            int startIndex = i * blockLength;
                            int endIndex = (i + 1) * blockLength - 1;
                            if (i == threadNum - 1) {
                                endIndex = len - 1;
                            }

                            Log.i(TAG, path);
                            new DownloadThread(startIndex, endIndex, i, path, threadNum).start();
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }


    /**
     * 加载启动信息
     *
     * @param textView 版本展示
     */
    private void InitData(TextView textView) {
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            textView.setText(textView.getText() + packageInfo.versionName);
            mLocalVersionCode = packageInfo.versionCode;

            if (SPUtils.getBoolean(this, ConstantValue.SETTINGS_UPDATE, false)) {
                //从服务器获取更新信息
                CheckVersion();
            } else {
                handler.sendEmptyMessageDelayed(ENTER_HOME, 2000);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测更新信息
     */
    private void CheckVersion() {
        new Thread() {

            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                InputStream inputStream = null;
                Message message = Message.obtain();
                try {
                    URL url = new URL("http://10.0.2.2:8080/updateSafeDefender.json");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(5);
                    httpURLConnection.setReadTimeout(3);
                    if (200 == httpURLConnection.getResponseCode()) {

                        inputStream = httpURLConnection.getInputStream();

                        String json = CommonUtils.StreamToString(inputStream);

                        if (!TextUtils.isEmpty(json)) {

                            JSONObject jsonObject = new JSONObject(json);
                            String versionName = jsonObject.getString("VersionName");
                            String versionCode = jsonObject.getString("VersionCode");
                            versionDesc = jsonObject.getString("VersionDesc");
                            downloadUrl = jsonObject.getString("DownloadUrl");

                            if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                                message.what = UPDATE_VERSION;
                            } else {
                                message.what = ENTER_HOME;
                            }

                        }
                    }

                } catch (MalformedURLException e) {
                    message.what = ERROR_URL;
                } catch (IOException e) {
                    message.what = ERROR_IO;
                } catch (JSONException e) {
                    message.what = ERROR_JSON;
                } finally {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - startTime < 2000) {
                        try {
                            Thread.sleep(2000 - (currentTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(message);
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE_VERSION:
                    UpdateVersion();
                    break;
                case ERROR_JSON:
                    CommonUtils.ShowToastInfo(getApplicationContext(), "Json解析异常！");
                    EnterHomeActivity();
                    break;
                case ERROR_IO:
                    CommonUtils.ShowToastInfo(getApplicationContext(), "文件IO异常！");
                    EnterHomeActivity();
                    break;
                case ERROR_URL:
                    CommonUtils.ShowToastInfo(getApplicationContext(), "url解析异常！");
                    EnterHomeActivity();
                    break;
                case ENTER_HOME:
                    EnterHomeActivity();
                    break;
                default:
                    break;
            }
        }
    };


    public class DownloadThread extends Thread {

        private int startIndex;
        private int endIndex;
        private int threadID;
        private String path;
        private int threadNum;

        public DownloadThread(int startIndex, int endIndex, int threadID, String path, int threadNum) {

            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.threadID = threadID;
            this.path = path;
            this.threadNum = threadNum;
        }

        @Override
        public void run() {

            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(1000);
                urlConnection.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
                if (urlConnection.getResponseCode() == 206) {

                    InputStream is = urlConnection.getInputStream();

                    RandomAccessFile raFile = new RandomAccessFile(path, "rwd");
                    raFile.seek(startIndex);
                    Log.i(TAG, startIndex + "");
                    byte[] buffer = new byte[1024 * 512];
                    int tempLen = 0;
                    while ((tempLen = is.read(buffer)) != -1) {

                        raFile.write(buffer, 0, tempLen);
                    }
                    raFile.close();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                synchronized (DownloadThread.class) {
                    successNum++;
                    if (successNum == threadNum) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                //intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                                startActivityForResult(intent, 1);
                            }
                        });
                    }
                }
            }

        }
    }
}
