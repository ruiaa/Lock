package com.chenji.lock.view.intercept.choose;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.chenji.lock.view.more.MoreActivity;
import com.chenji.lock.view.setting.SettingActivity;
import com.chenji.lock.view.time.MainActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Date;

public class ChooseInterceptActivity extends AppCompatActivity {


    //控制fragment的显示
    int fragCode = 0;
    FragmentManager fragmentManager;

    //记录返回键按下时间
    long timeKeyBack = 0;
    long oldTimeKeyBack = 0;

    //detailFragment 编号
    public static final int BACKGROUND = 1;
    public static final int LAUNCHER = 2;

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
        fragmentTransaction.add(R.id.common_fragment, new ChooseInterceptFragmentList());
        fragmentTransaction.commit();


        //监听活动条   //监听侧滑layout 启动和停止动画
        Toolbar toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        toolbar.setTitle(R.string.choose_intercept);
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
                    Toast.makeText(ChooseInterceptActivity.this, R.string.toast_key_back, Toast.LENGTH_SHORT).show();
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
            if (!State_Adding) {
                MyLog.i_chenji_log("choose exit");
                System.exit(0);
            }
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


    //控制fragment
    public void showDetailFrag(Fragment listFragment, int detailFragment) {
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(listFragment);

        switch (detailFragment) {
            case BACKGROUND: {
                fragmentTransaction.add(R.id.common_fragment, new ChooseInterceptFragmentBackground());
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


    //用于添加图片
    private String timeOrder;
    private File newImageFile;
    public static boolean State_Add_Image;
    private boolean State_Adding = false;

    static {
        State_Add_Image = false;
    }

    public static final int CHOOSE_IMAGE = 134;
    public static final int CROP_IMAGE = 135;

    public void addImage(String timeOrder) {
        this.timeOrder = timeOrder;
        int w = getResources().getDisplayMetrics().widthPixels;
        int h = getResources().getDisplayMetrics().heightPixels;
        try {
            newImageFile = File.createTempFile(timeOrder, ".JPEG", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");

            intent.putExtra("output", Uri.fromFile(newImageFile));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", w);
            intent.putExtra("aspectY", h);
            intent.putExtra("outputX", w / 2);
            intent.putExtra("outputY", h / 2);
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("return-data", false);
            intent.putExtra("noFaceDetection", true);

            State_Adding = true;
            startActivityForResult(intent, CHOOSE_IMAGE);
        } catch (Exception e) {
            MyLog.e_chenji_log("not create file", e);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        State_Adding = false;
        try {
            if (requestCode == CHOOSE_IMAGE) {
                if (resultCode == RESULT_OK && data != null && newImageFile.length() > 1000) {
                    boolean b = Util.addImage(newImageFile.getAbsolutePath(), timeOrder);
                    if (b) {
                        State_Add_Image = true;
                        SharedPreferences sharedPreferences = getSharedPreferences(MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(MyApplication.INTERCEPT_BACKGROUND_CHOSE, timeOrder);
                        editor.apply();
                    }
                    newImageFile.delete();
                    MyLog.i_chenji_log("crop ok");
                } else {
                    Util.deleteImage(timeOrder);
                    MyLog.i_chenji_log("crop no");
                }
            }

        } catch (Exception e) {
            MyLog.e_chenji_log("addImage onActivityResult 失败", e);
        }

    /*else if (requestCode == CROP_IMAGE) {
                if (resultCode == RESULT_OK && data != null) {
                    *//*boolean b=Util.addImage((Bitmap) data.getExtras().getParcelable("data"), timeOrder);*//*
                    State_Add_Image = true;
                    SharedPreferences sharedPreferences = getSharedPreferences(MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(MyApplication.SET_BACKGROUND_CHOSE, timeOrder);
                    editor.commit();
                    MyLog.i_chenji_log("crop ok");
                }else {
                    Util.deleteImage(timeOrder);
                    MyLog.i_chenji_log("crop no");
                }
            }*/

 /*Uri selectedImage = data.getData();*/
                    /*String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                    cursor.close();*/
                    /*int w = getResources().getDisplayMetrics().widthPixels;
                    int h = getResources().getDisplayMetrics().heightPixels;
                    File file = new File(SettingActivity.this.getFilesDir(), timeOrder);
                    file.createNewFile();

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(data.getData(), "image*//*");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", w);
                    intent.putExtra("aspectY", h);
                    intent.putExtra("outputX", w);
                    intent.putExtra("outputY", h);
                    intent.putExtra("scale", false);
                    intent.putExtra("scaleUpIfNeeded", true);
                    intent.putExtra("return-data", false);
                    intent.putExtra("noFaceDetection", true);
                    startActivityForResult(intent, CROP_IMAGE);*/
    }


}
