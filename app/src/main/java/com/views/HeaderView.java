
package com.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.utils.SystemUtil;

/**
 * @author JBT
 * @email 394584502@qq.com
 * @data 2013-8-9
 * @time 涓嬪崍11:45:49
 * @desc 可拖动listview头
 */
public class HeaderView extends ImageView {

    public static final int WRAP_CONTENT_HEIGHT = -1;
    public static final int WRAP_HEAD_HEIGHT = -2;

    private int mSlideHeaderBackgroundHeight = -1;
    private int mDefaultSlideHeaderViewHeight = 0;

    private Context mContext;

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();

    }

    public HeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public HeaderView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        setScaleType(ScaleType.CENTER_CROP);
        if (this.getDrawable() != null)
            mSlideHeaderBackgroundHeight = this.getDrawable().getIntrinsicHeight();
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 0);
        this.setLayoutParams(params);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setDefaultSlideHeaderViewHeight(int height, View headContentView) {
        if (height == WRAP_CONTENT_HEIGHT
                || height == WRAP_HEAD_HEIGHT) {
            mDefaultSlideHeaderViewHeight = height;
            requestLayout();
        } else {
            LayoutParams params = getLayoutParams();
            mDefaultSlideHeaderViewHeight = height;
            params.height = height;
            this.setLayoutParams(params);
            if (headContentView != null) {
                headContentView.setLayoutParams(params);
                headContentView.invalidate();
            }
        }

    }

    public void setSlideHeaderViewHeight(int height, View headContentView) {
        LayoutParams params = this.getLayoutParams();
        params.height = getLegalHeight(height);

        this.setLayoutParams(params);
        if (headContentView != null) {
            headContentView.setLayoutParams(params);
            headContentView.invalidate();
        }
        invalidate();
    }

    public float getSlidePercent() {
        float slideRange = mSlideHeaderBackgroundHeight - mDefaultSlideHeaderViewHeight;
        return (getSlideHeaderViewHeight() - mDefaultSlideHeaderViewHeight) / slideRange;
    }

    public int getSlideHeaderViewHeight() {
        LayoutParams params = this.getLayoutParams();
        return params.height;
    }

    public int getDefaultSlideHeaderViewHeight() {
        return mDefaultSlideHeaderViewHeight;
    }

    public void setSlideHeaderBackground(Bitmap slideBitmap) {
        if (slideBitmap != null) {
            this.setImageBitmap(slideBitmap);
            mSlideHeaderBackgroundHeight = computeRealHeight(
                    SystemUtil.getScreenSize(mContext)[0], slideBitmap.getWidth(),
                    slideBitmap.getHeight()
            );
        }
    }

    public void setSlideHeaderBackground(int resId) {
        try {
            this.setImageResource(resId);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(mContext.getResources(), resId, options);
            mSlideHeaderBackgroundHeight = computeRealHeight(
                    SystemUtil.getScreenSize(mContext)[0], options.outWidth, options.outHeight);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public void setSlideHeaderBackgroundHeight(int height) {
        mSlideHeaderBackgroundHeight = height;
    }

    /**
     * 用这个方法取得高度可以避免传入一个非法的高度
     *
     * @param height
     * @return
     */
    private int getLegalHeight(int height) {
        if (mSlideHeaderBackgroundHeight != -1 && height > mSlideHeaderBackgroundHeight) {
            return mSlideHeaderBackgroundHeight;
        }
        if (height < mDefaultSlideHeaderViewHeight) {
            return mDefaultSlideHeaderViewHeight;
        }
        return height;
    }

    /**
     * getHeight和getWidth获取回来的是bitmap的像素点，此方法用于获取真实高度，不然取出来的高度不会根据屏幕缩放
     *
     * @param realWidth
     */
    private int computeRealHeight(int realWidth, int width, int height) {
        if (width != 0) {
            return (realWidth * height) / width;
        }

        return 0;
    }
}
