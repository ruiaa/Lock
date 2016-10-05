package com.chenji.lock.Util;

import android.app.Application;
import android.content.Context;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.chenji.lock.dataResolver.DataResolver;

/**
 * Created by 志瑞 on 2016/1/22.
 */


/*<application*/
/*android:name="com..MyApplication"*/
public class MyApplication extends Application {

    //主进程
    public static final String FILE_SHARED="lock";
    public static final String SET_EXIT="setExit";                    //true退出锁定，false退出不锁定
    public static final String SET_UPDATE="setUpdate";               //int
    public static final String SET_LANGUAGE="setLanguage";          //int
    public static final String SET_BACKGROUND_CHOSE="setBackgroundChose";      // 已选背景时间序号 or 0~5
    public static final int SET_BACKGROUND_WITH=6;                                //6张自带背景
    public static final String INTERCEPT_BACKGROUND_CHOSE="interBack";         //拦截界面
    public static final int INTERCEPT_BACKGROUND_WITH=3;
    public static final String INTERCEPT_NORMAL_SHOW_TIME="intertime";         //是否显示时间
    public static final String FIRST_ENTER="first_enter";            //true第一次启动，false不是第一次
    public static final String PASSWORD="wop";                         //密码
    public static final String PASSWORD_QUESTION="wopqu";
    public static final String PASSWORD_ANSWER="wopan";


    //后台进程
    public static final String FILE_CONTROL="CONTROL";
    public static final String DATE_TODAY_SQL="DATE_TODAY";     // 数据库中当天日期  启动时和0点监听 调用countTime修




    //共享
    public static final String OPEN_LOCK="openlock";              //是否开启拦截

    public static final String JUDGE_INTERVAL="judge";                         //拦截判断间隔 0.1~~~1
    public static final String INTERCEPT_INTERVAL="interval";                //拦截时间间隔
    public static final int INTERCEPT_MODE_FORCE=1;                               //0
    public static final int INTERCEPT_MODE_WARN=2;                                //60~~300

    public static final String LAUNCHER="launcher";

    public static final String SET_BOOT_COMPLETED="bootCompleted";        //true开机自启，，false开机不自启




    public static DataResolver dataResolver;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        dataResolver=new DataResolver();

        FeedbackAPI.initAnnoy(this, BaiCKey);
    }

    private static final String BaiCKey="23321475";

    public static String getBaiCKey() {
        return BaiCKey;
    }

    public static Context getContext(){
        return context;
    }

}

