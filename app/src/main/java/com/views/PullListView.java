
package com.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ListView;


public class PullListView extends ListView {
    private Context mContext;

    private HeaderView mSlideHeaderView;

    private View headContentView;
    private View headView;

    private float mY = 0;

    private float mHistoricalY = 0;

    private int mHistoricalTop = 0;

    private int mInitialHeight = 0;

    private boolean mFlag = false;

    private boolean mSlideEnable = true;

    // 滑动方向是否向上
    private boolean mDirectionUp = false;

    private boolean mIsRefreshing = false;

    private OnRollBackListener mListener = null;

    private static final int REFRESH = 0;

    private static final int NORMAL = 1;

    // public KGPullListView(final Context context) {
    // super(context);
    // initialize(context);
    // }

    public PullListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    // public KGPullListView(final Context context, final AttributeSet attrs,
    // final int defStyle) {
    // super(context, attrs, defStyle);
    // initialize(context);
    // }

    /**
     * 设置回弹监听器
     *
     * @param listener
     */
    public void setOnRollBackListener(final OnRollBackListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(REFRESH);
                mHandler.removeMessages(NORMAL);
                mY = mHistoricalY = ev.getY();
                mInitialHeight = mSlideHeaderView.getSlideHeaderViewHeight();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private float freePercent = 0.0f;

    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_DOWN:
                freePercent = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mHistoricalTop = getChildAt(0).getTop();
                break;
            case MotionEvent.ACTION_UP:
                if (mSlideEnable) {
                    freePercent = mSlideHeaderView.getSlidePercent();
                    if (!mIsRefreshing) {
                        if (mDirectionUp) {
                            startRefreshing();
                            mHandler.sendMessage(mHandler.obtainMessage(REFRESH,
                                    (int) (ev.getY() - mY) / 2 + mInitialHeight, 0));
                        } else {
                            if (getChildAt(0).getTop() == 0) {
                                mHandler.sendMessage(mHandler.obtainMessage(NORMAL,
                                        (int) (ev.getY() - mY) / 2 + mInitialHeight, 0));
                            }
                        }
                    } else {
                        mHandler.sendMessage(mHandler.obtainMessage(REFRESH, (int) (ev.getY() - mY)
                                / 2 + mInitialHeight, 0));
                    }
                    mFlag = false;
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE && getFirstVisiblePosition() == 0
                && mSlideEnable) {
            float direction = ev.getY() - mHistoricalY;
            int height = (int) (ev.getY() - mY) / 2 + mInitialHeight;
            if (height < 0) {
                height = 0;
            }

            float deltaY = Math.abs(mY - ev.getY());
            ViewConfiguration config = ViewConfiguration.get(getContext());
            if (deltaY > config.getScaledTouchSlop()) {

                // Scrolling downward
                if (direction > 0) {
                    // Refresh bar is extended if top pixel of the first item is
                    // visible
                    if (getChildAt(0) != null && getChildAt(0).getTop() == 0) {
                        if (mHistoricalTop < 0) {

                            // mY = ev.getY(); // TODO works without
                            // this?mHistoricalTop = 0;
                        }
                        setHeaderHeight(height);
                        if (mListener != null && mSlideHeaderView != null) {
                            float percent = mSlideHeaderView.getSlidePercent();
                            mListener.onRolling(this, percent, 0, true);
                        }
                        // Stop list scroll to prevent the list from
                        // overscrolling
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        mFlag = false;
                    }
                } else if (direction < 0) {
                    if (getChildAt(0) != null && getChildAt(0).getTop() == 0) {
                        setHeaderHeight(height);
                        if (mListener != null && mSlideHeaderView != null) {
                            float percent = mSlideHeaderView.getSlidePercent();
                            mListener.onRolling(this, percent, 0, true);
                        }
                        if (getChildAt(1) != null && getChildAt(1).getTop() <= 1 && !mFlag) {
                            ev.setAction(MotionEvent.ACTION_DOWN);
                            mFlag = true;
                        }
                    }
                }
            }
            mHistoricalY = ev.getY();
        }
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    private void initialize(Context ctx) {
        mContext = ctx;
        mSlideHeaderView = new HeaderView(ctx);
        setHeaderHeight(mSlideHeaderView.getDefaultSlideHeaderViewHeight());
    }

    private void setHeaderHeight(final int height) {
        mSlideHeaderView.setSlideHeaderViewHeight(height, headContentView);
        if (!mIsRefreshing) {
            if (height > mSlideHeaderView.getDefaultSlideHeaderViewHeight() && !mDirectionUp) {
                mDirectionUp = true;
            } else if (height < mSlideHeaderView.getDefaultSlideHeaderViewHeight() && mDirectionUp) {
                mDirectionUp = false;
            }
        }
    }

    private void startRefreshing() {
        mIsRefreshing = true;

        if (mListener != null) {
            mListener.onRollBack(this);
        }
    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            int limit = 0;
            switch (msg.what) {
                case REFRESH:
                    limit = mSlideHeaderView.getDefaultSlideHeaderViewHeight();
                    break;
                case NORMAL:
                    limit = 0;
                    break;
            }

            if (msg.arg1 >= limit) {
                setHeaderHeight(msg.arg1);
                if (mListener != null && mSlideHeaderView != null) {
                    mListener.onRolling(PullListView.this, mSlideHeaderView.getSlidePercent(), freePercent, false);
                }
                int displacement = (msg.arg1 - limit) / 3;
                if (displacement == 0) {
                    mHandler.sendMessage(mHandler.obtainMessage(msg.what, msg.arg1 - 1, 0));
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(msg.what, msg.arg1 - displacement,
                            0));
                }
            }
        }

    };

    // 回滚监听器，在拖动列表，回弹的时候触发
    public interface OnRollBackListener {
        void onRollBack(PullListView listView);

        /**
         * @param listView    当前拖动的ListView
         * @param rollPercent 目前滚动的百分比
         * @param freePercent 触摸释放时的百分比
         * @param pullDown    是否"下拉”
         */
        void onRolling(PullListView listView, float rollPercent, float freePercent, boolean pullDown);
    }

    /**
     * 设置可拉动header，代替addHeader使用
     *
     * @param view
     */
    public void setSlideHeaderView(View view) {
        this.headContentView = view;
        FrameLayout container = new FrameLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 0);
        container.setLayoutParams(params);
        container.addView(mSlideHeaderView, 0);
        container.addView(headContentView, 1);
        super.addHeaderView(container);
    }


    @Override
    public void addHeaderView(View v) {
        super.addHeaderView(v);
        this.headView = v;
    }

    private boolean isAutoContentHeight = false;
    private boolean haveSetHeaderHeight = false;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!haveSetHeaderHeight) {
            View child4Measure = null;
            if (!isAutoContentHeight) {
                int dftSlideHeaderHeight = mSlideHeaderView.getDefaultSlideHeaderViewHeight();
                if (dftSlideHeaderHeight == HeaderView.WRAP_CONTENT_HEIGHT) {
                    child4Measure = headContentView;
                    isAutoContentHeight = true;
                } else if (dftSlideHeaderHeight == HeaderView.WRAP_HEAD_HEIGHT) {
                    child4Measure = headView;
                    isAutoContentHeight = true;
                }
            }

            if (isAutoContentHeight && null != child4Measure) {
                measureChild(child4Measure, widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int headHeight = child4Measure.getMeasuredHeight();
                if (headHeight > 0) {
                    haveSetHeaderHeight = true;
                    mSlideHeaderView.setSlideHeaderViewHeight(headHeight, child4Measure);
                    setDefaultSlideHeaderViewHeight(headHeight);
                }
            }
        }

    }

    /**
     * 设置默认显示高度
     *
     * @param height
     */
    public void setDefaultSlideHeaderViewHeight(int height) {
        mSlideHeaderView.setDefaultSlideHeaderViewHeight(height, headContentView);
    }

    /**
     * 设置KGPullListView顶部可拉动header背景图
     *
     * @param slideBitmap
     */
    public void setSlideHeaderBackground(Bitmap slideBitmap) {
        mSlideHeaderView.setSlideHeaderBackground(slideBitmap);
    }

    /**
     * 设置是否可滑动
     */
    public void setSlideEnable(boolean enable) {
        this.mSlideEnable = enable;
    }

    /**
     * 设置KGPullListView顶部可拉动header背景图
     *
     * @param resId
     */
    public void setSlideHeaderBackground(int resId) {
        mSlideHeaderView.setSlideHeaderBackground(resId);
    }


    public HeaderView getSlideHeaderView() {
        return mSlideHeaderView;
    }
}

/**
 * 使用案例：与ListView 布局使用一致
 * demo1:
 * PullListView   pullListView = findViewById();
 * pullListView.setAdapter();    //配置列表适配器
 * pullListView.addHeaderView()  //加表头  //也可不加
 * pullListView.setSlideEnable(true);      //使能可拉伸
 * pullListView.setSlideHeaderView(imageView);  //配置拉伸图
 **/