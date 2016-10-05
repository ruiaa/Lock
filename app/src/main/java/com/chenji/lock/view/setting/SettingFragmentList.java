package com.chenji.lock.view.setting;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.Util;
import com.chenji.lock.dataResolver.DataResolver;
import com.gc.materialdesign.views.Switch;
import com.umeng.analytics.MobclickAgent;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragmentList extends Fragment {

    SharedPreferences sharedPreferences;

    RelativeLayout setBackground;

    RelativeLayout setPassword;

    RelativeLayout setUpdate;
    TextView updateShow;
    int updateCode;
    int updateCodeClick;

    /*RelativeLayout setLanguage;
    TextView languageShow;
    int languageCode;
    int languageCodeClick;*/

    Switch setExit;
    boolean exitCode;

    Switch setBootCompleted;
    boolean bootCompleteCode;

    SettingActivity settingActivity;

    public SettingFragmentList() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_setting_list, container, false);
        sharedPreferences=this.getActivity().getSharedPreferences(MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);
        settingActivity=(SettingActivity)getActivity();

//背景设置
        setBackground=(RelativeLayout)view.findViewById(R.id.set_background);
        setBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingActivity.showDetailFrag(SettingFragmentList.this,settingActivity.BACKGROUND);
            }
        });

//密码重置
        setPassword=(RelativeLayout)view.findViewById(R.id.set_password);
        setPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingActivity.showDetailFrag(SettingFragmentList.this,settingActivity.PASSWORD);
            }
        });

//退出锁定设置
        setExit=(Switch)view.findViewById(R.id.set_lock_after_exit);
        exitCode=sharedPreferences.getBoolean(MyApplication.SET_EXIT,true);
        setExit.setChecked(exitCode);
        setExit.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(Switch aSwitch, boolean b) {
                if (b) {
                    exitCode = true;
                } else {
                    exitCode = false;
                }
            }
        });

//开机自启设置
        setBootCompleted=(Switch)view.findViewById(R.id.set_boot_completed);
        bootCompleteCode=sharedPreferences.getBoolean(MyApplication.SET_BOOT_COMPLETED, true);
        setBootCompleted.setChecked(bootCompleteCode);
        setBootCompleted.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(Switch aSwitch, boolean b) {
                if (b) {
                    bootCompleteCode = true;
                } else {
                    bootCompleteCode = false;
                }
            }
        });

//更新设置
        setUpdate=(RelativeLayout)view.findViewById(R.id.set_update);
        updateShow=(TextView)view.findViewById(R.id.set_update_show);
        updateCode=sharedPreferences.getInt(MyApplication.SET_UPDATE,1);
        updateCodeClick=updateCode;
        updateShow.setText(Util.transferUpdateSet(updateCode));
        setUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder updateBuilder=new AlertDialog.Builder(settingActivity);
                updateBuilder.setSingleChoiceItems(R.array.set_update_all, updateCode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateCodeClick=which;
                    }
                });
                updateBuilder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateCode=updateCodeClick;
                        updateShow.setText(Util.transferUpdateSet(updateCode));
                    }
                });
                updateBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                updateBuilder.create();
                updateBuilder.show();
            }
        });

/*//语言设置
        setLanguage = (RelativeLayout) view.findViewById(R.id.set_language);
        languageShow = (TextView) view.findViewById(R.id.set_language_show);
        languageCode = sharedPreferences.getInt(MyApplication.SET_LANGUAGE, 0);
        languageCodeClick = languageCode;
        languageShow.setText(Util.transferLanguageSet(languageCode));
        setLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder languageBuilder = new AlertDialog.Builder(settingActivity);
                languageBuilder.setSingleChoiceItems(R.array.set_language_all, languageCode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        languageCodeClick = which;
                    }
                });
                languageBuilder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        languageCode = languageCodeClick;
                        languageShow.setText(Util.transferLanguageSet(languageCode));
                    }
                });
                languageBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                languageBuilder.create();
                languageBuilder.show();
            }
        });*/





        return view;
    }


    @Override
    public void onPause() {
        DataResolver.Exit_Lock=exitCode;
        DataResolver.Update_Code=updateCode;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(MyApplication.SET_UPDATE,updateCode);
        /*editor.putInt(MyApplication.SET_LANGUAGE,languageCode);*/
        editor.putBoolean(MyApplication.SET_EXIT, exitCode);
        editor.putBoolean(MyApplication.SET_BOOT_COMPLETED,bootCompleteCode);
        MyApplication.dataResolver.setBOOT_COMPLETED(bootCompleteCode);
        editor.apply();
        super.onPause();
        MobclickAgent.onPageEnd("moreList");
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("moreList"); //统计页面，"MainScreen"为页面名称，可自定义
    }

}
