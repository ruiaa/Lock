package com.chenji.lock.view.time;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.AppInfo;
import com.chenji.lock.view.UseTimeView;
import com.github.glomadrian.loadingballs.BallView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;


public class MainFragmentAllApp extends Fragment {

    public static final String ICON = "icon";
    public static final String APP_NAME = "app_name";
    public static final String TIME = "time";

    private int max=0;
    private ListView allAppInfoListView;
    private ArrayList<AppInfo> arrayList;

    private BallView bar;

    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_all_app, container, false);
        bar=(BallView)view.findViewById(android.R.id.progress);
        allAppInfoListView = (ListView) view.findViewById(R.id.all_app_info_listView);
        mainActivity = (MainActivity) getActivity();

        //用asyncTask加载使用app信息
        AllAppInfoList update = new AllAppInfoList();
        update.execute(null, null);
        return view;
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("allApp"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("allApp");
    }


    public void onDestroy() {
        arrayList=null;
        super.onDestroy();
    }

    class AllAppInfoList extends AsyncTask<Integer, Integer, Integer > {

        public AllAppInfoList() {
            super();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            arrayList = MyApplication.dataResolver.getAppInfo(10000);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            //listView适配
            allAppInfoListView.setAdapter(new AllAppListAdapter());

            bar.setVisibility(View.GONE);

            allAppInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String string =  arrayList.get(position).packageName;
                    if (string.equals("com.chenji.lock") ){
                        //不对自身加锁
                        Toast.makeText(getActivity(), R.string.toast_cannot_lock_itself, Toast.LENGTH_SHORT).show();
                    } else {
                        mainActivity.showLockFromAllApp(string, MainFragmentAllApp.this);
                    }
                }
            });
            allAppInfoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String string = arrayList.get(position).packageName;
                    if (string.equals("com.chenji.lock")) {
                        //不对自身加锁
                        Toast.makeText(getActivity(), R.string.toast_cannot_lock_itself, Toast.LENGTH_SHORT).show();
                    } else {
                        mainActivity.showLockFromAllApp(string, MainFragmentAllApp.this);
                    }
                    return true;
                }
            });


           /* SimpleAdapter simpleAdapter = new SimpleAdapter(MyApplication.getContext(),
                    arrayList,
                    R.layout.item_all_app_info,
                    new String[]{"icon", "appName", "timeText", "timeImage"},
                    new int[]{R.id.all_app_info_item_icon,
                            R.id.all_app_info_item_appName,
                            R.id.all_app_info_item_timeText,
                            R.id.all_app_info_item_timeImage}
            );*/

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    class AllAppListAdapter extends BaseAdapter {


        private final class ItemView {
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (position == 0) {
                if (max==0) {
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

            itemView.appIcon.setImageDrawable(arrayList.get(position).appIcon);
            itemView.appName.setText(arrayList.get(position).appName);
            itemView.useTimeText.setText(Util.getMinuteTimeFromSecondTimeInSql(arrayList.get(position).usageTime));
            if (arrayList.get(position).usageTime!=0) {
                itemView.useTimeImage.setMax(max);
                itemView.useTimeImage.setThisLength(arrayList.get(position).usageTime);
                itemView.useTimeImage.setBackgroundResource(R.drawable.usage_image);
            }else {
                itemView.useTimeImage.setBackgroundResource(R.color.transparency);
            }

            return convertView;
        }
    }

}
