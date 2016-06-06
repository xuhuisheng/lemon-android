package com.mossle.android.fragment;

import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;

import com.mossle.android.AppConstants;
import com.mossle.android.R;
import com.mossle.android.support.MsgAsyncTask;
import com.mossle.android.activity.MsgActivity;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import android.annotation.TargetApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import android.app.Activity;
import android.view.LayoutInflater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.AdapterView;

import android.widget.AdapterView.OnItemClickListener;

import android.widget.ListView;

import com.mossle.android.AppConstants;
import com.mossle.android.R;
import com.mossle.android.support.ProcessDefinitionAsyncTask;
import com.mossle.android.support.ProcessInstanceAsyncTask;
import com.mossle.android.support.DraftAsyncTask;
import com.astuetz.PagerSlidingTabStrip;
import com.mossle.android.activity.MainActivity;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import android.util.Log;
import android.view.ViewGroup;
// import com.mossle.android.widget.PagerSlidingTabStrip;

public class ImFragment extends Fragment {
    private SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        sp = this.getActivity().getSharedPreferences("userInfo",
                Context.MODE_WORLD_READABLE);

        View view = inflater.inflate(R.layout.fragment_im, null);

        // 绑定XML中的ListView，作为Item的容器
        ListView listView = (ListView) view.findViewById(R.id.MyListView);
        this.initMsg(listView);
        // 添加消息处理
        listView.setOnItemClickListener(new ItemClickListener());

        return view;
    }

    public void initMsg(ListView listView) {
        String sessionId = sp.getString("SESSIONID", null);
        new MsgAsyncTask(AppConstants.getBaseUrl() + "/rs/android/msg/msg",
                this.getActivity(), sessionId).execute(listView);
    }

    // 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
    class ItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, // The AdapterView where
                                                     // the click happened
                View arg1, // The view within the AdapterView that was clicked
                int arg2, // The position of the view in the adapter
                long arg3 // The row id of the item that was clicked
        ) {
            // 在本例中arg2=arg3
            Map<String, Object> item = (Map<String, Object>) arg0
                    .getItemAtPosition(arg2);

            // 显示所选Item的ItemText
            // setTitle((String) item.get("ItemText"));
            String msgId = (String) item
                    .get("msgId");
            Intent intent = new Intent(ImFragment.this.getContext(), MsgActivity.class);
            intent.putExtra("msgId", msgId);
            intent.putExtra("formType", "startProcess");
            ImFragment.this.startActivity(intent);
        }
    }
}
