package com.mossle.android.support;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;

import android.os.AsyncTask;

import android.util.Log;

import android.view.View;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mossle.android.R;

import org.json.JSONObject;
import org.json.JSONTokener;

public class ProfileAsyncTask extends AsyncTask<View, Void, Void> {
    private String urlText;
    private Activity activity;
    private ListView mListView;
    private JSONObject jsonObject;
    private Exception exception;
    private View mView;
    private String sessionId;

	private String resultText;

    public ProfileAsyncTask(String urlText, Activity activity, String sessionId) {
        this.urlText = urlText;
        this.activity = activity;
        this.sessionId = sessionId;
    }

    protected Void doInBackground(View... views) {
        View view = views[0];

        try {
            URL url = new URL(urlText);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("sessionId", sessionId);
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            resultText = readText(is);
            JSONTokener jsonParser = new JSONTokener(resultText);
            JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
            String data = (String) jsonObject.get("data");
            jsonParser = new JSONTokener(data);
            this.jsonObject = (JSONObject) jsonParser.nextValue();
        } catch (Exception e) {
            Log.v("list", e.getMessage());
            this.exception = e;

            return null;
        }

        this.mView = view;

        return null;
    }

    public void updateData() {
        try {
            ((TextView) mView.findViewById(R.id.fragment_profile_username))
                    .setText((String) jsonObject.get("username"));
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.v("update", ex.getMessage());
            //Toast.makeText(activity.getApplication(), ex.getMessage(),
            //        Toast.LENGTH_LONG).show();
        }
        try {
            ((TextView) mView.findViewById(R.id.fragment_profile_displayName))
                    .setText((String) jsonObject.get("displayName"));
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.v("update", ex.getMessage());
            // Toast.makeText(activity.getApplication(), ex.getMessage(),
            //        Toast.LENGTH_LONG).show();
        }
        try {
            ((TextView) mView.findViewById(R.id.fragment_profile_email))
                    .setText((String) jsonObject.get("email"));
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.v("update", ex.getMessage());
            // Toast.makeText(activity.getApplication(), ex.getMessage(),
            //        Toast.LENGTH_LONG).show();
        }
        try {
            ((TextView) mView.findViewById(R.id.fragment_profile_mobile))
                    .setText((String) jsonObject.get("mobile"));
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.v("update", ex.getMessage());
            // Toast.makeText(activity.getApplication(), ex.getMessage(),
            //        Toast.LENGTH_LONG).show();
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
        if (jsonObject != null) {
            // Toast.makeText(activity.getApplication(), jsonArray.toString(),
            // Toast.LENGTH_LONG).show();
            this.updateData();
        } else {
            // Toast.makeText(activity.getApplication(), exception.toString(),
            //        Toast.LENGTH_SHORT).show();
			// Toast.makeText(activity.getApplication(), urlText, Toast.LENGTH_SHORT).show();
			// Toast.makeText(activity.getApplication(), resultText, Toast.LENGTH_SHORT).show();
        }

        this.mListView = null;
    }
}
