package com.chenji.lock.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chenji.lock.R;
import com.chenji.lock.Util.Util;

import java.util.ArrayList;

/**
 * Created by 志瑞 on 2016/3/5.
 */
public class ChooseDate extends Dialog {

    private Context context;
    private OnChoose onChoose;

    TextView textView;
    SeekBar bar;

    ArrayList<String> arrayList;
    int thisNumber;
    int allCount;

    int w;

    public ChooseDate(Context context, OnChoose onChoose,ArrayList<String> list) {
        super(context, R.style.lock_list_dialog);
        this.context = context;
        this.onChoose = onChoose;
        this.arrayList = list;
        w=context.getResources().getDisplayMetrics().widthPixels-(int) Util.getDimen(R.dimen.item_big_2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_date, null);
        setContentView(view);

        textView = (TextView) view.findViewById(R.id.dialog_choose_date_text);
        bar = (SeekBar) view.findViewById(R.id.dialog_choose_date_bar);

        setDataList();
    }


    private void setDataList() {
        allCount = arrayList.size();
        if (allCount<=1){
            bar.setMax(1);
        }else {
            bar.setMax(allCount - 1);
        }

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (allCount>1) {
                    thisNumber = progress;
                    if (thisNumber >= 0 && thisNumber <= allCount - 1) {
                        textView.setText(Util.getDateFromDateForSql(arrayList.get(thisNumber)));
                    }
                }else if (allCount==1){
                    textView.setText(Util.getDateFromDateForSql(arrayList.get(0)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                onChoose.onChoose(thisNumber);
                ChooseDate.this.hide();
            }
        });

    }

    public void setShowingNumber(int showingNumber) {
        thisNumber = showingNumber;
    }



    @Override
    public void show() {
        Window win = this.getWindow();
        win.setGravity(Gravity.END | Gravity.TOP);

        super.show();

        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.x = (int) Util.getDimen(R.dimen.item_big_4);
        params.y = (int) Util.getDimen(R.dimen.item_big);
        params.width = w;
        win.setAttributes(params);

        if (thisNumber >= 0 && thisNumber <= allCount - 1) {
            textView.setText(Util.getDateFromDateForSql(arrayList.get(thisNumber)));
            bar.setProgress(thisNumber);
        }
    }

    public interface OnChoose {
        public void onChoose(int chooseNumber);
    }
}
