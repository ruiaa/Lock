package com.chenji.lock.view.setting;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.Util;
import com.chenji.lock.view.GestureLockView;
import com.umeng.analytics.MobclickAgent;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragmentPassword extends Fragment {

    SettingActivity settingActivity;
    SharedPreferences sharedPreferences;

    ImageView findPassword;
    AlertDialog alertDialog=null;


    TextView textView;
    int wrongCount = 0;
    int setPassword = 0;
    String oldPassword;
    String newPassword = "";

    public SettingFragmentPassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_password, container, false);

        settingActivity = (SettingActivity) getActivity();
        sharedPreferences = settingActivity.getSharedPreferences(MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);
        oldPassword = sharedPreferences.getString(MyApplication.PASSWORD, "");

        findPassword=(ImageView)view.findViewById(R.id.find_password_set);
        textView = (TextView) view.findViewById(R.id.set_password_text_tip);
        GestureLockView gestureLockView = (GestureLockView) view.findViewById(R.id.set_password_lockView);

        textView.setText(Util.getString(R.string.password_old));
        gestureLockView.setCallback(new GestureLockView.GestureLockCallback() {
            @Override
            public void onFinish(String pwdString, GestureLockView.Result result) {
                if (setPassword == 0) {

                    boolean resu = oldPassword.equals(pwdString);
                    result.setRight(resu);
                    if (resu) {
                        setPassword = 1;
                        textView.setText(Util.getString(R.string.set_password_new));
                    } else {
                        Toast.makeText(settingActivity, R.string.toast_password_false, Toast.LENGTH_LONG).show();
                        wrongCount = wrongCount + 1;


                        if (wrongCount == 2) {
                            findPassword.setImageResource(R.drawable.wrong_find_password);
                        }
                    }

                } else if (setPassword == 1) {

                    result.setRight(true);
                    textView.setText(R.string.set_password_again);
                    newPassword = pwdString;
                    setPassword = 2;

                } else if (setPassword == 2) {

                    boolean resu = newPassword.equals(pwdString);
                    result.setRight(resu);
                    if (resu) {
                        Toast.makeText(settingActivity, R.string.set_password_again_right, Toast.LENGTH_SHORT).show();
                        textView.setText(R.string.set_password_again_right);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(MyApplication.PASSWORD, newPassword);
                        editor.apply();
                        oldPassword = newPassword;
                        settingActivity.turnBack();
                    } else {
                        textView.setText(R.string.set_password_again_wrong);
                        setPassword = 1;
                    }

                }
            }
        });





        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPassword();
            }
        });


        return view;
    }

    private void findPassword(){
        AlertDialog.Builder builder=null;
        View view = LayoutInflater.from(settingActivity).inflate(R.layout.find_password, null);
        TextView editTextQ = (TextView) view.findViewById(R.id.find_password_question);
        final EditText editTextA = (EditText) view.findViewById(R.id.find_password_answer);
        final TextView passwordShow = (TextView) view.findViewById(R.id.password_show);
        editTextQ.setText(sharedPreferences.getString(MyApplication.PASSWORD_QUESTION, ""));
        final String answer = sharedPreferences.getString(MyApplication.PASSWORD_ANSWER, "");

        Button buttonCancel=(Button)view.findViewById(R.id.find_password_cancel);
        Button buttonSure=(Button)view.findViewById(R.id.find_password_sure);

        builder = new AlertDialog.Builder(settingActivity);
        builder.setTitle(R.string.password_find);
        builder.setView(view);
        alertDialog=builder.create();


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        buttonSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = editTextA.getText().toString().trim();
                if (a.equals(Util.getString(R.string.input_password_answer)) || a.length() == 0) {
                    Toast.makeText(settingActivity, R.string.input_password_answer, Toast.LENGTH_SHORT).show();
                } else if (a.equals(answer)) {
                    passwordShow.setText(Util.getString(R.string.password_show) + oldPassword);
                    passwordShow.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(settingActivity, R.string.password_answer_wrong, Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("setPassword"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("setPassword");
    }

}
