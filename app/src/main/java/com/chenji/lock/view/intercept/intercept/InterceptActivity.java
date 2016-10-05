package com.chenji.lock.view.intercept.intercept;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chenji.lock.R;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.SqlInfo;
import com.chenji.lock.view.EnterActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by 志瑞 on 2016/1/26.
 */
public class InterceptActivity extends AppCompatActivity {

    String packageName;
    String lockOfUsePackage;
    int lockType;
    String order;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercept);

        //数据
        packageName=getIntent().getStringExtra(SqlInfo.PACKAGE);
        lockType=getIntent().getIntExtra(SqlInfo.LOCK_TYPE, 0);
        if (lockType==SqlInfo.LOCK_TYPE_USE){
            lockOfUsePackage=getIntent().getStringExtra(SqlInfo.PACKAGE+SqlInfo.LOCK_TYPE_USE);
        }else {
            lockOfUsePackage="";
        }
        order=Util.getInterceptBackgroundOrder();

        FragmentManager  fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (order.equals("1")){
            fragmentTransaction.add(R.id.intercept_fragment, InterceptFragmentStop.newInstance(packageName,lockType,lockOfUsePackage));
        }else if (order.equals("2")){
            if (lockType==SqlInfo.LOCK_TYPE_USE){
                Intent intent;
                PackageManager packageManager = this.getPackageManager();
                intent = packageManager.getLaunchIntentForPackage(lockOfUsePackage);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
                this.startActivity(intent);
                finish();
            }else {
                Util.backHome(this);
                finish();
            }
        }else {
            fragmentTransaction.add(R.id.intercept_fragment, InterceptFragmentNormal.newInstance(packageName,lockType,lockOfUsePackage));
            setBackground();
        }
        fragmentTransaction.commit();

    }

    public void setBackground(){
        getWindow().setBackgroundDrawable(Util.getInterceptBackground());
    }

    public void startEntry(){
        Intent intent = new Intent(this, EnterActivity.class);
        startActivity(intent);
        finish();
    }

    public void exit(){
        if (lockType==SqlInfo.LOCK_TYPE_USE){
            Intent intent;
            PackageManager packageManager = this.getPackageManager();
            intent = packageManager.getLaunchIntentForPackage(lockOfUsePackage);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
            this.startActivity(intent);
            finish();
        }else {
            Util.backHome(this);
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        if (lockType==SqlInfo.LOCK_TYPE_USE){
            Intent intent;
            PackageManager packageManager = this.getPackageManager();
            intent = packageManager.getLaunchIntentForPackage(lockOfUsePackage);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
            this.startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (lockType==SqlInfo.LOCK_TYPE_USE){
            Intent intent;
            PackageManager packageManager = this.getPackageManager();
            intent = packageManager.getLaunchIntentForPackage(lockOfUsePackage);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
            this.startActivity(intent);
            finish();
        }else {
            Util.backHome(this);
            finish();
        }
    }
}

