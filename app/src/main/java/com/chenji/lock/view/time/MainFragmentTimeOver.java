package com.chenji.lock.view.time;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.AppInfo;
import com.chenji.lock.model.SqlInfo;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;


public class MainFragmentTimeOver extends Fragment {

    String packageName;

    private TimeOver timeOver;

    private MainActivity mainActivity;

    public MainFragmentTimeOver() {
        // Required empty public constructor
    }


    public static MainFragmentTimeOver newInstance(String packageName) {
        MainFragmentTimeOver fragment = new MainFragmentTimeOver();
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
        View view = inflater.inflate(R.layout.fragment_main_time_over, container, false);

        mainActivity = (MainActivity) getActivity();

        //显示app图标
        AppInfo appInfo = MyApplication.dataResolver.getTheApp(packageName);
        ImageView appIcon = (ImageView) view.findViewById(R.id.time_over_icon);
        appIcon.setImageDrawable(appInfo.appIcon);
        TextView useTime = (TextView) view.findViewById(R.id.time_over_use_time);
        useTime.setText(AppInfo.minuteTime(appInfo.usageTime));


        //设置超时
        timeOver = new TimeOver(MyApplication.dataResolver.getAllTimeOver(packageName));
        ListView listView = (ListView) view.findViewById(R.id.time_over_list);
        listView.setAdapter(new TimeOverAdapter());

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("timeOver"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("timeOver");
        if (timeOver.getSize() == 7) {
            MyApplication.dataResolver.updateOverTime(SqlInfo.LOCK_MONDAY, packageName, timeOver.get(0));
            MyApplication.dataResolver.updateOverTime(SqlInfo.LOCK_TUESDAY, packageName, timeOver.get(1));
            MyApplication.dataResolver.updateOverTime(SqlInfo.LOCK_WEDNESDAY, packageName, timeOver.get(2));
            MyApplication.dataResolver.updateOverTime(SqlInfo.LOCK_THURSDAY, packageName, timeOver.get(3));
            MyApplication.dataResolver.updateOverTime(SqlInfo.LOCK_FRIDAY, packageName, timeOver.get(4));
            MyApplication.dataResolver.updateOverTime(SqlInfo.LOCK_SATURDAY, packageName, timeOver.get(5));
            MyApplication.dataResolver.updateOverTime(SqlInfo.LOCK_SUNDAY, packageName, timeOver.get(6));
        }
    }


    class TimeOverAdapter extends BaseAdapter {

        public class ItemView {
            public TextView week;
            public TextView time;
            public SeekBar bar;

            public ItemView(View convertView) {
                week = (TextView) convertView.findViewById(R.id.time_over_week);
                time = (TextView) convertView.findViewById(R.id.time_over_time);
                bar = (SeekBar) convertView.findViewById(R.id.time_over_bar);
            }
        }

        public TimeOverAdapter() {

        }

        @Override
        public int getCount() {
            return timeOver.getSize();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*itemView=null;
            if (convertView==null){
               */
 /*convertView.setTag(itemView);
            } else {
                itemView = (ItemView) convertView.getTag();
            }*/

            ItemView itemView;
            if (convertView == null) {
                convertView = LayoutInflater.from(mainActivity).inflate(R.layout.item_time_over, null);
                itemView = new ItemView(convertView);
                convertView.setTag(itemView);
                itemView.time.setText(Util.getMinuteTimeFromSecondTimeInSql(timeOver.get(position)));
                itemView.bar.setMax(24 * 3600);
                itemView.bar.setProgress(timeOver.get(position));
                switch (position) {
                    case 0: {
                        itemView.week.setText(R.string.monday_week);
                        MyOnSeekBarChangeListener listener0= new MyOnSeekBarChangeListener(itemView.time, position);
                        itemView.bar.setOnSeekBarChangeListener(listener0);
                        break;
                    }
                    case 1: {
                        itemView.week.setText(R.string.tuesday_week);
                        MyOnSeekBarChangeListener listener1= new MyOnSeekBarChangeListener(itemView.time, position);
                        itemView.bar.setOnSeekBarChangeListener(listener1);
                        break;
                    }
                    case 2: {
                        itemView.week.setText(R.string.wednesday_week);
                        MyOnSeekBarChangeListener listener2= new MyOnSeekBarChangeListener(itemView.time, position);
                        itemView.bar.setOnSeekBarChangeListener(listener2);
                        break;
                    }
                    case 3: {
                        itemView.week.setText(R.string.thursday_week);
                        MyOnSeekBarChangeListener listener3= new MyOnSeekBarChangeListener(itemView.time, position);
                        itemView.bar.setOnSeekBarChangeListener(listener3);
                        break;
                    }
                    case 4: {
                        itemView.week.setText(R.string.friday_week);
                        MyOnSeekBarChangeListener listener4= new MyOnSeekBarChangeListener(itemView.time, position);
                        itemView.bar.setOnSeekBarChangeListener(listener4);
                        break;
                    }
                    case 5: {
                        itemView.week.setText(R.string.saturday_week);
                        MyOnSeekBarChangeListener listener5= new MyOnSeekBarChangeListener(itemView.time, position);
                        itemView.bar.setOnSeekBarChangeListener(listener5);
                        break;
                    }
                    default: {
                        itemView.week.setText(R.string.sunday_week);
                        MyOnSeekBarChangeListener listener6= new MyOnSeekBarChangeListener(itemView.time, position);
                        itemView.bar.setOnSeekBarChangeListener(listener6);
                        break;
                    }
                }


            }


            return convertView;
        }


        private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

            final TextView time;
            final int position;

            public MyOnSeekBarChangeListener(TextView time, int position) {
                super();
                this.time = time;
                this.position=position;

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                time.setText(Util.getMinuteTimeFromSecondTimeInSql(progress));
                timeOver.set(position, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

           /* @Override
            public void onValueChanged(int i) {
                time.setText(Util.getMinuteTimeFromSecondTimeInSql(i));
                timeOver.set(position, i);
            }*/
        }

    }

    private int getWeek(int position) {
        switch (position) {
            case 0: {
                return R.string.monday;
            }
            case 1: {
                return R.string.tuesday;
            }
            case 2: {
                return R.string.wednesday;
            }
            case 3: {
                return R.string.thursday;
            }
            case 4: {
                return R.string.friday;
            }
            case 5: {
                return R.string.saturday;
            }
            default: {
                return R.string.sunday;
            }
        }
    }

    {
/*    class TimeOverAdapter extends RecyclerView.Adapter<TimeOverAdapter.ItemView> {

        class ItemView extends RecyclerView.ViewHolder {
            public TextView week;
            public TextView time;
            public SeekBar bar;

            public ItemView(View itemView) {
                super(itemView);
                week = (TextView) itemView.findViewById(R.id.time_over_week);
                time = (TextView) itemView.findViewById(R.id.time_over_time);
                bar = (SeekBar) itemView.findViewById(R.id.time_over_bar);
            }
        }

        public TimeOverAdapter() {
            super();
        }

        @Override
        public int getItemCount() {
            return timeOver.getSize();
        }

        @Override
        public TimeOverAdapter.ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemView(LayoutInflater.from(mainActivity).inflate(R.layout.item_time_over, null));
        }

        @Override
        public void onBindViewHolder(TimeOverAdapter.ItemView holder, int position) {

            holder.time.setText(Util.getMinuteTimeFromSecondTimeInSql(timeOver.get(position)));

            holder.bar.setMax(24 * 3600);
            holder.bar.setProgress(timeOver.get(position));

            switch (position) {
                case 0: {
                    holder.week.setText(R.string.monday_week);
                    break;
                }
                case 1: {
                    holder.week.setText(R.string.tuesday_week);
                    break;
                }
                case 2: {
                    holder.week.setText(R.string.wednesday_week);
                    break;
                }
                case 3: {
                    holder.week.setText(R.string.thursday_week);
                    break;
                }
                case 4: {
                    holder.week.setText(R.string.friday_week);
                    break;
                }
                case 5: {
                    holder.week.setText(R.string.saturday_week);
                    break;
                }
                default: {
                    holder.week.setText(R.string.sunday_week);
                    break;
                }
            }

            holder.bar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener(holder.time, position));
        }


        private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

            TextView time;
            int position;

            public MyOnSeekBarChangeListener(TextView time, int position) {
                super();
                this.time = time;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                time.setText(Util.getMinuteTimeFromSecondTimeInSql(progress));
                timeOver.set(position, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        }
    }*/
    }

    class TimeOver {
        int i0;
        int i1;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int size;

        public TimeOver(ArrayList<Integer> sevenInt) {
            size = sevenInt.size();
            if (size == 7) {
                i0 = sevenInt.get(0);
                i1 = sevenInt.get(1);
                i2 = sevenInt.get(2);
                i3 = sevenInt.get(3);
                i4 = sevenInt.get(4);
                i5 = sevenInt.get(5);
                i6 = sevenInt.get(6);
            }
        }

        public int get(int position) {
            switch (position) {
                case 0: {
                    return i0;
                }
                case 1: {
                    return i1;
                }
                case 2: {
                    return i2;
                }
                case 3: {
                    return i3;
                }
                case 4: {
                    return i4;
                }
                case 5: {
                    return i5;
                }
                default: {
                    return i6;
                }
            }
        }

        public void set(int position, int val) {
            switch (position) {
                case 0: {
                    i0 = val;
                    break;
                }
                case 1: {
                    i1 = val;
                    break;
                }
                case 2: {
                    i2 = val;
                    break;
                }
                case 3: {
                    i3 = val;
                    break;
                }
                case 4: {
                    i4 = val;
                    break;
                }
                case 5: {
                    i5 = val;
                    break;
                }
                default: {
                    i6 = val;
                    break;
                }
            }

        }

        public int getSize() {
            return size;
        }
    }
}
