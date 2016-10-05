package com.chenji.lock.Util;

import android.util.Log;

/**
 * Created by 志瑞 on 2016/1/22.
 */
public class MyLog {
    private static final boolean SHOWLOG=true;
    private static final String TV="chenji___Verbose";
    private static final String TD="chenji___Debug";
    private static final String TI="chenji___Info";
    private static final String TW="chenji___Warn";
    private static final String TE="chenji___Error";
    private static final String TA="chenji___Assert";

    public static void v_chenji_log(String msg){
        if(SHOWLOG){
            Log.v(TV, msg);
        }
    }
    public static void v_chenji_log(String msg,Exception e){
        if(SHOWLOG){
            Log.v(TV, msg+e.getMessage());
        }
    }
    public static void d_chenji_log(String msg){
        if(SHOWLOG){
            Log.d(TD, msg);
        }
    }
    public static void d_chenji_log(String msg,Exception e){
        if(SHOWLOG){
            Log.d(TV, msg + e.getMessage());
        }
    }
    public static void i_chenji_log(String msg){
        if(SHOWLOG){
            Log.i(TI, msg);
        }
    }
    public static void i_chenji_log(String msg,Exception e){
        if(SHOWLOG){
            Log.i(TV, msg + e.getMessage());
        }
    }
    public static void w_chenji_log(String msg){
        if(SHOWLOG){
            Log.w(TW, msg);
        }
    }
    public static void w_chenji_log(String msg,Exception e){
        if(SHOWLOG){
            Log.w(TV, msg + e.getMessage());
        }
    }
    public static void e_chenji_log(String msg){
        if(SHOWLOG){
            Log.e(TE, msg);
        }
    }
    public static void e_chenji_log(String msg,Exception e){
        if(SHOWLOG){
            Log.e(TV, msg + e.getMessage());
        }
    }

}
