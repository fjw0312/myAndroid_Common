package com.viewbase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mycom.R;
import com.utils.LogcatFileHelper;

/**
 * Created by jiongfang on 2018/9/3.
 * 系统对话框类 基础类
 * 实例化 new 必须在MainThread
 */
public class SystemDialogBase {

    public SystemDialogBase(Context context) {
        mContext = context;

    }

    private int Width = 0;
    private int Height = 0;
    private int x = -1;
    private int y = 0;

    public void setDialogWidth(int Width) {
        this.Width = Width;
    }

    public void setDialogHeight(int Height) {
        this.Height = Height;
    }

    public void setDialogWidthHeight(int Width, int Height) {
        this.Width = Width;
        this.Height = Height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private AlertDialog alert;
    public boolean isShow = false;

    private static final String TAG = "SystemDialogBase";
    private Context mContext;
    private View mView;
    //布局参数.
    WindowManager.LayoutParams params;
    //实例化的WindowManager.
    WindowManager windowManager;

    //初始化 浮窗
    private void initOverlaysView(View addView) {
        // alert =  new AlertDialog.Builder(mContext, R.style.sysDialog).setView(mView).setCancelable(true).create();
        alert = new AlertDialog.Builder(mContext).setView(mView).setCancelable(true).create();
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isShow = false;
                LogcatFileHelper.w("Jiong>>" + TAG, "alert>>>onDismiss  去除浮窗！");
            }
        });
        //  alert.getWindow().setDimAmount(0);//设置昏暗度为0
        alert.getWindow().setBackgroundDrawableResource(R.color.transparent);
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        alert.show();

        //设置对话框尺寸
        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (x != -1 && y != -1) {
            lp.x = x;
            lp.y = y;
            lp.gravity = Gravity.LEFT | Gravity.TOP;
        } else if (x != -1 && y == -1) {
            lp.x = x;
            lp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        } else if (x == -1 && y != -1) {
            lp.y = y;
            lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        } else {
            lp.gravity = Gravity.CENTER;
        }


        if (Width != 0 && Height != 0) {
            lp.width = Width;
            lp.height = Height;
        } else if (Width != 0 && Height == 0) {
            lp.width = Width;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        } else if (Width == 0 && Height != 0) {
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;//宽高可设置具体大小
            lp.height = Height;
        }

        alert.getWindow().setAttributes(lp);

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
    public void showOverlayView() {
        if (mView != null) {
            initOverlaysView(mView);
        }
    }

    private FindViewInterface findViewInterface;

    public interface FindViewInterface {
        public void OnFindViewById(View view);
    }

    // 关闭 浮窗
    public void removeOverlays() {
        isShow = false;
        if (alert != null) {
            alert.dismiss();
        }
    }

    //隐藏浮窗
    public void dismissDelayDialog(int delayMs) {
        mDismissHandler.removeCallbacks(mDismissRunnable);
        mDismissHandler.postDelayed(mDismissRunnable, delayMs);
    }

    //隐藏浮窗
    public void dismissDialog() {
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
