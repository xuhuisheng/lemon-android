package com.mossle.android.support;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import android.app.Activity;

import android.os.AsyncTask;

import android.util.Log;

import android.widget.ListView;
import android.widget.Toast;

import com.mossle.android.activity.FormActivity;
import com.mossle.android.form.*;

import org.json.JSONArray;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FormAsyncTask extends AsyncTask<Activity, Void, Void> {
    private String urlText;
    private FormActivity formActivity;
    private ListView mListView;
    private JSONArray jsonArray;
    private Exception exception;
    private String sessionId;
    private XmlGuiForm theForm;
    private String tag = "form";
    private String processDefinitionId;
    private Exception e;
    private String data;

    public FormAsyncTask(String urlText, FormActivity formActivity,
            String sessionId, String data) {
        this.urlText = urlText;
        this.formActivity = formActivity;
        this.sessionId = sessionId;
        this.data = data;
    }

    protected Void doInBackground(Activity... activities) {
        // ListView listView = listViews[0];
        try {
            Log.i(tag, "ProcessForm");

            URL url = new URL(urlText);
            Log.i(tag, url.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            conn.setRequestMethod("POST");
            conn.addRequestProperty("sessionId", sessionId);
            conn.setDoOutput(true);

            conn.getOutputStream().write(data.getBytes());

            InputStream is = conn.getInputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document dom = db.parse(is);
            Element root = dom.getDocumentElement();
            NodeList forms = root.getElementsByTagName("form");

            if (forms.getLength() < 1) {
                // nothing here??
                Log.e(tag, "No form, let's bail");
                // Toast.makeText(this.activity.getApplication(), "No form, let's bail",
                // Toast.LENGTH_LONG).show();
                exception = new IllegalArgumentException("no form");

                return null;
            }

            Node form = forms.item(0);
            theForm = new XmlGuiForm();

            // process form level
            NamedNodeMap map = form.getAttributes();
            theForm.setFormNumber(map.getNamedItem("id").getNodeValue());
            theForm.setFormName(map.getNamedItem("name").getNodeValue());
			if (map.getNamedItem("readOnly") != null)
			{
			theForm.setReadOnly("true".equals(map.getNamedItem("readOnly").getNodeValue()));
			}

            if (map.getNamedItem("submitTo") != null) {
                theForm.setSubmitTo(map.getNamedItem("submitTo").getNodeValue());
            } else {
                theForm.setSubmitTo("loopback");
            }

            // now process the fields
            NodeList fields = root.getElementsByTagName("field");

            for (int i = 0; i < fields.getLength(); i++) {
                Node fieldNode = fields.item(i);
                NamedNodeMap attr = fieldNode.getAttributes();
                XmlGuiFormField tempField = new XmlGuiFormField();
                tempField.setName(attr.getNamedItem("name").getNodeValue());
                tempField.setLabel(attr.getNamedItem("label").getNodeValue());
                tempField.setType(attr.getNamedItem("type").getNodeValue());

                if (attr.getNamedItem("value") != null) {
                    tempField.setValue(attr.getNamedItem("value")
                            .getNodeValue());
                }

                if (attr.getNamedItem("required").getNodeValue().equals("Y")) {
                    tempField.setRequired(true);
                } else {
                    tempField.setRequired(false);
                }
				if (attr.getNamedItem("readOnly").getNodeValue().equals("Y")) {
					tempField.setReadOnly(true);
				} else {
					tempField.setReadOnly(false);
				}

                tempField.setOptions(attr.getNamedItem("options")
                        .getNodeValue());
                theForm.getFields().add(tempField);
            }

            Log.i(tag, theForm.toString());
        } catch (Exception e) {
            Log.e(tag, "Error occurred in ProcessForm:" + e.getMessage());
            e.printStackTrace();
            // Toast.makeText(this.activity.getApplication(), e.toString(),
            // Toast.LENGTH_LONG).show();
            exception = e;
        }

        // this.mListView = listView;

        // return true;
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
            // Toast.makeText(this.formActivity.getApplication(),
            //        exception.toString(), Toast.LENGTH_LONG).show();
            this.formActivity.onFailure();
        } else {
            // Toast.makeText(this.formActivity.getApplication(), "success",
            //        Toast.LENGTH_LONG).show();
            this.formActivity.onSuccess(theForm);
        }
    }
}
