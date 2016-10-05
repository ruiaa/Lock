package com.chenji.lock.view.intercept.choose;


import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;
import com.chenji.lock.Util.Util;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseInterceptFragmentBackground extends Fragment {

    private final String IMAGE = "i";
    private final String ORDER = "o";

    GridView gridView;
    SetBackgroundAdapter setBackgroundAdapter;
    ButtonFloat editButton;
    TextView textView;

    private ProgressBarCircularIndeterminate bar;

    private ArrayList<Map<String, Object>> dataList;
    private int choseCount;


    private boolean stateForDelete = false;

    private boolean stateFirstCreate=true;

    String timeOrder;


    private ChooseInterceptActivity chooseInterceptActivity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public ChooseInterceptFragmentBackground() {
        // Required empty public constructor
    }

    private ArrayList<Map<String, Object>> getListAndChoseCount() {
        ArrayList<String> orderList = new ArrayList<>();

        ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> map;
        map = new HashMap<>();
        map.put(IMAGE, null);
        map.put(ORDER, "0");
        arrayList.add(map);
        orderList.add("0");

        map = new HashMap<>();
        map.put(IMAGE, null);
        map.put(ORDER, "1");
        arrayList.add(map);
        orderList.add("1");

        map = new HashMap<>();
        map.put(IMAGE, null);
        map.put(ORDER, "2");
        arrayList.add(map);
        orderList.add("2");

        try {
            String[] strings = MyApplication.getContext().fileList();

            if (strings.length != 0) {
                for (String s : strings) {
                    if (s.charAt(0) == 'i') {
                        map = new HashMap<>();
                        map.put(IMAGE, Util.getImage(s,4));
                        map.put(ORDER, s);
                        arrayList.add(map);
                        orderList.add(s);
                    }
                }
            }

            map = new HashMap<>();
            map.put(IMAGE, null);
            map.put(ORDER, "4");
            arrayList.add(map);
            choseCount = orderList.indexOf(sharedPreferences.getString(MyApplication.INTERCEPT_BACKGROUND_CHOSE, "0"));

            MyLog.i_chenji_log("getList  ok");
            return arrayList;
        } catch (Exception e) {
            MyLog.e_chenji_log("getList 失败", e);
            return arrayList;
        }
    }

    private int addImage() {
        String s = sharedPreferences.getString(MyApplication.INTERCEPT_BACKGROUND_CHOSE, "0");
        if (s.equals(timeOrder)) {
            Map<String, Object> map;
            map = new HashMap<>();
            map.put(IMAGE,Util.getImage(s,4));
            map.put(ORDER, s);
            int i=dataList.size();
            dataList.add(i - 1, map);
            return i-1;
        } else {
            return choseCount;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_intercept_background ,container, false);

        chooseInterceptActivity = (ChooseInterceptActivity) getActivity();
        sharedPreferences = getActivity().getSharedPreferences(MyApplication.FILE_SHARED, Activity.MODE_PRIVATE);

        bar=(ProgressBarCircularIndeterminate)view.findViewById(android.R.id.progress);
        gridView = (GridView) view.findViewById(R.id.intercept_background_grid_view);

        GetAllBackgroundAsyncTask getAllBackgroundAsyncTask=new GetAllBackgroundAsyncTask(gridView);
        getAllBackgroundAsyncTask.execute(null,null);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!stateForDelete) {
                    if (position == dataList.size() - 1) {
                        timeOrder = Util.getTimeOrderForIntercept();
                        chooseInterceptActivity.addImage(timeOrder);
                    } else if (position != choseCount) {
                        choseCount = position;
                        setBackgroundAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!stateForDelete) {
                    if (position == dataList.size() - 1) {
                        timeOrder = Util.getTimeOrderForBackground();
                        chooseInterceptActivity.addImage(timeOrder);
                    } else if (position != choseCount) {
                        choseCount = position;
                        setBackgroundAdapter.notifyDataSetChanged();
                    }
                }
                return true;
            }
        });

        editButton = (ButtonFloat) view.findViewById(R.id.intercept_background_edit);
        textView=(TextView)view.findViewById(android.R.id.text1);
        editButton.setDrawableIcon(Util.getDrawable(R.drawable.ic_delete_white));
        editButton.setBackgroundColor(Util.getColor(R.color.text_red));
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateForDelete) {
                    stateForDelete = false;
                    textView.setText(R.string.delete);
                    textView.setTextColor(Util.getColor(R.color.text_red));
                    editButton.setDrawableIcon(Util.getDrawable(R.drawable.ic_delete_white));
                    editButton.setBackgroundColor(Util.getColor(R.color.text_red));
                    setBackgroundAdapter.notifyDataSetChanged();
                } else {
                    stateForDelete = true;
                    textView.setText(R.string.finish);
                    textView.setTextColor(Util.getColor(R.color.text_green));
                    editButton.setDrawableIcon(Util.getDrawable(R.drawable.ic_sure_white));
                    editButton.setBackgroundColor(Util.getColor(R.color.text_green));
                    setBackgroundAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        editor = sharedPreferences.edit();
        editor.putString(MyApplication.INTERCEPT_BACKGROUND_CHOSE, (String) dataList.get(choseCount).get(ORDER));
        editor.apply();
        MobclickAgent.onPageEnd("chooseBackground");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stateFirstCreate){
            gridView.setSelection(choseCount);
            stateFirstCreate=false;
        }else if (ChooseInterceptActivity.State_Add_Image){
            ChooseInterceptActivity.State_Add_Image=false;
            choseCount = addImage();
            setBackgroundAdapter.notifyDataSetChanged();
            gridView.setSelection(choseCount);
        }

        MobclickAgent.onPageStart("chooseBackground");
    }

    @Override
    public void onDestroy() {
        dataList = null;
        super.onDestroy();
    }

    private class GetAllBackgroundAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        private GridView gridView;

        public GetAllBackgroundAsyncTask(GridView gridView) {
            super();
            this.gridView=gridView;
        }


        @Override
        protected Integer doInBackground(Integer... params) {
            dataList = getListAndChoseCount();
            return 1;
        }

        @Override
        protected void onPostExecute(Integer i) {
            setBackgroundAdapter = new SetBackgroundAdapter();

            bar.setVisibility(View.GONE);
            gridView.setAdapter(setBackgroundAdapter);

        }
    }

    private class SetBackgroundAdapter extends BaseAdapter {


        public final class ItemView {
            public ImageView imageView;
            public ImageView choseImage;
            public Button deleteButton;
        }

        public SetBackgroundAdapter() {
        }

        @Override
        public int getCount() {
            return dataList.size();
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
            ItemView itemView = null;
            if (convertView == null) {
                itemView = new ItemView();
                convertView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_choose_image, null);
                itemView.imageView = (ImageView) convertView.findViewById(R.id.choose_image);
                itemView.choseImage = (ImageView) convertView.findViewById(R.id.choose_image_chose);
                itemView.deleteButton = (Button) convertView.findViewById(R.id.choose_image_delete);
                convertView.setTag(itemView);
            } else {
                itemView = (ItemView) convertView.getTag();
            }

            switch (position) {
                case 0: {
                    itemView.imageView.setImageDrawable(Util.getDrawable(R.drawable.set_background_0));
                    itemView.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
                case 1: {
                    itemView.imageView.setImageDrawable(Util.getDrawable(R.drawable.set_background_1));
                    itemView.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
                case 2: {
                    itemView.imageView.setImageDrawable(Util.getDrawable(R.drawable.set_background_2));
                    itemView.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
                default: {
                    if (position == dataList.size() - 1){
                        itemView.imageView.setImageDrawable(Util.getDrawable(R.drawable.ic_add));
                        itemView.imageView.setScaleType(ImageView.ScaleType.CENTER);
                    }else {
                        itemView.imageView.setImageDrawable((Drawable) dataList.get(position).get(IMAGE));
                        itemView.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                    break;
                }
            }

            itemView.choseImage.setVisibility(View.GONE);
            itemView.deleteButton.setVisibility(View.GONE);

            if (choseCount == position) {
                itemView.choseImage.setVisibility(View.VISIBLE);
            }
            if (stateForDelete) {
                if (position >= MyApplication.INTERCEPT_BACKGROUND_WITH && position != dataList.size() - 1) {
                    itemView.deleteButton.setVisibility(View.VISIBLE);
                }
            }


            if (position >= MyApplication.INTERCEPT_BACKGROUND_WITH && position != dataList.size() - 1) {
                itemView.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (stateForDelete) {
                            if (choseCount == position) {
                                editor = sharedPreferences.edit();
                                editor.putString(MyApplication.INTERCEPT_BACKGROUND_CHOSE, "0");
                                editor.commit();
                                choseCount = 0;
                                setBackgroundAdapter.notifyDataSetChanged();
                            }
                            String s = (String) dataList.get(position).get(ORDER);
                            dataList.remove(position);
                            setBackgroundAdapter.notifyDataSetChanged();
                            Util.deleteImage(s);
                        }
                    }
                });
            }

            return convertView;
        }

    }
}
