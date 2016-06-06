package com.mossle.android.support;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.net.ConnectivityManager;

import android.os.AsyncTask;

import android.widget.Toast;

import com.mossle.android.AppConstants;
import com.mossle.android.R;
import com.mossle.android.activity.MainActivity;

public class UpdateInfoAsyncTask extends AsyncTask<MainActivity, Void, Void> {
    private MainActivity mainActivity;
    private Exception exception;
    private UpdateInfo info;
    private String localVersion;

    public UpdateInfoAsyncTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        try {
            this.localVersion = getVersionName();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected Void doInBackground(MainActivity... mainActivities) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (!connectivityManager.getActiveNetworkInfo().isAvailable()) {
            exception = new Exception("无法联网");

            return null;
        }

        try {
            String urlText = mainActivity.getResources().getString(
                    R.string.url_server);
            URL url = new URL(urlText);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            info = UpdateInfoParser.getUpdateInfo(is);
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;

            // Toast.makeText(loginActivity, e.toString(), Toast.LENGTH_LONG)
            // .show();
            // Toast.makeText(loginActivity, "用户名或密码错误，请重新登录",
            // Toast.LENGTH_LONG).show();
        }

        return null;
    }

    protected void onPostExecute(Void result) {
        if (exception != null) {
            // Toast.makeText(mainActivity, exception.toString(),
            //        Toast.LENGTH_SHORT).show();
        }

        if (info == null) {
            return;
        }

        if (info.getVersion().equals(localVersion)) {
            // Log.i(TAG, "版本号相同");
            // Toast.makeText(mainActivity.getApplicationContext(), "不需要更新",
            //        Toast.LENGTH_SHORT).show();

            // LoginMain();
        } else {
            // Log.i(TAG, "版本号不相同 ");
            mainActivity.showUpdataDialog(info);
        }
    }

    private String getVersionName() throws Exception {
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageManager packageManager = mainActivity.getPackageManager();

        PackageInfo packInfo = packageManager.getPackageInfo(
                mainActivity.getPackageName(), 0);

        return packInfo.versionName;
    }
}
