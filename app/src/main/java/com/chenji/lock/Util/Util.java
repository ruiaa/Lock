package com.chenji.lock.Util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.chenji.lock.R;
import com.chenji.lock.model.SqlInfo;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 志瑞 on 2016/1/29.
 */
public class Util {

    /* public static final String YEAR = "year";
     public static final String MONTH = "month";
     public static final String DAY = "day";*/

    /*$$$*/
    /*$$$*/
    //$$$  获取系统资源
    /*$$$*/

    public static String getString(int stringFromR) {
        return MyApplication.getContext().getResources().getString(stringFromR);
    }

    public static int getColor(int colorFromR) {
        if (Build.VERSION.SDK_INT >= 23) {
            return MyApplication.getContext().getResources().getColor(colorFromR, null);
        } else {
            return MyApplication.getContext().getResources().getColor(colorFromR);
        }
    }

    public static int getColor(int colorFromR, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= 23) {
            return MyApplication.getContext().getResources().getColor(colorFromR, theme);
        } else {
            return MyApplication.getContext().getResources().getColor(colorFromR);
        }
    }

    public static Drawable getDrawable(int drawableFromR) {
        if (Build.VERSION.SDK_INT >= 21) {
            return MyApplication.getContext().getResources().getDrawable(drawableFromR, null);
        } else {
            return MyApplication.getContext().getResources().getDrawable(drawableFromR);
        }
    }

    public static Drawable getDrawable(int drawableFromR, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= 21) {
            return MyApplication.getContext().getResources().getDrawable(drawableFromR, theme);
        } else {
            return MyApplication.getContext().getResources().getDrawable(drawableFromR);
        }
    }

    public static float getDimen(int dimenFromR) {
        return MyApplication.getContext().getResources().getDimension(dimenFromR);
    }

    public static void backHome(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    /*$$$*/
    /*$$$*/
    //$$$  格式转换
    /*$$$*/

    //用于数据库列名的日期
    public static String dateForSql(int year, int month_StartForm1, int day) {
        if (day < 10) {
            if (month_StartForm1 < 10) {
                return "sql" + String.valueOf(year) +
                        String.valueOf(0) + String.valueOf(month_StartForm1) +
                        String.valueOf(0) + String.valueOf(day);
            } else {
                return "sql" + String.valueOf(year) +
                        String.valueOf(month_StartForm1) +
                        String.valueOf(0) + String.valueOf(day);
            }
        } else {
            if (month_StartForm1 < 10) {
                return "sql" + String.valueOf(year) +
                        String.valueOf(0) + String.valueOf(month_StartForm1) +
                        String.valueOf(day);
            } else {
                return "sql" + String.valueOf(year) +
                        String.valueOf(month_StartForm1) +
                        String.valueOf(day);
            }
        }
    }

    public static String dateForSql(Calendar calendar) {
        return Util.dateForSql(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String getDateFromDateForSql(String dateForSql) {
        int length = dateForSql.length();
        return dateForSql.substring(3, length - 4) + "年" +
                dateForSql.substring(length - 4, length - 2) + "月" +
                dateForSql.substring(length - 2) + "日";
    }

    public static String getDateOfMonthFromDateForSql(String dateForSql) {
        int length = dateForSql.length();
        return dateForSql.substring(length - 4, length - 2) + "/" +
                dateForSql.substring(length - 2);
    }

    public static String dateForSqlToday() {
        Calendar calendar = Calendar.getInstance();
        return Util.dateForSql(calendar);
    }

    public static String getWeekForSql() {
        String currentWeek;

        Calendar calendar;
        calendar = Calendar.getInstance();
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
        return currentWeek;
    }

    //用于数据库的24小时时间
    public static String getTimeFromLockTimeInSql(int hour,int minute) {
        String h;
        String m;
        if (hour<=9){
            h="0"+hour;
        }else {
            h=""+hour;
        }
        if(minute<=9){
            m="0"+minute;
        }else {
            m=""+minute;
        }
        return h + ":" + m;
    }

    public static String getTimeFromLockTimeInSql(int lockTimeInSql_second){
        int hour=lockTimeInSql_second / 60;
        int minute=lockTimeInSql_second % 60;
        return getTimeFromLockTimeInSql(hour,minute);
    }

    public static String getMinuteTimeFromSecondTimeInSql(int secondTime) {
        if (secondTime == 0) {
            return "0" + MyApplication.getContext().getResources().getString(R.string.minute);
        } else if (secondTime < 60) {
            return ">1" + MyApplication.getContext().getResources().getString(R.string.minute);
        } else if (secondTime > 3660) {
            return String.valueOf(secondTime / 3600) + MyApplication.getContext().getResources().getString(R.string.hour) +
                    String.valueOf((secondTime % 3600) / 60) + MyApplication.getContext().getResources().getString(R.string.minute);
        } else {
            return String.valueOf(secondTime / 60) + MyApplication.getContext().getResources().getString(R.string.minute);
        }
    }

    public static float getTimeOfHourFromSecond(int timeOfSecond) {
        int t = timeOfSecond / 36;
        if (t == 0) {
            return 0.01f;
        } else {
            return ((float) t) / 100;
        }
    }

    public static int getNowSecond(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return hour*3600+minute*60+second;
    }

    //图片与字节数组
    public static Drawable transferBytesAndDrawable(byte[] bytes) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return new BitmapDrawable(MyApplication.getContext().getResources(), bitmap);
        } catch (Exception e) {
            MyLog.e_chenji_log("blob转图片失败", e);
            return null;
        }
    }

    public static byte[] transferBytesAndDrawable(BitmapDrawable bitmapDrawable) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            MyLog.e_chenji_log("图片转bytes存储失败");
            return null;
        }
    }

    //语言设置与代号
    public static String transferLanguageSet(int languageCode) {
        switch (languageCode) {
            case 0:
                return getString(R.string.language_chinese);
            case 1:
                return getString(R.string.language_english);
            case 2:
                return getString(R.string.language_japanese);
            default:
                return null;
        }
    }

    //更新设置与代号
    public static String transferUpdateSet(int updateCode) {
        switch (updateCode) {
            case 0:
                return getString(R.string.set_update_warn);
            case 1:
                return getString(R.string.set_update_download);
            default:
                return null;
        }
    }

    //dp,px,sp
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }




    /*$$$*/
    /*$$$*/
    //$$$  图片储存
    /*$$$*/

    public static Drawable getImage(String imageOrder, int scale) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(MyApplication.getContext().getFilesDir().getPath() + "/" + imageOrder, options);
            Drawable drawable = new BitmapDrawable(MyApplication.getContext().getResources(), bitmap);
            bitmap = null;
            MyLog.i_chenji_log("getImage  ok");
            return drawable;
        } catch (Exception e) {
            MyLog.e_chenji_log("getImage  失败", e);
            return null;
        }
    }

    public static boolean addImage(String newImageFile, String imageOrder) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(newImageFile);
            FileOutputStream fileOutputStream = MyApplication.getContext().openFileOutput(imageOrder, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            MyLog.i_chenji_log("addImage  ok " + bitmap.getByteCount());
            bitmap.recycle();
            return true;
        } catch (Exception e) {
            MyLog.e_chenji_log("addImage  失败", e);
            return false;
        }
 /*public static boolean addImage(Bitmap bitmap,String order) {
        try {
            FileOutputStream fileOutputStream=MyApplication.getContext().openFileOutput(order, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            MyLog.i_chenji_log("addImage  ok");
            return true;
        }catch (Exception e){
            MyLog.e_chenji_log("addImage  失败",e);
            return false;
        }

    }*/
    }

    public static void deleteImage(String imageOrder) {
        try {
            MyApplication.getContext().deleteFile(imageOrder);
        } catch (Exception e) {
            MyLog.e_chenji_log("deleteImage", e);
        }

    }

    //全局背景
    public static String getTimeOrderForBackground() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return "b" + year + day + hour + minute + second;
    }

    public static Drawable getBackground() {
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);
        String order = sharedPreferences.getString(MyApplication.SET_BACKGROUND_CHOSE, "0");
        return getBackground(order);
    }

    public static Drawable getBackground(String order) {
        if (order.equals("0")) {
            return getDrawable(R.drawable.set_background_0);
        } else if (order.equals("1")) {
            return getDrawable(R.drawable.set_background_1);
        } else if (order.equals("2")) {
            return getDrawable(R.drawable.set_background_2);
        } else if (order.equals("3")) {
            return getDrawable(R.drawable.set_background_3);
        } else if (order.equals("4")) {
            return getDrawable(R.drawable.set_background_4);
        } else if (order.equals("5")) {
            return getDrawable(R.drawable.set_background_5);
        } else {
            return getImage(order, 1);
        }
    }

    //拦截背景
    public static String getTimeOrderForIntercept() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return "i" + year + day + hour + minute + second;
    }

    public static Drawable getInterceptBackground() {
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);
        String order = sharedPreferences.getString(MyApplication.INTERCEPT_BACKGROUND_CHOSE, "0");
        return getInterceptBackground(order);
    }

    public static String getInterceptBackgroundOrder(){
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);
        return sharedPreferences.getString(MyApplication.INTERCEPT_BACKGROUND_CHOSE, "0");
    }

    public static boolean getInterceptNormalShowTime(){
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences(MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean(MyApplication.INTERCEPT_NORMAL_SHOW_TIME, true);
    }

    public static Drawable getInterceptBackground(String order) {
        if (order.equals("0")) {
            return getDrawable(R.drawable.set_background_0);
        } else if (order.equals("1")) {
            return getDrawable(R.drawable.set_background_1);
        } else if (order.equals("2")) {
            return getDrawable(R.drawable.set_background_2);
        } else {
            return getImage(order, 1);
        }
    }


    //前台应用检测
    /*
     * AndroidManifest中加入此权限<uses-permission
      * xmlns:tools="http://schemas.android.com/tools"
      * android:name="android.permission.PACKAGE_USAGE_STATS"
     * tools:ignore="ProtectedPermissions" />
     * 打开手机设置，点击安全-高级，在有权查看使用情况的应用中，为这个App打上勾
     *
     */

/*    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean queryUsageStats(Context context, String packageName) {
        class RecentUseComparator implements Comparator<UsageStats> {
            @Override
            public int compare(UsageStats lhs, UsageStats rhs) {
                return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : (lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1;
            }
        }
        RecentUseComparator mRecentComp = new RecentUseComparator();
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 1000 * 10, ts);
        if (usageStats == null || usageStats.size() == 0) {
            if (havePermission(context) == false) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Toast.makeText(context, "权限不够\n请打开手机设置，点击安全-高级，在有权查看使用情况的应用中，为这个App打上勾", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        Collections.sort(usageStats, mRecentComp);
        String currentTopPackage = usageStats.get(0).getPackageName();
        if (currentTopPackage.equals(packageName)) {
            return true;
        } else {
            return false;
        }
    }*/

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Map<String, Integer> getTodayTimeMap() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long zoneTime = calendar.getTimeInMillis();
        long ts = System.currentTimeMillis();
        String packageName;
        int usageTime;

        UsageStatsManager usageStatsManager = (UsageStatsManager) MyApplication.getContext().getSystemService("usagestats");
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, zoneTime, ts);

        try {
            if (queryUsageStats == null || queryUsageStats.isEmpty()) {
                return null;
            } else {
                Map<String, Integer> timeMap = new HashMap<>();
                for (UsageStats usageStats : queryUsageStats) {
                    packageName = usageStats.getPackageName();
                    usageTime = (int) (usageStats.getTotalTimeInForeground() / 1000);
                    if (timeMap.containsKey(packageName)) {
                        usageTime = timeMap.get(packageName) + usageTime;
                    }
                    timeMap.put(packageName, usageTime);
                    MyLog.i_chenji_log(packageName + "  " + usageTime);
                }
                return timeMap;
            }
        } catch (Exception e) {
            MyLog.e_chenji_log("getTodayTimeMap ", e);
            return null;
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String getForegroundApp(Context context) {

        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");

        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 100, ts);

        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        } else {

            UsageStats recentStats = null;

            for (UsageStats usageStats : queryUsageStats) {

                MyLog.i_chenji_log(usageStats.getPackageName());

                if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                    recentStats = usageStats;
                }
            }
            return recentStats.getPackageName();
        }
    }

    /**
     * 判断是否有用权限
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean havePermission(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            MyLog.i_chenji_log("check havePermission  ok");
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            MyLog.e_chenji_log("check  havePermission");
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean noRunningOnStop(Context context) {

        if (Build.VERSION.SDK_INT >= 21) {
            String s = getForegroundApp(context);
            if (s == null || s.equals("com.chenji.lock")) {
                return false;
            }
        } else {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            String s = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
            if (s.equals("com.chenji.lock")) {
                return false;
            }
        }
        return true;
    }

    public static String getAppName(String packageName) {

        try {
            String appName = MyApplication.getContext().getPackageManager().getPackageInfo(packageName, android.content.pm.PackageManager.GET_INSTRUMENTATION).
                    applicationInfo.loadLabel(MyApplication.getContext().getPackageManager()).toString();
            return appName;
        } catch (Exception e) {
            MyLog.e_chenji_log("getAppName", e);
            return null;
        }
    }




   /* public static HashMap<String, Integer> nextDate(int year, int month_StartForm1, int day) {
        HashMap<String, Integer> date = new HashMap<>();
        switch (month_StartForm1) {
            case 1: {
                if (day == 31) {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1 + 1);
                    date.put(Util.DAY, 1);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day + 1);
                }
                break;
            }
            case 2: {
                if ((year % 4) == 0) {
                    if (day == 29) {
                        date.put(Util.YEAR, year);
                        date.put(Util.MONTH, month_StartForm1 + 1);
                        date.put(Util.DAY, 1);
                    } else {
                        date.put(Util.YEAR, year);
                        date.put(Util.MONTH, month_StartForm1);
                        date.put(Util.DAY, day + 1);
                    }
                } else {
                    if (day == 28) {
                        date.put(Util.YEAR, year);
                        date.put(Util.MONTH, month_StartForm1 + 1);
                        date.put(Util.DAY, 1);
                    } else {
                        date.put(Util.YEAR, year);
                        date.put(Util.MONTH, month_StartForm1);
                        date.put(Util.DAY, day + 1);
                    }
                }
                break;
            }
            case 3: {
                if (day == 31) {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1 + 1);
                    date.put(Util.DAY, 1);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day + 1);
                }
                break;
            }
            case 4: {
                if (day == 30) {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1 + 1);
                    date.put(Util.DAY, 1);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day + 1);
                }
                break;
            }
            case 5: {
                if (day == 31) {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1 + 1);
                    date.put(Util.DAY, 1);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day + 1);
                }
                break;
            }
            case 6: {
                if (day == 30) {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1 + 1);
                    date.put(Util.DAY, 1);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day + 1);
                }
                break;
            }
            case 7: {
                if (day == 31) {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1 + 1);
                    date.put(Util.DAY, 1);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day + 1);
                }
                break;
            }
            case 8: {
                if (day == 31) {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1 + 1);
                    date.put(Util.DAY, 1);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day + 1);
                }
                break;
            }
            case 9: {
                if (day == 30) {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1 + 1);
                    date.put(Util.DAY, 1);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day + 1);
                }
                break;
            }
            case 10: {
                if (day == 31) {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1 + 1);
                    date.put(Util.DAY, 1);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day + 1);
                }
                break;
            }
            case 11: {
                if (day == 30) {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1 + 1);
                    date.put(Util.DAY, 1);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day + 1);
                }
                break;
            }
            default: {
                if (day == 31) {
                    date.put(Util.YEAR, year + 1);
                    date.put(Util.MONTH, 1);
                    date.put(Util.DAY, 1);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day + 1);
                }
                break;
            }
        }
        return date;
    }

    public static String nextDateForSql(int year, int month_StartForm1, int day) {
        switch (month_StartForm1) {
            case 1: {
                if (day == 31) {
                    return Util.dateForSql(year, month_StartForm1 + 1, 1);
                } else {
                    return Util.dateForSql(year, month_StartForm1, day + 1);
                }
            }
            case 2: {
                if ((year % 4) == 0) {
                    if (day == 29) {
                        return Util.dateForSql(year, month_StartForm1 + 1, 1);
                    } else {
                        return Util.dateForSql(year, month_StartForm1, day + 1);
                    }
                } else {
                    if (day == 28) {
                        return Util.dateForSql(year, month_StartForm1 + 1, 1);
                    } else {
                        return Util.dateForSql(year, month_StartForm1, day + 1);
                    }
                }
            }
            case 3: {
                if (day == 31) {
                    return Util.dateForSql(year, month_StartForm1 + 1, 1);
                } else {
                    return Util.dateForSql(year, month_StartForm1, day + 1);
                }
            }
            case 4: {
                if (day == 30) {
                    return Util.dateForSql(year, month_StartForm1 + 1, 1);
                } else {
                    return Util.dateForSql(year, month_StartForm1, day + 1);
                }
            }
            case 5: {
                if (day == 31) {
                    return Util.dateForSql(year, month_StartForm1 + 1, 1);
                } else {
                    return Util.dateForSql(year, month_StartForm1, day + 1);
                }
            }
            case 6: {
                if (day == 30) {
                    return Util.dateForSql(year, month_StartForm1 + 1, 1);
                } else {
                    return Util.dateForSql(year, month_StartForm1, day + 1);
                }
            }
            case 7: {
                if (day == 31) {
                    return Util.dateForSql(year, month_StartForm1 + 1, 1);
                } else {
                    return Util.dateForSql(year, month_StartForm1, day + 1);
                }
            }
            case 8: {
                if (day == 31) {
                    return Util.dateForSql(year, month_StartForm1 + 1, 1);
                } else {
                    return Util.dateForSql(year, month_StartForm1, day + 1);
                }
            }
            case 9: {
                if (day == 30) {
                    return Util.dateForSql(year, month_StartForm1 + 1, 1);
                } else {
                    return Util.dateForSql(year, month_StartForm1, day + 1);
                }
            }
            case 10: {
                if (day == 31) {
                    return Util.dateForSql(year, month_StartForm1 + 1, 1);
                } else {
                    return Util.dateForSql(year, month_StartForm1, day + 1);
                }
            }
            case 11: {
                if (day == 30) {
                    return Util.dateForSql(year, month_StartForm1 + 1, 1);
                } else {
                    return Util.dateForSql(year, month_StartForm1, day + 1);
                }
            }
            default: {
                if (day == 31) {
                    return Util.dateForSql(year + 1, 1, 1);
                } else {
                    return Util.dateForSql(year, month_StartForm1, day + 1);
                }
            }
        }
    }

    public static String nextDateForSql(String thisDateForSql){
        int year;
        int month_StartForm1;
        int day;
        char[] chars=thisDateForSql.toCharArray();
        int lengthOfChars=chars.length;
        try {
            day =transferChar(chars[lengthOfChars-1])+
                    transferChar(chars[lengthOfChars-2])*10;

            month_StartForm1=transferChar(chars[lengthOfChars-3])+
                    transferChar(chars[lengthOfChars-4])*10;

            year=transferChar(chars[lengthOfChars-5]);
            if ((chars[lengthOfChars-6])!=10){
                year=year+(chars[lengthOfChars-6])*10;
            }
            if ((chars[lengthOfChars-7])!=10){
                year=year+(chars[lengthOfChars-7])*100;
            }
            if ((chars[lengthOfChars-8])!=10){
                year=year+(chars[lengthOfChars-8])*100;
            }
            return nextDateForSql(year,month_StartForm1,day);
        }catch (Exception e){
            return null;
        }
    }

    public static HashMap<String, Integer> lastDate(int year, int month_StartForm1, int day) {
        HashMap<String, Integer> date = new HashMap<>();
        switch (month_StartForm1) {
            case 1: {
                if (day == 1) {
                    date.put(Util.YEAR, year - 1);
                    date.put(Util.MONTH, 12);
                    date.put(Util.DAY, 31);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day - 1);
                }
                break;
            }

            case 3: {
                if ((year % 4) == 0) {
                    if (day == 1) {
                        date.put(Util.YEAR, year);
                        date.put(Util.MONTH, month_StartForm1 - 1);
                        date.put(Util.DAY, 29);
                    } else {
                        date.put(Util.YEAR, year);
                        date.put(Util.MONTH, month_StartForm1);
                        date.put(Util.DAY, day - 1);
                    }
                } else {
                    if (day == 1) {
                        date.put(Util.YEAR, year);
                        date.put(Util.MONTH, month_StartForm1 - 1);
                        date.put(Util.DAY, 28);
                    } else {
                        date.put(Util.YEAR, year);
                        date.put(Util.MONTH, month_StartForm1);
                        date.put(Util.DAY, day - 1);
                    }
                }
                break;
            }

            case 2:
            case 4:
            case 6:
            case 8:
            case 9:
            case 11:{
                if (day == 1) {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1 - 1);
                    date.put(Util.DAY, 31);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day - 1);
                }
                break;
            }

            case 5:
            case 7:
            case 10:
            case 12:{
                if (day == 1) {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1 - 1);
                    date.put(Util.DAY, 30);
                } else {
                    date.put(Util.YEAR, year);
                    date.put(Util.MONTH, month_StartForm1);
                    date.put(Util.DAY, day - 1);
                }
                break;
            }

            default:break;
        }
        return date;
    }

    public static String lastDateForSql(int year, int month_StartForm1, int day){
        switch (month_StartForm1) {
            case 1: {
                if (day == 1) {
                    return Util.dateForSql(year-1,12,31);
                } else {
                    return Util.dateForSql(year,month_StartForm1,day-1);
                }
            }

            case 3: {
                if ((year % 4) == 0) {
                    if (day == 1) {
                        return Util.dateForSql(year,month_StartForm1-1,29);
                    } else {
                        return Util.dateForSql(year,month_StartForm1,day-1);
                    }
                } else {
                    if (day == 1) {
                        return Util.dateForSql(year,month_StartForm1-1,28);
                    } else {
                        return Util.dateForSql(year,month_StartForm1,day-1);
                    }
                }
            }

            case 2:
            case 4:
            case 6:
            case 8:
            case 9:
            case 11:{
                if (day == 1) {
                    return Util.dateForSql(year,month_StartForm1-1,31);
                } else {
                    return Util.dateForSql(year,month_StartForm1,day-1);
                }
            }

            case 5:
            case 7:
            case 10:
            case 12:{
                if (day == 1) {
                    return Util.dateForSql(year,month_StartForm1-1,30);
                } else {
                    return Util.dateForSql(year,month_StartForm1,day-1);
                }
            }

            default:return null;
        }
    }

    public static String lastDateForSql(String thisDateForSql){
        int year;
        int month_StartForm1;
        int day;
        char[] chars=thisDateForSql.toCharArray();
        int lengthOfChars=chars.length;
        try {
            day =transferChar(chars[lengthOfChars-1])+
                    transferChar(chars[lengthOfChars-2])*10;

            month_StartForm1=transferChar(chars[lengthOfChars-3])+
                    transferChar(chars[lengthOfChars-4])*10;

            year=transferChar(chars[lengthOfChars-5]);
            if ((chars[lengthOfChars-6])!=10){
                year=year+(chars[lengthOfChars-6])*10;
            }
            if ((chars[lengthOfChars-7])!=10){
                year=year+(chars[lengthOfChars-7])*100;
            }
            if ((chars[lengthOfChars-8])!=10){
                year=year+(chars[lengthOfChars-8])*100;
            }
            return lastDateForSql(year,month_StartForm1,day);
        }catch (Exception e){
            return null;
        }
    }*/


}
