package com.chenji.lock.view.more;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.chenji.lock.view.count.TimeCountActivity;
import com.chenji.lock.view.intercept.choose.ChooseInterceptActivity;
import com.chenji.lock.view.setting.SettingActivity;
import com.chenji.lock.view.time.MainActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.Date;

public class MoreActivity extends AppCompatActivity {


    //控制fragment的显示
    //0:list  1:detail
    int fragCode = 0;
    FragmentManager fragmentManager;
    MoreFragmentFeedback moreFragmentFeedback;

    //记录返回键按下时间
    long timeKeyBack = 0;
    long oldTimeKeyBack = 0;

    //detailFragment 编号
    public static final int USE_HELP = 1;
    public static final int FEEDBACK = 2;
    public static final int ABOUT = 3;

    //open_lock 沙漏
    TextView openLockText;
    AnimationDrawable hourglass=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        /*getWindow().setBackgroundDrawable(Util.getBackground());*/
        fragmentManager = getFragmentManager();

        //打开主界面
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragCode = 0;
        fragmentTransaction.add(R.id.common_fragment, new MoreFragmentList());
        fragmentTransaction.commit();


        //监听活动条        //监听侧滑layout 启动和停止动画
        Toolbar toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        toolbar.setTitle(R.string.more);
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
                    Toast.makeText(MoreActivity.this, R.string.toast_key_back, Toast.LENGTH_SHORT).show();
                }
            } else {
                turnBack();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.dataResolver.setOpenLock(DataResolver.Open_Lock);
        if (DataResolver.Exit_Lock && Util.noRunningOnStop(this)) {
            MyLog.i_chenji_log("more exit");
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



/*    //监听活动条
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.more_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.main_share: {
                break;
            }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }*/

    //控制fragment
    public void showDetailFrag(Fragment listFragment, int detailFragment) {
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(listFragment);

        switch (detailFragment) {
            case ABOUT: {
                fragmentTransaction.add(R.id.common_fragment, new MoreFragmentAbout());
                break;
            }
            case FEEDBACK: {
                fragmentTransaction.add(R.id.common_fragment, new MoreFragmentFeedback());
                break;
            }
            case USE_HELP: {
                fragmentTransaction.add(R.id.common_fragment, new MoreFragmentUseHelp());
                break;
            }
            default:
                break;
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragCode = 1;
    }

    public void turnBack() {
        fragmentManager.popBackStack();
        fragCode = fragCode - 1;
    }

}
