package com.chenji.lock.view.time;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.AppInfo;
import com.chenji.lock.model.SqlInfo;
import com.chenji.lock.view.OverTimeView;
import com.gc.materialdesign.views.ButtonFloat;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;


public class MainFragmentLock extends Fragment {

    MainActivity mainActivity;

    private String packageName;
    AppInfo appInfo;

    ArrayList<HashMap<String, Object>> lockList;

    ListView lockListView;
    View footerView;

    public static MainFragmentLock newInstance(String packageName){
        MainFragmentLock fragment=new MainFragmentLock();
        Bundle args = new Bundle();
        args.putString(SqlInfo.PACKAGE, packageName);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            packageName=getArguments().getString(SqlInfo.PACKAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        footerView=View.inflate(getActivity(),R.layout.item_lock_time_footer,null);
        View view=inflater.inflate(R.layout.fragment_main_lock, container, false);

        mainActivity=(MainActivity)getActivity();
        appInfo = MyApplication.dataResolver.getTheApp(packageName);

        //显示图标和使用时间
        ImageView appIcon = (ImageView) view.findViewById(R.id.lock_app_icon);
        appIcon.setImageDrawable(appInfo.appIcon);
        TextView useTime = (TextView) view.findViewById(R.id.lock_use_time);
        useTime.setText(AppInfo.minuteTime(appInfo.usageTime));

        //listView适配
        lockListView = (ListView) view.findViewById(R.id.lock_time_list);
        lockList = MyApplication.dataResolver.getLock(packageName);
        if (lockList.isEmpty()) {
           /* lockListView.setBackgroundResource(R.drawable.ic_launcher);*/
            MyLog.i_chenji_log("no lock");
        } else {

            lockListView.addFooterView(footerView,null,false);
            LockListAdapter lockListAdapter = new LockListAdapter(lockList);
            lockListView.setAdapter(lockListAdapter);
            MyLog.i_chenji_log("show lock");
        }

        //Add按钮监听
        ButtonFloat addLock = (ButtonFloat) view.findViewById(R.id.lock_add);
        addLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.addLock(packageName,MainFragmentLock.this);
            }
        });

        //超时
        TextView overTimeText=(TextView)view.findViewById(R.id.lock_over_time_text);
        OverTimeView overTimeView=(OverTimeView)view.findViewById(R.id.lock_over_time);
        ArrayList<Integer> time=MyApplication.dataResolver.getTimeOver(packageName);
        int timeAll;
        int timeUse;
        int timeOpen;
        if (time.size()==3){
            timeAll=time.get(0);
            timeUse=time.get(1);
            timeOpen=time.get(2);
            if (timeAll==0){
                overTimeText.setText(Util.getString(R.string.left) + Util.getMinuteTimeFromSecondTimeInSql(0));
                overTimeView.setTime(1,1);
            }else if (timeAll<timeUse){
                overTimeText.setText(Util.getString(R.string.left) + Util.getMinuteTimeFromSecondTimeInSql(0));
                overTimeView.setTime(1,1);
            }else {
                overTimeText.setText(Util.getString(R.string.left) + Util.getMinuteTimeFromSecondTimeInSql(timeAll - timeUse));
                overTimeView.setTime(timeUse, timeAll);
            }
        }
        overTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.updateOverTime(packageName,MainFragmentLock.this);
            }
        });
        RelativeLayout relativeLayout=(RelativeLayout)view.findViewById(R.id.lock_app);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.updateOverTime(packageName,MainFragmentLock.this);
            }
        });

        return view;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("lock"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("lock");
    }


    class LockListAdapter extends BaseAdapter {

        private ArrayList<HashMap<String, Object>> lockList;

        public final class ItemView {
            public TextView type;
            public TextView startTime;
            public TextView finishTime;
            public ButtonFloat delete;

            public TextView monday;
            public TextView tuesday;
            public TextView wednesday;
            public TextView thursday;
            public TextView friday;
            public TextView saturday;
            public TextView sunday;
        }

        public LockListAdapter(ArrayList<HashMap<String, Object>> lockList) {
            this.lockList = lockList;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ItemView itemView = null;
            if (convertView == null) {
                itemView = new ItemView();
                convertView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_lock_time, null);

                itemView.type = (TextView) convertView.findViewById(R.id.lock_type);
                itemView.startTime = (TextView) convertView.findViewById(R.id.lock_start);
                itemView.finishTime = (TextView) convertView.findViewById(R.id.lock_finish);
                itemView.delete = (ButtonFloat) convertView.findViewById(R.id.lock_delete);
                itemView.monday = (TextView) convertView.findViewById(R.id.lock_monday);
                itemView.tuesday = (TextView) convertView.findViewById(R.id.lock_tuesday);
                itemView.wednesday = (TextView) convertView.findViewById(R.id.lock_wednesday);
                itemView.thursday = (TextView) convertView.findViewById(R.id.lock_thursday);
                itemView.friday = (TextView) convertView.findViewById(R.id.lock_friday);
                itemView.saturday = (TextView) convertView.findViewById(R.id.lock_saturday);
                itemView.sunday = (TextView) convertView.findViewById(R.id.lock_sunday);

                convertView.setTag(itemView);
            } else {
                itemView = (ItemView) convertView.getTag();
            }

            //类型
            if (((Integer) lockList.get(position).get(SqlInfo.LOCK_TYPE)).intValue() == 1) {
                itemView.type.setText((String) MyApplication.getContext().getResources().getString(R.string.lock_forbidden));
                itemView.type.setTextColor(Util.getColor(R.color.lock_type_forbidden));
            } else {
                itemView.type.setText((String) MyApplication.getContext().getResources().getString(R.string.lock_use));
                itemView.type.setTextColor(Util.getColor(R.color.lock_type_use));
            }


            //开始时间
            int startTime = ((Integer) lockList.get(position).get(SqlInfo.LOCK_START_TIME)).intValue();
            try {
                String start = Util.getTimeFromLockTimeInSql(startTime);
                itemView.startTime.setText(start);
                start = null;
            } catch (Exception e) {
                MyLog.e_chenji_log("锁 start时间格式转换失败" + e.toString());
            }

            //结束时间
            int finishTime = ((Integer) lockList.get(position).get(SqlInfo.LOCK_FINISH_TIME)).intValue();
            try {
                String finish = Util.getTimeFromLockTimeInSql(finishTime);
                itemView.finishTime.setText(finish);
                finish = null;
            } catch (Exception e) {
                MyLog.e_chenji_log("锁 finish时间格式转换失败" + e.toString());
            }

            //重复星期
            if (((Integer) (lockList.get(position).get(SqlInfo.LOCK_MONDAY))).intValue() == 1) {
                itemView.monday.setBackgroundResource(R.color.checked);
            }
            if (((Integer) (lockList.get(position).get(SqlInfo.LOCK_TUESDAY))).intValue() == 1) {
                itemView.tuesday.setBackgroundResource(R.color.checked);
            }
            if (((Integer) (lockList.get(position).get(SqlInfo.LOCK_WEDNESDAY))).intValue() == 1) {
                itemView.wednesday.setBackgroundResource(R.color.checked);
            }
            if (((Integer) (lockList.get(position).get(SqlInfo.LOCK_THURSDAY))).intValue() == 1) {
                itemView.thursday.setBackgroundResource(R.color.checked);
            }
            if (((Integer) (lockList.get(position).get(SqlInfo.LOCK_FRIDAY))).intValue() == 1) {
                itemView.friday.setBackgroundResource(R.color.checked);
            }
            if (((Integer) (lockList.get(position).get(SqlInfo.LOCK_SATURDAY))).intValue() == 1) {
                itemView.saturday.setBackgroundResource(R.color.checked);
            }
            if (((Integer) (lockList.get(position).get(SqlInfo.LOCK_SUNDAY))).intValue() == 1) {
                itemView.sunday.setBackgroundResource(R.color.checked);
            }

            //删除按钮
            itemView.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.dataResolver.deleteLock(((Integer) lockList.get(position).get(SqlInfo.LOCK_ORDER)).intValue());
                    MainActivity mainActivity=(MainActivity)getActivity();
                    mainActivity.reShow(MainFragmentLock.this);
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return lockList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }


}
