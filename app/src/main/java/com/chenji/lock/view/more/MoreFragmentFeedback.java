package com.chenji.lock.view.more;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenji.lock.R;
import com.umeng.analytics.MobclickAgent;


public class MoreFragmentFeedback extends Fragment {

    MoreActivity moreActivity;

    public MoreFragmentFeedback() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_more_feedback, container, false);

        moreActivity=(MoreActivity)getActivity();


        return view;
    }



    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("moreFeed"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("moreFeed");
    }
}
