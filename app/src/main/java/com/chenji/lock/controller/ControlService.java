package com.chenji.lock.controller;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;

public class ControlService extends Service {

    private SqlOperation sqlOperation;
    private StateReceiver stateReceiver;



    public ControlService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sqlOperation = new SqlOperation();

        stateReceiver = new StateReceiver(sqlOperation);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(stateReceiver, intentFilter);


        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= 20) {
            if (powerManager.isInteractive()) {
                SqlOperation.state_screen = SqlOperation.SCREEN_ON;
            } else {
                SqlOperation.state_screen = SqlOperation.SCREEN_OFF;
            }
        } else {
            if (powerManager.isScreenOn()) {
                SqlOperation.state_screen = SqlOperation.SCREEN_ON;
            } else {
                SqlOperation.state_screen = SqlOperation.SCREEN_OFF;
            }
        }

        SqlOperation.state_count_time = SqlOperation.NOT_COUNT_TIME;

        SharedPreferences mySharedPreferences=MyApplication.getContext().getSharedPreferences(
                MyApplication.FILE_CONTROL, Activity.MODE_PRIVATE);
        SqlOperation.Judge_Interval=mySharedPreferences.getFloat(MyApplication.JUDGE_INTERVAL,0.1F);
        SqlOperation.Intercept_Interval=mySharedPreferences.getInt(MyApplication.INTERCEPT_INTERVAL, 0);
        SqlOperation.Open_Lock=mySharedPreferences.getBoolean(MyApplication.OPEN_LOCK, true);
        SqlOperation.Launcher=mySharedPreferences.getString(MyApplication.LAUNCHER,null);

        sqlOperation.recordTime();
        MyLog.i_chenji_log("ControlService start ok");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent startService = new Intent(MyApplication.getContext(), ControlService.class);
        startService(startService);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
