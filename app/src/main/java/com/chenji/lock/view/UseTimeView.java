package com.chenji.lock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 志瑞 on 2016/2/14.
 */
public class UseTimeView extends View {


    private int max=0;
    private float thisLength=0;

/*    private RectF rectF;
    private static Paint paint;

    static {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Util.getColor(R.color.use_time_image));
    }*/

    public UseTimeView(Context context) {
        super(context);
    }

    public UseTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UseTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*private void getParams(AttributeSet attrs){
        *//**//*if (attrs!=null){
            TypedArray typedArray=getContext().obtainStyledAttributes(attrs, R.styleable.use_time_view);
            thisColor=typedArray.getColor(R.styleable.use_time_view_thisColor, Color.BLUE);
            max= typedArray.getInt(R.styleable.use_time_view_max,0);
            thisLength= typedArray.getInt(R.styleable.use_time_view_thisLength,0);
            typedArray.recycle();
        }*//**//*


        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(thisColor);
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

/*
            if (thisLength != 0) {
                rectF = new RectF(
                        0,
                        0,
                        (getMeasuredWidth()) * ((float) thisLength / (float) max),
                        getMeasuredHeight()
                );
                canvas.drawRoundRect(rectF, 24, 24, paint);
            }
*/

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (thisLength==0){
            setMeasuredDimension(0,0);
        }else {
            setMeasuredDimension((int)(MeasureSpec.getSize(widthMeasureSpec)*thisLength), MeasureSpec.getSize(heightMeasureSpec));
        }
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setThisLength(int thisLength) {
        if (thisLength!=0&&max!=0) {
            this.thisLength = (float)thisLength/(float)max;
        }else {
           this.thisLength=0;
        }
    }
}
