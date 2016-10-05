package com.chenji.lock.model;

/**
 * Created by 志瑞 on 2015/12/22.
 */
public class SqlInfo {




    // 数据库版本号
    public static final int DATABASE_VERSION = 1;
    // 数据库名
    public static final String DATABASE_NAME = "AppInfo.db";


    // 数据表名
    public static final String TABLE_Info = "info";
    //appInfo列名
    //app属性 varChar blob
    public static final String APP_NAME = "aN";
    public static final String PACKAGE="package";
    public static final String VERSION_NAME = "vN";
    public static final String ICON = "icon";
    public static final String APP_TYPE = "Type";     //default 1
    public static final String INSTALL = "install";  //default 1安装 2卸载

    public static final int INSTALL_TRUE=1;
    public static final int INSTALL_FALSE=2;


    //app类型
    public static final int APP_TYPE_USELESS=0;
    public static final int APP_TYPE_USE = 1;

    //app使用时间 当天当前  integer default 0
    public static  String USAGE_TIME ;
    static {
        SqlInfo.USAGE_TIME="0";
    }


    //app的锁
    // 数据表名
    public static final String TABLE_Lock = "lock";
    //appInfo列名
    //主键 app
    //public static final String APP_NAME="aN";
    //时间排序
    public static final String LOCK_ORDER="lO";
    //锁类型 1禁用 2锁定
    public static final String LOCK_TYPE="lT";
    public static final int LOCK_TYPE_FORBIDDEN=1;
    public static final int LOCK_TYPE_USE=2;
    public static final int LOCK_TYPE_OVER_TIME=3;

    public static final String LOCK_OPEN="lopen";
    public static final int LOCK_OPEN_OPEN=1;
    public static final int LOCK_OPEN_CLOSE=0;
    //重复星期 1Mon 2Tues 3Wes 4Thurs 5Fri 6Sat 7Sun
    public static final String LOCK_WEEK="lW";
    public static final String LOCK_MONDAY="lMON";
    public static final String LOCK_TUESDAY="lTUE";
    public static final String LOCK_WEDNESDAY="lWED";
    public static final String LOCK_THURSDAY="lTHU";
    public static final String LOCK_FRIDAY="lFRI";
    public static final String LOCK_SATURDAY="lSAT";
    public static final String LOCK_SUNDAY="lSUN";
    //时间
    public static final String LOCK_START_TIME="lS";
    public static final String LOCK_FINISH_TIME="lF";



    /*//背景图
    // 数据表名
    public static final String TABLE_IMAGE="image";

    public static final String IMAGE_IMAGE="ima";
    public static final String IMAGE_ORDER="imaOrd";

    public static final String IMAGE_SELECTED="imaSel";
    public static final int IMAGE_SET_BACKGROUND_UNSELECTED=1;
    public static final int IMAGE_SET_BACKGROUND_SELECTED=11;
    public static final int IMAGE_INTERCEPT_BACKGROUND_UNSELECTED=2;
    public static final int IMAGE_INTERCEPT_BACKGROUND_SELECTED=21;*/





}
