package com.chenji.lock.view.count;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;
import com.chenji.lock.Util.Util;
import com.chenji.lock.dataResolver.DataResolver;
import com.chenji.lock.view.ChooseDate;
import com.chenji.lock.view.intercept.choose.ChooseInterceptActivity;
import com.chenji.lock.view.more.MoreActivity;
import com.chenji.lock.view.setting.SettingActivity;
import com.chenji.lock.view.time.MainActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TimeCountActivity extends AppCompatActivity {


    //控制fragment的显示
    //0:oldDate
    int fragCode = 0;
    FragmentManager fragmentManager;

    //控制fragmentOldDate的显示
    HashMap<Integer, Fragment> hadNewFragment;
    int allCount;
    int thisNumber;
    ArrayList<String> dateList;

    //日期选择
    ChooseDate chooseDate;

    //监听手势
    float downX = 0;
    float downY = 0;
    float upX = 0;
    float upY = 0;

    //记录返回键按下时间
    long timeKeyBack = 0;
    long oldTimeKeyBack = 0;

    //open_lock 沙漏
    TextView openLockText;
    AnimationDrawable hourglass=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        /*getWindow().setBackgroundDrawable(Util.getBackground());*/
      /*  SqlInfo.USAGE_TIME=MyApplication.getContext().getSharedPreferences(MyApplication.FILE_SHARED, Activity.MODE_PRIVATE).getString(MyApplication.DATE_TODAY_SQL, "0");
*/

     /*   if (Build.VERSION.SDK_INT >= 21) {
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
        }*/

        fragmentManager = getFragmentManager();

        dateList = MyApplication.dataResolver.getDate();
        if (dateList == null) {
            thisNumber = 0;
            allCount = 1;
        } else {
            allCount = dateList.size();
            thisNumber = allCount - 1;
        }


        MyLog.i_chenji_log(dateList.size() + dateList.toString());

        hadNewFragment = new HashMap<>();

        //打开主界面
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        TimeCountFragmentOldDate timeCountFragmentOldDate = TimeCountFragmentOldDate.newInstance(Util.dateForSqlToday());
        fragmentTransaction.add(R.id.common_fragment, timeCountFragmentOldDate);
        hadNewFragment.put(thisNumber, timeCountFragmentOldDate);
        fragmentTransaction.commit();


        //日期选择
        chooseDate = new ChooseDate(
                this,
                new ChooseDate.OnChoose() {
                    @Override
                    public void onChoose(int chooseNumber) {
                        showAllAppOfAnotherOldDate(chooseNumber);
                    }
                },
                dateList);

        //活动条    //监听侧滑layout 启动和停止动画
        Toolbar toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        toolbar.setTitle(R.string.app_use_count);
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
                    Toast.makeText(TimeCountActivity.this, R.string.toast_key_back, Toast.LENGTH_SHORT).show();
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
            MyLog.i_chenji_log("count exit");
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
        getMenuInflater().inflate(R.menu.count_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.count_date: {
                chooseDate.setShowingNumber(thisNumber);
                chooseDate.show();
                break;
            }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    //监听上下左右拉
    public boolean setMyTouchListener(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //按下
            downX = event.getX();
            downY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //离开
            upX = event.getX();
            upY = event.getY();
            if ((upX - downX) > 320) {
                //右滑 last 后退
                MyLog.i_chenji_log("右滑");
                showAllAppOfAnotherOldDate(thisNumber - 1);
                return true;

            } else if ((downX - upX) > 320) {
                //左滑 next 前进
                MyLog.i_chenji_log("左滑");
                showAllAppOfAnotherOldDate(thisNumber + 1);
                return true;

            } else if ((downY - upY) > 320) {
                //上滑
                MyLog.i_chenji_log("上滑");

            } else if ((upY - downY) > 320) {
                //下滑
                MyLog.i_chenji_log("下滑");

            }
        }
        return false;
    }


    //控制fragment
    public void showAllAppOfAnotherOldDate(int number) {

        if (fragCode>=1){
            turnBack();
        }

        if (number >= 0 && number < allCount) {
            FragmentTransaction fragmentTransaction;
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.hide(hadNewFragment.get(thisNumber));
            if (hadNewFragment.containsKey(number)) {
                fragmentTransaction.show(hadNewFragment.get(number));
                fragmentTransaction.commit();
            } else {
                TimeCountFragmentOldDate timeCountFragmentOldDate = TimeCountFragmentOldDate.newInstance(dateList.get(number));
                fragmentTransaction.add(R.id.common_fragment, timeCountFragmentOldDate);
                hadNewFragment.put(number, timeCountFragmentOldDate);
                fragmentTransaction.commit();
            }
            thisNumber = number;
            fragCode = 0;
        }


        MyLog.i_chenji_log(thisNumber+"");
    }

    public void showTheAppUsage(Fragment fragment,String packageName){
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment);
        fragmentTransaction.add(R.id.common_fragment,TimeCountFragmentUsage.newInstance(packageName));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragCode = 1;
        MyLog.i_chenji_log(packageName);
    }

    public void turnBack() {
        fragmentManager.popBackStack();
        fragCode = fragCode - 1;
    }
}
