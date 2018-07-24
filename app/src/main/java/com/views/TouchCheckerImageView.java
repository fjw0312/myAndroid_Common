package com.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.utils.LogcatFileHelper;

import java.util.ArrayList;

/**
 * Created by jiongfang on 2018/5/21.
 * 点击 选择区域 触发 控件
 */
public class TouchCheckerImageView extends ImageView {
    public TouchCheckerImageView(Context context) {
        super(context);
        init();
    }

    public TouchCheckerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchCheckerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
    }

    public interface TouchChecker
    {
        boolean isInTouchArea(int x, int y, int width, int height);  //判断点击区域
    }
    private TouchChecker touchChecker;

    public void setTouchChecker(TouchChecker touchChecker)
    {
        this.touchChecker = touchChecker;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchChecker != null)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {

                if (touchChecker.isInTouchArea((int) event.getX(), (int) event.getY(), getWidth(), getHeight()))
                {
                    LogcatFileHelper.i("Jiong>>","TouchCheckerImageView 触发点击事件！---选中区域");
                    return super.onTouchEvent(event);
                }
                else
                {
                    LogcatFileHelper.i("Jiong>>","TouchCheckerImageView 触发点击事件！---未  选中区域");
                    return false;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     *
     * */


    // 内部类 判断 点击区域为 透明区域
    public class BitmapTouchChecker implements TouchChecker
    {
        private Bitmap bitmap;

        public BitmapTouchChecker(Bitmap bitmap)
        {
            this.bitmap = bitmap;
        }

        @Override
        public boolean isInTouchArea(int x, int y, int width, int height)
        {
            if (bitmap != null)
            {
                int pixel = bitmap.getPixel(x, y);

                if (((pixel >> 24) & 0xff) > 0)
                {
                    Log.d("Jiong>>", "BitmapTouchChecker>>>isInTouchArea return true");

                    return true;
                }
            }

            Log.d("Jiong>>", "BitmapTouchChecker>>>isInTouchArea return false");

            return false;
        }
    }


    //判断 点击区域 为 某矩形内
    public class RectTouchChecker implements TouchChecker
    {
        private ArrayList<Rect> rectList;

        public RectTouchChecker(ArrayList<Rect> rectList)
        {
            this.rectList = rectList;
        }

        @Override
        public boolean isInTouchArea(int x, int y, int width, int height)
        {
            if (rectList != null)
            {
                for (Rect rect : rectList)
                {
                    if (rect.contains(x, y))
                    {
                        Log.d("RectTouchChecker", "isInTouchArea return true");

                        return true;
                    }
                }

            }

            Log.d("RectTouchChecker", "isInTouchArea return false");

            return false;
        }

    }
}
