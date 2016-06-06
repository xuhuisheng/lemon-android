package com.mossle.android.support;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class CmsAsyncTask extends AsyncTask<ListView, Void, Void> {
    private String urlText;
    private Activity activity;
    private ListView mListView;
    private JSONArray jsonArray;
    private Exception exception;
    private String sessionId;

    public CmsAsyncTask(String urlText, Activity activity, String sessionId) {
        this.urlText = urlText;
        this.activity = activity;
        this.sessionId = sessionId;
    }

    protected Void doInBackground(ListView... listViews) {
        ListView listView = listViews[0];

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
            String result = readText(is);
            JSONTokener jsonParser = new JSONTokener(result);
            JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
            String data = (String) jsonObject.get("data");
            jsonParser = new JSONTokener(data);
            jsonArray = (JSONArray) jsonParser.nextValue();
        } catch (Exception e) {
            Log.v("list", e.getMessage());
            this.exception = e;

            return null;
        }

        this.mListView = listView;

        return null;
    }

    public void updateData() {
        Log.v("update", jsonArray.toString());

        try {
            // 生成动态数组，并且转载数据
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put("ItemTitle", (String) jsonObject.get("title"));
                map.put("ItemText", (String) jsonObject.get("content"));
                // map.put("id", jsonObject.get("id").toString());
                list.add(map);
            }

            // 生成适配器，数组===》ListItem
            SimpleAdapter simpleAdapter = new SimpleAdapter(activity, list,
                    R.layout.item_cms,
                    new String[] { "ItemTitle", "ItemText" }, new int[] {
                            R.id.ItemTitle, R.id.ItemText });
            // 添加并且显示
            mListView.setAdapter(simpleAdapter);
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
        if (jsonArray != null) {
            // Toast.makeText(activity.getApplication(), jsonArray.toString(),
            // Toast.LENGTH_LONG).show();
            this.updateData();
        } else {
            // Toast.makeText(activity.getApplication(), exception.toString(),
            //        Toast.LENGTH_LONG).show();
        }

        this.mListView = null;
    }
}
