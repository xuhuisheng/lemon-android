package com.mossle.android.activity;

import java.io.File;

import java.lang.reflect.Field;

import android.annotation.TargetApi;

import android.app.Activity;
import android.app.AlertDialog;
import com.mossle.android.fragment.FragmentHelper;
import android.app.AlertDialog.Builder;

import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.content.res.Configuration;

import android.net.Uri;

import android.os.Build;
import android.os.Bundle;

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
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

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

import com.readystatesoftware.systembartint.SystemBarTintManager;

public class MainActivity extends AppCompatActivity {
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private Toolbar toolbar;
    protected MaterialMenuIconToolbar materialMenu;
    protected SystemBarTintManager mTintManager;
	private FragmentHelper fragmentHelper = new FragmentHelper();

    /**
     * FragmentTabhost
     */
    private FragmentTabHost mTabHost;

    /**
     *
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.activity_main);

        this.initView();
        // this.initDrawer();
        this.initToolBar();

        new UpdateInfoAsyncTask(this).execute(this);
    }

    protected void initToolBar() {
        this.toolbar = (Toolbar) this.findViewById(R.id.toolbar);

        if (toolbar != null) {
			// App Logo
			// toolbar.setLogo(R.drawable.ic_launcher);
			// Title
			toolbar.setTitle("LemonOA");
            this.setSupportActionBar(this.toolbar);
/*
            this.materialMenu = new MaterialMenuIconToolbar(this, -1,
                    Stroke.REGULAR) {
                public int getToolbarViewId() {
                    return R.id.toolbar;
                }
            };
            this.materialMenu.setState(IconState.BURGER);
			
            this.toolbar.setNavigationOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(MainActivity.this
                            .findViewById(R.id.left_drawer));
                }
            });
			*/
        }
    }

    /**
     * 初始化组件
     */
    private void initView() {

        // 找到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		this.fragmentHelper.init(mTabHost, this);
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // init the ListView and Adapter, nothing new
        // initListView();

        // set a custom shadow that overlays the main content when the drawer
        // opens
        // mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
        // GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                new Toolbar(this), R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to
                                         // onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to
                                         // onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        // this.getSupportActionBar().setDisplayUseLogoEnabled(true);
        // this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // this.getSupportActionBar().setHomeButtonEnabled(true);
        // this.getSupportActionBar().setLogo(R.drawable.ic_launcher);
        // Note: getActionBar() Added in API level 11
        setDrawerLeftEdgeSize(this, mDrawerLayout, 0.3F);
    }

    private void initListView() {
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // Highlight the selected item, update the title, and close the
                // drawer
                mDrawerList.setItemChecked(position, true);
                setTitle(mPlanetTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
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
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
		*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

    public static void setDrawerLeftEdgeSize(Activity activity,
            DrawerLayout drawerLayout, float displayWidthPercentage) {
        if ((activity == null) || (drawerLayout == null)) {
            return;
        }

        try {
            // find ViewDragHelper and set it accessible
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField(
                    "mLeftDragger");
            leftDraggerField.setAccessible(true);

            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField
                    .get(drawerLayout);

            // find edgesize and set is accessible
            Field edgeSizeField = leftDragger.getClass().getDeclaredField(
                    "mEdgeSize");
            edgeSizeField.setAccessible(true);

            int edgeSize = edgeSizeField.getInt(leftDragger);

            // set new edgesize
            // Point displaySize = new Point();
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize,
                    (int) (dm.widthPixels * displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
            // ignore
        } catch (IllegalArgumentException e) {
            // ignore
        } catch (IllegalAccessException e) {
            // ignore
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

    public void showUpdataDialog(final UpdateInfo info) {
        AlertDialog.Builder builder = new Builder(this);

        builder.setTitle("版本升级");

        builder.setMessage(info.getDescription());

        // 当点确定按钮时从服务器上下载 新的apk 然后安装 װ
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Log.i(TAG, "下载apk,更新");
                downLoadApk(info);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                // do sth
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    /*
     * 
     * 从服务器中下载APK
     */
    protected void downLoadApk(final UpdateInfo info) {
        final ProgressDialog pd; // 进度条对话框

        pd = new ProgressDialog(this);

        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        pd.setMessage("正在下载更新");

        pd.show();

        new Thread() {
            @Override
            public void run() {
                try {
                    File file = DownloadManager.getFileFromServer(
                            info.getUrl(), pd);

                    sleep(3000);

                    installApk(file);

                    pd.dismiss(); // 结束掉进度条对话框
                } catch (Exception e) {
                    // Message msg = new Message();

                    // msg.what = DOWN_ERROR;

                    // handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // 安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();

        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);

        // 执行的数据类型
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");

        startActivity(intent);
    }
}
