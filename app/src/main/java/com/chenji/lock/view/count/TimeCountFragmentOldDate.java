package com.chenji.lock.view.count;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.AppInfo;
import com.chenji.lock.view.UseTimeView;
import com.github.glomadrian.loadingballs.BallView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;


public class TimeCountFragmentOldDate extends Fragment {

    public static final String ICON = "icon";
    public static final String APP_NAME = "app_name";
    public static final String TIME = "time";

    private ListView allAppInfoListView;
    private ArrayList<AppInfo> arrayList;
    private int max = 0;

    private BallView bar;

    TimeCountActivity timeCountActivity;

    //用于从构造器获取日期
    public static String DATE_FOR_SQL = "date";
    private String dateForSql;
    //日期
    private String date;

    View headerView;

    public static TimeCountFragmentOldDate newInstance(String dateForSql) {
        TimeCountFragmentOldDate fragment = new TimeCountFragmentOldDate();
        Bundle args = new Bundle();
        args.putString(TimeCountFragmentOldDate.DATE_FOR_SQL, dateForSql);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.dateForSql = getArguments().getString(TimeCountFragmentOldDate.DATE_FOR_SQL);
            this.date = Util.getDateFromDateForSql(this.dateForSql);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        timeCountActivity = (TimeCountActivity) getActivity();

        headerView = View.inflate(getActivity(), R.layout.old_date, null);
        TextView oldDate = (TextView) headerView.findViewById(R.id.old_date);
        oldDate.setText(date);

        View view = inflater.inflate(R.layout.fragment_main_all_app, container, false);
        bar = (BallView) view.findViewById(android.R.id.progress);
        allAppInfoListView = (ListView) view.findViewById(R.id.all_app_info_listView);

        //用asyncTask加载使用app信息
        AllAppInfoList update = new AllAppInfoList(allAppInfoListView);
        update.execute(null, null);
        return view;
    }

    @Override
    public void onDestroy() {
        arrayList = null;
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("OldDate"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("OldDate");
    }


    class AllAppInfoList extends AsyncTask<Integer, Integer, Integer> {


        public AllAppInfoList(ListView allAppInfoListView) {
            super();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            arrayList = MyApplication.dataResolver.getAppInfo(10000, dateForSql);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            bar.setVisibility(View.GONE);

            //适配数据
            allAppInfoListView.addHeaderView(headerView, null, false);
            allAppInfoListView.setAdapter(new AllAppListAdapter());

            //监听
            allAppInfoListView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return timeCountActivity.setMyTouchListener(event);
                }
            });

            allAppInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        timeCountActivity.showTheAppUsage(TimeCountFragmentOldDate.this, arrayList.get(position - 1).packageName);
                    }
                }
            });
        }


    }


    class AllAppListAdapter extends BaseAdapter {




        public final class ItemView {
            public ImageView appIcon;
            public TextView appName;
            public TextView useTimeText;
            public UseTimeView useTimeImage;
        }

        public AllAppListAdapter() {
            super();

        }

        @Override
        public int getCount() {
            return arrayList.size();
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

            if (position == 0) {
                if (max == 0) {
                    max = arrayList.get(position).usageTime;
                }
            }

            ItemView itemView = null;
            if (convertView == null) {
                itemView = new ItemView();
                convertView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_all_app_info, null);
                itemView.appIcon = (ImageView) convertView.findViewById(R.id.all_app_info_item_icon);
                itemView.appName = (TextView) convertView.findViewById(R.id.all_app_info_item_appName);
                itemView.useTimeText = (TextView) convertView.findViewById(R.id.all_app_info_item_timeText);
                itemView.useTimeImage = (UseTimeView) convertView.findViewById(R.id.all_app_info_item_timeImage);


                convertView.setTag(itemView);

            } else {
                itemView = (ItemView) convertView.getTag();
            }


            itemView.appIcon.setImageDrawable((arrayList.get(position).appIcon));
            itemView.appName.setText(arrayList.get(position).appName);
            itemView.useTimeText.setText(Util.getMinuteTimeFromSecondTimeInSql(arrayList.get(position).usageTime));
            itemView.useTimeImage.setMax(max);
            itemView.useTimeImage.setThisLength(arrayList.get(position).usageTime);
            itemView.useTimeImage.setBackgroundResource(R.drawable.usage_image);

/* MyLog.i_chenji_log(arrayList.get(position).usageTime+arrayList.get(position).appName);*/
            return convertView;
        }
    }

}
