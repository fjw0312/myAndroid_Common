package com.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by jiongfang on 2018/9/10.
 * 圆形图片&圆角图片  自定义控件
 */
public class RoundImageView extends ImageView {
    public RoundImageView(Context context) {
        super(context);
        initialShader();
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialShader();
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialShader();
    }

    public static final int RECTANGLE = 0;
    public static final int CIRCULAR = 1;

    private static final float DEFAULT_RADIUS = 50f;
    private float mRadius = DEFAULT_RADIUS;
    private int mRoundType = CIRCULAR;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Shader mShader;

    public void setmRadius(float radius) {
        mRadius = radius;
    }

    public void setmRoundType(int type) {
        mRoundType = type;
    }


    private Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof ColorDrawable) {
            Rect rect = drawable.getBounds();
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;
            int color = ((ColorDrawable) drawable).getColor();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
            return bitmap;
        } else {
            return null;
        }
    }

    private void initialShader() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBitmap = getBitmap(getDrawable());
        if (mBitmap != null) {
            mShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(mShader);
        }
    }

    private boolean isCircular() {
        return mRoundType == CIRCULAR;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*
         *如果是圆形的话需要让View的宽高比例一样
         */
        if (isCircular()) {
            int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
            int height = Math.min(width, getMeasuredHeight());
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (getDrawable() == null) {
            return;
        }
        int width = getWidth();
        int height = getHeight();
        float radius;
        /*
         *如果是圆形则绘制圆形图片，否则绘制圆角矩形
         */
        if (isCircular()) {
            radius = width / 2;
            canvas.drawCircle(width / 2, height / 2, radius, mPaint);
        } else {
            radius = mRadius;
            RectF rectF = new RectF();
            rectF.set(0, 0, width, height);
            canvas.drawRoundRect(rectF, radius, radius, mPaint);
        }
    }

    /**
     * 使用demo:
     * 布局与ImageView 一致 默认圆形图片  可设置setmRoundType() 为圆角矩形图片
     * RoundImageView = findViewById();
     * RoundImageView.setmRoundType();
     * */

}
