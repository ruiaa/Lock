package com.chenji.lock.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;

/**
 * Created by 志瑞 on 2016/1/22.
 */
public class AppInfo {

    public String packageName;
    public String appName;
    public int appType;
    public Drawable appIcon;
    public int usageTime;

    public AppInfo() {
        this.appName = null;
    }

    public AppInfo(String packageName,String appName, int appType, byte[] appIcon, int usageTime) {
        this.packageName=packageName;
        this.appName = appName;
        this.appType = appType;
        this.usageTime = usageTime;

        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(appIcon, 0, appIcon.length);
            this.appIcon = new BitmapDrawable(MyApplication.getContext().getResources(), bitmap);
        } catch (Exception e) {
            this.appIcon = null;
            MyLog.e_chenji_log("new AppInfo blob转图片失败", e);
        }

    }

    public static String minuteTime(int secondTime) {
        if (secondTime == 0) {
            return "0"+ MyApplication.getContext().getResources().getString(R.string.minute);
        } else if (secondTime < 60) {
            return ">1" + MyApplication.getContext().getResources().getString(R.string.minute);
        } else if (secondTime > 3660) {
            return String.valueOf(secondTime / 3600) + MyApplication.getContext().getResources().getString(R.string.hour) +
                    String.valueOf((secondTime % 3600) / 60) + MyApplication.getContext().getResources().getString(R.string.minute);
        } else {
            return String.valueOf(secondTime / 60) + MyApplication.getContext().getResources().getString(R.string.minute);
        }
    }
}
