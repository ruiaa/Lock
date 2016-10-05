package com.chenji.lock.controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;

public class StateReceiver extends BroadcastReceiver {
    SqlOperation sqlOperation;

    public StateReceiver() {

    }

    public StateReceiver(SqlOperation sqlOperation) {
        this.sqlOperation = sqlOperation;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //亮屏
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            SqlOperation.state_screen = SqlOperation.SCREEN_ON;
            MyLog.i_chenji_log("StateReceiver  亮屏");

        }

        //暗屏
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            SqlOperation.state_screen = SqlOperation.SCREEN_OFF;
            MyLog.i_chenji_log("StateReceiver  暗屏");
        }

        //日期变化
        if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
            sqlOperation.countTime();
            MyLog.i_chenji_log("StateReceiver  日期变化");
        }

        //开机
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            //开启控制后台
            if (MyApplication.getContext().getSharedPreferences(MyApplication.FILE_CONTROL, Activity.MODE_PRIVATE).
                    getBoolean(MyApplication.SET_BOOT_COMPLETED, true)) {
                Intent startService = new Intent(MyApplication.getContext(), ControlService.class);
                MyApplication.getContext().startService(startService);
            }
        }
    }
}
