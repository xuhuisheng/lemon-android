
package com.mossle.android.fragment;

import com.mossle.android.R;
import java.io.File;

import java.lang.reflect.Field;

import android.annotation.TargetApi;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.AlertDialog.Builder;

import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.content.res.Configuration;

import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.DisplayMetrics;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.View.OnClickListener;

import android.view.Window;
import android.view.WindowManager;

import android.widget.AdapterView;

import android.widget.AdapterView.OnItemClickListener;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TabHost.TabSpec;

import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable.IconState;
import com.balysv.materialmenu.MaterialMenuDrawable.Stroke;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;

import com.mossle.android.R;
import com.mossle.android.fragment.ContractFragment;
import com.mossle.android.fragment.ImFragment;
import com.mossle.android.fragment.ProfileFragment;
import com.mossle.android.fragment.ToolFragment;
import com.mossle.android.support.DownloadManager;
import com.mossle.android.support.UpdateInfo;
import com.mossle.android.support.UpdateInfoAsyncTask;
import android.widget.TabHost.OnTabChangeListener;
import android.graphics.Color;

public class FragmentHelper implements OnTabChangeListener {
	private Activity activity;

    private Class<?>[] classArray = {
		ImFragment.class,
		ContractFragment.class,
		ToolFragment.class,
		ProfileFragment.class
	};

	private String[] labelArray = {
		"消息", "通讯录", "应用", "设置"
	};

    private int[] imageUnselectedArray = {
		R.drawable.tt_tab_chat_nor,
		R.drawable.tt_tab_contact_nor,
		R.drawable.tt_tab_internal_nor,
		R.drawable.tt_tab_me_nor
	};

	private int[] imageSelectedArray = {
		R.drawable.tt_tab_chat_sel,
		R.drawable.tt_tab_contact_sel,
		R.drawable.tt_tab_internal_sel,
		R.drawable.tt_tab_me_sel
	};

	private View[] viewArray = new View[4];

	public void init(FragmentTabHost fragmentTabHost, Activity activity) {
		this.activity = activity;
        // 得到fragment的个数
        int count = classArray.length;

        for (int i = 0; i < count; i++) {
            // 给每个Tab按钮设置图标、文字和内容
            TabSpec tabSpec = fragmentTabHost.newTabSpec(labelArray[i]).setIndicator(
                    getTabItemView(i, activity));
            // 将Tab按钮添加进Tab选项卡中
            fragmentTabHost.addTab(tabSpec, classArray[i], null);

            // 设置Tab按钮的背景
            //mTabHost.getTabWidget().getChildAt(i)
            // .setBackgroundResource(R.drawable.tt_tab_bk);
        }
		this.selectTab(0);
		fragmentTabHost.setOnTabChangedListener(this);
	}

    private View getTabItemView(int index, Activity activity) {
		LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view
                .findViewById(R.id.activiti_main_tab_image);
        imageView.setImageResource(imageUnselectedArray[index]);

        TextView textView = (TextView) view
                .findViewById(R.id.activiti_main_tab_text);
        textView.setText(labelArray[index]);

		this.viewArray[index] = view;

        return view;
    }

	@Override
	public void onTabChanged(String tabId) {
		//System.out.println(tabId);
		//Toast.makeText(this.activity, "tabId : " + tabId,
        //            Toast.LENGTH_LONG).show();
		clearSelection();
		int index = -1;
		for (int i = 0; i < this.labelArray.length; i++) {
			if (tabId.equals(this.labelArray[i])) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			return;
		}

		this.selectTab(index);
 
	}

	public void selectTab(int index) {
		View view = viewArray[index];
		ImageView imageView = (ImageView) view
				.findViewById(R.id.activiti_main_tab_image);
		imageView.setImageResource(imageSelectedArray[index]);
		TextView textView = (TextView) view
				.findViewById(R.id.activiti_main_tab_text);
		textView.setTextColor(Color.parseColor("#33CCFF")); 
	}
	
	/** 
     * 清除掉所有的选中状态。 
     */  
    private void clearSelection() {
		for (int i = 0; i < viewArray.length; i++) {
			View view = this.viewArray[i];
			ImageView imageView = (ImageView) view
					.findViewById(R.id.activiti_main_tab_image);
			imageView.setImageResource(imageUnselectedArray[i]);
			TextView textView = (TextView) view
					.findViewById(R.id.activiti_main_tab_text);
			textView.setTextColor(Color.parseColor("#82858b"));  
		}  
    } 
}
