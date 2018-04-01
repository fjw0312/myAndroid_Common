package com.views;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by jiongfang on 2018/3/15.
 *  垂直 seekBar  自定义控件
 */
public class VerticalSeekBar extends AppCompatSeekBar {

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    private int mProgress = 0;

    public synchronized void setmProgress(int progress) {
        setProgress(progress+12);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }
    public int getmProgress(){
        return getProgress()-12;
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
               setStartTrackingTouch(this);
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                setStopTrackingTouch(this);
                break;

            case MotionEvent.ACTION_CANCEL:
                Log.i("Jiong","ACTION_CANCEL");
                break;
        }
        return true;
    }

    TrackingTouchListener trackingTouchListener;
    public interface TrackingTouchListener{
        public void OnStartTrackingTouch(VerticalSeekBar verticalSeekBar);
        public void OnStopTrackingTouch(VerticalSeekBar verticalSeekBar);
    }

    public void setTrackingTouchListener(TrackingTouchListener trackingTouchListener){
        this.trackingTouchListener = trackingTouchListener;
    }
    private void setStartTrackingTouch(VerticalSeekBar verticalSeekBar){
        if(trackingTouchListener!=null){
            trackingTouchListener.OnStartTrackingTouch(verticalSeekBar);
        }

    }
    private void setStopTrackingTouch(VerticalSeekBar verticalSeekBar){
        if(trackingTouchListener!=null) {
            trackingTouchListener.OnStopTrackingTouch(verticalSeekBar);
        }
    }

}
