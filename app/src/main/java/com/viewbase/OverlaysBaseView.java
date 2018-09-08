package com.viewbase;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by jiongfang on 2018/3/28.
 * 系统浮窗 基础类
 * 实例化 new 必须在MainThread
 */
public class OverlaysBaseView {

    public OverlaysBaseView(Context context){
        mContext = context;

    }

    private static final String TAG = "OverlaysBaseView";
    private Context mContext;
    private View mView;
    //布局参数.
    WindowManager.LayoutParams params;
    //实例化的WindowManager.
    WindowManager windowManager;

    //初始化 浮窗
    private void initOverlaysView(View addView){
        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，TYPE_SYSTEM_ALERT一般都在应用程序窗口之上.
        if (Build.VERSION.SDK_INT >= 23) {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;    //sdk >= 23
            // params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }else{
            params.type = WindowManager.LayoutParams.TYPE_TOAST;            //sdk  <= 23
        }
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                |WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        //params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM; //设置flags.
        params.gravity = Gravity.CENTER; //设置窗口初始停靠位置. //params.gravity = Gravity.LEFT | Gravity.TOP;
        params.format = PixelFormat.TRANSLUCENT;// params.format = PixelFormat.RGBA_8888;//设置效果为背景透明.

        //设置悬浮窗口长宽数据.
        //注意，这里的width和height均使用px而非dp.这里我偷了个懒
        //如果你想完全对应布局设置，需要先获取到机器的dpi
        //px与dp的换算为px = dp * (dpi / 160).
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        //params.x = 0;
        //params.y = 0;

        windowManager.addView(addView,params);
    }

    //添加视图布局
    public View setUpView(int layout_id,FindViewInterface findViewInterface){ //获得 控件 并 操作
        mView = LayoutInflater.from(mContext).inflate(layout_id, null);
        this.findViewInterface = findViewInterface;
        if(this.findViewInterface!=null){
            this.findViewInterface.OnFindViewById(mView);
        }
        return mView;
    }
    //显示 浮窗视图
    public void showOverlayView(){
        if(mView!=null){
            initOverlaysView(mView);
        }
    }

    private FindViewInterface findViewInterface;
    public interface FindViewInterface{
        public void OnFindViewById(View view);
    }

    // 关闭 浮窗
    public void removeOverlays(){
        windowManager.removeView(mView);
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
            removeOverlays();
        }
    };

    /**
     * 使用方式
     * 1.定义该变量 OverlaysBaseView
     * 2.实例化 new OverlaysBaseView
     * 3.设置视图 setUpView()
     * 4.显示浮窗 showOverlayView()
     * */

}
