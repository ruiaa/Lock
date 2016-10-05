package com.chenji.lock.view.time;


import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.AppInfo;
import com.chenji.lock.model.SqlInfo;
import com.gc.materialdesign.views.ButtonFloat;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;


public class MainFragmentLockAdd extends Fragment {


    String packageName;
    int type = 1;
    int start = -1;
    int finish = -1;
    int week = 0;
    int week1 = 0;
    int week2 = 0;
    int week3 = 0;
    int week4 = 0;
    int week5 = 0;
    int week6 = 0;
    int week7 = 0;

    TextView lockStart;
    TextView lockFinish;


    public MainFragmentLockAdd() {
        // Required empty public constructor
    }


    public static MainFragmentLockAdd newInstance(String packageName) {
        MainFragmentLockAdd fragment = new MainFragmentLockAdd();
        Bundle args = new Bundle();
        args.putString(SqlInfo.PACKAGE, packageName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.packageName = getArguments().getString(SqlInfo.PACKAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_lock_add, container, false);

        ToggleButton lockType;

        ToggleButton lockWeek1;
        ToggleButton lockWeek2;
        ToggleButton lockWeek3;
        ToggleButton lockWeek4;
        ToggleButton lockWeek5;
        ToggleButton lockWeek6;
        ToggleButton lockWeek7;

        ButtonFloat lockConfirm;


        lockStart = (TextView) view.findViewById(R.id.lock_add_start);
        lockFinish = (TextView) view.findViewById(R.id.lock_add_finish);
        lockStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                start = hourOfDay * 60 + minute;
                                lockStart.setText(Util.getTimeFromLockTimeInSql(hourOfDay,minute));
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                ).show();
            }
        });
        lockFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                finish = hourOfDay * 60 + minute;
                                lockFinish.setText(Util.getTimeFromLockTimeInSql(hourOfDay,minute));
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                ).show();
            }
        });


        //显示app图标
        AppInfo appInfo = MyApplication.dataResolver.getTheApp(packageName);

        ImageView appIcon = (ImageView) view.findViewById(R.id.lock_app_icon);
        appIcon.setImageDrawable(appInfo.appIcon);
        TextView useTime = (TextView) view.findViewById(R.id.lock_use_time);
        useTime.setText(AppInfo.minuteTime(appInfo.usageTime));

        //获取时间锁的类型 1,禁用,未选择
        lockType = (ToggleButton) view.findViewById(R.id.lock_add_type);
        lockType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type = 2;
                } else {
                    type = 1;
                }
            }
        });


        //获取重复星期
        lockWeek1 = (ToggleButton) view.findViewById(R.id.lock_add_monday);
        lockWeek1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    week1 = 1;
                } else {
                    week1 = 0;
                }
            }
        });
        lockWeek2 = (ToggleButton) view.findViewById(R.id.lock_add_tuesday);
        lockWeek2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    week2 = 1;
                } else {
                    week2 = 0;
                }
            }
        });
        lockWeek3 = (ToggleButton) view.findViewById(R.id.lock_add_wednesday);
        lockWeek3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    week3 = 1;
                } else {
                    week3 = 0;
                }
            }
        });
        lockWeek4 = (ToggleButton) view.findViewById(R.id.lock_add_thursday);
        lockWeek4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    week4 = 1;
                } else {
                    week4 = 0;
                }
            }
        });
        lockWeek5 = (ToggleButton) view.findViewById(R.id.lock_add_friday);
        lockWeek5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    week5 = 1;
                } else {
                    week5 = 0;
                }
            }
        });
        lockWeek6 = (ToggleButton) view.findViewById(R.id.lock_add_saturday);
        lockWeek6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    week6 = 1;
                } else {
                    week6 = 0;
                }
            }
        });
        lockWeek7 = (ToggleButton) view.findViewById(R.id.lock_add_sunday);
        lockWeek7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    week7 = 1;
                } else {
                    week7 = 0;
                }
            }
        });

        //监听确定按钮
        lockConfirm = (ButtonFloat) view.findViewById(R.id.lock_add_confirm);
        lockConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (start == -1) {
                    //未选择开始时间
                    Toast.makeText(getActivity(), R.string.toast_no_choose_start_time, Toast.LENGTH_SHORT).show();
                } else if (finish == -1) {
                    //未选择结束时间
                    Toast.makeText(getActivity(), R.string.toast_no_choose_finish_time, Toast.LENGTH_SHORT).show();
                } else if (start >= finish) {
                    Toast.makeText(getActivity(), R.string.toast_finishAfterStart, Toast.LENGTH_SHORT).show();
                } else {
                    if ((week1 + week2 + week3 + week4 + week5 + week6 + week7) == 0) {
                        week = 0;
                    } else {
                        week = 1;
                    }
                    MyApplication.dataResolver.addLock(packageName, type, week, week1, week2, week3, week4, week5, week6, week7, start, finish);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.turnBack();
                }
            }
        });

        return view;
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("addLock"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("addLock");
    }
}
