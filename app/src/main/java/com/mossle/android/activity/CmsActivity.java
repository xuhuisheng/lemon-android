package com.mossle.android.activity;

import android.annotation.TargetApi;

import android.app.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;
import android.view.MenuItem;

import android.widget.ListView;
import android.widget.Toast;

import com.mossle.android.AppConstants;
import com.mossle.android.R;
import com.mossle.android.support.CmsAsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.readystatesoftware.systembartint.SystemBarTintManager;

public class CmsActivity extends AppCompatActivity {
    private SharedPreferences sp;
    protected SystemBarTintManager mTintManager;
    private Toolbar toolbar;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
        setContentView(R.layout.activity_cms);
		this.initToolBar();
        this.initTask();
    }

    public void initTask() {
        ListView listView = (ListView) this.findViewById(R.id.activity_cms);
        String sessionId = sp.getString("SESSIONID", null);
        new CmsAsyncTask(AppConstants.getBaseUrl() + "/rs/android/cms/articles",
                this, sessionId).execute(listView);
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

    protected void initToolBar() {
        this.toolbar = (Toolbar) this.findViewById(R.id.toolbar);

        if (toolbar != null) {
			// App Logo
			// toolbar.setLogo(R.drawable.ic_launcher);
			// Title
			toolbar.setTitle("公告");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
