package com.mossle.android.activity;

import java.util.Map;

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
import android.view.MenuItem;

import com.mossle.android.AppConstants;
import com.mossle.android.R;
import com.mossle.android.support.ProcessDefinitionAsyncTask;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import android.support.v7.app.AppCompatActivity;

public class ProcessDefinitionActivity extends AppCompatActivity {
    private SharedPreferences sp;
    protected SystemBarTintManager mTintManager;
    private Toolbar toolbar;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
        setContentView(R.layout.activity_process_definition);
		this.initToolBar();
    }

    public void initTask() {
        ListView listView = (ListView) this.findViewById(R.id.activity_process_definition);
        String sessionId = sp.getString("SESSIONID", null);
        new ProcessDefinitionAsyncTask(AppConstants.getBaseUrl()
                + "/rs/android/bpm/processDefinitions", this, sessionId)
                .execute(listView);
        listView.setOnItemClickListener(new ItemClickListener());
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
			toolbar.setTitle("流程定义");
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
        this.initTask();
    }

    class ItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            // Map<String, Object> item = (Map<String, Object>) arg0
            //        .getItemAtPosition(arg2);

            // String taskId = (String) item.get("taskId");
            // Intent intent = new Intent(ProcessDefinitionActivity.this, FormActivity.class);
            // intent.putExtra("taskId", taskId);
            // intent.putExtra("formType", "completeTask");
            // TaskActivity.this.startActivity(intent);
            // 在本例中arg2=arg3
            Map<String, Object> item = (Map<String, Object>) arg0
                    .getItemAtPosition(arg2);

            // 显示所选Item的ItemText
            // setTitle((String) item.get("ItemText"));
            String processDefinitionId = (String) item
                    .get("processDefinitionId");
            Intent intent = new Intent(ProcessDefinitionActivity.this, FormActivity.class);
            intent.putExtra("processDefinitionId", processDefinitionId);
            intent.putExtra("formType", "startProcess");
            ProcessDefinitionActivity.this.startActivity(intent);
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
}
