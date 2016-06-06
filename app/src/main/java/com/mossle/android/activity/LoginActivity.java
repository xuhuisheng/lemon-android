package com.mossle.android.activity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.SharedPreferences.Editor;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import com.mossle.android.AppConstants;
import android.os.Bundle;
import android.content.DialogInterface;

import android.view.View;

import android.view.View.OnClickListener;

import android.view.Window;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.text.TextUtils;

import android.widget.CompoundButton.OnCheckedChangeListener;

import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import com.mossle.android.R;
import com.mossle.android.support.LoginAsyncTask;
import android.app.AlertDialog;

public class LoginActivity extends Activity {
    private EditText userName;
    private EditText password;
    private CheckBox rememberPassword;
    private CheckBox autoLogin;
    private Button loginButton;
    private String userNameValue;
    private String passwordValue;
    private SharedPreferences sp;
    private TextView mSwitchLoginServer;
	private TextView login1;
	private TextView login2;
	private TextView login3;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 去除标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        // 获得实例对象
        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
        userName = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        // rememberPassword = (CheckBox) findViewById(R.id.cb_mima);
		// autoLogin = (CheckBox) findViewById(R.id.cb_auto);
		loginButton = (Button) findViewById(R.id.login_button);

        // 判断记住密码多选框的状态
        if (sp.getBoolean("ISCHECK", false)) {
            // 设置默认是记录密码状态
            rememberPassword.setChecked(true);
            userName.setText(sp.getString("USERNAME", "").toLowerCase());
            password.setText(sp.getString("PASSWORD", ""));

            // 判断自动登陆多选框状态
            if (sp.getBoolean("AUTO_ISCHECK", false)) {
                // 设置默认是自动登录状态
                autoLogin.setChecked(true);

                // 跳转界面
                Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.finish();
            }
        }

        // 登录监听事件 现在默认为用户名为：lingo 密码：1
        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
				// LoginActivity.this.findViewById(R.id.login_page).setVisibility(View.GONE);
				LoginActivity.this.findViewById(R.id.login_status).setVisibility(View.VISIBLE);
                userNameValue = userName.getText().toString().toLowerCase();
                passwordValue = password.getText().toString();
                new LoginAsyncTask(LoginActivity.this, userNameValue,
                        passwordValue, sp).execute(LoginActivity.this);
            }
        });

        // 监听记住密码多选框按钮事件
        /*
		rememberPassword
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        if (rememberPassword.isChecked()) {
                            System.out.println("记住密码已选中");
                            sp.edit().putBoolean("ISCHECK", true).commit();
                        } else {
                            System.out.println("记住密码没有选中");
                            sp.edit().putBoolean("ISCHECK", false).commit();
                        }
                    }
                });

        // 监听自动登录多选框事件
        autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                if (autoLogin.isChecked()) {
                    System.out.println("自动登录已选中");
                    sp.edit().putBoolean("AUTO_ISCHECK", true).commit();
                } else {
                    System.out.println("自动登录没有选中");
                    sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
                }
            }
        });
		*/
        mSwitchLoginServer = (TextView) findViewById(R.id.sign_switch_login_server);
        mSwitchLoginServer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, android.R.style.Theme_Holo_Light_Dialog));
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialog_view = inflater.inflate(R.layout.tt_custom_dialog, null);
                final EditText editText = (EditText)dialog_view.findViewById(R.id.dialog_edit_content);
                // editText.setText(SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVER));
                editText.setText(AppConstants.getBaseUrl());
				TextView textText = (TextView)dialog_view.findViewById(R.id.dialog_title);
                textText.setText(R.string.switch_login_server_title);

				final TextView login1 = (TextView) dialog_view.findViewById(R.id.login_1);
				final TextView login2 = (TextView) dialog_view.findViewById(R.id.login_2);
				final TextView login3 = (TextView) dialog_view.findViewById(R.id.login_3);

				OnClickListener onClickListener = new OnClickListener() {
					public void onClick(View v) {
						TextView t = (TextView) v;
						String value = t.getText().toString().trim();
						AppConstants.setBaseUrl(value);
						editText.setText(value);
					}
				};

				login1.setOnClickListener(onClickListener);
				login2.setOnClickListener(onClickListener);
				login3.setOnClickListener(onClickListener);

                builder.setView(dialog_view);
                builder.setPositiveButton(getString(R.string.tt_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(!TextUtils.isEmpty(editText.getText().toString().trim()))
                        {
                            //SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVER,editText.getText().toString().trim());
							System.out.println(editText.getText().toString().trim());
							AppConstants.setBaseUrl(editText.getText().toString().trim());
                            dialog.dismiss();
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.tt_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
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

    public void onSuccess(String sessionId) {
        sp.edit().putString("SESSIONID", sessionId).commit();
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();

        // 登录成功和记住密码框为选中状态才保存用户信息
        //if (rememberPassword.isChecked()) {
            // 记住用户名、密码、
            Editor editor = sp.edit();
            editor.putString("USERNAME", userNameValue);
            editor.putString("PASSWORD", passwordValue);
            editor.commit();
        //}

        // 跳转界面
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);

        this.finish();
    }

	public void onFailure() {
		// LoginActivity.this.findViewById(R.id.login_page).setVisibility(View.VISIBLE);
		LoginActivity.this.findViewById(R.id.login_status).setVisibility(View.GONE);
	}
}
