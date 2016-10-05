package com.chenji.lock.dataResolver;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.AppInfo;
import com.chenji.lock.model.DataUri;
import com.chenji.lock.model.SqlInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by 志瑞 on 2016/3/3.
 */
public class DataResolver {

    public static int Update_Code = 1;
    public static boolean Exit_Lock = true;
    public static boolean Open_Lock=true;


    public static ContentResolver resolver = null;

    public DataResolver() {
        if (resolver == null) {
            resolver = MyApplication.getContext().getContentResolver();
        }
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);
        Update_Code = sharedPreferences.getInt(MyApplication.SET_UPDATE, 1);
        Exit_Lock = sharedPreferences.getBoolean(MyApplication.SET_EXIT, true);
/*        Open_Lock=sharedPreferences.getBoolean(MyApplication.OPEN_LOCK,true);*/
        //由EntryActivity初始化
    }

    //共享参数
    //拦截参数
    public void updateIntercept(float judgeInterval, int interceptInterval) {
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(MyApplication.JUDGE_INTERVAL, judgeInterval);
        contentValues.put(MyApplication.INTERCEPT_INTERVAL, interceptInterval);
        resolver.update(getUri(DataUri.INTERCEPT), contentValues, null, null);
    }

    //查询与更新 是否打开锁
    public void updateOpenLock() {
        if (resolver.delete(getUri(DataUri.OPEN_LOCK), null, null) == 1) {
            Open_Lock=true;
        } else {
            Open_Lock=false;
        }
        MyLog.i_chenji_log("updateOpenLock" + Open_Lock);
    }

    public void setOpenLock(boolean openLock) {
        Open_Lock=openLock;
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(DataUri.OPEN_LOCK, openLock);
        resolver.update(getUri(DataUri.OPEN_LOCK), contentValues, null, null);
    }

    //桌面
    public String getLauncher(){
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(
                MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);
        return sharedPreferences.getString(MyApplication.LAUNCHER,null);
    }

    public void setLauncher(String launcher){
        if (launcher==null||launcher.equals("")){

        }else {
            SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(
                    MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(MyApplication.LAUNCHER, launcher);
            editor.apply();

            ContentValues contentValues;
            contentValues = new ContentValues();
            contentValues.put(MyApplication.LAUNCHER,launcher);
            resolver.update(getUri(DataUri.LAUNCHER), contentValues, null, null);
        }
    }


    //开机自启
    public void setBOOT_COMPLETED(boolean boot_completed){
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(MyApplication.SET_BOOT_COMPLETED,boot_completed);
        resolver.update(getUri(DataUri.BOOT_COMPLETED), contentValues, null, null);
    }


    //获取是否处于前台
    public boolean getRunning() {
        if (resolver.delete(getUri(DataUri.RUNNING), null, null) == 1) {
            return false;
        } else {
            return true;
        }
    }








    //查询app
    public AppInfo getTheApp(String packageName) {
        AppInfo appInfo;
        String date = Util.dateForSqlToday();
        String[] strs = {SqlInfo.PACKAGE, SqlInfo.APP_NAME, SqlInfo.APP_TYPE, SqlInfo.ICON, date};
        try {
            Cursor cursor = resolver.query(getUri(DataUri.INFO), strs,
                    SqlInfo.PACKAGE + "='" + packageName + "'", null, null);

            if (cursor != null && cursor.moveToFirst()) {
                appInfo = new AppInfo(
                        cursor.getString(cursor.getColumnIndex(SqlInfo.PACKAGE)),
                        cursor.getString(cursor.getColumnIndex(SqlInfo.APP_NAME)),
                        cursor.getInt(cursor.getColumnIndex(SqlInfo.APP_TYPE)),
                        cursor.getBlob(cursor.getColumnIndex(SqlInfo.ICON)),
                        cursor.getInt(cursor.getColumnIndex(date)));
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
            Cursor cursor = resolver.query(getUri(DataUri.INFO), strs,
                    SqlInfo.PACKAGE + "='" + packageName + "'", null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Drawable drawable = Util.transferBytesAndDrawable(cursor.getBlob(cursor.getColumnIndex(SqlInfo.ICON)));
                cursor.close();
                return drawable;
            } else {
                cursor.close();
                MyLog.e_chenji_log("getIcon 读取失败 null");
                return null;
            }
        } catch (Exception e) {
            MyLog.e_chenji_log("getIcon 读取失败" + e);
            return null;
        }
    }

    public ArrayList<AppInfo> getAppInfo(int topNumber) {

        ArrayList<AppInfo> appInfos = new ArrayList<>();
        String[] strs = {SqlInfo.PACKAGE, SqlInfo.APP_NAME, SqlInfo.APP_TYPE, SqlInfo.ICON, Util.dateForSqlToday()};

        try {
            Cursor cursor = resolver.query(getUri(DataUri.INFO), strs,
                    SqlInfo.INSTALL + "=" + SqlInfo.INSTALL_TRUE + " and " +
                            SqlInfo.APP_TYPE + "=" + SqlInfo.APP_TYPE_USE,
                    null, Util.dateForSqlToday() + " desc");

            if (cursor != null && cursor.moveToFirst()) {
                int i = 0;
                do {
                    AppInfo appInfo = new AppInfo(
                            cursor.getString(cursor.getColumnIndex(SqlInfo.PACKAGE)),
                            cursor.getString(cursor.getColumnIndex(SqlInfo.APP_NAME)),
                            cursor.getInt(cursor.getColumnIndex(SqlInfo.APP_TYPE)),
                            cursor.getBlob(cursor.getColumnIndex(SqlInfo.ICON)),
                            cursor.getInt(cursor.getColumnIndex(Util.dateForSqlToday())));
                    appInfos.add(appInfo);
                    i = i + 1;
                } while (cursor.moveToNext() && i < topNumber);
            }
            MyLog.i_chenji_log("getAppInfo ok " + Util.dateForSqlToday());
            cursor.close();
        } catch (Exception e) {
            MyLog.e_chenji_log("getAppInfo 读取失败" + e.toString());
        }

        return appInfos;
    }

    public ArrayList<AppInfo> getAppInfo(int topNumber, String dateForSql) {

        ArrayList<AppInfo> appInfos = new ArrayList<>();
        String[] strs = {SqlInfo.PACKAGE, SqlInfo.APP_NAME, SqlInfo.APP_TYPE, SqlInfo.ICON, dateForSql};

        try {
            Cursor cursor = resolver.query(getUri(DataUri.INFO), strs,
                    SqlInfo.APP_TYPE + "=" + SqlInfo.APP_TYPE_USE + " and " +
                            dateForSql + ">0",
                    null, dateForSql + " desc");

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

                    /*MyLog.i_chenji_log(appInfo.appName+appInfo.usageTime);*/
                } while (cursor.moveToNext() && i < topNumber);
            }
            MyLog.i_chenji_log("getAppInfo ok " + dateForSql);
            cursor.close();
        } catch (Exception e) {
            MyLog.e_chenji_log("getAppInfo 读取失败" + e.toString());
        }

        return appInfos;
    }

    public ArrayList<String> getDate() {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Cursor cursor = resolver.query(getUri(DataUri.INFO), null,
                    SqlInfo.PACKAGE + "=" + "'com.chenji.lock'",
                    null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String[] strings = cursor.getColumnNames();
                for (String s : strings) {
                    if (s.charAt(0) == 's') {
                        arrayList.add(s);
                    }
                }
                cursor.close();
                MyLog.i_chenji_log("getdate ok");
                return arrayList;
            } else {
                cursor.close();
                MyLog.e_chenji_log("getdate 读取失败");
                return arrayList;
            }
        } catch (Exception e) {
            MyLog.e_chenji_log("getdate 读取失败" + e.getMessage());
            return arrayList;
        }
    }

    public ArrayList<HashMap<String, Float>> getTheAppUsage(String packageName) {
        ArrayList<HashMap<String, Float>> arrayList = new ArrayList<>();
        try {
            Cursor cursor = resolver.query(getUri(DataUri.INFO), null,
                    SqlInfo.PACKAGE + "='" + packageName + "'",
                    null, null);

            if (cursor != null && cursor.moveToFirst()) {
                HashMap<String, Float> hashMap;
                String date;
                int usage;
                String[] strings = cursor.getColumnNames();
                for (String s : strings) {
                    if (s.charAt(0) == 's') {
                        hashMap = new HashMap<>();
                        date = Util.getDateOfMonthFromDateForSql(s);
                        usage = cursor.getInt(cursor.getColumnIndex(s));
                        hashMap.put(date, Util.getTimeOfHourFromSecond(usage));
                        arrayList.add(hashMap);
                    }
                }
                cursor.close();
                MyLog.i_chenji_log("getTheAppUsage ok");
                return arrayList;
            } else {
                cursor.close();
                MyLog.e_chenji_log("getTheAppUsage 读取失败");
                return arrayList;
            }
        } catch (Exception e) {
            MyLog.e_chenji_log("getTheAppUsage 读取失败" + e.getMessage());
            return arrayList;
        }
    }

    public ArrayList<Integer> getTimeOver(String packageName) {
        String week = Util.getWeekForSql();
        String date = Util.dateForSqlToday();
        String[] strs = {date, week, SqlInfo.LOCK_OPEN};
        try {
            Cursor cursor = resolver.query(getUri(DataUri.INFO), strs,
                    SqlInfo.PACKAGE + "='" + packageName + "'", null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int time = cursor.getInt(cursor.getColumnIndex(week));
                int use = cursor.getInt(cursor.getColumnIndex(date));
                int open = cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_OPEN));
                cursor.close();
                ArrayList<Integer> integers = new ArrayList<>();
                integers.add(time);
                integers.add(use);
                integers.add(open);
                return integers;
            } else {
                cursor.close();
                return null;
            }
        } catch (Exception e) {
            MyLog.e_chenji_log("getIcon 读取失败" + e);
            return null;
        }
    }

    public ArrayList<Integer> getAllTimeOver(String packageName) {
        String[] strs = {SqlInfo.LOCK_MONDAY, SqlInfo.LOCK_TUESDAY, SqlInfo.LOCK_WEDNESDAY,
                SqlInfo.LOCK_THURSDAY, SqlInfo.LOCK_FRIDAY, SqlInfo.LOCK_SATURDAY, SqlInfo.LOCK_SUNDAY};
        try {
            Cursor cursor = resolver.query(getUri(DataUri.INFO), strs,
                    SqlInfo.PACKAGE + "='" + packageName + "'", null, null);

            if (cursor != null && cursor.moveToFirst()) {
                ArrayList<Integer> integers = new ArrayList<>();
                integers.add(cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_MONDAY)));
                integers.add(cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_TUESDAY)));
                integers.add(cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_WEDNESDAY)));
                integers.add(cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_THURSDAY)));
                integers.add(cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_FRIDAY)));
                integers.add(cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_SATURDAY)));
                integers.add(cursor.getInt(cursor.getColumnIndex(SqlInfo.LOCK_SUNDAY)));
                cursor.close();
                return integers;
            } else {
                cursor.close();
                return null;
            }
        } catch (Exception e) {
            MyLog.e_chenji_log("getIcon 读取失败" + e);
            return null;
        }
    }

    public void updateOverTimeOpen(String packageName, int open) {
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(SqlInfo.LOCK_OPEN, open);
        try {
            resolver.update(getUri(DataUri.INFO), contentValues, SqlInfo.PACKAGE + "='" + packageName + "'", null);
            MyLog.i_chenji_log("updateOverTimeOpen ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("updateOverTimeOpen 失败", e);
        }
    }

    public void updateOverTime(String weekForSql, String packageName, int time) {
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(weekForSql, time);
        contentValues.put(SqlInfo.LOCK_OPEN, SqlInfo.LOCK_OPEN_OPEN);
        try {
            resolver.update(getUri(DataUri.INFO), contentValues, SqlInfo.PACKAGE + "='" + packageName + "'", null);
            MyLog.i_chenji_log("updateOverTime ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("updateOverTime 失败", e);
        }
    }









    //查询锁
    public ArrayList<HashMap<String, Object>> getLock(String packageName) {
        String[] strings = {SqlInfo.LOCK_TYPE, SqlInfo.LOCK_ORDER,
                SqlInfo.LOCK_MONDAY, SqlInfo.LOCK_TUESDAY, SqlInfo.LOCK_WEDNESDAY,
                SqlInfo.LOCK_THURSDAY, SqlInfo.LOCK_FRIDAY, SqlInfo.LOCK_SATURDAY, SqlInfo.LOCK_SUNDAY,
                SqlInfo.LOCK_START_TIME, SqlInfo.LOCK_FINISH_TIME};
        ArrayList<HashMap<String, Object>> lockTimeList = new ArrayList<HashMap<String, Object>>();
        try {
            Cursor cursor = resolver.query(getUri(DataUri.LOCK), strings,
                    SqlInfo.PACKAGE + "='" + packageName + "'", null, SqlInfo.LOCK_ORDER + " asc");
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
            Cursor cursorForbidden = resolver.query(getUri(DataUri.LOCK), strings,
                    SqlInfo.LOCK_TYPE + "=" + SqlInfo.LOCK_TYPE_FORBIDDEN + " and " +
                            SqlInfo.LOCK_START_TIME + " < " + currentTime + " and " +
                            SqlInfo.LOCK_FINISH_TIME + " > " + currentTime + " and " +
                            "(" + currentWeek + "=1 " + " or " + SqlInfo.LOCK_WEEK + "=0 )",
                    null, SqlInfo.LOCK_ORDER + " desc");

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
            Cursor cursorUse = resolver.query(getUri(DataUri.LOCK), strings,
                    SqlInfo.LOCK_TYPE + "=" + SqlInfo.LOCK_TYPE_USE + " and " +
                            SqlInfo.LOCK_START_TIME + " < " + currentTime + " and " +
                            SqlInfo.LOCK_FINISH_TIME + " > " + currentTime + " and " +
                            "(" + currentWeek + "=1 " + " or " + SqlInfo.LOCK_WEEK + "=0 )",
                    null, SqlInfo.LOCK_ORDER + " desc");

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
        for (String packageName : getCurrentUseLockList()) {
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
                Cursor cursorUse = resolver.query(getUri(DataUri.LOCK), strings,
                        SqlInfo.PACKAGE + "='" + packageName + "'" + " and " +
                                SqlInfo.LOCK_TYPE + "=" + SqlInfo.LOCK_TYPE_USE + " and " +
                                SqlInfo.LOCK_START_TIME + " < " + currentTime + " and " +
                                SqlInfo.LOCK_FINISH_TIME + " > " + currentTime + " and " +
                                "(" + currentWeek + "=1 " + " or " + SqlInfo.LOCK_WEEK + "=0 )",
                        null, SqlInfo.LOCK_FINISH_TIME + " desc");
                if (cursorUse != null && cursorUse.moveToFirst()) {
                    startTime = cursorUse.getInt(cursorUse.getColumnIndex(SqlInfo.LOCK_START_TIME));
                    finishTime = cursorUse.getInt(cursorUse.getColumnIndex(SqlInfo.LOCK_FINISH_TIME));
                    order = cursorUse.getInt(cursorUse.getColumnIndex(SqlInfo.LOCK_ORDER));
                }
                cursorUse.close();
            } else if (lockType == SqlInfo.LOCK_TYPE_FORBIDDEN) {
                Cursor cursorForbidden = resolver.query(getUri(DataUri.LOCK), strings,
                        SqlInfo.PACKAGE + "='" + packageName + "'" + " and " +
                                SqlInfo.LOCK_TYPE + "=" + SqlInfo.LOCK_TYPE_FORBIDDEN + "  and " +
                                SqlInfo.LOCK_START_TIME + " < " + currentTime + " and " +
                                SqlInfo.LOCK_FINISH_TIME + " > " + currentTime + " and " +
                                "(" + currentWeek + "=1 " + " or " + SqlInfo.LOCK_WEEK + "=0 )",
                        null, SqlInfo.LOCK_FINISH_TIME + " desc");
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
            resolver.insert(getUri(DataUri.LOCK), contentValues);
            MyLog.i_chenji_log("添加锁 ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("添加锁 失败 ", e);
        }
    }

    public void deleteLock(int order) {
        try {
            resolver.delete(getUri(DataUri.LOCK), SqlInfo.LOCK_ORDER + "=" + order, null);
            MyLog.i_chenji_log("删除锁 ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("删除锁 失败 ", e);
        }
    }

    public void updateLockOpen(int order, int open) {
        ContentValues contentValues;
        contentValues = new ContentValues();
        contentValues.put(SqlInfo.LOCK_OPEN, open);
        try {
            resolver.update(getUri(DataUri.LOCK), contentValues, SqlInfo.LOCK_ORDER + "=" + order, null);
            MyLog.i_chenji_log("updateLockOpen ok");
        } catch (Exception e) {
            MyLog.e_chenji_log("updateLockOpen 失败", e);
        }
    }

    public static Uri getUri(String lastUri) {
        return Uri.parse(DataUri.CONTENT_URI + lastUri);
    }

}
