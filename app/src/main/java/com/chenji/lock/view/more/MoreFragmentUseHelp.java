package com.chenji.lock.view.more;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.Util;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

public class MoreFragmentUseHelp extends Fragment {

    MoreActivity moreActivity;


    public MoreFragmentUseHelp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_more_use_help, container, false);
        moreActivity=(MoreActivity)getActivity();

        ListView listView=(ListView)view.findViewById(R.id.more_use_help_list);
        listView.setAdapter(new UseHelpAdapter(getData()));

        return view;
    }



    private ArrayList<HashMap<String,String>> getData(){
        ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
        HashMap<String,String> hashMap;
        hashMap=new HashMap<>();
        hashMap.put("q", Util.getString(R.string.question_lock));
        hashMap.put("a",Util.getString(R.string.answer_lock));
        arrayList.add(hashMap);

        hashMap=new HashMap<>();
        hashMap.put("q", Util.getString(R.string.question_lock_forbidden));
        hashMap.put("a",Util.getString(R.string.answer_lock_forbidden));
        arrayList.add(hashMap);

        hashMap=new HashMap<>();
        hashMap.put("q", Util.getString(R.string.question_lock_use));
        hashMap.put("a",Util.getString(R.string.answer_lock_use));
        arrayList.add(hashMap);

        hashMap=new HashMap<>();
        hashMap.put("q", Util.getString(R.string.question_lock_over));
        hashMap.put("a",Util.getString(R.string.answer_lock_over));
        arrayList.add(hashMap);

        hashMap=new HashMap<>();
        hashMap.put("q", Util.getString(R.string.question_judge_interval));
        hashMap.put("a",Util.getString(R.string.answer_judge_interval));
        arrayList.add(hashMap);

        hashMap=new HashMap<>();
        hashMap.put("q", Util.getString(R.string.question_no_date));
        hashMap.put("a",Util.getString(R.string.answer_no_date));
        arrayList.add(hashMap);



        return arrayList;
    }

    class UseHelpAdapter extends BaseAdapter{

        ArrayList<HashMap<String,String>> arrayList;

        public UseHelpAdapter(ArrayList<HashMap<String,String>> arrayList) {
            super();
            this.arrayList=arrayList;
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

        private final class ItemView{
            public TextView question;
            public TextView answer;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemView itemView = null;
            if (convertView == null) {
                itemView = new ItemView();
                convertView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_q_and_a, null);
                itemView.question=(TextView)convertView.findViewById(R.id.item_help_question);
                itemView.answer=(TextView)convertView.findViewById(R.id.item_help_answer);
                convertView.setTag(itemView);
            } else {
                itemView = (ItemView) convertView.getTag();
            }



            itemView.question.setText(arrayList.get(position).get("q"));
            itemView.answer.setText(arrayList.get(position).get("a"));

            return convertView;
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("moreHelp"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("moreHelp");
    }
}
