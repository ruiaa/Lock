package com.chenji.lock.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.SqlInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 志瑞 on 2016/2/26.
 */
public class LockListDialog extends Dialog {

    private Context context;
    private ArrayList<HashMap<String, Object>> arrayList;
    private OnDialogClickListener onDialogClickListener;
    private int lockType;


    public LockListDialog(Context context,
                          ArrayList<HashMap<String, Object>> arrayList,
                          OnDialogClickListener onDialogClickListener,
                          int lockType) {
        super(context, R.style.lock_list_dialog);
        this.context = context;
        this.arrayList = arrayList;
        this.onDialogClickListener = onDialogClickListener;
        this.lockType = lockType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog



        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_icon_with_lock, null);
        setContentView(view);

        TextView textView = (TextView) view.findViewById(R.id.icon_with_lock_type);
        if (lockType == SqlInfo.LOCK_TYPE_USE) {
            textView.setText(R.string.lock_use_new);
            textView.setTextColor(Util.getColor(R.color.lock_type_use));
        } else {
            textView.setText(R.string.lock_forbidden_new);
            textView.setTextColor(Util.getColor(R.color.lock_type_forbidden));
        }

        ListView listView = (ListView) view.findViewById(R.id.icon_with_lock_icon);
        listView.setAdapter(new LockListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDialogClickListener.onClick((String) arrayList.get(position).get(SqlInfo.PACKAGE));
                arrayList = null;
                LockListDialog.this.dismiss();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onDialogClickListener.onClick((String) arrayList.get(position).get(SqlInfo.PACKAGE));
                arrayList = null;
                LockListDialog.this.dismiss();
                return true;
            }
        });

    }

    @Override
    public void show() {
        Window win = this.getWindow();
        win.setGravity(Gravity.END | Gravity.TOP);

        super.show();

        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.x=(int) Util.getDimen(R.dimen.item_big);
        params.y = (int) Util.getDimen(R.dimen.item_big);
        params.width = (int)Util.getDimen(R.dimen.item_xBig);
        win.setAttributes(params);


    }

    //监听器接口
    public interface OnDialogClickListener {
        public void onClick(String packageName);
    }

    //dialog 的lockList
    private class LockListAdapter extends BaseAdapter {

        private LayoutInflater listContainer;

        public final class ItemView {
            public ImageView appIcon;
        }

        public LockListAdapter() {
            super();
            listContainer = LayoutInflater.from(MyApplication.getContext());
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
            ItemView itemView = null;
            if (convertView == null) {
                itemView = new ItemView();
                convertView = listContainer.inflate(R.layout.item_icon, null);
                itemView.appIcon = (ImageView) convertView.findViewById(R.id.item_icon_icon);
                convertView.setTag(itemView);
            } else {
                itemView = (ItemView) convertView.getTag();
            }
            itemView.appIcon.setImageDrawable((Drawable) arrayList.get(position).get(SqlInfo.ICON));
            return convertView;
        }
    }

}
