package com.chenji.lock.controller;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.CreateSql;
import com.chenji.lock.model.SqlInfo;
import com.chenji.lock.view.intercept.intercept.InterceptActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 志瑞 on 2015/12/22.
 */
public class SqlOperation {

    private static CreateSql createSql;
    private static SQLiteDatabase sqLiteDatabase;

    private static List<String> currentUseLockList;
    private static List<String> currentForbiddenLockList;
    private static List<String> currentTimeOver;

    //本地储存
    public static float Judge_Interval = 0.1F;                 //判断间隔 0.1 ~~ 1
    public static int Intercept_Interval = 0;          //拦截间隔  0,,60~~300
    public static String Launcher;                      //桌面app名
    public static boolean Open_Lock;                   //打开时间锁

    //运行时状态
    public static int state_screen = 1;              //屏幕亮，屏幕暗
    public static final int SCREEN_ON = 1;
    public static final int SCREEN_OFF = 2;
    public static int state_count_time = 1;         //是否在进行countTime
    public static final int IS_COUNT_TIME = 1;
    public static final int NOT_COUNT_TIME = 2;
    public static int Running = 0;                   //时间锁正在运行

    static {
        currentUseLockList = new ArrayList<>();
        currentForbiddenLockList = new ArrayList<>();
        currentTimeOver = new ArrayList<>();
        createSql = new CreateSql();
        sqLiteDatabase = createSql.getWritableDatabase();

        Launcher = null;
    }

    public SqlOperation() {

    }


//记录时间  使用线程

    public void recordTime() {
        SqlOperation sqlOperation = this;
        ThreadRecordTime threadRecordTime = new ThreadRecordTime(sqlOperation);
        threadRecordTime.start();
    }

    //记录时间  计数  count             1秒循环
    //拦截      计数  intercept_count   循环间隔  ControlService.Intercept_Interval：强制 0，，提醒 30~300秒
    //                调用updateTime 60秒循环  调用updateApp  updateCurrentLockList
    //先进行一次countTime updateApp updateCurrentLockList
    private class ThreadRecordTime extends Thread {

        private SqlOperation sqlOperation;

        public ThreadRecordTime(SqlOperation sqlOperation) {
            super();
            this.sqlOperation = sqlOperation;
        }

        @Override
        public void run() {
            super.run();

            sqlOperation.countTime();
            sqlOperation.updateApp();
            sqlOperation.updateCurrentLockList();

            //定时循环
            //21以上  拦截（精度Judge_Interval）   （每分钟）更新锁      更新当天时间（更新app）
            //21以下  拦截（精度Judge_Interval）    记录时间（精度Judge_Interval）     （每分钟）更新所记录的时间（更新app 更新锁）

            if (Build.VERSION.SDK_INT >= 21) {

//api21以上
/*$$$*/


                // 定时更新数据库，还是获取数据前更新？？

                sqlOperation.updateTodayTime();
                UsageStatsManager usageStatsManager = (UsageStatsManager) MyApplication.getContext().getSystemService("usagestats");
                List<UsageStats> queryUsageStats;
                String runningActivityPck;
                UsageStats recentStats;
                long thisTime;
                int intercept_count = 0;
                int judge_count = 0;
                int updateTime_count = 0;

                boolean running = false;


                MyLog.e_chenji_log("Build.VERSION.SDK_INT >= 21");
                MyLog.i_chenji_log("recordTime ok");
                while (true) {

                    //亮屏判断
                    if ((state_screen == SCREEN_ON) && (state_count_time == NOT_COUNT_TIME)) {
                        try {
                            intercept_count = intercept_count + 1;
                            judge_count = judge_count + 1;
                            updateTime_count = updateTime_count + 1;

                            thisTime = System.currentTimeMillis();
                            queryUsageStats = usageStatsManager.queryUsageStats(
                                    UsageStatsManager.INTERVAL_BEST, thisTime - (long) (1000 * Judge_Interval), thisTime);

                            if (queryUsageStats == null || queryUsageStats.isEmpty()) {
                                runningActivityPck = null;
                            } else {
                                recentStats = null;
                                for (UsageStats usageStats : queryUsageStats) {
                                    if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                                        recentStats = usageStats;
                                    }
                                }
                                runningActivityPck = recentStats.getPackageName();
                            }


                            //拦截
                            if (runningActivityPck != null) {
                                if (runningActivityPck.equals("com.chenji.lock")) {
                                    Running = 1;
                                } else {
                                    Running = 0;
                                    if (intercept_count >= Intercept_Interval / Judge_Interval) {
                                        sqlOperation.intercept(runningActivityPck);
                                        intercept_count = 0;
                                    }
                                }
                               /* MyLog.i_chenji_log("当前活动  " + runningActivityPck);*/
                            }

                            //更新锁
                            if (judge_count >= 60 / Judge_Interval) {
                                sqlOperation.updateCurrentLockList();
                                judge_count = 0;
                            }

                            //更新时间
                            if (Running == 1) {
                                if (updateTime_count >= 60 / Judge_Interval) {
                                    sqlOperation.updateTodayTime();
                                    updateTime_count = 0;
                                }
                            } else {
                                if (updateTime_count >= 6000 / Judge_Interval) {
                                    sqlOperation.updateTodayTime();
                                    updateTime_count = 0;
                                }
                            }
                          /*  MyLog.v_chenji_log("当前活动  无变更");*/

                        } catch (Exception e) {
                            MyLog.e_chenji_log("当前活动查询失败 ", e);
                        }

                        try {
                            Thread.sleep((long) (1000 * Judge_Interval));
                        } catch (Exception e) {
                            MyLog.e_chenji_log("recordTime 线程睡眠 失败" + e.getMessage());
                        }


                    } else {
                        try {
                            Thread.sleep(3000L);
                        } catch (Exception e) {
                            MyLog.e_chenji_log("recordTime 线程睡眠 失败" + e.getMessage());
                        }
                    }
                }


            } else {
/*$$$*/
//api21以下
                int intercept_count = 0;
                int judge_count = 0;

                HashMap<String, Integer> timeRecord = new HashMap<String, Integer>();

                ActivityManager activityManager = (ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
                String runningActivityPck;

                MyLog.i_chenji_log("recordTime ok");
                while (true) {

                    //亮屏判断
                    if ((state_screen == SCREEN_ON) && (state_count_time == NOT_COUNT_TIME)) {
                        try {
                            judge_count = judge_count + 1;
                            intercept_count = intercept_count + 1;

                            runningActivityPck = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();

                            if (runningActivityPck.equals("com.chenji.lock")) {
                                Running = 1;
                            } else {
                                Running = 0;
                                //拦截
                                if (intercept_count >= Intercept_Interval / Judge_Interval) {
                                    sqlOperation.intercept(runningActivityPck);
                                    intercept_count = 0;
                                }
                            }


                            //时间记录  更新锁
                            if (timeRecord.containsKey(runningActivityPck)) {
                                Integer newTime = new Integer(timeRecord.get(runningActivityPck).intValue() + 1);
                                timeRecord.put(runningActivityPck, newTime);
                            } else {
                                timeRecord.put(runningActivityPck, 1);
                            }
                            if (judge_count >= 60 / Judge_Interval) {
                                sqlOperation.updateTime(timeRecord);
                                timeRecord = new HashMap<String, Integer>();
                                judge_count = 0;
                            }


                          /*  MyLog.v_chenji_log("记录当前活动  " + Util.getAppName(runningActivityPck));*/
                        } catch (Exception e) {
                            MyLog.e_chenji_log("当前活动查询失败 ", e);
                        }


                        try {
                            Thread.sleep((long) (997 * Judge_Interval));
                        } catch (Exception e) {
                            MyLog.e_chenji_log("recordTime 线程睡眠 失败" + e.getMessage());
                        }
                    } else {
                        try {
                            Thread.sleep(3000L);
                        } catch (Exception e) {
                            MyLog.e_chenji_log("recordTime 线程睡眠 失败" + e.getMessage());
                        }
                    }
                }
            }
        }
    }


    //拦截
    public boolean intercept(String appPackage) {

        if (!Open_Lock) {
            return false;
        }
        if (Launcher != null) {
            if (appPackage.equals(Launcher)) {
                return false;
            }
        }

        //1禁用锁拦截
        if (SqlOperation.currentForbiddenLockList.contains(appPackage)) {
                /*ActivityManager activityManager = (ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
                activityManager.killBackgroundProcesses(appPackage);*/

            Intent intent = new Intent(MyApplication.getContext(), InterceptActivity.class);
            intent.putExtra(SqlInfo.PACKAGE, appPackage);
            intent.putExtra(SqlInfo.LOCK_TYPE, SqlInfo.LOCK_TYPE_FORBIDDEN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getContext().startActivity(intent);

            MyLog.i_chenji_log("拦截  " + Util.getAppName(appPackage));
            return true;
        }

        //超时锁拦截
        if (SqlOperation.currentTimeOver.contains(appPackage)) {
            Intent intent = new Intent(MyApplication.getContext(), InterceptActivity.class);
            intent.putExtra(SqlInfo.PACKAGE, appPackage);
            intent.putExtra(SqlInfo.LOCK_TYPE, SqlInfo.LOCK_TYPE_OVER_TIME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getContext().startActivity(intent);

            MyLog.i_chenji_log("拦截  " + Util.getAppName(appPackage));
            return true;
        }

        //2锁定锁拦截
        if (SqlOperation.currentUseLockList.isEmpty()) {
            //没锁定锁 不拦截
        } else if (SqlOperation.currentUseLockList.contains(appPackage)) {
            //锁定该app 不拦截
        } else {
            //锁定其他app 拦截

            /*ActivityManager activityManager = (ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses(appPackage);*/

            Intent intent = new Intent(MyApplication.getContext(), InterceptActivity.class);
            intent.putExtra(SqlInfo.PACKAGE, appPackage);
            intent.putExtra(SqlInfo.PACKAGE + SqlInfo.LOCK_TYPE_USE, currentUseLockList.get(0));
            intent.putExtra(SqlInfo.LOCK_TYPE, SqlInfo.LOCK_TYPE_USE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getContext().startActivity(intent);

            MyLog.i_chenji_log("非锁定 " + Util.getAppName(appPackage));
            return true;
        }

        return true;
    }


    //更新所用时间  使用线程  同时更新app 锁
    public void updateTime(HashMap<String, Integer> timeHashMap) {
        ThreadUpdateTime threadUpdateTime = new ThreadUpdateTime(timeHashMap, SqlOperation.this);
        threadUpdateTime.start();
    }

    //更新时间的线程   调用updateApp  updateCurrentLockList
    private class ThreadUpdateTime extends Thread {

        private HashMap<String, Integer> timeHashMap;
        private SqlOperation sqlOperation;

        public ThreadUpdateTime(HashMap<String, Integer> timeHashMap, SqlOperation sqlOperation) {
            super();
            this.timeHashMap = timeHashMap;
            this.sqlOperation = sqlOperation;
        }

        @Override
        public void run() {
            super.run();
            sqlOperation.updateApp();
            ContentValues contentValues;
            String[] strings = {SqlInfo.PACKAGE, SqlInfo.USAGE_TIME};
            try {
                Cursor cursor = null;
                for (Map.Entry<String, Integer> timeMap : timeHashMap.entrySet()) {
                    cursor = sqLiteDatabase.query(SqlInfo.TABLE_Info, strings,
                            SqlInfo.PACKAGE + "='" + timeMap.getKey() + "'", null, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        int newTime = cursor.getInt(cursor.getColumnIndex(SqlInfo.USAGE_TIME));
                        contentValues = new ContentValues();
                        contentValues.put(SqlInfo.USAGE_TIME, (timeMap.getValue().intValue()) * Judge_Interval + newTime);
                        sqLiteDatabase.update(SqlInfo.TABLE_Info, contentValues,
                                SqlInfo.PACKAGE + "='" + timeMap.getKey() + "'", null);
                    }
                }
                cursor.close();
                MyLog.i_chenji_log("updateTime ok");
            } catch (Exception e) {
                MyLog.e_chenji_log("updateTime 失败" + e.getMessage());
            } finally {

                //更新锁到静态列表
                sqlOperation.updateCurrentLockList();
            }
        }
    }

    //21以上更新时间
    public void updateTodayTime() {
        if (Build.VERSION.SDK_INT >= 21) {
            ThreadUpdateTodayTime threadUpdateTodayTime = new ThreadUpdateTodayTime(SqlOperation.this);
            threadUpdateTodayTime.start();
        }
    }

    private class ThreadUpdateTodayTime extends Thread {

        private SqlOperation sqlOperation;

        public ThreadUpdateTodayTime(SqlOperation sqlOperation) {
            super();
            this.sqlOperation = sqlOperation;
        }

        @Override
        public void run() {
            super.run();
            HashMap<String, Integer> timeMap = Util.getTodayTimeMap();

            sqlOperation.updateApp();
            ContentValues contentValues;
            String packageName = "";
            int useTime = 0;
            try {
                sqLiteDatabase.beginTransaction();
                if (timeMap != null) {
                    for (HashMap.Entry<String, Integer> timePair : timeMap.entrySet()) {
                        packageName = timePair.getKey();
                        useTime = timePair.getValue();
                        contentValues = new ContentValues();
                        contentValues.put(SqlInfo.USAGE_TIME, useTime);
                        sqLiteDatabase.update(SqlInfo.TABLE_Info, contentValues, SqlInfo.PACKAGE + "='" + packageName + "'", null);
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                    MyLog.i_chenji_log("updateTodayTime ok");
                } else {
                    MyLog.i_chenji_log("updateTodayTime null");
                }
            } catch (Exception e) {
                MyLog.e_chenji_log("updateTodayTime 失败" + e.getMessage());
            } finally {
                sqLiteDatabase.endTransaction();
            }
        }
    }

    //更新app
    public void updateApp() {

        //手机现有app
        ArrayList<String> newInstallApp = new ArrayList<String>();
        //数据库 已安装app
        ArrayList<String> oldInstallApp = new ArrayList<String>();
        //数据库 已卸载app
        ArrayList<String> oldUnInstallApp = new ArrayList<String>();

        //数据库 已安装app 已卸载app
        String[] strings = {SqlInfo.PACKAGE};
        try {


            Cursor cursor = sqLiteDatabase.query(SqlInfo.TABLE_Info, strings, SqlInfo.INSTALL + "=1", null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    oldInstallApp.add(cursor.getString(cursor.getColumnIndex(SqlInfo.PACKAGE)));
                } while (cursor.moveToNext());
            }
            cursor.close();

            Cursor cursorUnInstall = sqLiteDatabase.query(SqlInfo.TABLE_Info, strings, SqlInfo.INSTALL + "=2", null, null, null, null);
            if (cursorUnInstall != null && cursorUnInstall.moveToFirst()) {
                do {
                    oldUnInstallApp.add(cursorUnInstall.getString(cursorUnInstall.getColumnIndex(SqlInfo.PACKAGE)));
                } while (cursorUnInstall.moveToNext());
            }
            cursorUnInstall.close();
        } catch (Exception e) {
            MyLog.e_chenji_log("update 获取数据库已有包名失败" + e);
        }

        PackageManager packageManager = MyApplication.getContext().getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);

        String packageName = "";
        int appType;
        ByteArrayOutputStream appIcon;
        ContentValues contentValues;
        contentValues = new ContentValues();

        sqLiteDatabase.beginTransaction();

        //数据对比
        //新安装，相同，卸载(删除锁)，重安装
        try {

            //将手机的app与数据库中的对比
            for (PackageInfo packageInfo : packageInfos) {

                packageName = packageInfo.packageName;

                //手机现有app
                newInstallApp.add(packageName);

                if (oldInstallApp.contains(packageName)) {
                    //对应
                } else if (oldUnInstallApp.contains(packageName)) {
                    //现有的标为已卸载了
                    contentValues.put(SqlInfo.INSTALL, 1);
                    sqLiteDatabase.update(SqlInfo.TABLE_Info, contentValues,
                            SqlInfo.PACKAGE + "='" + packageName + "'", null);
                    contentValues = new ContentValues();
                } else {
                    //现有的不在数据库

                    appType = Type.getAppType(packageManager, packageInfo);

                    if (appType == SqlInfo.APP_TYPE_USELESS) {
                        ////没有桌面图标的不储存图标
                    } else {
                        appIcon = new ByteArrayOutputStream();
                        try {
                            ((BitmapDrawable) packageInfo.applicationInfo.loadIcon(MyApplication.getContext().getPackageManager())).
                                    getBitmap().compress(Bitmap.CompressFormat.PNG, 100, appIcon);
                        } catch (Exception e) {
                            MyLog.e_chenji_log("updateApp 图标存储失败");
                        }
                        contentValues.put(SqlInfo.ICON, appIcon.toByteArray());
                    }

                    contentValues.put(SqlInfo.APP_NAME, packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.GET_INSTRUMENTATION).
                            applicationInfo.loadLabel(MyApplication.getContext().getPackageManager()).toString());
                    contentValues.put(SqlInfo.PACKAGE, packageName);
                    contentValues.put(SqlInfo.APP_TYPE, appType);
                    sqLiteDatabase.insert(SqlInfo.TABLE_Info, null, contentValues);
                    contentValues = new ContentValues();
                }

            }


            //将数据库中已安装的与手机上的对比
            for (String oldInApp : oldInstallApp) {
                if (newInstallApp.contains(oldInApp)) {
                    //数据库中已安装的在手机上
                } else {
                    //数据库已安装的不在手机上
                    contentValues.put(SqlInfo.INSTALL, 2);
                    sqLiteDatabase.update(SqlInfo.TABLE_Info, contentValues,
                            SqlInfo.PACKAGE + "='" + oldInApp + "'", null);
                    sqLiteDatabase.delete(SqlInfo.TABLE_Lock, SqlInfo.PACKAGE + "='" + oldInApp + "'", null);
                    contentValues = new ContentValues();
                }
            }


            sqLiteDatabase.setTransactionSuccessful();
            MyLog.i_chenji_log("updateApp ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("updateApp 失败" + e.toString());
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    //更新锁到静态列表
    public void updateCurrentLockList() {


        Calendar calendar;
        calendar = Calendar.getInstance();

        int currentTime = (calendar.get(Calendar.HOUR_OF_DAY)) * 60 + calendar.get(Calendar.MINUTE);
        String currentWeek = Util.getWeekForSql();

        String[] strings = {SqlInfo.PACKAGE};
        try {
            Cursor cursorUse = sqLiteDatabase.query(SqlInfo.TABLE_Lock, strings,
                    SqlInfo.LOCK_OPEN + " = " + SqlInfo.LOCK_OPEN_OPEN + " and " +
                            SqlInfo.LOCK_TYPE + "=" + SqlInfo.LOCK_TYPE_USE + " and " +
                            SqlInfo.LOCK_START_TIME + " < " + currentTime + " and " +
                            SqlInfo.LOCK_FINISH_TIME + " > " + currentTime + " and " +
                            "(" + currentWeek + "=1 " + " or " + SqlInfo.LOCK_WEEK + "=0 )",
                    null, null, null, SqlInfo.LOCK_ORDER + " desc");

            Cursor cursorForbidden = sqLiteDatabase.query(SqlInfo.TABLE_Lock, strings,
                    SqlInfo.LOCK_OPEN + " = " + SqlInfo.LOCK_OPEN_OPEN + " and " +
                            SqlInfo.LOCK_TYPE + "=" + SqlInfo.LOCK_TYPE_FORBIDDEN + " and " +
                            SqlInfo.LOCK_START_TIME + " < " + currentTime + " and " +
                            SqlInfo.LOCK_FINISH_TIME + " > " + currentTime + " and " +
                            "(" + currentWeek + "=1 " + " or " + SqlInfo.LOCK_WEEK + "=0 )",
                    null, null, null, SqlInfo.LOCK_ORDER + " desc");

            Cursor cursorTimeOver = sqLiteDatabase.query(SqlInfo.TABLE_Info, strings,
                    SqlInfo.LOCK_OPEN + " = " + SqlInfo.LOCK_OPEN_OPEN + " and " +
                            currentWeek + " < " + SqlInfo.USAGE_TIME,
                    null, null, null, null);

            String lockPackageName;
            ArrayList<String> use = new ArrayList<String>();
            if (cursorUse != null && cursorUse.moveToFirst()) {
                do {
                    lockPackageName = cursorUse.getString(cursorUse.getColumnIndex(SqlInfo.PACKAGE));
                    if (use.contains(lockPackageName)) {
                        //已经包含
                    } else {
                        use.add(lockPackageName);
                    }
                } while (cursorUse.moveToNext());
            }
            ArrayList<String> forbidden = new ArrayList<String>();
            if (cursorForbidden != null && cursorForbidden.moveToFirst()) {
                do {
                    lockPackageName = cursorForbidden.getString(cursorForbidden.getColumnIndex(SqlInfo.PACKAGE));
                    if (forbidden.contains(lockPackageName)) {
                        //已经包含
                    } else {
                        forbidden.add(lockPackageName);
                    }
                } while (cursorForbidden.moveToNext());
            }
            ArrayList<String> over = new ArrayList<String>();
            if (cursorTimeOver != null && cursorTimeOver.moveToFirst()) {
                do {
                    lockPackageName = cursorTimeOver.getString(cursorTimeOver.getColumnIndex(SqlInfo.PACKAGE));
                    if (over.contains(lockPackageName)) {
                        //已经包含
                    } else {
                        over.add(lockPackageName);
                    }
                } while (cursorTimeOver.moveToNext());
            }

            SqlOperation.currentUseLockList = use;
            SqlOperation.currentForbiddenLockList = forbidden;
            SqlOperation.currentTimeOver = over;

           /* for (String string : currentUseLockList) {
                MyLog.i_chenji_log("updateCurrentLockList  use  " + string);
            }
            for (String string : currentForbiddenLockList) {
                MyLog.i_chenji_log("updateCurrentLockList  forbidden  " + string);
            }*/

            use = null;
            forbidden = null;
            over = null;
            lockPackageName = null;
            cursorUse.close();
            cursorForbidden.close();
            cursorTimeOver.close();

            MyLog.i_chenji_log("updateCurrentLockList 查询锁 ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("updateCurrentLockList 查询锁 失败   " + e.toString());
        } finally {
            strings = null;
            calendar = null;
        }
    }

    //时间统计 日期变化(table_info增加列，table_lock删除当天锁)
    public void countTime() {
        Calendar calendar = Calendar.getInstance();
        String dateToday = Util.dateForSql(calendar);

        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(MyApplication.FILE_CONTROL, Activity.MODE_PRIVATE);
        String dateSqlToday = sharedPreferences.getString(MyApplication.DATE_TODAY_SQL, "0");

        //判断数据库与手机日期是否同步
        if (dateToday.equals(dateSqlToday)) {
            //不用更新
            SqlInfo.USAGE_TIME = dateToday;
            MyLog.i_chenji_log("countTime  ok same day");

        } else {
            // 停止recordTime
            state_count_time = IS_COUNT_TIME;

            //增加dateToday列
            try {
                sqLiteDatabase.execSQL("alter table " + SqlInfo.TABLE_Info +
                        " add " + dateToday + " integer not null default 0 ");
                //将USAGE_TIME换为今天
                SqlInfo.USAGE_TIME = dateToday;
                SharedPreferences.Editor myEditor = sharedPreferences.edit();
                myEditor.putString(MyApplication.DATE_TODAY_SQL, dateToday);
                myEditor.apply();
            } catch (Exception e) {
                MyLog.e_chenji_log("countTime 增加列 失败 ", e);
            }


            state_count_time = NOT_COUNT_TIME;

            //删除当天锁
            sqLiteDatabase.delete(SqlInfo.TABLE_Lock, SqlInfo.LOCK_WEEK + "=" + 0, null);
            MyLog.i_chenji_log("countTime  ok another day");
        }
    }






    public Cursor queryInfo(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return sqLiteDatabase.query(SqlInfo.TABLE_Info, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public Cursor queryLock(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return sqLiteDatabase.query(SqlInfo.TABLE_Lock, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public void insertLock(ContentValues values) {
        sqLiteDatabase.insert(SqlInfo.TABLE_Lock, null, values);
        updateCurrentLockList();
    }

    public void deleteLock(String selection, String[] selectionArgs) {
        sqLiteDatabase.delete(SqlInfo.TABLE_Lock, selection, selectionArgs);
        updateCurrentLockList();
    }


    public void updateLock(ContentValues values, String selection, String[] selectionArgs) {
        sqLiteDatabase.update(SqlInfo.TABLE_Lock, values, selection, selectionArgs);
        updateCurrentLockList();
    }

    public void updateLockOverTime(ContentValues values, String selection, String[] selectionArgs) {
        sqLiteDatabase.update(SqlInfo.TABLE_Info, values, selection, selectionArgs);
        updateCurrentLockList();
    }














   /*


    //查询app
    public AppInfo getTheApp(String packageName) {
        AppInfo appInfo;
        String[] strs = {SqlInfo.PACKAGE,SqlInfo.APP_NAME, SqlInfo.APP_TYPE, SqlInfo.ICON, SqlInfo.USAGE_TIME};
        try {
            Cursor cursor = sqLiteDatabase.query(SqlInfo.TABLE_Info, strs,
                    SqlInfo.PACKAGE + "='" + packageName + "'", null,
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                appInfo = new AppInfo(
                        cursor.getString(cursor.getColumnIndex(SqlInfo.PACKAGE)),
                        cursor.getString(cursor.getColumnIndex(SqlInfo.APP_NAME)),
                        cursor.getInt(cursor.getColumnIndex(SqlInfo.APP_TYPE)),
                        cursor.getBlob(cursor.getColumnIndex(SqlInfo.ICON)),
                        cursor.getInt(cursor.getColumnIndex(SqlInfo.USAGE_TIME)));
            } else {
                appInfo = new AppInfo();
            }
            cursor.close();
        } catch (Exception e) {
            MyLog.e_chenji_log("getTheApp 读取失败" + e.getMessage());
            appInfo = new AppInfo();
        }
        MyLog.i_chenji_log("getTheApp ok");
        return appInfo;
    }

    public Drawable getIcon(String packageName) {
        String[] strs = {SqlInfo.ICON};
        try {
            Cursor cursor = sqLiteDatabase.query(SqlInfo.TABLE_Info, strs,
                    SqlInfo.PACKAGE + "='" + packageName + "'", null,
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Drawable drawable = Util.transferBytesAndDrawable(cursor.getBlob(cursor.getColumnIndex(SqlInfo.ICON)));
                cursor.close();
                return drawable;
            } else {
                cursor.close();
                return null;
            }
        } catch (Exception e) {
            MyLog.e_chenji_log("getIcon 读取失败" + e);
            return null;
        }
    }

    public ArrayList<AppInfo> getAppInfo(int topNumber) {

        ArrayList<AppInfo> appInfos = new ArrayList<>();
        String[] strs = {SqlInfo.PACKAGE,SqlInfo.APP_NAME, SqlInfo.APP_TYPE, SqlInfo.ICON, SqlInfo.USAGE_TIME};

        try {
            Cursor cursor = sqLiteDatabase.query(SqlInfo.TABLE_Info, strs,
                    SqlInfo.INSTALL + "=" + SqlInfo.INSTALL_TRUE + " and " +
                            SqlInfo.APP_TYPE + "=" + SqlInfo.APP_TYPE_USE,
                    null, null, null, SqlInfo.USAGE_TIME + " desc");

            if (cursor != null && cursor.moveToFirst()) {
                int i = 0;
                do {
                    AppInfo appInfo = new AppInfo(
                            cursor.getString(cursor.getColumnIndex(SqlInfo.PACKAGE)),
                            cursor.getString(cursor.getColumnIndex(SqlInfo.APP_NAME)),
                            cursor.getInt(cursor.getColumnIndex(SqlInfo.APP_TYPE)),
                            cursor.getBlob(cursor.getColumnIndex(SqlInfo.ICON)),
                            cursor.getInt(cursor.getColumnIndex(SqlInfo.USAGE_TIME)));
                    appInfos.add(appInfo);
                    i = i + 1;
                } while (cursor.moveToNext() && i < topNumber);
            }
            MyLog.i_chenji_log("getAppInfo ok " + SqlInfo.USAGE_TIME);
            cursor.close();
        } catch (Exception e) {
            MyLog.e_chenji_log("getAppInfo 读取失败" + e.toString());
        }

        return appInfos;
    }

    public ArrayList<AppInfo> getAppInfo(int topNumber, String dateForSql) {

        ArrayList<AppInfo> appInfos = new ArrayList<>();
        String[] strs = {SqlInfo.PACKAGE,SqlInfo.APP_NAME, SqlInfo.APP_TYPE, SqlInfo.ICON, dateForSql};

        try {
            Cursor cursor = sqLiteDatabase.query(SqlInfo.TABLE_Info, strs,
                    SqlInfo.APP_TYPE + "=" + SqlInfo.APP_TYPE_USE + " and " +
                            dateForSql + ">0 ",
                    null, null, null, dateForSql + " desc");

            if (cursor != null && cursor.moveToFirst()) {
                int i = 0;
                do {
                    AppInfo appInfo = new AppInfo(
                            cursor.getString(cursor.getColumnIndex(SqlInfo.PACKAGE)),
                            cursor.getString(cursor.getColumnIndex(SqlInfo.APP_NAME)),
                            cursor.getInt(cursor.getColumnIndex(SqlInfo.APP_TYPE)),
                            cursor.getBlob(cursor.getColumnIndex(SqlInfo.ICON)),
                            cursor.getInt(cursor.getColumnIndex(dateForSql)));
                    appInfos.add(appInfo);
                    i = i + 1;
                } while (cursor.moveToNext() && i < topNumber);
            }
            MyLog.i_chenji_log("getAppInfo ok " + dateForSql);
            cursor.close();
        } catch (Exception e) {
            MyLog.e_chenji_log("getAppInfo 读取失败" + e.toString());
        }

        return appInfos;
    }


    //查询锁
    public ArrayList<HashMap<String, Object>> getLock(String packageName) {
        String[] strings = {SqlInfo.LOCK_TYPE, SqlInfo.LOCK_ORDER,
                SqlInfo.LOCK_MONDAY, SqlInfo.LOCK_TUESDAY, SqlInfo.LOCK_WEDNESDAY,
                SqlInfo.LOCK_THURSDAY, SqlInfo.LOCK_FRIDAY, SqlInfo.LOCK_SATURDAY, SqlInfo.LOCK_SUNDAY,
                SqlInfo.LOCK_START_TIME, SqlInfo.LOCK_FINISH_TIME};
        ArrayList<HashMap<String, Object>> lockTimeList = new ArrayList<HashMap<String, Object>>();
        try {
            Cursor cursor = sqLiteDatabase.query(SqlInfo.TABLE_Lock, strings,
                    SqlInfo.PACKAGE + "='" + packageName + "'", null, null, null, SqlInfo.LOCK_ORDER + " asc");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put(SqlInfo.LOCK_TYPE, cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_TYPE)));
                    hashMap.put(SqlInfo.LOCK_ORDER, cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_ORDER)));
                    hashMap.put(SqlInfo.LOCK_MONDAY, cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_MONDAY)));
                    hashMap.put(SqlInfo.LOCK_TUESDAY, cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_TUESDAY)));
                    hashMap.put(SqlInfo.LOCK_WEDNESDAY, cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_WEDNESDAY)));
                    hashMap.put(SqlInfo.LOCK_THURSDAY, cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_THURSDAY)));
                    hashMap.put(SqlInfo.LOCK_FRIDAY, cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_FRIDAY)));
                    hashMap.put(SqlInfo.LOCK_SATURDAY, cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_SATURDAY)));
                    hashMap.put(SqlInfo.LOCK_SUNDAY, cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_SUNDAY)));
                    hashMap.put(SqlInfo.LOCK_START_TIME, cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_START_TIME)));
                    hashMap.put(SqlInfo.LOCK_FINISH_TIME, cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_FINISH_TIME)));
                    lockTimeList.add(hashMap);
                } while (cursor.moveToNext());
            }
            cursor.close();
            MyLog.i_chenji_log("查询锁 ok");
            return lockTimeList;
        } catch (Exception e) {
            MyLog.e_chenji_log("查询锁 失败 ", e);
            return null;
        }
    }

    public ArrayList<String> getCurrentForbiddenLockList() {
        Calendar calendar;
        int currentTime;
        String currentWeek;


        calendar = Calendar.getInstance();
        currentTime = (calendar.get(Calendar.HOUR_OF_DAY)) * 60 + calendar.get(Calendar.MINUTE);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY: {
                currentWeek = SqlInfo.LOCK_MONDAY;
                break;
            }
            case Calendar.TUESDAY: {
                currentWeek = SqlInfo.LOCK_TUESDAY;
                break;
            }
            case Calendar.WEDNESDAY: {
                currentWeek = SqlInfo.LOCK_WEDNESDAY;
                break;
            }
            case Calendar.THURSDAY: {
                currentWeek = SqlInfo.LOCK_THURSDAY;
                break;
            }
            case Calendar.FRIDAY: {
                currentWeek = SqlInfo.LOCK_FRIDAY;
                break;
            }
            case Calendar.SATURDAY: {
                currentWeek = SqlInfo.LOCK_SATURDAY;
                break;
            }
            default: {
                currentWeek = SqlInfo.LOCK_SUNDAY;
                break;
            }
        }

        String[] strings = {SqlInfo.PACKAGE};
        try {
            Cursor cursorForbidden = sqLiteDatabase.query(SqlInfo.TABLE_Lock, strings,
                    SqlInfo.LOCK_TYPE + "=" + SqlInfo.LOCK_TYPE_FORBIDDEN + " and " +
                            SqlInfo.LOCK_START_TIME + " < " + currentTime + " and " +
                            SqlInfo.LOCK_FINISH_TIME + " > " + currentTime + " and " +
                            "(" + currentWeek + "=1 " + " or " + SqlInfo.LOCK_WEEK + "=0 )",
                    null, null, null, SqlInfo.LOCK_ORDER + " desc");

            String packageName;
            ArrayList<String> forbidden = new ArrayList<String>();
            if (cursorForbidden != null && cursorForbidden.moveToFirst()) {
                do {
                    packageName = cursorForbidden.getString(cursorForbidden.getColumnIndex(SqlInfo.PACKAGE));
                    if (forbidden.contains(packageName)) {
                        //已经包含
                    } else {
                        forbidden.add(packageName);
                    }
                } while (cursorForbidden.moveToNext());
            }

            packageName = null;
            strings = null;
            calendar = null;
            cursorForbidden.close();

            MyLog.i_chenji_log("getCurrentForbiddenLockList 查询锁 ok");
            return forbidden;
        } catch (Exception e) {
            MyLog.e_chenji_log("getCurrentForbiddenLockList 查询锁 失败   " + e.toString());
            return null;
        }
    }

    public ArrayList<String> getCurrentUseLockList() {
        Calendar calendar;
        int currentTime;
        String currentWeek;


        calendar = Calendar.getInstance();
        currentTime = (calendar.get(Calendar.HOUR_OF_DAY)) * 60 + calendar.get(Calendar.MINUTE);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY: {
                currentWeek = SqlInfo.LOCK_MONDAY;
                break;
            }
            case Calendar.TUESDAY: {
                currentWeek = SqlInfo.LOCK_TUESDAY;
                break;
            }
            case Calendar.WEDNESDAY: {
                currentWeek = SqlInfo.LOCK_WEDNESDAY;
                break;
            }
            case Calendar.THURSDAY: {
                currentWeek = SqlInfo.LOCK_THURSDAY;
                break;
            }
            case Calendar.FRIDAY: {
                currentWeek = SqlInfo.LOCK_FRIDAY;
                break;
            }
            case Calendar.SATURDAY: {
                currentWeek = SqlInfo.LOCK_SATURDAY;
                break;
            }
            default: {
                currentWeek = SqlInfo.LOCK_SUNDAY;
                break;
            }
        }

        String[] strings = {SqlInfo.PACKAGE};
        try {
            Cursor cursorUse = sqLiteDatabase.query(SqlInfo.TABLE_Lock, strings,
                    SqlInfo.LOCK_TYPE + "=" + SqlInfo.LOCK_TYPE_USE + " and " +
                            SqlInfo.LOCK_START_TIME + " < " + currentTime + " and " +
                            SqlInfo.LOCK_FINISH_TIME + " > " + currentTime + " and " +
                            "(" + currentWeek + "=1 " + " or " + SqlInfo.LOCK_WEEK + "=0 )",
                    null, null, null, SqlInfo.LOCK_ORDER + " desc");

            String packageName;
            ArrayList<String> use = new ArrayList<String>();
            if (cursorUse != null && cursorUse.moveToFirst()) {
                do {
                    packageName = cursorUse.getString(cursorUse.getColumnIndex(SqlInfo.PACKAGE));
                    if (use.contains(packageName)) {
                        //已经包含
                    } else {
                        use.add(packageName);
                    }
                } while (cursorUse.moveToNext());
            }

            for (String string : currentUseLockList) {
                MyLog.i_chenji_log("updateCurrentLockList  use  " + string);
            }
            for (String string : currentForbiddenLockList) {
                MyLog.i_chenji_log("updateCurrentLockList  forbidden  " + string);
            }

            packageName = null;
            strings = null;
            calendar = null;
            cursorUse.close();

            MyLog.i_chenji_log("updateCurrentUseLockList 查询锁 ok");
            return use;
        } catch (Exception e) {
            MyLog.e_chenji_log("updateCurrentUseLockList 查询锁 失败   " + e.toString());
            return null;
        }
    }

    public ArrayList<HashMap<String, Object>> getIconWithUseCurrentLock() {
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
        HashMap<String, Object> hashMap;
        for (String packageName: getCurrentUseLockList()) {
            hashMap = new HashMap<>();
            hashMap.put(SqlInfo.PACKAGE, packageName);
            hashMap.put(SqlInfo.ICON, getIcon(packageName));
            arrayList.add(hashMap);
        }
        return arrayList;
    }

    public ArrayList<HashMap<String, Object>> getIconWithForbiddenCurrentLock() {
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
        HashMap<String, Object> hashMap;
        for (String packageName : getCurrentForbiddenLockList()) {
            hashMap = new HashMap<>();
            hashMap.put(SqlInfo.PACKAGE, packageName);
            hashMap.put(SqlInfo.ICON, getIcon(packageName));
            arrayList.add(hashMap);
        }
        return arrayList;
    }

    public HashMap<String, Integer> getLocTime(String packageName, int lockType) {

        int startTime = 0;
        int finishTime = 0;
        int order = 0;

        Calendar calendar;
        int currentTime;
        String currentWeek;

        calendar = Calendar.getInstance();
        currentTime = (calendar.get(Calendar.HOUR_OF_DAY)) * 60 + calendar.get(Calendar.MINUTE);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY: {
                currentWeek = SqlInfo.LOCK_MONDAY;
                break;
            }
            case Calendar.TUESDAY: {
                currentWeek = SqlInfo.LOCK_TUESDAY;
                break;
            }
            case Calendar.WEDNESDAY: {
                currentWeek = SqlInfo.LOCK_WEDNESDAY;
                break;
            }
            case Calendar.THURSDAY: {
                currentWeek = SqlInfo.LOCK_THURSDAY;
                break;
            }
            case Calendar.FRIDAY: {
                currentWeek = SqlInfo.LOCK_FRIDAY;
                break;
            }
            case Calendar.SATURDAY: {
                currentWeek = SqlInfo.LOCK_SATURDAY;
                break;
            }
            default: {
                currentWeek = SqlInfo.LOCK_SUNDAY;
                break;
            }
        }

        String[] strings = {SqlInfo.LOCK_ORDER, SqlInfo.LOCK_START_TIME, SqlInfo.LOCK_FINISH_TIME};
        try {
            if (lockType == SqlInfo.LOCK_TYPE_USE) {
                Cursor cursorUse = sqLiteDatabase.query(SqlInfo.TABLE_Lock, strings,
                        SqlInfo.PACKAGE + "='" + packageName+ "'" + " and " +
                                SqlInfo.LOCK_TYPE + "=" + SqlInfo.LOCK_TYPE_USE + " and " +
                                SqlInfo.LOCK_START_TIME + " < " + currentTime + " and " +
                                SqlInfo.LOCK_FINISH_TIME + " > " + currentTime + " and " +
                                "(" + currentWeek + "=1 " + " or " + SqlInfo.LOCK_WEEK + "=0 )",
                        null, null, null, SqlInfo.LOCK_FINISH_TIME + " desc");
                if (cursorUse != null && cursorUse.moveToFirst()) {
                    startTime = cursorUse.getInt(cursorUse.getColumnIndex(SqlInfo.LOCK_START_TIME));
                    finishTime = cursorUse.getInt(cursorUse.getColumnIndex(SqlInfo.LOCK_FINISH_TIME));
                    order = cursorUse.getInt(cursorUse.getColumnIndex(SqlInfo.LOCK_ORDER));
                }
                cursorUse.close();
            } else if (lockType == SqlInfo.LOCK_TYPE_FORBIDDEN) {
                Cursor cursorForbidden = sqLiteDatabase.query(SqlInfo.TABLE_Lock, strings,
                        SqlInfo.PACKAGE + "='" + packageName + "'" + " and " +
                                SqlInfo.LOCK_TYPE + "=" + SqlInfo.LOCK_TYPE_FORBIDDEN + "  and " +
                                SqlInfo.LOCK_START_TIME + " < " + currentTime + " and " +
                                SqlInfo.LOCK_FINISH_TIME + " > " + currentTime + " and " +
                                "(" + currentWeek + "=1 " + " or " + SqlInfo.LOCK_WEEK + "=0 )",
                        null, null, null, SqlInfo.LOCK_FINISH_TIME + " desc");
                if (cursorForbidden != null && cursorForbidden.moveToFirst()) {
                    startTime = cursorForbidden.getInt(cursorForbidden.getColumnIndex(SqlInfo.LOCK_START_TIME));
                    finishTime = cursorForbidden.getInt(cursorForbidden.getColumnIndex(SqlInfo.LOCK_FINISH_TIME));
                    order = cursorForbidden.getInt(cursorForbidden.getColumnIndex(SqlInfo.LOCK_ORDER));
                }
                cursorForbidden.close();
            }

            MyLog.i_chenji_log("getLockFinishTime ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("getLockFinishTime 失败   " + e.toString());
        } finally {
            strings = null;
            calendar = null;
            HashMap hashMap = new HashMap();
            hashMap.put(SqlInfo.LOCK_START_TIME, startTime);
            hashMap.put(SqlInfo.LOCK_FINISH_TIME, finishTime);
            hashMap.put(SqlInfo.LOCK_ORDER, order);
            return hashMap;
        }
    }


    //添加锁 插入数据库
    //参数app_name type week start finish
    public void addLock(String packageName, int type, int week, int mon, int tue, int wed, int thu, int fri, int sat, int sun, int start, int finish) {
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(SqlInfo.PACKAGE, packageName);
        contentValues.put(SqlInfo.LOCK_WEEK, week);
        contentValues.put(SqlInfo.LOCK_MONDAY, mon);
        contentValues.put(SqlInfo.LOCK_TUESDAY, tue);
        contentValues.put(SqlInfo.LOCK_WEDNESDAY, wed);
        contentValues.put(SqlInfo.LOCK_THURSDAY, thu);
        contentValues.put(SqlInfo.LOCK_FRIDAY, fri);
        contentValues.put(SqlInfo.LOCK_SATURDAY, sat);
        contentValues.put(SqlInfo.LOCK_SUNDAY, sun);
        contentValues.put(SqlInfo.LOCK_TYPE, type);
        contentValues.put(SqlInfo.LOCK_START_TIME, start);
        contentValues.put(SqlInfo.LOCK_FINISH_TIME, finish);
        try {
            sqLiteDatabase.insert(SqlInfo.TABLE_Lock, null, contentValues);
            MyLog.i_chenji_log("添加锁 ok");
            updateCurrentLockList();
        } catch (Exception e) {
            MyLog.e_chenji_log("添加锁 失败 ", e);
        }
    }

    //删除锁 删除数据库
    //参数 order_id
    public void deleteLock(int order) {
        try {
            sqLiteDatabase.delete(SqlInfo.TABLE_Lock, SqlInfo.LOCK_ORDER + "=" + order, null);
            MyLog.i_chenji_log("删除锁 ok");
            updateCurrentLockList();
        } catch (Exception e) {
            MyLog.e_chenji_log("删除锁 失败 ", e);
        }
    }
*/

/*    public void initImageTable() {
        String[] strings = {SqlInfo.IMAGE_ORDER};
        try {
            Cursor cursor = sqLiteDatabase.query(SqlInfo.TABLE_IMAGE, strings,
                    null, null, null, null, null);

            if (cursor.getCount()==0) {
                ContentValues contentValues;
                contentValues = new ContentValues();
                contentValues.put(SqlInfo.IMAGE_IMAGE,Util.transferBytesAndDrawable((BitmapDrawable)(Util.getDrawable(R.drawable.set_background_5))));
                contentValues.put(SqlInfo.IMAGE_SELECTED, SqlInfo.IMAGE_SET_BACKGROUND_SELECTED);
                sqLiteDatabase.insert(SqlInfo.TABLE_IMAGE, null, contentValues);
                contentValues = new ContentValues();
                contentValues.put(SqlInfo.IMAGE_IMAGE,Util.transferBytesAndDrawable((BitmapDrawable)(Util.getDrawable(R.drawable.set_background_1))));
                contentValues.put(SqlInfo.IMAGE_SELECTED, SqlInfo.IMAGE_SET_BACKGROUND_UNSELECTED);
                sqLiteDatabase.insert(SqlInfo.TABLE_IMAGE, null, contentValues);
                contentValues = new ContentValues();
                contentValues.put(SqlInfo.IMAGE_IMAGE,Util.transferBytesAndDrawable((BitmapDrawable)(Util.getDrawable(R.drawable.set_background_2))));
                contentValues.put(SqlInfo.IMAGE_SELECTED, SqlInfo.IMAGE_SET_BACKGROUND_UNSELECTED);
                sqLiteDatabase.insert(SqlInfo.TABLE_IMAGE, null, contentValues);
                contentValues = new ContentValues();
                contentValues.put(SqlInfo.IMAGE_IMAGE,Util.transferBytesAndDrawable((BitmapDrawable)(Util.getDrawable(R.drawable.set_background_3))));
                contentValues.put(SqlInfo.IMAGE_SELECTED, SqlInfo.IMAGE_SET_BACKGROUND_UNSELECTED);
                sqLiteDatabase.insert(SqlInfo.TABLE_IMAGE, null, contentValues);
                contentValues = new ContentValues();
                contentValues.put(SqlInfo.IMAGE_IMAGE,Util.transferBytesAndDrawable((BitmapDrawable)(Util.getDrawable(R.drawable.set_background_4))));
                contentValues.put(SqlInfo.IMAGE_SELECTED, SqlInfo.IMAGE_SET_BACKGROUND_UNSELECTED);
                sqLiteDatabase.insert(SqlInfo.TABLE_IMAGE, null, contentValues);
                contentValues = new ContentValues();
                contentValues.put(SqlInfo.IMAGE_IMAGE,Util.transferBytesAndDrawable((BitmapDrawable)(Util.getDrawable(R.drawable.set_background_0))));
                contentValues.put(SqlInfo.IMAGE_SELECTED, SqlInfo.IMAGE_SET_BACKGROUND_UNSELECTED);
                sqLiteDatabase.insert(SqlInfo.TABLE_IMAGE, null, contentValues);
            }
            MyLog.i_chenji_log("initImageTable ok"+cursor.getCount());
            cursor.close();
        } catch (Exception e) {
            MyLog.e_chenji_log("initImageTable 失败" + e.toString());
        }
    }

    public Drawable getSetBackground() {
        String[] strings = {SqlInfo.IMAGE_IMAGE};
        try {
            Cursor cursor = sqLiteDatabase.query(SqlInfo.TABLE_IMAGE, strings,
                    SqlInfo.IMAGE_SELECTED + "=" + SqlInfo.IMAGE_SET_BACKGROUND_SELECTED,
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Drawable drawable = Util.transferBytesAndDrawable(cursor.getBlob(cursor.getColumnIndex(SqlInfo.IMAGE_IMAGE)));
                cursor.close();
                MyLog.d_chenji_log("getSetBackground  ok");
                return drawable;
            } else {
                return null;
            }
        } catch (Exception e) {
            MyLog.e_chenji_log("getSetBackground 读取失败" + e.toString());
            return null;
        }
    }

    public int getSetBackgroundOrder() {
        String[] strings = {SqlInfo.IMAGE_ORDER};
        try {
            Cursor cursor = sqLiteDatabase.query(SqlInfo.TABLE_IMAGE, strings,
                    SqlInfo.IMAGE_SELECTED + "=" + SqlInfo.IMAGE_SET_BACKGROUND_SELECTED,
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int order = cursor.getInt(cursor.getColumnIndex(SqlInfo.IMAGE_ORDER));
                cursor.close();
                MyLog.d_chenji_log("getSetBackgroundOrder  ok");
                return order;
            } else {
                return -1;
            }
        } catch (Exception e) {
            MyLog.e_chenji_log("getSetBackground 读取失败" + e.toString());
            return -1;
        }
    }

    public ArrayList<HashMap<String, Object>> getAllSetBackground() {
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
        HashMap<String, Object> hashMap;
        String[] strings = {SqlInfo.IMAGE_IMAGE, SqlInfo.IMAGE_ORDER};
        try {
            Cursor cursor = sqLiteDatabase.query(SqlInfo.TABLE_IMAGE, strings,
                    SqlInfo.IMAGE_SELECTED + "=" + SqlInfo.IMAGE_SET_BACKGROUND_SELECTED + " or " +
                            SqlInfo.IMAGE_SELECTED + "=" + SqlInfo.IMAGE_SET_BACKGROUND_UNSELECTED,
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    hashMap = new HashMap<>();
                    hashMap.put(SqlInfo.IMAGE_IMAGE, Util.transferBytesAndDrawable(cursor.getBlob(cursor.getColumnIndex(SqlInfo.IMAGE_IMAGE))));
                    hashMap.put(SqlInfo.IMAGE_ORDER, Util.transferBytesAndDrawable(cursor.getBlob(cursor.getColumnIndex(SqlInfo.IMAGE_ORDER))));
                    arrayList.add(hashMap);
                } while (cursor.moveToNext());
            }
            MyLog.i_chenji_log("getAllSetBackground ok ");
            cursor.close();
            return arrayList;
        } catch (Exception e) {
            MyLog.e_chenji_log("getAllSetBackground 读取失败" + e.toString());
            return null;
        }
    }

    public int addSetBackground(BitmapDrawable bitmapDrawable) {
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(SqlInfo.IMAGE_SELECTED, SqlInfo.IMAGE_SET_BACKGROUND_UNSELECTED);
        try {
            sqLiteDatabase.update(SqlInfo.TABLE_IMAGE, contentValues,
                    SqlInfo.IMAGE_SELECTED + "=" + SqlInfo.IMAGE_SET_BACKGROUND_SELECTED, null);
            MyLog.i_chenji_log("addSetBackground ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("addSetBackground 失败 ", e);
        }


        contentValues = new ContentValues();
        contentValues.put(SqlInfo.IMAGE_IMAGE, Util.transferBytesAndDrawable(bitmapDrawable));
        contentValues.put(SqlInfo.IMAGE_SELECTED, SqlInfo.IMAGE_SET_BACKGROUND_SELECTED);
        try {
            sqLiteDatabase.insert(SqlInfo.TABLE_IMAGE, null, contentValues);
            MyLog.i_chenji_log("addSetBackground ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("addSetBackground 失败 ", e);
        }

        String[] strings = {SqlInfo.IMAGE_ORDER};
        try {
            Cursor cursor = sqLiteDatabase.query(SqlInfo.TABLE_IMAGE, strings,
                    SqlInfo.IMAGE_SELECTED + "=" + SqlInfo.IMAGE_SET_BACKGROUND_SELECTED,
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int order = cursor.getInt(cursor.getColumnIndex(SqlInfo.IMAGE_ORDER));
                cursor.close();
                return order;
            } else {
                return -1;
            }
        } catch (Exception e) {
            MyLog.e_chenji_log("获取order 读取失败" + e.toString());
            return -1;
        }
    }

    public void changeSetBackground(int newOrder) {
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(SqlInfo.IMAGE_SELECTED, SqlInfo.IMAGE_SET_BACKGROUND_UNSELECTED);
        try {
            sqLiteDatabase.update(SqlInfo.TABLE_IMAGE, contentValues,
                    SqlInfo.IMAGE_SELECTED + "=" + SqlInfo.IMAGE_SET_BACKGROUND_SELECTED, null);
            MyLog.i_chenji_log("changeSetBackground ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("changeSetBackground 失败 ", e);
        }

        contentValues = new ContentValues();
        contentValues.put(SqlInfo.IMAGE_SELECTED, SqlInfo.IMAGE_SET_BACKGROUND_SELECTED);
        try {
            sqLiteDatabase.update(SqlInfo.TABLE_IMAGE, contentValues,
                    SqlInfo.IMAGE_ORDER + "=" + newOrder, null);
            MyLog.i_chenji_log("changeSetBackground ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("changeSetBackground 失败 ", e);
        }
    }

    public void deleteSetBackground(int order) {
        try {
            sqLiteDatabase.delete(SqlInfo.TABLE_IMAGE, SqlInfo.IMAGE_ORDER + "=" + order, null);
            MyLog.i_chenji_log("deleteSetBackground ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("deleteSetBackground 失败 ", e);
        }
    }*/


}
