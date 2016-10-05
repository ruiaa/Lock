package com.chenji.lock.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.MyLog;
import com.chenji.lock.dataResolver.DataResolver;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by 志瑞 on 2016/1/22.
 */
public class FirstActivity extends Activity {
    private Activity mActivity;
    private ViewFlipper picture;
    private GestureDetector myGestureDetector;
    private int[] picture_id= new int[]{/*R.mipmap.welcome1, R.mipmap.welcome2, R.mipmap.welcome3, R.mipmap.welcome4*/};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_first);

        mActivity=this;
        myGestureDetector = new GestureDetector(this,new simpleGestureListener());
        picture=(ViewFlipper)findViewById(R.id.first_picture);
        // 添加图片源
        for (int i = 0; i < picture_id.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(picture_id[i]);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            picture.addView(iv);
        }

        picture.setFocusable(true);
        picture.setClickable(true);
        picture.setLongClickable(true);
        picture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return myGestureDetector.onTouchEvent(event);
            }
        });


    }

    private class simpleGestureListener extends
            GestureDetector.SimpleOnGestureListener {


        final int FLING_MIN_DISTANCE = 100;
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE) {

                if (picture.getDisplayedChild()==(picture_id.length-1)) {
                    finish();
                }
                else{
                    // Fling left
                    Animation lInAnim = AnimationUtils.loadAnimation(mActivity, R.anim.push_left_in);       // 向左滑动左侧进入的渐变效果（alpha 0.1  -> 1.0）
                    Animation lOutAnim = AnimationUtils.loadAnimation(mActivity, R.anim.push_left_out);     // 向左滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）

                    picture.setInAnimation(lInAnim);
                    picture.setOutAnimation(lOutAnim);
                    picture.showNext();
                }

            } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE) {
                if (!(picture.getDisplayedChild()==0)){
                    // Fling right
                    Animation rInAnim = AnimationUtils.loadAnimation(mActivity, R.anim.push_right_in);  // 向右滑动左侧进入的渐变效果（alpha  0.1 -> 1.0）
                    Animation rOutAnim = AnimationUtils.loadAnimation(mActivity, R.anim.push_right_out); // 向右滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）

                    picture.setInAnimation(rInAnim);
                    picture.setOutAnimation(rOutAnim);
                    picture.showPrevious();
                }

            }
            return true;
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (DataResolver.Exit_Lock&& MyApplication.dataResolver.getRunning()){
            MyLog.i_chenji_log("exit");
            System.exit(0);
        }
    }
}
