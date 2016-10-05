package com.chenji.lock.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;
import com.chenji.lock.Util.Util;
import com.chenji.lock.controller.ControlService;
import com.chenji.lock.dataResolver.DataResolver;
import com.chenji.lock.view.time.MainActivity;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

public class EnterActivity extends Activity {

    ImageView findPassword;
    AlertDialog alertDialog;


    TextView textView;
    int wrongCount = 0;
    int setPassword;
    String newPassword;
    String  password;

    SharedPreferences mySharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //开启控制后台
        Intent startService = new Intent(MyApplication.getContext(), ControlService.class);
        startService(startService);

        //判断是否是第一次打开应用
        mySharedPreferences = getSharedPreferences(MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);

        if (mySharedPreferences.getBoolean(MyApplication.FIRST_ENTER,true)) {

            //进入欢迎界面

            /*Intent welIntent=new Intent(this,FirstActivity.class);
            startActivity(welIntent);*/

            //设置友盟参数
            setYMon();

            setContentView(R.layout.activity_enter);

            //初始化密码
            GestureLockView gestureLockView = (GestureLockView) findViewById(R.id.lockView);
            textView = (TextView) findViewById(R.id.set_password_new);
            textView.setText(R.string.set_password_new);
            newPassword = "";
            setPassword = 1;
            gestureLockView.setCallback(new GestureLockView.GestureLockCallback() {
                @Override
                public void onFinish(String pwdString, GestureLockView.Result result) {
                    if (setPassword == 1) {
                        result.setRight(true);
                        textView.setText(R.string.set_password_again);
                        newPassword = pwdString;
                        setPassword = 2;
                    } else if (setPassword == 2) {
                        boolean resu = newPassword.equals(pwdString);
                        result.setRight(resu);
                        if (resu) {
                            textView.setText(R.string.set_password_again_right);
                            SharedPreferences.Editor myEditor = mySharedPreferences.edit();
                            myEditor.putString(MyApplication.PASSWORD, newPassword);
                            myEditor.putBoolean(MyApplication.FIRST_ENTER, false);
                            myEditor.apply();
                            setPasswordQuestion();

                        } else {
                            textView.setText(R.string.set_password_again_wrong);
                            setPassword = 1;
                        }
                    }
                }
            });
        } else {

            setContentView(R.layout.activity_enter);
            findPassword = (ImageView) findViewById(R.id.find_password_new);

            //输入密码
            GestureLockView gestureLockView = (GestureLockView) findViewById(R.id.lockView);
            password = mySharedPreferences.getString(MyApplication.PASSWORD, "");
            gestureLockView.setCallback(new GestureLockView.GestureLockCallback() {
                @Override
                public void onFinish(String pwdString, GestureLockView.Result result) {
                    boolean resu = password.equals(pwdString);
                    result.setRight(resu);
                    if (resu) {

                        //初始化 是否开启时间锁
                        MyApplication.dataResolver.updateOpenLock();

                        Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                        intent.putExtra("first", true);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(EnterActivity.this, R.string.toast_password_false, Toast.LENGTH_LONG).show();
                        wrongCount = wrongCount + 1;
                        if (wrongCount == 2) {
                            findPassword.setImageResource(R.drawable.wrong_find_password);
                        }
                    }

                }
            });

            //找回密码
            findPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findPassword();
                }
            });


            //设置友盟参数  设置广点通广告
            /*EnterAsyncTask enterAsyncTask=new EnterAsyncTask();
            enterAsyncTask.execute(null,null);*/

            //设置友盟参数
            setYMon();

            //设置广点通广告
            setBanner((FrameLayout) findViewById(R.id.ad_entry_password));
        }
    }


    private void setPasswordQuestion(){
        View view= LayoutInflater.from(EnterActivity.this).inflate(R.layout.set_password_question,null);
        final EditText editTextQ=(EditText)view.findViewById(android.R.id.text1);
        final EditText editTextA=(EditText)view.findViewById(android.R.id.text2);
        AlertDialog.Builder builder=new AlertDialog.Builder(EnterActivity.this);
        builder.setTitle(R.string.set_password_question);
        builder.setView(view);
        builder.setPositiveButton(R.string.sure, null);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = editTextQ.getText().toString().trim();
                String a = editTextA.getText().toString().trim();
                if (q.equals(Util.getString(R.string.input_password_question)) || q.length() == 0) {
                    Toast.makeText(EnterActivity.this, R.string.input_password_question, Toast.LENGTH_SHORT).show();
                } else if (a.equals(Util.getString(R.string.input_password_answer)) || a.length() == 0) {
                    Toast.makeText(EnterActivity.this, R.string.input_password_answer, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                    startActivity(intent);
                    SharedPreferences.Editor myEditor = mySharedPreferences.edit();
                    myEditor.putString(MyApplication.PASSWORD_QUESTION, q);
                    myEditor.putString(MyApplication.PASSWORD_ANSWER, a);
                    myEditor.apply();
                    finish();
                }
            }
        });
    }

    private void findPassword(){
        AlertDialog.Builder builder=null;
        View view = LayoutInflater.from(this).inflate(R.layout.find_password, null);
        TextView editTextQ = (TextView) view.findViewById(R.id.find_password_question);
        final EditText editTextA = (EditText) view.findViewById(R.id.find_password_answer);
        final TextView passwordShow = (TextView) view.findViewById(R.id.password_show);
        editTextQ.setText(mySharedPreferences.getString(MyApplication.PASSWORD_QUESTION, ""));
        final String answer =mySharedPreferences.getString(MyApplication.PASSWORD_ANSWER, "");

        Button buttonCancel=(Button)view.findViewById(R.id.find_password_cancel);
        Button buttonSure=(Button)view.findViewById(R.id.find_password_sure);

        builder = new AlertDialog.Builder(EnterActivity.this);
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
                    Toast.makeText(EnterActivity.this, R.string.input_password_answer, Toast.LENGTH_SHORT).show();
                } else if (a.equals(answer)) {
                    passwordShow.setText(Util.getString(R.string.password_show) + password);
                    passwordShow.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(EnterActivity.this, R.string.password_answer_wrong, Toast.LENGTH_SHORT).show();
                }
            }
        });




        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }


    {
/*    private void findPassword(){
        View view= LayoutInflater.from(EnterActivity.this).inflate(R.layout.find_password,null);
        TextView editTextQ=(TextView)view.findViewById(R.id.find_password_question);
        final EditText editTextA=(EditText)view.findViewById(R.id.find_password_answer);
        final TextView passwordShow=(TextView)view.findViewById(R.id.password_show);
        editTextQ.setText(mySharedPreferences.getString(MyApplication.PASSWORD_QUESTION,""));
        final String answer=mySharedPreferences.getString(MyApplication.PASSWORD_ANSWER,"");

        AlertDialog.Builder builder=new AlertDialog.Builder(EnterActivity.this);
        builder.setTitle(R.string.password_find);
        builder.setView(view);
        builder.setPositiveButton(R.string.sure, null);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = editTextA.getText().toString().trim();
                if (a.equals(Util.getString(R.string.input_password_answer)) || a.length() == 0) {
                    Toast.makeText(EnterActivity.this, R.string.input_password_answer, Toast.LENGTH_SHORT).show();
                } else if (a.equals(answer)) {
                    passwordShow.setText(Util.getString(R.string.password_show) + password);
                    passwordShow.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(EnterActivity.this, R.string.password_answer_wrong, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }*/

    }



    private void setYMon(){
        //友盟
        AnalyticsConfig.enableEncrypt(true);
        MobclickAgent.openActivityDurationTrack(false);

        UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_NOTIFICATION);
        UmengUpdateAgent.setUpdateCheckConfig(false); //toast显示错误
        UmengUpdateAgent.setUpdateAutoPopup(true);  //弹出对话框或者通知栏
        UmengUpdateAgent.setUpdateOnlyWifi(true);
    }

    private void setBanner(FrameLayout bannerContainer){
        BannerView bv= new BannerView(this, ADSize.BANNER, "1105221526", "5040305912022758");
        bv.setRefresh(30);
        bv.setADListener(new AbstractBannerADListener() {
            @Override
            public void onNoAD(int arg0) {
                MyLog.i_chenji_log("BannerNoAD,,eCode=" + arg0);
            }

            @Override
            public void onADReceiv() {
                MyLog.i_chenji_log(" ONBannerReceive");
            }
        });
        bannerContainer.addView(bv);
        bv.loadAD();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (DataResolver.Exit_Lock&&Util.noRunningOnStop(this)){
            MyLog.i_chenji_log("enter exit");
            System.exit(0);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

     class EnterAsyncTask extends AsyncTask<Integer,Integer,Integer>{
         public EnterAsyncTask() {
             super();
         }

         @Override
         protected void onPostExecute(Integer integer) {
             super.onPostExecute(integer);
             //初始化 是否开启时间锁
             MyApplication.dataResolver.updateOpenLock();

             //设置友盟参数
             setYMon();

             //设置广点通广告
             setBanner((FrameLayout) findViewById(R.id.ad_entry_password));
         }

         @Override
         protected Integer doInBackground(Integer... params) {
             return null;
         }
     }
}
