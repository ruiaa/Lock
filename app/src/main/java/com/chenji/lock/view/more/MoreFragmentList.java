package com.chenji.lock.view.more;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.chenji.lock.R;
import com.chenji.lock.Util.MyLog;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;


public class MoreFragmentList extends Fragment {

    ProgressBarCircularIndeterminate bar;



    public MoreFragmentList() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view=inflater.inflate(R.layout.fragment_more_list, container, false);

        RelativeLayout useHelp=(RelativeLayout)view.findViewById(R.id.more_use_help);
        RelativeLayout feedback=(RelativeLayout)view.findViewById(R.id.more_feedback);
        RelativeLayout about=(RelativeLayout)view.findViewById(R.id.more_about_me);
        RelativeLayout checkUpdate=(RelativeLayout)view.findViewById(R.id.more_check_update);

        final MoreActivity moreActivity=(MoreActivity)getActivity();

        //使用帮助
        useHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreActivity.showDetailFrag(MoreFragmentList.this,moreActivity.USE_HELP);
            }
        });

        //用户反馈
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackAPI.openFeedbackActivity(moreActivity);
            }
        });

        //关于
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreActivity.showDetailFrag(MoreFragmentList.this, moreActivity.ABOUT);
            }
        });

        //检查更新
        bar=(ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBarCircularIndeterminate);
        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                UmengUpdateAgent.setUpdateOnlyWifi(false);
                UmengUpdateAgent.setUpdateAutoPopup(false);
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                        bar.setVisibility(View.GONE);
                        switch (updateStatus) {
                            case UpdateStatus.Yes: // has update
                                UmengUpdateAgent.showUpdateDialog(moreActivity, updateInfo);
                                break;
                            case UpdateStatus.No: // has no update
                                Toast.makeText(moreActivity, "已经是最新版的", Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.NoneWifi: // none wifi
                                Toast.makeText(moreActivity, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.Timeout: // time out
                                Toast.makeText(moreActivity, "超时", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                UmengUpdateAgent.update(moreActivity);
                MyLog.i_chenji_log("forceUpdate");
            }
        });


        return view;
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("moreList"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("moreList");
    }
}
