package com.chenji.lock.view.intercept.intercept;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.SqlInfo;
import com.chenji.lock.view.CircleTimeView;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

public class InterceptFragmentNormal extends Fragment {


    private String usingPackageName;
    private String lockOfUsePackage;
    private  String appName;
    private int lockType;

    private ImageView icon;
    private TextView finishText;
    private CircleTimeView circleTimeView;

    private InterceptActivity interceptActivity;


    public InterceptFragmentNormal() {

    }


    public static InterceptFragmentNormal newInstance(String packageName, int type,String lockOfUsePackage) {
        InterceptFragmentNormal fragment = new InterceptFragmentNormal();
        Bundle args = new Bundle();
        args.putString(SqlInfo.PACKAGE, packageName);
        args.putInt(SqlInfo.LOCK_TYPE, type);
        if (type==SqlInfo.LOCK_TYPE_USE){
            args.putString(SqlInfo.PACKAGE + SqlInfo.LOCK_TYPE_USE, lockOfUsePackage);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           usingPackageName=getArguments().getString(SqlInfo.PACKAGE);
            lockType=getArguments().getInt(SqlInfo.LOCK_TYPE);
            if (lockType==SqlInfo.LOCK_TYPE_USE){
                lockOfUsePackage=getArguments().getString(SqlInfo.PACKAGE+SqlInfo.LOCK_TYPE_USE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_intercept_normal, container, false);

        interceptActivity=(InterceptActivity)getActivity();

        //图标
        icon=(ImageView)view.findViewById(R.id.intercept_normal_icon);
        circleTimeView=(CircleTimeView)view.findViewById(R.id.intercept_normal_circle);

        //时间提示，锁的结束时间
        finishText=(TextView)view.findViewById(R.id.intercept_normal_finish_time);

        if (Util.getInterceptNormalShowTime()){
            ShowView showView=new ShowView();
            showView.execute(null, null);
        }else {

        }

        return view;
    }



    class ShowView extends AsyncTask<Integer,Integer,Integer> {

        //数据
        HashMap<String,Integer> h;
        int startTime;
        int finishTime;
        int order;

        public ShowView() {
            super();
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            if (lockType==SqlInfo.LOCK_TYPE_OVER_TIME){
                //超时锁
                appName= Util.getAppName(usingPackageName);
                finishTime=MyApplication.dataResolver.getTimeOver(usingPackageName).get(0);

            }else if (lockType==SqlInfo.LOCK_TYPE_FORBIDDEN){
                //禁用锁
                appName= Util.getAppName(usingPackageName);
                h = MyApplication.dataResolver.getLocTime(usingPackageName, lockType);
                startTime = h.get(SqlInfo.LOCK_START_TIME);
                finishTime = h.get(SqlInfo.LOCK_FINISH_TIME);
                order = h.get(SqlInfo.LOCK_ORDER);

            }else if (lockType==SqlInfo.LOCK_TYPE_USE){
                //锁定锁
                appName= Util.getAppName(lockOfUsePackage);
                h = MyApplication.dataResolver.getLocTime(lockOfUsePackage, lockType);
                startTime = h.get(SqlInfo.LOCK_START_TIME);
                finishTime = h.get(SqlInfo.LOCK_FINISH_TIME);
                order = h.get(SqlInfo.LOCK_ORDER);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            String text;
            if (lockType==SqlInfo.LOCK_TYPE_FORBIDDEN){
                text=appName+"  "+
                        Util.getString(R.string.lock_forbidden)+"  "+
                        Util.getString(R.string.lock_finish)+"  "+
                        Util.getTimeFromLockTimeInSql(finishTime);
                icon.setImageDrawable(MyApplication.dataResolver.getIcon(usingPackageName));
            }else if (lockType==SqlInfo.LOCK_TYPE_USE){
                text=appName+"  "+
                        Util.getString(R.string.lock_use)+"  "+
                        Util.getString(R.string.lock_finish)+"  "+
                        Util.getTimeFromLockTimeInSql(finishTime);
                icon.setImageDrawable(MyApplication.dataResolver.getIcon(lockOfUsePackage));
            }else {
                text=appName+"  "+
                        Util.getString(R.string.time_over_over)+"  "+
                        Util.getMinuteTimeFromSecondTimeInSql(finishTime);
                icon.setImageDrawable(MyApplication.dataResolver.getIcon(usingPackageName));
            }
            finishText.setText(text);

            circleTimeView.setData(lockType,startTime,finishTime,finishTime);

            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interceptActivity.startEntry();
                }
            });
        }
    }




    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("interceptNormal"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("interceptNormal");
    }
}
