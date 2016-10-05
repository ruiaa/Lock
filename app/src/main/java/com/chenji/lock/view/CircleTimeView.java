package com.chenji.lock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.chenji.lock.R;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.SqlInfo;

/**
 * Created by 志瑞 on 2016/3/17.
 */
public class CircleTimeView extends View {

    private int width;
    private int type;
    private int circleRadius;

    private int startTime=0;
    private int finishTime=0;
    private int overTime=0;
    private int thisTime=0;

    private static Paint textPaint;
    private static Paint lockUsePaint;
    private static Paint lockForbiddenPaint;
    private static Paint usePaint;
    private static Paint uselessPaint;
    private static Paint circlePaint;



    static {
        textPaint=new Paint();
        textPaint.setColor(Util.getColor(R.color.text_gray));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(Util.getDimen(R.dimen.text_small));

        lockUsePaint=new Paint();
        lockUsePaint.setColor(Util.getColor(R.color.lock_type_use));
        lockUsePaint.setStrokeWidth(50);
        lockUsePaint.setAntiAlias(true);
        lockUsePaint.setStyle(Paint.Style.STROKE);

        lockForbiddenPaint=new Paint();
        lockForbiddenPaint.setColor(Util.getColor(R.color.lock_type_forbidden));
        lockForbiddenPaint.setStrokeWidth(50);
        lockForbiddenPaint.setAntiAlias(true);
        lockForbiddenPaint.setStyle(Paint.Style.STROKE);

        usePaint=new Paint();
        usePaint.setColor(Util.getColor(R.color.lock_use_time));
        usePaint.setStrokeWidth(50);
        usePaint.setAntiAlias(true);
        usePaint.setStyle(Paint.Style.STROKE);

        uselessPaint=new Paint();
        uselessPaint.setColor(Util.getColor(R.color.lock_useless));
        uselessPaint.setStrokeWidth(50);
        uselessPaint.setAntiAlias(true);
        uselessPaint.setStyle(Paint.Style.STROKE);

        circlePaint=new Paint();
        circlePaint.setColor(Util.getColor(R.color.text_gray));
        circlePaint.setStrokeWidth(5);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
    }

    public void setData(int type,int startTime,int finishTime,int overTime){
        this.type=type;
        this.startTime=startTime;
        this.finishTime=finishTime;
        this.overTime=overTime;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       int w=MeasureSpec.getSize(widthMeasureSpec);
        int h=MeasureSpec.getSize(heightMeasureSpec);
        if (w>=h){
            width=h;
        }else {
            width=w;
        }
        circleRadius=width/3;
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        thisTime=Util.getNowSecond()/60;
        canvas.drawCircle(width/2,width/2,width/3,circlePaint);
        for (int i=1;i<=24;i=i+1){
            canvas.save();
            canvas.rotate((i - 1) * 360 / 24, width/2,width/2);
            canvas.drawText(String.valueOf(i-1),width/2,width/2-circleRadius+50,textPaint);
            canvas.restore();
        }

        RectF rectF=new RectF(width/2-circleRadius-28,
                width/2-circleRadius-28,
                width/2+circleRadius+28,
                width/2+circleRadius+28);

        if (type== SqlInfo.LOCK_TYPE_OVER_TIME){
            canvas.drawArc(rectF,
                    270,
                    360 * overTime / (24 * 60),
                    true, lockForbiddenPaint);

            canvas.drawArc(rectF,
                    360 * overTime / (24 * 60) - 90,
                    360- 360 * overTime / (24 * 60),
                    false, uselessPaint);
        }else if (type==SqlInfo.LOCK_TYPE_USE){
            canvas.drawArc(rectF,
                    360 * thisTime / (24 * 60) - 90,
                    360 * finishTime / (24 * 60) - 360 * thisTime / (24 * 60),
                    false, lockUsePaint);
            canvas.drawArc(rectF,
                    360 * startTime / (24 * 60) - 90,
                    360 * thisTime / (24 * 60) - 360 * startTime / (24 * 60),
                    false, usePaint);
            canvas.drawArc(rectF,
                    360 * finishTime / (24 * 60) - 90,
                    360 * startTime / (24 * 60) +(360- 360 * finishTime / (24 * 60)),
                    false, uselessPaint);
        }else {
            canvas.drawArc(rectF,
                    360*thisTime/(24*60)-90,
                    360*finishTime/(24*60)-360*thisTime/(24*60),
                    false,lockForbiddenPaint);
            canvas.drawArc(rectF,
                    360 * startTime / (24 * 60) - 90,
                    360 * thisTime / (24 * 60) - 360 * startTime / (24 * 60),
                    false, usePaint);
            canvas.drawArc(rectF,
                    360 * finishTime / (24 * 60) - 90,
                    360 * startTime / (24 * 60) +(360- 360 * finishTime / (24 * 60)),
                    false, uselessPaint);
        }
    }

    public CircleTimeView(Context context) {
        super(context);
    }

    public CircleTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
