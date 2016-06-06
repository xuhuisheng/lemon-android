package com.mossle.android.support;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.TextView;

import android.app.Activity;

import android.os.AsyncTask;

import android.util.Log;

import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.mossle.android.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.mossle.android.activity.MsgActivity;

public class MsgViewAsyncTask extends AsyncTask<TextView, Void, Void> {
    private String urlText;
    private Activity activity;
    private TextView mTextView;
    private Exception exception;
    private String sessionId;
	private String msgId;
	private String data;
	private JSONObject jsonObject;

    public MsgViewAsyncTask(String urlText, Activity activity, String sessionId, String msgId) {
        this.urlText = urlText;
        this.activity = activity;
        this.sessionId = sessionId;
		this.msgId = msgId;
    }

    protected Void doInBackground(TextView... textViews) {
        TextView textView = textViews[0];

        try {
            URL url = new URL(urlText);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("sessionId", sessionId);
            conn.setDoInput(true);
			conn.setDoOutput(true);
            conn.connect();
			conn.getOutputStream().write(("msgId=" + msgId).getBytes());

            InputStream is = conn.getInputStream();
            String result = readText(is);
            JSONTokener jsonParser = new JSONTokener(result);
            jsonObject = (JSONObject) jsonParser.nextValue();
            data = (String) jsonObject.get("data");

            jsonParser = new JSONTokener(data);
            jsonObject = (JSONObject) jsonParser.nextValue();
        } catch (Exception e) {
            Log.v("list", e.getMessage());
            this.exception = e;

            return null;
        }

        this.mTextView = textView;

        return null;
    }

    public void updateData() {
        Log.v("update", jsonObject.toString());

        try {
			String content = (String)jsonObject.get("content");
			this.mTextView.setText(content);
            // Toast.makeText(activity.getApplication(), "MsgActivity : " + content,
            //        Toast.LENGTH_SHORT).show();
			String data = (String) jsonObject.get("data");
			((MsgActivity)this.activity).setHumanTaskId(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.v("update", ex.getMessage());
            // Toast.makeText(activity.getApplication(), "MsgActivity : " + ex.getMessage(),
            //        Toast.LENGTH_SHORT).show();
        }
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
			// Toast.makeText(activity.getApplication(), "MsgActivity : " + exception, Toast.LENGTH_SHORT).show();
		}
        if (jsonObject != null) {
            // Toast.makeText(activity.getApplication(), jsonArray.toString(),
            // Toast.LENGTH_SHORT).show();
            this.updateData();
        } else {
            // Toast.makeText(activity.getApplication(), exception.toString(),
            //         Toast.LENGTH_SHORT).show();
			// Toast.makeText(activity.getApplication(), data, Toast.LENGTH_SHORT).show();
			// Toast.makeText(activity.getApplication(), "MsgActivity : " + jsonObject, Toast.LENGTH_SHORT).show();
        }

        this.mTextView = null;
    }
}
