package com.mossle.android.activity;

import java.util.Map;
import java.util.HashMap;

import android.annotation.TargetApi;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.AdapterView;

import android.widget.AdapterView.OnItemClickListener;

import android.widget.ListView;
import android.widget.TextView;
import android.view.MenuItem;
import android.widget.Button;

import com.mossle.android.AppConstants;
import com.mossle.android.R;
import com.mossle.android.support.MsgViewAsyncTask;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class MsgActivity extends AppCompatActivity {
    private SharedPreferences sp;
    protected SystemBarTintManager mTintManager;
    private Toolbar toolbar;
	private String msgId;
    public final static String TAG = "tag";
	private TextView textView;
	private Map<String, Object> data = new HashMap<String, Object>();
	private String humanTaskId = "1";

    public void onCreate(Bundle bundle) {
		try{
			super.onCreate(bundle);
			sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
			setContentView(R.layout.activity_msg);
			this.textView = (TextView) this.findViewById(R.id.view_text_view);
			this.initToolBar();
		}catch(Exception ex) {
			ex.printStackTrace();
			// Toast.makeText(this.getApplication(), "MsgActivity : " + ex.getMessage(), Toast.LENGTH_SHORT).show();;
		}
    }

    public void initMsg() {
		try{
			Intent startingIntent = getIntent();

			if (startingIntent == null) {
				Log.e(TAG, "No Intent?  We're not supposed to be here...");
				finish();

				return;
			}

			this.msgId = startingIntent.getStringExtra("msgId");
			// Toast.makeText(this.getApplication(), "MsgActivity : " + this.msgId, Toast.LENGTH_SHORT).show();

			String sessionId = sp.getString("SESSIONID", null);
			new MsgViewAsyncTask(AppConstants.getBaseUrl()
					+ "/rs/android/msg/view", this, sessionId, msgId)
					.execute(textView);
			this.textView.setOnClickListener(new ClickListener());
		}catch(Exception ex) {
			ex.printStackTrace();
			// Toast.makeText(this.getApplication(), "MsgActivity : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
		/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 状态栏透明 需要在创建SystemBarTintManager 之前调用。
            setTranslucentStatus(true);
        }

        this.mTintManager = new SystemBarTintManager(this);
        this.mTintManager.setStatusBarTintEnabled(true);
        this.mTintManager.setTintColor(this.getResources().getColor(
                R.color.primary));
		*/
    }

    protected void initToolBar() {
        this.toolbar = (Toolbar) this.findViewById(R.id.toolbar);

        if (toolbar != null) {
			// App Logo
			// toolbar.setLogo(R.drawable.ic_launcher);
			// Title
			toolbar.setTitle("私信");
            this.setSupportActionBar(this.toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
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

    @Override
    public void onResume() {
        super.onResume();

        this.refresh();
    }

    private void refresh() {
        this.initMsg();
    }

	class ClickListener implements Button.OnClickListener {
		public void onClick(View v) {
            Intent intent = new Intent(MsgActivity.this, FormActivity.class);
            intent.putExtra("taskId", humanTaskId);
            intent.putExtra("formType", "completeTask");
            MsgActivity.this.startActivity(intent);
		}
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	public void setHumanTaskId(String humanTaskId) {
            this.humanTaskId = humanTaskId;
        }
}
