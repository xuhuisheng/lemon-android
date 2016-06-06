package com.mossle.android.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;

import android.widget.AdapterView.OnItemClickListener;

import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.mossle.android.R;
import com.mossle.android.activity.ProcessInstanceActivity;
import com.mossle.android.activity.ProcessDefinitionActivity;
import com.mossle.android.activity.CmsActivity;
import com.mossle.android.activity.TaskActivity;

public class ToolFragment extends Fragment {
    private Activity ctx;
    private View view;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    protected Toolbar toolbar;

    public ToolFragment() {
        this.addTool("待办任务", R.drawable.ic_launcher, TaskActivity.class);
        this.addTool("我的流程", R.drawable.ic_launcher, ProcessInstanceActivity.class);
        this.addTool("流程定义", R.drawable.ic_launcher, ProcessDefinitionActivity.class);
        this.addTool("通知公告", R.drawable.ic_launcher, CmsActivity.class);
    }

    public void addTool(String name, int image, Class activity) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("image", image);
        map.put("activity", activity);
        list.add(map);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (view == null) {
            ctx = this.getActivity();
            view = ctx.getLayoutInflater()
                    .inflate(R.layout.fragment_tool, null);
        } else {
            ViewGroup parent = (ViewGroup) view.getParent();

            if (parent != null) {
                parent.removeView(view);
            }
        }

        // initialize the GridView
        GridView gridView = (GridView) view.findViewById(R.id.grid_view);

        // 生成动态数组，并且转入数据
        List<Map<String, Object>> lstImageItem = new ArrayList<Map<String, Object>>();

        for (Map<String, Object> m : list) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", m.get("image")); // 添加图像资源的ID
            map.put("ItemText", m.get("name")); // 按序号做ItemText
            map.put("activity", m.get("activity"));
            lstImageItem.add(map);
        }

        // 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
        SimpleAdapter saImageItems = new SimpleAdapter(ctx, // 没什么解释
                lstImageItem, // 数据来源
                R.layout.item_tool, // night_item的XML实现

                // 动态数组与ImageItem对应的子项
                new String[] { "ItemImage", "ItemText" },

                // ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[] { R.id.ItemImage, R.id.ItemText });
        // 添加并且显示
        gridView.setAdapter(saImageItems);
        // 添加消息处理
        gridView.setOnItemClickListener(new ItemClickListener());

        return view;
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
            Class activityClass = (Class) item.get("activity");
            Intent intent = new Intent(getActivity(), activityClass);
            startActivity(intent);
        }
    }
}
