package com.views;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.utils.LogcatFileHelper;

/**
 * Created by jiongfang on 2018/3/15.
 * 垂直 seekBar  自定义控件
 */
public class VerticalSeekBar extends AppCompatSeekBar {

    public VerticalSeekBar(Context context) {
        super(context);
        initHintPopup(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHintPopup(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {

        super(context, attrs);
        initHintPopup(context);
    }

    PopupWindow mPopup;
    TextView mPopupTextView;
    int mPopupWidth = 80;

    public void initHintPopup(Context context) {
        /*
        String popupText = null;
        View contentView=LayoutInflater.from(context).inflate(R.layout.popup, null, false);
        mPopup = new PopupWindow(contentView, 100, 100, true);
       // mPopup = new PopupWindow(contentView, mPopupWidth, ViewGroup.LayoutParams.WRAP_CONTENT, false);

        mPopup.setBackgroundDrawable(new ColorDrawable(Color.RED));// 设置PopupWindow的背景
        mPopup.setOutsideTouchable(true); // // 设置PopupWindow是否能响应外部点击事件
        //mPopup.setTouchable(true);  // 设置PopupWindow是否能响应点击事件
        // 显示PopupWindow，其中：
        // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
        mPopup.showAsDropDown(this, 20, 30);
        // 或者也可以调用此方法显示PopupWindow，其中：
         // 第一个参数是PopupWindow的父View，第二个参数是PopupWindow相对父View的位置，
         // 第三和第四个参数分别是PopupWindow相对父View的x、y偏移
        //mPopup.showAtLocation(this, Gravity.TOP, 20, 20);
        */
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
        mProgress = progress;
        setProgress(mProgress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    public int getmProgress() {
        return getProgress();
    }

    protected void onDraw(Canvas c) {
        /*
        if(isShowTink){
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setTextSize(20);
            RectF rectF = new RectF();
            rectF.left = eventX;
            rectF.right = eventX-50;
            rectF.top = eventY;
            rectF.bottom = eventY+50;
            c.drawText(String.valueOf(mProgress),eventX,eventY,paint);
            LogcatFileHelper.i("Jiong》》","seekbar 绘制刻度！！！！！！！！！！");
        }
        */
        c.rotate(-90);
        c.translate(-getHeight(), 0);
        super.onDraw(c);


    }

    int mprocess;
    int currentProcess;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mprocess = getMax() - (int) (getMax() * event.getY() / getHeight());
                currentProcess = getmProgress();
                if (mprocess != currentProcess) {
                    setmProgress(mprocess);
                    setChangeProcess(this, mprocess);
                }
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                setStartTrackingTouch(this);
                //        Log.i("Jiong>.","dispatchTouchEvent   .ACTION_DOWN ");
                break;
            case MotionEvent.ACTION_MOVE:
                mprocess = getMax() - (int) (getMax() * event.getY() / getHeight());
                currentProcess = getmProgress();
                if (mprocess != currentProcess) {
                    //            Log.i("Jiong>.","dispatchTouchEvent   .ACTION_MOVE  set  mprocess="+mprocess+"    currentProcess="+currentProcess);
                    setmProgress(mprocess);
                    setChangeProcess(this, mprocess);
                }
                //        Log.i("Jiong>.","dispatchTouchEvent   .ACTION_MOVE ");
                break;
            case MotionEvent.ACTION_UP:
                mprocess = getMax() - (int) (getMax() * event.getY() / getHeight());
                currentProcess = getmProgress();
                if (mprocess != currentProcess) {
                    setmProgress(mprocess);
                    setChangeProcess(this, mprocess);
                }
                //        Log.i("Jiong>.","dispatchTouchEvent   .ACTION_UP ");
                setStopTrackingTouch(this);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    boolean isShowTink = false;
    float eventX = 0f;
    float eventY = 0f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isShowTink = true;

            case MotionEvent.ACTION_MOVE:
                isShowTink = true;

            case MotionEvent.ACTION_UP:
                isShowTink = false;

                break;

            case MotionEvent.ACTION_CANCEL:
                isShowTink = false;
                Log.i("Jiong", "ACTION_CANCEL");
                break;
        }
        return true;
    }

    // 宽高值互换处理
    private void trackTouchEvent(MotionEvent event) {
        final int height = getHeight();
        final int available = height - getPaddingBottom() - getPaddingTop();
        int Y = (int) event.getY();
        float scale;
        float progress = 0;
        if (Y > height - getPaddingBottom()) {
            scale = 0.0f;
        } else if (Y < getPaddingTop()) {
            scale = 1.0f;
        } else {
            scale = (float) (height - getPaddingBottom() - Y) / (float) available;
        }
        final int max = getMax();
        progress = scale * max;
        setProgress((int) progress);
        LogcatFileHelper.i("Jiong>>", "VerticalSeekBar 设置进度 progress=" + progress);
    }


    TrackingTouchListener trackingTouchListener;

    public interface TrackingTouchListener {
        public void OnStartTrackingTouch(VerticalSeekBar verticalSeekBar);

        public void OnStopTrackingTouch(VerticalSeekBar verticalSeekBar);

        public void OnProcessChange(VerticalSeekBar verticalSeekBar, int process);
    }

    public void setTrackingTouchListener(TrackingTouchListener trackingTouchListener) {
        this.trackingTouchListener = trackingTouchListener;
    }

    private void setStartTrackingTouch(VerticalSeekBar verticalSeekBar) {
        if (trackingTouchListener != null) {
            trackingTouchListener.OnStartTrackingTouch(verticalSeekBar);
        }

    }

    private void setStopTrackingTouch(VerticalSeekBar verticalSeekBar) {
        if (trackingTouchListener != null) {
            trackingTouchListener.OnStopTrackingTouch(verticalSeekBar);
        }
    }

    private void setChangeProcess(VerticalSeekBar verticalSeekBar, int process) {
        if (trackingTouchListener != null) {
            trackingTouchListener.OnProcessChange(verticalSeekBar, process);
        }
    }

}
