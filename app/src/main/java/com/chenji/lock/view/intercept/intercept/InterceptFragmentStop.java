package com.chenji.lock.view.intercept.intercept;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chenji.lock.R;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.SqlInfo;


public class InterceptFragmentStop extends Fragment {

    private String usingPackageName;
    private String lockOfUsePackage;
    private  String appName;
    private int lockType;

    private InterceptActivity interceptActivity;

    public InterceptFragmentStop() {
        // Required empty public constructor
    }

    public static InterceptFragmentStop newInstance(String packageName, int type,String lockOfUsePackage) {
        InterceptFragmentStop fragment = new InterceptFragmentStop();
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

        if (lockType==SqlInfo.LOCK_TYPE_OVER_TIME){
            //超时锁
            appName= Util.getAppName(usingPackageName);
        }else if (lockType==SqlInfo.LOCK_TYPE_FORBIDDEN){
            //禁用锁
            appName= Util.getAppName(usingPackageName);
        }else if (lockType==SqlInfo.LOCK_TYPE_USE){
            //锁定锁
            appName= Util.getAppName(lockOfUsePackage);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_intercept_stop, container, false);

        interceptActivity=(InterceptActivity)getActivity();


        TextView textViewTip=(TextView)view.findViewById(R.id.intercept_stop_tip);
        TextView textViewSure=(TextView)view.findViewById(R.id.intercept_stop_sure);
        textViewTip.setText( Util.getAppName(usingPackageName)+"，"+Util.getString(R.string.crash));
        textViewSure.setText(Util.getString(R.string.sure));
        textViewSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interceptActivity.exit();
            }
        });
        return view;
    }

}
