package com.chenji.lock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.Util;

/**
 * Created by 志瑞 on 2016/2/19.
 */
public class ChooseImageView extends ImageView {

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
    }

    public ChooseImageView(Context context) {
        super(context);
    }

    public ChooseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChooseImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private static Paint paint;
    private int width;
    private int height;
    private static double widthAndHeight;

    static {
        widthAndHeight = (double) (MyApplication.getContext().getResources().getDisplayMetrics().heightPixels) /
                (double) (MyApplication.getContext().getResources().getDisplayMetrics().widthPixels);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Util.getColor(R.color.text_gray));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
            canvas.drawRect(0, 0, width, height, paint);
            canvas.restore();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = (int) ((double) (MeasureSpec.getSize(widthMeasureSpec)) * widthAndHeight);
        setMeasuredDimension(width, height);
    }
}
