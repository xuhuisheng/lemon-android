package com.mossle.android.activity;

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
import com.mossle.android.support.TaskAsyncTask;
import com.astuetz.PagerSlidingTabStrip;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import android.util.Log;
import android.view.ViewGroup;

public class TaskActivity extends AppCompatActivity {
    private SharedPreferences sp;
    protected SystemBarTintManager mTintManager;
    private Toolbar toolbar;

	private ViewPager pager = null;
    private PagerSlidingTabStrip tabStrip = null;
    private List<View> viewContainer = new ArrayList<View>();
    private List<String> titleContainer = new ArrayList<String>();
    public final static String TAG = "tag";

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
        setContentView(R.layout.activity_process_instance);

        // this.refresh();
		this.initToolBar(); 

        pager = (ViewPager) this.findViewById(R.id.viewpager);
        tabStrip = (PagerSlidingTabStrip) this.findViewById(R.id.tabstrip);
        //取消tab下面的长横线
        // tabStrip.setDrawFullUnderline(false);
        //设置tab的背景色
        //tabStrip.setBackgroundColor(this.getResources().getColor(R.color.bg));
        //设置当前tab页签的下划线颜色
        //tabStrip.setTabIndicatorColor(this.getResources().getColor(R.color.red));
        // tabStrip.setTextSpacing(200);
         
        View view1 = LayoutInflater.from(this).inflate(R.layout.view_task_personal, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.view_task_group, null);
		View view3 = LayoutInflater.from(this).inflate(R.layout.view_task_complete, null);
		View view4 = LayoutInflater.from(this).inflate(R.layout.view_task_delegate, null);
		//viewpager开始添加view
        viewContainer.add(view1);
        viewContainer.add(view2);
		viewContainer.add(view3);
		viewContainer.add(view4);
		//页签项
        titleContainer.add("待办");
        titleContainer.add("待领");
        titleContainer.add("已办");
        titleContainer.add("经手");

        pager.setAdapter(new PagerAdapter() {
 
            //viewpager中的组件数量
            @Override
            public int getCount() {
                return viewContainer.size();
            }
          //滑动切换的时候销毁当前的组件
            @Override
            public void destroyItem(ViewGroup container, int position,
                    Object object) {
                ((ViewPager) container).removeView(viewContainer.get(position));
            }
          //每次滑动的时候生成的组件
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
				View view = viewContainer.get(position);
                ((ViewPager) container).addView(view);
				if (position == 0) {
					TaskActivity.this.initTask(view, "tasksPersonal");
				} else if (position == 1) {
					TaskActivity.this.initTask(view, "tasksGroup");
				} else if (position == 2) {
					TaskActivity.this.initTask(view, "tasksComplete");
				} else if (position == 3) {
					TaskActivity.this.initTask(view, "tasksDelegate");
				}
                return view;
            }
 
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }
 
            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }
 
            @Override
            public CharSequence getPageTitle(int position) {
                return titleContainer.get(position);
            }
        });
 
        pager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
                Log.d(TAG, "--------changed:" + arg0);
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                Log.d(TAG, "-------scrolled arg0:" + arg0);
                Log.d(TAG, "-------scrolled arg1:" + arg1);
                Log.d(TAG, "-------scrolled arg2:" + arg2);
            }
 
            @Override
            public void onPageSelected(int arg0) {
                Log.d(TAG, "------selected:" + arg0);
            }
        });
		tabStrip.setViewPager(pager);
    }

    public void initTask(View view, String urlSuffix) {
		// 绑定XML中的ListView，作为Item的容器
        ListView listView = (ListView) view.findViewById(R.id.activity_task);
        String sessionId = sp.getString("SESSIONID", null);
        new TaskAsyncTask(AppConstants.getBaseUrl()
                + "/rs/android/task/" + urlSuffix, this, sessionId)
                .execute(listView);
        // 添加消息处理
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
			toolbar.setTitle("任务");
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
        // this.refresh();
    }

    //private void refresh() {
    //    this.initProcessDefinition();
    //    this.initProcessInstance();
    //}

    class ItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            Map<String, Object> item = (Map<String, Object>) arg0
                    .getItemAtPosition(arg2);

            String taskId = (String) item.get("taskId");
            Intent intent = new Intent(TaskActivity.this, FormActivity.class);
            intent.putExtra("taskId", taskId);
            intent.putExtra("formType", "completeTask");
            TaskActivity.this.startActivity(intent);
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
