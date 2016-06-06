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
import com.mossle.android.support.ProcessDefinitionAsyncTask;
import com.mossle.android.support.ProcessInstanceAsyncTask;
import com.mossle.android.support.DraftAsyncTask;
import com.astuetz.PagerSlidingTabStrip;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import android.util.Log;
import android.view.ViewGroup;
// import com.mossle.android.widget.PagerSlidingTabStrip;

public class ProcessInstanceActivity extends AppCompatActivity {
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
         
        View view1 = LayoutInflater.from(this).inflate(R.layout.view_process_instance_running, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.view_process_instance_complete, null);
		View view3 = LayoutInflater.from(this).inflate(R.layout.view_process_instance_draft, null);
		//viewpager开始添加view
        viewContainer.add(view1);
        viewContainer.add(view2);
		viewContainer.add(view3);
		//页签项
        titleContainer.add("未结流程");
        titleContainer.add("已结流程");
        titleContainer.add("草稿箱");

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
					ProcessInstanceActivity.this.initProcessInstance(view, "processInstancesRunning");
				} else if (position == 1) {
					ProcessInstanceActivity.this.initProcessInstance(view, "processInstancesComplete");
				} else if (position == 2) {
					ProcessInstanceActivity.this.initDraft(view, "processInstancesDraft");
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

    public void initProcessDefinition(View view) {
		// 绑定XML中的ListView，作为Item的容器
        ListView listView = (ListView) view.findViewById(R.id.activity_bpm_def);
        String sessionId = sp.getString("SESSIONID", null);
        new ProcessDefinitionAsyncTask(AppConstants.getBaseUrl()
                + "/rs/android/bpm/processDefinitions", this, sessionId)
                .execute(listView);
        // 添加消息处理
        listView.setOnItemClickListener(new ItemClickListener());
    }

    public void initProcessInstance(View view, String urlSuffix) {
        // 绑定XML中的ListView，作为Item的容器
        ListView listView = (ListView) view
                .findViewById(R.id.activity_bpm_inst);
        String sessionId = sp.getString("SESSIONID", null);
        new ProcessInstanceAsyncTask(AppConstants.getBaseUrl()
                + "/rs/android/bpm/" + urlSuffix, this, sessionId)
                .execute(listView);
    }

	public void initDraft(View view, String urlSuffix) {
        ListView listView = (ListView) view
                .findViewById(R.id.activity_bpm_inst);
        String sessionId = sp.getString("SESSIONID", null);
        new DraftAsyncTask(AppConstants.getBaseUrl()
                + "/rs/android/bpm/" + urlSuffix, this, sessionId)
                .execute(listView);
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
			toolbar.setTitle("流程");
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
            String processDefinitionId = (String) item
                    .get("processDefinitionId");
            Intent intent = new Intent(ProcessInstanceActivity.this, FormActivity.class);
            intent.putExtra("processDefinitionId", processDefinitionId);
            intent.putExtra("formType", "startProcess");
            ProcessInstanceActivity.this.startActivity(intent);
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
