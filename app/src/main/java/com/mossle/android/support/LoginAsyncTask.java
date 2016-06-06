package com.mossle.android.support;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;

import android.content.SharedPreferences;

import android.os.AsyncTask;

import android.telephony.TelephonyManager;

import android.widget.Toast;

import com.mossle.android.AppConstants;
import com.mossle.android.activity.LoginActivity;

import org.json.JSONObject;
import org.json.JSONTokener;

public class LoginAsyncTask extends AsyncTask<LoginActivity, Void, Void> {
    private String urlText = AppConstants.getBaseUrl() + "/rs/android/login";
    private LoginActivity loginActivity;
    private String sessionId;
    private String username;
    private String password;
    private SharedPreferences sp;
    private Exception exception;
    private String deviceCode;
    private String deviceName;

    public LoginAsyncTask(LoginActivity loginActivity, String username,
            String password, SharedPreferences sp) {
        this.loginActivity = loginActivity;
        this.username = username;
        this.password = password;
        this.sp = sp;
        this.deviceCode = this.findImei();
        this.deviceName = android.os.Build.MODEL;

        String sdk = android.os.Build.VERSION.SDK; // SDK号
        String model = android.os.Build.MODEL; // 手机型号
        String release = android.os.Build.VERSION.RELEASE; // android系统版本号
    }

    protected Void doInBackground(LoginActivity... loginActivities) {
        try {
            URL url = new URL(urlText);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            String text = "username=" + username + "&password=" + password
                    + "&type=android&code=" + deviceCode + "&name="
                    + deviceName;
            conn.getOutputStream().write(text.getBytes());

            InputStream is = conn.getInputStream();
            String result = readText(is);
            JSONTokener jsonParser = new JSONTokener(result);
            JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
            String data = (String) jsonObject.get("data");
            this.sessionId = data;
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;

            // Toast.makeText(loginActivity, e.toString(), Toast.LENGTH_LONG)
            // .show();
            Toast.makeText(loginActivity, "用户名或密码错误，请重新登录",
             Toast.LENGTH_LONG).show();
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
            exception = ex;

            return null;
        }
    }

    protected void onPostExecute(Void result) {
        if (sessionId != null) {
            loginActivity.onSuccess(sessionId);
        } else {
			loginActivity.onFailure();
            Toast.makeText(loginActivity, "用户名或密码错误，请重新登录", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public String findImei() {
        String imei = ((TelephonyManager) loginActivity
                .getSystemService(Activity.TELEPHONY_SERVICE)).getDeviceId();

        return imei;
    }
}
