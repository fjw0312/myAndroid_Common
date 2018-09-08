package com.mycom.Demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mycom.R;
import com.utils.FullScreenUI;
import com.views.TranslucentActionBar;
import com.views.TranslucentScrollView;


/**
 * Created by jiongfang on 2018/9/7.
 */
public class TranslucentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translucent);
        FullScreenUI.FullScreenUI(this);
        init();
    }


    private TranslucentScrollView translucentScrollView;
    private TranslucentActionBar actionBar;
    private View zoomView;

    private void init() {
        actionBar = (TranslucentActionBar) findViewById(R.id.actionbar);
        //初始actionBar
        actionBar.setData("我的", 0, null, 0, null, null);
        //开启渐变
        actionBar.setNeedTranslucent();
        //设置状态栏高度
        actionBar.setStatusBarHeight(getStatusBarHeight());


        zoomView = findViewById(R.id.lay_header);
        translucentScrollView = (TranslucentScrollView) findViewById(R.id.pullzoom_scrollview);
        //关联需要渐变的视图
        translucentScrollView.setTransView(actionBar);
        //关联伸缩的视图
        translucentScrollView.setPullZoomView(zoomView);
        //设置透明度变化监听
        translucentScrollView.setTranslucentChangedListener(new TranslucentScrollView.TranslucentChangedListener() {
            @Override
            public void onTranslucentChanged(int transAlpha) {
                actionBar.tvTitle.setVisibility(transAlpha > 48 ? View.VISIBLE : View.GONE);
            }
        });

    }


    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
