package com.chenji.lock.view.intercept.choose;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.Util;
import com.gc.materialdesign.views.Switch;
import com.umeng.analytics.MobclickAgent;

public class ChooseInterceptFragmentList extends Fragment {

    ChooseInterceptActivity chooseInterceptActivity;
    SharedPreferences sharedPreferences;

    ToggleButton interceptMode;
    int mode;

    RelativeLayout interceptInterval;
    SeekBar chooseInterval;
    float judge_interval;
    int intercept_interval;

    RelativeLayout interceptBackground;

    boolean showTimeCode;

    public ChooseInterceptFragmentList() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_intercept_list, container, false);

        chooseInterceptActivity = (ChooseInterceptActivity) getActivity();
        sharedPreferences = chooseInterceptActivity.getSharedPreferences(MyApplication.FILE_SHARED, Context.MODE_PRIVATE);

//拦截模式
// 拦截间隔
        judge_interval = sharedPreferences.getFloat(MyApplication.JUDGE_INTERVAL, 0.1F);
        intercept_interval = sharedPreferences.getInt(MyApplication.INTERCEPT_INTERVAL, 0);
        if (intercept_interval == 0) {
            mode = MyApplication.INTERCEPT_MODE_FORCE;
            intercept_interval=60;
        } else {
            mode = MyApplication.INTERCEPT_MODE_WARN;
        }


        interceptMode = (ToggleButton) view.findViewById(R.id.choose_intercept_mode);
        interceptInterval = (RelativeLayout) view.findViewById(R.id.choose_intercept_interval);
        chooseInterval = (SeekBar) view.findViewById(R.id.choose_intercept_interval_choose);
        if (mode == MyApplication.INTERCEPT_MODE_FORCE) {
            interceptMode.setChecked(false);
            interceptInterval.setVisibility(View.GONE);
        } else {
            interceptMode.setChecked(true);
            interceptInterval.setVisibility(View.VISIBLE);
        }


        interceptMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mode = MyApplication.INTERCEPT_MODE_WARN;
                } else {
                    mode = MyApplication.INTERCEPT_MODE_FORCE;
                }
                if (mode == MyApplication.INTERCEPT_MODE_FORCE) {
                    interceptMode.setChecked(false);
                    interceptInterval.setVisibility(View.GONE);
                } else {
                    interceptMode.setChecked(true);
                    chooseInterval.setProgress(intercept_interval - 60);
                    interceptInterval.setVisibility(View.VISIBLE);
                }
            }
        });

//选择拦截间隔
        final TextView textView1 = (TextView) view.findViewById(android.R.id.text1);
        textView1.setText(String.valueOf(intercept_interval) + Util.getString(R.string.second));
        chooseInterval.setMax(300 - 60);
        chooseInterval.setProgress(intercept_interval - 60);
        chooseInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                intercept_interval = progress + 60;
                textView1.setText(String.valueOf(intercept_interval) + Util.getString(R.string.second));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


//选择拦截精度
        SeekBar judgeBar = (SeekBar) view.findViewById(R.id.choose_intercept_judge_interval_choose);
        final TextView textView2 = (TextView) view.findViewById(android.R.id.text2);
        textView2.setText(String.valueOf(judge_interval) + Util.getString(R.string.second));
        judgeBar.setMax(9);
        judgeBar.setProgress((int) (judge_interval * 10 - 1));
        judgeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                judge_interval = ((float) (progress + 1)) / 10F;
                textView2.setText(String.valueOf(judge_interval) + Util.getString(R.string.second));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//拦截背景
        interceptBackground = (RelativeLayout) view.findViewById(R.id.choose_intercept_background);
        interceptBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseInterceptActivity.showDetailFrag(ChooseInterceptFragmentList.this, chooseInterceptActivity.BACKGROUND);
            }
        });


//显示图标
        Switch showTime=(Switch)view.findViewById(R.id.choose_intercept_show_time);
        showTimeCode=sharedPreferences.getBoolean(MyApplication.INTERCEPT_NORMAL_SHOW_TIME,true);
        showTime.setChecked(showTimeCode);
        showTime.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(Switch aSwitch, boolean b) {
                if (b) {
                    showTimeCode = true;
                } else {
                    showTimeCode = false;
                }
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (mode == MyApplication.INTERCEPT_MODE_WARN) {
            editor.putInt(MyApplication.INTERCEPT_INTERVAL, intercept_interval);
            MyApplication.dataResolver.updateIntercept(judge_interval,intercept_interval);
        } else {
            editor.putInt(MyApplication.INTERCEPT_INTERVAL, 0);
            MyApplication.dataResolver.updateIntercept(judge_interval, 0);
        }
        editor.putFloat(MyApplication.JUDGE_INTERVAL, judge_interval);
        editor.putBoolean(MyApplication.INTERCEPT_NORMAL_SHOW_TIME,showTimeCode);
        editor.apply();
        super.onPause();

        MobclickAgent.onPageEnd("chooseList");
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("chooseList"); //统计页面，"MainScreen"为页面名称，可自定义
    }

}
