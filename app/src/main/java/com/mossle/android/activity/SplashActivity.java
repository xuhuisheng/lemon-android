package com.mossle.android.activity;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.mossle.android.R;
import com.mossle.android.support.CheckLoginAsyncTask;

public class SplashActivity extends Activity {
    private SharedPreferences sp;
    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            new CheckLoginAsyncTask(SplashActivity.this, sp)
                    .execute(SplashActivity.this);
        }
    };

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_splash);
        this.getWindow().setBackgroundDrawableResource(R.drawable.splash);

        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);

        mMainHandler.sendEmptyMessageDelayed(0, 500);
    }

    @Override
    public void onBackPressed() {
    }

    public void jumpToMain() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(getApplication(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void jumpToLogin() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(getApplication(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
