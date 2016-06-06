package com.mossle.android.fragment;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.SharedPreferences.Editor;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;

import android.view.View.OnClickListener;

import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Toast;

import com.mossle.android.AppConstants;
import com.mossle.android.R;
import com.mossle.android.activity.LoginActivity;
import com.mossle.android.support.ProfileAsyncTask;

public class ProfileFragment extends Fragment {
    private SharedPreferences sp;
    private Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        sp = this.getActivity().getSharedPreferences("userInfo",
                Context.MODE_WORLD_READABLE);

        View view = inflater.inflate(R.layout.fragment_profile, null);
        logoutButton = (Button) view.findViewById(R.id.fragment_profile_logout);
        this.onLogout();
        this.initProfile(view);

        return view;
    }

    public void initProfile(View view) {
        String sessionId = sp.getString("SESSIONID", null);
        new ProfileAsyncTask(
                AppConstants.getBaseUrl() + "/rs/android/user/profile",
                this.getActivity(), sessionId).execute(view);
    }

    public void onLogout() {
        logoutButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
				String result = "";
                try {
                    URL url = new URL(AppConstants.getBaseUrl()
                            + "/rs/android/logout");
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("POST");

                    String sessionId = ProfileFragment.this.sp.getString(
                            "SESSIONID", null);
                    conn.addRequestProperty("sessionId", sessionId);
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    result = readText(is);

                    Toast.makeText(ProfileFragment.this.getActivity(), "注销成功",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(ProfileFragment.this.getActivity(),
                            ex.getMessage(), Toast.LENGTH_SHORT).show();
					Toast.makeText(ProfileFragment.this.getActivity(),
						result, Toast.LENGTH_LONG).show();
                }

                try {
                    Editor editor = sp.edit();
                    editor.putString("SESSIONID", "");
                    editor.putBoolean("AUTO_ISCHECK", false);
                    editor.commit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Intent intent = new Intent(ProfileFragment.this.getActivity(),
                        LoginActivity.class);
                ProfileFragment.this.getActivity().startActivity(intent);
                ProfileFragment.this.getActivity().finish();
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
}
