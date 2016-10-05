package com.chenji.lock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.chenji.lock.R;
import com.chenji.lock.Util.Util;

import java.util.ArrayList;

/**
 * Created by 志瑞 on 2016/3/12.
 */
public class GestureLock extends View {

    private static final int TOUCH_POINT_DURATION  = 150;// 点选中时候的动画时间
    private static final int WRONG_DURATION = 1500;// 错误路径持续时长

    //颜色
    private static final int ERROR_COLOR = Util.getColor(R.color.password_wrong);
    private static final int BETWEEN_COLOR = Util.getColor(R.color.password_between);

    //画笔
    private static Paint Point_wrong_paint;
    private static Paint Point_paint;

    private static Paint Line_wrong_paint;
    private static Paint Line_paint;

    static {
        Point_wrong_paint = new Paint();
        Point_wrong_paint.setColor(ERROR_COLOR);
        Point_wrong_paint.setStyle(Paint.Style.FILL_AND_STROKE);

        Point_paint = new Paint();
        Point_paint.setColor(BETWEEN_COLOR);
        Point_paint.setStyle(Paint.Style.FILL_AND_STROKE);


        Line_paint = new Paint();
        Line_paint.setColor(BETWEEN_COLOR);
        Line_paint.setStyle(Paint.Style.STROKE);

        Line_wrong_paint = new Paint();
        Line_wrong_paint.setColor(ERROR_COLOR);
        Line_wrong_paint.setStyle(Paint.Style.STROKE);

    }

    //样式
    private static final int ROW_COUNT = 3;
    private static final int COLUMN_COUNT = 3;

    private static float Padding = 1;
    private static float Distance_Between_Points = 1;

    private static float Point_Radius = 1;
    private static float Distance_Touch_Point = 1;


    //view 大小
    private int width;

    //手势变量
    private Path confirmPath;
    private float[] movingPath;
    private ArrayList<MyPoint> myPoints;

    //状态
    private boolean gestureNormal=true;

    private class MyPoint {
        int number;  //九宫格 1~~9
        float x;
        float y;
        boolean selected=false;

        public MyPoint(float x, float y, int number) {
            this.number = number;
            this.x = x;
            this.y = y;
        }

        // 判定是否应该连接该点
        public boolean isTouch(float x, float y) {
            if (selected){
                return false;
            }else {
                boolean isTouch = (x - this.x) * (x - this.x) + (this.y - y) * (this.y - y)
                        <= Distance_Touch_Point * Distance_Touch_Point;
                selected=isTouch;
                return isTouch;
            }
        }

    }

    private void initEntry() {
        confirmPath = new Path();
        movingPath = new float[4];


        myPoints = new ArrayList<>(ROW_COUNT * COLUMN_COUNT);
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < ROW_COUNT; j++) {
                int index = myPoints.size();
                myPoints.add(index, new MyPoint(i * Distance_Between_Points + Padding, j * Distance_Between_Points + Padding, index + 1));
            }
        }

    }
    private void resetEntry(){
        confirmPath.reset();
        movingPath=new float[4];
        for (MyPoint myPoint:myPoints){
            myPoint.selected=false;
        }
    }
    private String getLineCode(){
        String s="";
        for (MyPoint myPoint:myPoints){
            if (myPoint.selected) {
                s = s + myPoint.number;
            }
        }
        return s;
    }


    public GestureLock(Context context) {
        super(context);
        initEntry();
    }

    public GestureLock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEntry();
    }

    public GestureLock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEntry();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (gestureNormal) {

            for (MyPoint myPoint : myPoints) {
                canvas.drawCircle(myPoint.x, myPoint.y, Point_Radius, Point_paint);
            }
            canvas.drawPath(confirmPath,Line_paint);
            canvas.drawLine(movingPath[0],movingPath[1],movingPath[2],movingPath[3],Line_paint);

        } else {

            for (MyPoint myPoint : myPoints) {
                canvas.drawCircle(myPoint.x, myPoint.y, Point_Radius, Point_wrong_paint);
            }
            canvas.drawPath(confirmPath,Line_wrong_paint);
            canvas.drawLine(movingPath[0], movingPath[1], movingPath[2], movingPath[3], Line_wrong_paint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        w = Math.min(w, h);
        setMeasuredDimension(w, w);
        width = w;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eX = event.getX();
        float eY = event.getY();
        int action = event.getAction();

        MyPoint touchMyPoint=null;
        boolean touch=false;
        for (MyPoint myPoint:myPoints){
            if (myPoint.isTouch(eX,eY)){
               touchMyPoint=myPoint;
                touch=true;
                break;
            }
        }


        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (touch) {
                    confirmPath.moveTo(touchMyPoint.x, touchMyPoint.y);
                    movingPath[0] = touchMyPoint.x;
                    movingPath[1] = touchMyPoint.y;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (touch) {
                    if (confirmPath.isEmpty()) {
                        confirmPath.moveTo(touchMyPoint.x, touchMyPoint.y);
                        movingPath[0] = touchMyPoint.x;
                        movingPath[1] = touchMyPoint.y;
                    } else {
                        confirmPath.lineTo(touchMyPoint.x, touchMyPoint.y);
                        movingPath[0] = touchMyPoint.x;
                        movingPath[1] = touchMyPoint.y;
                    }
                }
                movingPath[2] = eX;
                movingPath[3] = eY;

                invalidate();
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                movingPath = new float[4];
                if (touch) {
                    confirmPath.lineTo(touchMyPoint.x, touchMyPoint.y);
                }
                if (getLineCode().length() > 0) {
                    if (null != callback) {
                        callback.onFinish(getLineCode(), result);
                    }
                    if (result.isRight) {
                        gestureNormal=true;
                    } else {
                        gestureNormal = false;
                        handler.postDelayed(task, WRONG_DURATION);
                    }
                }
                invalidate();
                break;
            }
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }


    private Handler handler = new Handler();
    private Runnable task = new Runnable() {

        @Override
        public void run() {
            resetEntry();
        }
    };


    private Result result;

    private GestureLockCallback callback;

    public void setCallback(GestureLockCallback callback) {
        this.callback = callback;
    }

    public class Result {
        private boolean isRight=false;

        public boolean isRight() {
            return isRight;
        }

        public void setRight(boolean isRight) {
            this.isRight = isRight;
        }

    }

    public interface GestureLockCallback {
        public void onFinish(String pwdString, Result result);
    }
}
