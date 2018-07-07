package com.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/10/19.
 * 修改 状态栏颜色
 * 使用方式：在 Activity onCreate(){中设置下面方法}
 * StatusBarCompat.compat(this, Color.RED)   //第二个参数是想要设置的颜色
 * 注意：使用的Activity 最好继承AppCompatActivity
 *
 * 隐藏标题栏： supportRequestWindowFeature(Window.FEATURENOTITLE)  //继承AppCompatActivity时使用：
 *             requestWindowFeature(Window.FEATURENOTITLE)   //继承activity时使用：
 */

public class StatusBarCompat {

    private static final int INVALID_VAL = -1;
    private static final int COLOR_DEFAULT = Color.parseColor("#20000000");


    public static void compat(Activity activity, int statusColor)
    {

        //当前手机版本为5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (statusColor != INVALID_VAL)
            {
                activity.getWindow().setStatusBarColor(statusColor);
            }
            return;
        }

        //当前手机版本为4.4
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            int color = COLOR_DEFAULT;
            ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            if (statusColor != INVALID_VAL)
            {
                color = statusColor;
            }
            View statusBarView = new View(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(activity));
            statusBarView.setBackgroundColor(color);
            contentView.addView(statusBarView, lp);
        }

    }

    public static void compat(Activity activity)  //不设置  默认Styles.xml颜色
    {
        compat(activity, INVALID_VAL);
    }


    public static int getStatusBarHeight(Context context)
    {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getDaoHangHeight(Context context) {
        int result = 0;
        int resourceId=0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid!=0){
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            LogcatFileHelper.i("Jiong>>","获得导航栏高度: "+context.getResources().getDimensionPixelSize(resourceId));
            return context.getResources().getDimensionPixelSize(resourceId);
        }else
            return 0;
    }

    public static void getSreenSize(Activity activity){  //注意：获取 到没 计算包含导航栏 高度
        //屏幕
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        LogcatFileHelper.i("Jiong>>","获得屏幕 高度:"+dm.heightPixels +"   屏幕 宽度:"+dm.widthPixels+"  density:"+dm.density +"   densityDpi:"+dm.densityDpi);
        LogcatFileHelper.i("Jiong>>", "xdpi:"+dm.xdpi+"  ydpi:"+dm.ydpi);

        //应用区域
        Rect outRect1 = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        LogcatFileHelper.i("Jiong>>","获得应用区域 高度:"+outRect1.height()+ "应用区域 宽度:"+outRect1.width());
        LogcatFileHelper.i("Jiong>>","获得应用区域 距离顶部:"+outRect1.top+ "屏幕  距离底部部:"+outRect1.bottom);


    }
}
