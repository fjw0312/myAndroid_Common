package com.activitybase;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jiongfang on 2018/5/7.
 * activity 自定义基类  实现 页面右滑退出   ----  该功能 可以考虑 开源框架https://github.com/oubowu/SlideBack
 * 使用方式 直接继承  并AndroidMain.xml 配置 them="myThem"
 */
public class SlideBaseActivity extends AppCompatActivity {

    View decorView;
    int screenWidth;//屏宽

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;

//        setContentView(R.layout.activity_test);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    float startX, startY, endX, endY, distanceX, distanceY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = event.getX();
                endY = event.getY();
                distanceX = endX - startX;
                distanceY = Math.abs(endY - startY);
                //1.判断手势右滑  2.横向滑动的距离要大于竖向滑动的距离
                if (endX - startX > 0 && distanceY < distanceX) {
                    decorView.setX(distanceX);
                }
                break;
            case MotionEvent.ACTION_UP:
                endX = event.getX();
                distanceX = endX - startX;
                endY = event.getY();
                distanceY = Math.abs(endY - startY);
                //1.判断手势右滑  2.横向滑动的距离要大于竖向滑动的距离 3.横向滑动距离大于屏幕2分之一才能finish
                if (endX - startX > 0 && distanceY < distanceX && distanceX > screenWidth / 2) {
                    moveOn(distanceX);
                }
                //1.判断手势右滑  2.横向滑动的距离要大于竖向滑动的距离 但是横向滑动距离不够则返回原位置
                else if (endX - startX > 0 && distanceY < distanceX) {
                    backOrigin(distanceX);
                } else {
                    decorView.setX(0);
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }


    /**
     * 返回原点
     *
     * @param distanceX 横向滑动距离
     */
    private void backOrigin(float distanceX) {
        ObjectAnimator.ofFloat(decorView, "X", distanceX, 0).setDuration(300).start();
    }

    /**
     * 划出屏幕
     *
     * @param distanceX 横向滑动距离
     */
    private void moveOn(float distanceX) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(distanceX, screenWidth);
        valueAnimator.setDuration(300);
        valueAnimator.start();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                decorView.setX((Float) animation.getAnimatedValue());
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    /**
     * 如果使用 开源框架：https://github.com/oubowu/SlideBack
     *API说明
     1.先 继承 SlideBackActivity
     void setSlideable(boolean) —— 设置滑动返回是否可用，false不可用，默认为true
     void setPreviousActivitySlideFollow(boolean) —— 设置前一个activity的页面是否跟随滑动面一起滑动，false不滑动，默认为true
     void onSlideBack() —— 滑动退出时调用的回调方法，派生类可以重写这个方法，例如可以做一些统计工作，统计关闭activity的方式，多少是滑动返回关闭的
     void setShadowResource(int) —— 设置阴影的资源id，一般都是写一个shape drawable
     *
     * */
}
