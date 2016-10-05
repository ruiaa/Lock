package com.chenji.lock.controller;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.chenji.lock.model.SqlInfo;

/**
 * Created by 志瑞 on 2015/12/23.
 */
public class Type {

    public static int getAppType(PackageManager packageManager,PackageInfo packageInfo){
        if (packageManager.getLaunchIntentForPackage(packageInfo.packageName)==null){
            //没有桌面图标
            return SqlInfo.APP_TYPE_USELESS;
        }else {
            return SqlInfo.APP_TYPE_USE;
        }
    }

}
