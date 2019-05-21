package com.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.utils.LogcatFileHelper;

/**
 * Created by jiongfang on 2018/5/21.
 * <p>
 * 过滤 点击 透明区域  图形控件
 */
public class TouchTransImageView extends ImageView {
    public TouchTransImageView(Context context) {
        super(context);
        init();
    }

    public TouchTransImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchTransImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }


    private Bitmap bitmap;
    private Bitmap zoomBitmap;
    public TouchChecker touchChecker;

    public interface TouchChecker {
        boolean hasTouchChecker(View view);
    }

    public void setTouchImageView(ImageView imageView, TouchChecker touchChecker) {
        bitmap = ((BitmapDrawable) (imageView.getDrawable())).getBitmap();
        zoomBitmap = zoomImg(bitmap, 1080, 1920);
        LogcatFileHelper.i("Jiong>>", "TouchTransImageView 设置 触发点击事件  bitmap---Width:" + bitmap.getWidth() + "   Height:" + bitmap.getHeight());
        LogcatFileHelper.i("Jiong>>", "TouchTransImageView 设置 触发点击事件  zoomBitmap---Width:" + zoomBitmap.getWidth() + "   Height:" + zoomBitmap.getHeight());
        this.touchChecker = touchChecker;
    }

    public void setTouchImageView(ImageView imageView, int zoomWidth, int zoomHeight, TouchChecker touchChecker) {
        bitmap = ((BitmapDrawable) (imageView.getDrawable())).getBitmap();
        zoomBitmap = zoomImg(bitmap, zoomWidth, zoomHeight);
        LogcatFileHelper.i("Jiong>>", "TouchTransImageView 设置 触发点击事件  bitmap---Width:" + bitmap.getWidth() + "   Height:" + bitmap.getHeight());
        LogcatFileHelper.i("Jiong>>", "TouchTransImageView 设置 触发点击事件  zoomBitmap---Width:" + zoomBitmap.getWidth() + "   Height:" + zoomBitmap.getHeight());
        this.touchChecker = touchChecker;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (zoomBitmap != null) {
            //if (((pixel >> 24) & 0xff) > 0)
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                //           LogcatFileHelper.i("Jiong>>","TouchTransImageView 触发点击事件   区域 X:"+event.getX()+" Y:"+event.getY());
                int pixel = zoomBitmap.getPixel((int) (event.getX()), ((int) event.getY()));
                // 透明 判断
                if (pixel == 0) {
                    //          LogcatFileHelper.i("Jiong>>","TouchTransImageView 触发点击事件！---透明区域");
                    return false;
                } else {
                    //         LogcatFileHelper.i("Jiong>>","TouchTransImageView 触发点击事件！---普通 区域");

                    return touchChecker.hasTouchChecker(TouchTransImageView.this);
                }

            }
        }
        return false;
    }

    //bitmap 缩放
    private static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


    /**
     * 使用demo:
     * buttonLeft = view.findViewById(R.id.button_left);  //获取控件
     * buttonLeft.setTouchImageView(buttonLeft, new  TouchTransImageView.TouchChecker()); //设置非透明区域触发监听
     *
     */

}
