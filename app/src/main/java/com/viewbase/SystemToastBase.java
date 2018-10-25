package com.viewbase;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.mycom.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射Toast控制显示的位置，时间，动画，View
 * Created by  on 2017/2/27.
 */

public class SystemToastBase {

    private final static String TAG = "SystemToastManager";
    //反射Toast的使用类
    private Context mContext;
    private Field mParamsField;
    private WindowManager.LayoutParams mParams;
    private Toast mToast;
    private Object mTN;
    private Method mShowMethod;
    private Method mHideMethod;
    private boolean isShow = false;
    private int mGravity = Gravity.CENTER;
    private int mXOffset = 0;
    private int mYOffset = 0;

    private int mHeight = WindowManager.LayoutParams.MATCH_PARENT;
    private int mWidth = WindowManager.LayoutParams.MATCH_PARENT;

    private View mView;

    public SystemToastBase(Context context) {
        mContext = context;
        mToast = new Toast(mContext);
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        mGravity = gravity;
        mXOffset = xOffset;
        mYOffset = yOffset;
    }

    public void setSize(int height, int width) {
        mHeight = height;
        mWidth = width;
    }

    private void initSystemToast() {
        try {
            Field tnField = mToast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mToast.setGravity(mGravity, mXOffset, mYOffset);

            mTN = tnField.get(mToast);
            mShowMethod = mTN.getClass().getMethod("show");
            mHideMethod = mTN.getClass().getMethod("hide");

            mParamsField = mTN.getClass().getDeclaredField("mParams");
            mParamsField.setAccessible(true);
            mParams = (WindowManager.LayoutParams) mParamsField.get(mTN);
            mParams.height = mHeight;
            mParams.width = mWidth;
            mParams.format = PixelFormat.RGBA_8888;

            mParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FindViewInterface findViewInterface;

    public interface FindViewInterface {
        public void OnFindViewById(View view);
    }

    //添加视图布局
    public View setUpView(int layout_id, FindViewInterface findViewInterface) { //获得 控件 并 操作
        mView = LayoutInflater.from(mContext).inflate(layout_id, null);
        this.findViewInterface = findViewInterface;
        if (this.findViewInterface != null) {
            this.findViewInterface.OnFindViewById(mView);
        }
        return mView;
    }

    //显示 浮窗视图
    public void showToastView() {
        if (mView != null) {
        } else {
            mView = LayoutInflater.from(mContext).inflate(R.layout.overlay_demo, null);
        }
        addView(mView);
    }

    // 关闭 浮窗
    public void removeToastView() {
        removeView();
    }


    public void addView(View view) {
        if (isShow) return;

        initSystemToast();
        mToast.setView(view);
        try {
            /**调用tn.mShowMethod()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, mToast.getView());
            mShowMethod.invoke(mTN);
            isShow = true;
            mView = view;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeView() {
        if (!isShow) return;

        try {
            if (null != mView) {
                // 解决消失时渐隐动画导致的闪烁问题
                mView.setVisibility(View.INVISIBLE);
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mView) {
                            mView.setVisibility(View.VISIBLE);
                            mView = null;
                        }
                    }
                }, 100);
            }
            mHideMethod.invoke(mTN);
            //mToast.cancel();

            isShow = false;
            Log.i(TAG, "hideView called");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "hideView exception " + e.getMessage());
        }
    }

    //隐藏浮窗
    public void dismissDelayToast(int delayMs) {
        mDismissHandler.removeCallbacks(mDismissRunnable);
        mDismissHandler.postDelayed(mDismissRunnable, delayMs);
    }

    //隐藏浮窗
    public void dismissToast() {
        mDismissHandler.removeCallbacks(mDismissRunnable);
        mDismissHandler.post(mDismissRunnable);
    }

    private Handler mDismissHandler = new Handler();
    private Runnable mDismissRunnable = new Runnable() {
        @Override
        public void run() {
            removeView();
        }
    };

    /**
     * 使用方式
     * 1.定义该变量 SystemToastBase
     * 2.实例化 new SystemToastBase
     * 3.设置视图 setUpView()
     * 4.显示浮窗 showOverlayView()
     * */

}
