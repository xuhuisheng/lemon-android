package com.mossle.android.support;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;

import android.net.ConnectivityManager;

import android.os.AsyncTask;

import android.widget.Toast;

import com.mossle.android.AppConstants;
import com.mossle.android.activity.SplashActivity;

public class CheckLoginAsyncTask extends AsyncTask<SplashActivity, Void, Void> {
    private String urlText = AppConstants.getBaseUrl() + "/rs/android/checkLogin";
    private SplashActivity splashActivity;
    private String sessionId;
    private SharedPreferences sp;
    private Exception exception;
    private int responseCode;
    private String resultText;

    public CheckLoginAsyncTask(SplashActivity splashActivity,
            SharedPreferences sp) {
        this.splashActivity = splashActivity;
        this.sp = sp;
        this.sessionId = sp.getString("SESSIONID", null);
    }

    protected Void doInBackground(SplashActivity... splashActivities) {
        ConnectivityManager connectivityManager = (ConnectivityManager) splashActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (!connectivityManager.getActiveNetworkInfo().isAvailable()) {
            exception = new Exception("无法联网");

            return null;
        }

        try {
            URL url = new URL(urlText);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.addRequestProperty("sessionId", sessionId);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            String text = "type=android&code=1111&name=MI2S";
            conn.getOutputStream().write(text.getBytes());

            InputStream is = conn.getInputStream();
            resultText = readText(is);
            responseCode = conn.getResponseCode();

            if ((resultText == null) || "".equals(resultText)
                    || "null".equals(resultText)) {
                responseCode = 204;
            }
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

    public String readText(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len = -1;

            while ((len = is.read(b, 0, 1024)) != -1) {
                baos.write(b, 0, len);
            }

            is.close();

            return new String(baos.toByteArray(), "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();

            return null;
        }
    }

    protected void onPostExecute(Void result) {
        if (exception != null) {
            // Toast.makeText(splashActivity, exception.getMessage(),
            //         Toast.LENGTH_LONG).show();
        }

        if (responseCode == 200) {
            splashActivity.jumpToMain();
        } else {
            splashActivity.jumpToLogin();
        }
    }
}
