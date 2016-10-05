package com.chenji.lock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.chenji.lock.R;
import com.chenji.lock.Util.Util;

/**
 * Created by 志瑞 on 2016/2/28.
 */
public class LockTimeView extends View {

    private int x;
    private int y;

    private static Paint paint;

    static {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Util.getColor(R.color.use_time_image));
    }

    public LockTimeView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
