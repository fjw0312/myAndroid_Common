package com.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mycom.R;

import java.util.List;

/**
 * Created by  on 2017/4/6.
 * fjw0312 测试OK
 */

public class GalleyViewPager extends RelativeLayout {

    private static final String TAG = "GalleyViewPager";

    private int mLastX = 0;

    private int mLastY = 0;

    private static final int offset = 3;
    private int pageLimit = 4;
    private int pageMargin = 0;
    private int leftRightMargin = 300;
    private boolean isRun = true;  //是否循环滑动


    public GalleyViewPager(Context context) {
        this(context, null);
    }

    public GalleyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.galley_viewpager, this);
        mViewPager = (ImageViewPager) findViewById(R.id.vp_conver_flow);
    }

    /**
     * 用于左右滚动
     */
    private ImageViewPager mViewPager;
    List<Integer> lstIds;
    private int lastPosition = 1;

    // 设置 适配器
    public void setInit(List<Integer> lstImageId) {

        if (isRun && lstImageId.size() >= 3) { // 如果数据大于一条
            //    lstImageId.add(0,lstImageId.get(lstImageId.size()-2));//            添加最后2页到前2页
            //    lstImageId.add(0,lstImageId.get(lstImageId.size()-1));//            添加最后2页到前2页
            lstImageId.add(lstImageId.get(0));//            添加第一页(经过上行的添加已经是第二页了)到最后一页
            lstImageId.add(lstImageId.get(1));//
            lstImageId.add(lstImageId.get(2));//    1->2->3->....->1->2->3   2=>2
            lstIds = lstImageId;
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                int currentPosition;

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    Log.i("Jiong>>", TAG + "  onPageScrolled>>  position=" + position + "   positionOffset=" + positionOffset + "    positionOffsetPixels=" + positionOffsetPixels);
                    if (isRun) {
                        if (lastPosition == position + 1 && position == 1) {         //左滑到 第2个
                            Log.i("Jiong>>", "左滑   position == 1");
                            mViewPager.setCurrentItem(lstIds.size() - 2, false);
                        } else if (lastPosition == position - 1 && position == lstIds.size() - 2) {   //右滑到 第2个
                            Log.i("Jiong>>", "右滑   position = size-2");
                            mViewPager.setCurrentItem(1, false);
                        }
                    }
                    lastPosition = position;
                }

                @Override
                public void onPageSelected(int position) {
                    Log.i("Jiong>>", TAG + "  onPageSelected>> position=" + position);
                    currentPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    Log.i("Jiong>>", TAG + "  onPageScrollStateChanged>> state=" + state);
                }
            });
        }
        // 设置适配器
        mViewPager.init_adapter(lstImageId);
        mViewPager.setOffscreenPageLimit(pageLimit);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(leftRightMargin, 0, leftRightMargin, 0);
        mViewPager.setLayoutParams(layoutParams);
        mViewPager.setPageMargin(pageMargin);//页面间距
        mViewPager.setPageTransformer(true, mViewPager.new GallyPageTransformer());
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    //设置当前 第几个
    public void setCurrentItem(int position) {

        this.mViewPager.setCurrentItem(position);
        //    lastPosition = position;
    }


    public void setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
    }

    public void setPageMargin(int pageMargin) {
        this.pageMargin = pageMargin;
    }

    public void setLeftRightMargin(int leftRightMargin) {
        this.leftRightMargin = leftRightMargin;
    }

    public void setIsRun(boolean isRun) {
        this.isRun = isRun;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //   return mViewPager.onInterceptTouchEvent(ev);
        return false;
    }

    /**
     * 通过requestDisallowInterceptTouchEvent(true)方法来影响父View是否拦截事件，
     * 在左右滑动的时候请求父View ScrollView不要拦截事件，其他的时候拦截事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 保证子View能够接收到Action_move事件
                //            getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                int dealtX = x - mLastX;
                int dealtY = y - mLastY;
                Log.d(TAG, "dealtX:=" + dealtX + " x:" + x + " mLastX:" + mLastX);
                Log.d(TAG, "dealtY:=" + dealtY + " y:" + y + " mLastY:" + mLastY);
                // 拦截的判断依据是左右滑动
                if (Math.abs(dealtX) >= Math.abs(dealtY) - offset) {
                    //                getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    //                getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mViewPager.onTouchEvent(event);
    }
}

