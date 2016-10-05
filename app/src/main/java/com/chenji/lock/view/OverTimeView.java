package com.chenji.lock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.chenji.lock.R;
import com.chenji.lock.Util.Util;

/**
 * Created by 志瑞 on 2016/3/7.
 */
public class OverTimeView extends View {

    private int use=1;
    private int all=1;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Util.getColor(R.color.over_time_use));
        RectF rectF = new RectF(
                0,
                0,
                (getMeasuredWidth()) * ((float) use / (float) all),
                getMeasuredHeight());
        canvas.drawRoundRect(rectF, 24, 24, paint);
    }


    public void setTime(int use,int all){
        this.use=use;
        this.all=all;
    }

    public OverTimeView(Context context) {
        super(context);
    }

    public OverTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
