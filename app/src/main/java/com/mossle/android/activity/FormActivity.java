package com.mossle.android.activity;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import android.widget.EditText;

import java.util.Map;

import android.annotation.TargetApi;
import android.view.MenuItem;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.AlertDialog.*;

import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.LinearLayout;

import android.widget.LinearLayout.LayoutParams;

import android.widget.ScrollView;

import com.mossle.android.AppConstants;
import com.mossle.android.R;
import com.mossle.android.form.*;
import com.mossle.android.support.FormAsyncTask;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.json.JSONObject;
import java.util.Calendar;  
import android.app.Activity;  
import android.app.DatePickerDialog;  
import android.app.Dialog;  
import android.app.DatePickerDialog.OnDateSetListener;  
import android.os.Bundle;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.Button;  
import android.widget.DatePicker;  
import android.widget.TextView;  

public class FormActivity extends AppCompatActivity {
	static final int DATE_DIALOG_ID = 0; 
    /** Called when the activity is first created. */
    String tag = FormActivity.class.getName();
    XmlGuiForm theForm;
    ProgressDialog progressDialog;
    Handler progressHandler;
    private SharedPreferences sp;
    private String formType;
    private String processDefinitionId;
    private String taskId;
    private String urlText;
    private String sessionId;
    protected SystemBarTintManager mTintManager;
    private Toolbar toolbar;
	private LinearLayout linearLayout;
	private LinearLayout ll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);

        this.sessionId = sp.getString("SESSIONID", null);

        String formNumber = "";
        Intent startingIntent = getIntent();

        if (startingIntent == null) {
            Log.e(tag, "No Intent?  We're not supposed to be here...");
            finish();

            return;
        }

        this.formType = startingIntent.getStringExtra("formType");
        this.processDefinitionId = startingIntent
                .getStringExtra("processDefinitionId");
        this.taskId = startingIntent.getStringExtra("taskId");

        if ("startProcess".equals(formType) && (processDefinitionId == null)) {
            AlertDialog.Builder bd = new AlertDialog.Builder(this);
            AlertDialog ad = bd.create();
            ad.setTitle("Error");
            ad.setMessage("processDefinitionId is null");
            ad.show();
        }

        if ("completeTask".equals(formType) && (taskId == null)) {
            AlertDialog.Builder bd = new AlertDialog.Builder(this);
            AlertDialog ad = bd.create();
            ad.setTitle("Error");
            ad.setMessage("taskId is null");
            ad.show();
        }

        if ("startProcess".equals(formType)) {
            this.urlText = AppConstants.getBaseUrl()
                    + "/rs/android/form/viewStartForm";
        } else {
            this.urlText = AppConstants.getBaseUrl()
                    + "/rs/android/form/viewTaskForm";
        }

        GetFormData(formNumber);
		//this.initToolBar();
		this.initView();
    }

    public void onSuccess(XmlGuiForm theForm) {
        this.theForm = theForm;
        DisplayForm();
    }

    public void onFailure() {
        Log.e(tag, "Couldn't parse the Form.");

        AlertDialog.Builder bd = new AlertDialog.Builder(this);
        AlertDialog ad = bd.create();
        ad.setTitle("Error");
        ad.setMessage("Could not parse the Form data");
        ad.show();
    }

	public void initView() {
		linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(android.widget.LinearLayout.VERTICAL);

		//
		android.support.design.widget.AppBarLayout appBarLayout = new android.support.design.widget.AppBarLayout(
				this);
		linearLayout.addView(appBarLayout);
		appBarLayout.setLayoutParams(new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		// // appBarLayout.setStyle("@style/FitSystemWindows");
		this.toolbar = new android.support.v7.widget.Toolbar(
				this);
		appBarLayout.addView(this.toolbar);

		// // toolbar.setLayoutParams(new LayoutParams(
		// // ViewGroup.LayoutParams.MATCH_PARENT, "?attr/actionBarSize"));
		// //toolbar.setBackground("?attr/colorPrimary");
		// // stoolbar.setElevation(8F);

		// //toolbar.setLayoutScrollFlags("scroll|enterAlways");
		// //toolbar.setTheme("@style/Base.ThemeOverlay.AppCompat.Dark.ActionBar");

		//
		ScrollView sv = new ScrollView(this);
		linearLayout.addView(sv);

		ll = new LinearLayout(this);
		sv.addView(ll);
		ll.setOrientation(android.widget.LinearLayout.VERTICAL);

		this.setContentView(linearLayout);
		// this.setTitle(theForm.getFormName());

		if (this.toolbar != null) {
			// App Logo
			// toolbar.setLogo(R.drawable.ic_launcher);
			// Title
			this.toolbar.setTitle("表单");
			this.setSupportActionBar(this.toolbar);
			this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			this.getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
	}

    /*
     * <LinearLayout android:layout_width="fill_parent" android:layout_height="fill_parent"
     * android:orientation="vertical" >
     * 
     * <android.support.design.widget.AppBarLayout android:layout_width="match_parent"
     * android:layout_height="wrap_content" style="@style/FitSystemWindows" android:id="@+id/appBarLayout">
     * <android.support.v7.widget.Toolbar android:id="@+id/toolbar" android:layout_width="match_parent"
     * android:layout_height="?attr/actionBarSize" android:background="?attr/colorPrimary" app:elevation="8dp"
     * app:layout_scrollFlags="scroll|enterAlways" app:theme="@style/Base.ThemeOverlay.AppCompat.Dark.ActionBar"/>
     * </android.support.design.widget.AppBarLayout>
     */
    private boolean DisplayForm() {
        try {

            // walk thru our form elements and dynamically create them, leveraging our mini library of tools.
            int i;

            for (i = 0; i < theForm.fields.size(); i++) {
				XmlGuiFormField xmlGuiFormField = theForm.fields.elementAt(i);
                String label = (xmlGuiFormField.isRequired() ? "*"
                        : "") + xmlGuiFormField.getLabel();
                String value = xmlGuiFormField.getValue();

                if (xmlGuiFormField.getType().equals("text")) {
					if (xmlGuiFormField.isReadOnly()) {
						xmlGuiFormField.obj = new XmlGuiLabel(this,
                            label, value);
					} else {
						xmlGuiFormField.obj = new XmlGuiEditBox(this,
                            label, value);
					}
                    ll.addView((View) xmlGuiFormField.obj);
                }

                if (xmlGuiFormField.getType().equals("numeric")) {
					if (xmlGuiFormField.isReadOnly()) {
						xmlGuiFormField.obj = new XmlGuiLabel(this,
                            label, value);
					} else {
						xmlGuiFormField.obj = new XmlGuiEditBox(this,
								label, value);
						((XmlGuiEditBox) xmlGuiFormField.obj)
								.makeNumeric();
					}
                    ll.addView((View) xmlGuiFormField.obj);
                }

                if (xmlGuiFormField.getType().equals("choice")) {
					if (xmlGuiFormField.isReadOnly()) {
						xmlGuiFormField.obj = new XmlGuiLabel(this,
                            label, value);
					} else {
						xmlGuiFormField.obj = new XmlGuiPickOne(this,
                            label, xmlGuiFormField.getOptions(),
                            value);
					}
                    ll.addView((View) xmlGuiFormField.obj);
                }

                if (xmlGuiFormField.getType().equals("datepicker")) {
					if (xmlGuiFormField.isReadOnly()) {
						xmlGuiFormField.obj = new XmlGuiLabel(this,
                            label, value);
					} else {
						xmlGuiFormField.obj = new XmlGuiDatePicker(this,
                            label,
                            value);
					}
                    ll.addView((View) xmlGuiFormField.obj);
                }

            }

			if (!theForm.isReadOnly()) {
				this.addButton();
			}

            return true;
        } catch (Exception e) {
            Log.e(tag, "Error Displaying Form");

            return false;
        }
    }

	public void addButton() {

            Button btn = new Button(this);
            btn.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            ll.addView(btn);

            btn.setText("提交");
            btn.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    // check if this form is Valid
                    if (!CheckForm()) {
                        AlertDialog.Builder bd = new AlertDialog.Builder(ll
                                .getContext());
                        AlertDialog ad = bd.create();
                        ad.setTitle("提示");
                        ad.setMessage("请填写所有必填字段(*)");
                        ad.show();

                        return;
                    }

                    if (theForm.getSubmitTo().equals("loopback")) {
                        // just display the results to the screen
                        String formResults = theForm.getFormattedResults();
                        Log.i(tag, formResults);

                        AlertDialog.Builder bd = new AlertDialog.Builder(ll
                                .getContext());
                        AlertDialog ad = bd.create();
                        ad.setTitle("Results");
                        ad.setMessage(formResults);
                        ad.show();

                        return;
                    } else {
                        if (!SubmitForm()) {
                            AlertDialog.Builder bd = new AlertDialog.Builder(ll
                                    .getContext());
                            AlertDialog ad = bd.create();
                            ad.setTitle("Error");
                            ad.setMessage("Error submitting form");
                            ad.show();

                            return;
                        }
                    }
                }
            });
	}

    private boolean SubmitForm() {
        try {
            boolean ok = true;
            this.progressDialog = ProgressDialog.show(this,
                    theForm.getFormName(), "Saving Form Data", true, false);
            this.progressHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // process incoming messages here
                    switch (msg.what) {
                    case 0:
                        // update progress bar
                        progressDialog.setMessage("" + (String) msg.obj);

                        break;

                    case 1:
                        progressDialog.cancel();
                        finish();

                        break;

                    case 2:
                        progressDialog.cancel();

                        break;
                    }

                    super.handleMessage(msg);
                }
            };

            Thread workthread = new Thread(new TransmitFormData(theForm));

            workthread.start();

            return ok;
        } catch (Exception e) {
            Log.e(tag, "Error in SubmitForm()::" + e.getMessage());
            e.printStackTrace();

            // tell user we failed....
            Message msg = new Message();
            msg.what = 1;
            this.progressHandler.sendMessage(msg);

            return false;
        }
    }

    private boolean CheckForm() {
        try {
            int i;
            boolean good = true;

            for (i = 0; i < theForm.fields.size(); i++) {
				XmlGuiFormField xmlGuiFormField = theForm.fields.elementAt(i);
				if (xmlGuiFormField.isReadOnly()) {
					continue;
				}
                String fieldValue = (String) xmlGuiFormField
                        .getData();
                Log.i(tag, theForm.fields.elementAt(i).getName() + " is ["
                        + fieldValue + "]");

                if (xmlGuiFormField.isRequired()) {
                    if (fieldValue == null) {
                        good = false;
                    } else {
                        if (fieldValue.trim().length() == 0) {
                            good = false;
                        }
                    }
                }
            }

            return good;
        } catch (Exception e) {
            Log.e(tag, "Error in CheckForm()::" + e.getMessage());
            e.printStackTrace();

            return false;
        }
    }

    private boolean GetFormData(String formNumber) {
        String data = "";

        if ("startProcess".equals(formType)) {
            data = "processDefinitionId=" + processDefinitionId;
        } else {
            data = "taskId=" + taskId;
        }

        new FormAsyncTask(urlText, this, sessionId, data).execute(this);

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
		/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        this.mTintManager = new SystemBarTintManager(this);
        this.mTintManager.setStatusBarTintEnabled(true);
        this.mTintManager.setTintColor(this.getResources().getColor(
                R.color.primary));
		*/
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }

        win.setAttributes(winParams);
    }

    private class TransmitFormData implements Runnable {
        XmlGuiForm _form;
        Message msg;

        TransmitFormData(XmlGuiForm form) {
            this._form = form;
        }

        public void run() {
            try {
                msg = new Message();
                msg.what = 0;
                msg.obj = ("Connecting to Server");
                progressHandler.sendMessage(msg);

                String urlText = AppConstants.getBaseUrl() + "/rs/android";

                if ("startProcess".equals(formType)) {
                    urlText += "/bpm/startProcess";
                } else {
                    urlText += "/task/completeTask";
                }

                URL url = new URL(urlText);

                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.addRequestProperty("sessionId", sessionId);
                conn.setDoOutput(true);

                BufferedOutputStream wr = new BufferedOutputStream(
                        conn.getOutputStream());
                Map<String, Object> data = _form.getMapData();
                JSONObject json = new JSONObject(data);
                String text = "data=" + json.toString();

                if ("startProcess".equals(formType)) {
                    text += ("&processDefinitionId=" + processDefinitionId);
                } else {
                    text += ("&taskId=" + taskId);
                }

                wr.write(text.getBytes());
                wr.flush();
                wr.close();

                msg = new Message();
                msg.what = 0;
                msg.obj = ("Data Sent");
                progressHandler.sendMessage(msg);

                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line = "";
                Boolean bSuccess = false;

                bSuccess = conn.getResponseCode() == 200;

                while ((line = rd.readLine()) != null) {
                    if (line.indexOf("SUCCESS") != -1) {
                        bSuccess = true;
                    }

                    // Process line...
                    Log.v(tag, line);
                }

                wr.close();
                rd.close();

                if (bSuccess) {
                    msg = new Message();
                    msg.what = 0;
                    msg.obj = ("Form Submitted Successfully");
                    progressHandler.sendMessage(msg);

                    msg = new Message();
                    msg.what = 1;
                    progressHandler.sendMessage(msg);

                    return;
                }
            } catch (Exception e) {
                Log.d(tag, "Failed to send form data: " + e.getMessage());
                msg = new Message();
                msg.what = 0;
                msg.obj = ("Error Sending Form Data");
                progressHandler.sendMessage(msg);
            }

            msg = new Message();
            msg.what = 2;
            progressHandler.sendMessage(msg);
        }
    }

	/*
    protected void initToolBar() {
        this.toolbar = (Toolbar) this.findViewById(R.id.toolbar);

        if (toolbar != null) {
			// App Logo
			// toolbar.setLogo(R.drawable.ic_launcher);
			// Title
			toolbar.setTitle("表单");
            this.setSupportActionBar(this.toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
	}
	*/

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	public void setEditText(EditText editText) {
		this.editText = editText;
	}
	
	//需要定义弹出的DatePicker对话框的事件监听器：  
    private DatePickerDialog.OnDateSetListener mDateSetListener = new OnDateSetListener() {  
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {  
                mYear = year;  
                mMonth = monthOfYear;  
                mDay = dayOfMonth;  
                //设置文本的内容：  
                editText.setText(new StringBuilder()  
                            .append(mYear).append("-")  
                            .append(mMonth + 1).append("-")//得到的月份+1，因为从0开始  
                            .append(mDay).append(""));  
            }  
        };  

	int mYear;
	int mMonth;
	int mDay;
	EditText editText;

    /** 
     * 当Activity调用showDialog函数时会触发该函数的调用： 
     */  
    @Override  
    protected Dialog onCreateDialog(int id) {  
           switch (id) {  
            case DATE_DIALOG_ID:  
                return new DatePickerDialog(this,mDateSetListener, mYear, mMonth, mDay);  
            }  
            return null;  
    } 
}
