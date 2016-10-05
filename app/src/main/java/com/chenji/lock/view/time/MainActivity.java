package com.chenji.lock.view.time;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;
import com.chenji.lock.Util.Util;
import com.chenji.lock.dataResolver.DataResolver;
import com.chenji.lock.model.SqlInfo;
import com.chenji.lock.view.LockListDialog;
import com.chenji.lock.view.count.TimeCountActivity;
import com.chenji.lock.view.intercept.choose.ChooseInterceptActivity;
import com.chenji.lock.view.more.MoreActivity;
import com.chenji.lock.view.setting.SettingActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.Date;

public class MainActivity extends AppCompatActivity {




    //记录返回键按下时间
    long timeKeyBack = 0;
    long oldTimeKeyBack = 0;

    //控制fragment的显示
    //0:allApp  1:lock  2:addLock
    int fragCode = 0;
    FragmentManager fragmentManager;

    Fragment mainFragmentAllApp;

    //open_lock 沙漏
    TextView openLockText;
    AnimationDrawable hourglass=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        /*getWindow().setBackgroundDrawable(Util.getBackground());*/

        if (Build.VERSION.SDK_INT>=21) {
            if (!Util.havePermission(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.no_permission);
                builder.setPositiveButton(R.string.get_permission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);
                    }
                });
                builder.create();
                builder.show();
            }
        }
        fragmentManager = getFragmentManager();

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction;
            fragmentTransaction = fragmentManager.beginTransaction();
            mainFragmentAllApp = new MainFragmentAllApp();
            fragCode = 0;
            fragmentTransaction.add(R.id.common_fragment, mainFragmentAllApp);
            fragmentTransaction.commit();
        }


        //活动条     //监听侧滑layout 启动和停止动画
        Toolbar toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        toolbar.setTitle(R.string.all_app_info);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.common_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (hourglass==null) {
                    openLockText=(TextView) findViewById(R.id.nav_imageView_open_lock);
                    if (DataResolver.Open_Lock){
                        openLockText.setText(R.string.open_lock_true);
                        openLockText.setTextColor(Util.getColor(R.color.open_lock_false_text));
                    }else {
                        openLockText.setText(R.string.open_lock_false);
                        openLockText.setTextColor(Util.getColor(R.color.open_lock_true_text));
                    }

                    ImageView imageView = (ImageView) findViewById(R.id.nav_imageView_hourglass);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (DataResolver.Open_Lock){
                                DataResolver.Open_Lock=false;
                                hourglass.stop();
                                openLockText.setText(R.string.open_lock_false);
                                openLockText.setTextColor(Util.getColor(R.color.open_lock_true_text));
                            }else {
                                DataResolver.Open_Lock=true;
                                hourglass.start();
                                openLockText.setText(R.string.open_lock_true);
                                openLockText.setTextColor(Util.getColor(R.color.open_lock_false_text));
                            }
                        }
                    });
                    hourglass = (AnimationDrawable) imageView.getBackground();
                }
                if (MyApplication.dataResolver.Open_Lock&&(!hourglass.isRunning())){
                    hourglass.start();
                }

            }


            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (hourglass.isRunning()) {
                    hourglass.stop();
                }
            }

        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        //监听左侧滑菜单
        NavigationView navigationView = (NavigationView) findViewById(R.id.common_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {

                int id = item.getItemId();

                Intent intent;

                if (id == R.id.all_app_info) {
                    intent = new Intent(MyApplication.getContext(), MainActivity.class);
                } else if (id == R.id.app_use_count) {
                    intent = new Intent(MyApplication.getContext(), TimeCountActivity.class);
                } else if (id == R.id.choose_intercept) {
                    intent = new Intent(MyApplication.getContext(), ChooseInterceptActivity.class);
                } else if (id == R.id.setting) {
                    intent = new Intent(MyApplication.getContext(), SettingActivity.class);
                } else if (id == R.id.more) {
                    intent = new Intent(MyApplication.getContext(), MoreActivity.class);
                } else {
                    intent = null;
                }
                startActivity(intent);

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.common_drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                finish();
                return true;
            }
        });



        //友盟更新
        if (getIntent().getBooleanExtra("first",false)){
            UmengUpdateAgent.setUpdateOnlyWifi(true);  //在Wi-Fi接入情况下才进行自动提醒
            if (DataResolver.Update_Code==1){
                UmengUpdateAgent.silentUpdate(this);
            }else {
                UmengUpdateAgent.update(this);
            }
            MyLog.i_chenji_log("entry");
        }

    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.common_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragCode == 0) {
                oldTimeKeyBack = timeKeyBack;
                Date date = new Date();
                timeKeyBack = (date.getTime()) / 1000;
                if (timeKeyBack - oldTimeKeyBack < 5) {
                   Util.backHome(this);
                } else {
                    Toast.makeText(MainActivity.this, R.string.toast_key_back, Toast.LENGTH_SHORT).show();
                }
            } else {
                turnBack();
            }
        }
/*    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MyLog.i_chenji_log("onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fragCode==0){
                oldTimeKeyBack = timeKeyBack;
                Date date = new Date();
                timeKeyBack = (date.getTime()) / 1000;
                if (timeKeyBack - oldTimeKeyBack < 5) {
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, R.string.toast_key_back, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }else {
                turnBack();
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }*/
    }

    @Override
    protected void onStop() {
        MyLog.i_chenji_log("main on stop");
        super.onStop();
        MyApplication.dataResolver.setOpenLock(DataResolver.Open_Lock);
        if (DataResolver.Exit_Lock&&Util.noRunningOnStop(this)){
            MyLog.i_chenji_log(" main exit");
            System.exit(0);
        }
    }
    public void onResume() {

        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    //监听活动条
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.main_lock_use: {
                LockListDialog lockListDialog = new LockListDialog(
                        this,
                        MyApplication.dataResolver.getIconWithUseCurrentLock(),
                        new LockListDialog.OnDialogClickListener() {
                            @Override
                            public void onClick(String packageName) {
                                showLockFromDialog(packageName);
                            }
                        },
                        SqlInfo.LOCK_TYPE_USE
                );
                lockListDialog.show();
                break;
            }
            case R.id.main_lock_forbidden: {
                LockListDialog lockListDialog = new LockListDialog(
                        this,
                        MyApplication.dataResolver.getIconWithForbiddenCurrentLock(),
                        new LockListDialog.OnDialogClickListener() {
                            @Override
                            public void onClick(String packageName) {
                                showLockFromDialog(packageName);
                            }
                        },
                        SqlInfo.LOCK_TYPE_FORBIDDEN
                );
                lockListDialog.show();
                break;
            }
            /*case R.id.main_share: {
                break;
            }*/
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    //控制fragment
    public void showLockFromDialog(String packageName) {
        switch (fragCode) {
            case 0: {
                break;
            }
            case 1: {
                fragmentManager.popBackStack();
                break;
            }
            case 2: {
                fragmentManager.popBackStack();
                fragmentManager.popBackStack();
                break;
            }
            default:
                break;
        }
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.detach(mainFragmentAllApp);
        fragmentTransaction.add(R.id.common_fragment, MainFragmentLock.newInstance(packageName));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragCode = 1;
    }

    public void showLockFromAllApp(String packageName, Fragment mainFragmentAllApp) {

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.detach(mainFragmentAllApp);
        fragmentTransaction.add(R.id.common_fragment, MainFragmentLock.newInstance(packageName));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragCode = 1;
    }

    public void addLock(String packageName, Fragment mainFragmentLock) {
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.detach(mainFragmentLock);
        fragmentTransaction.add(R.id.common_fragment, MainFragmentLockAdd.newInstance(packageName));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragCode = 2;
    }

    public void updateOverTime(String packageName, Fragment mainFragmentLock) {
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.detach(mainFragmentLock);
        fragmentTransaction.add(R.id.common_fragment, MainFragmentTimeOver.newInstance(packageName));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragCode = 2;
    }

    public void turnBack() {
        fragmentManager.popBackStack();
        fragCode = fragCode - 1;
    }

    public void reShow(Fragment fragment) {
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.detach(fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }

}
