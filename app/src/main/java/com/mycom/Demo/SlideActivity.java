package com.mycom.Demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;

import com.activitybase.SlideBaseActivity;
import com.mycom.R;

import java.lang.ref.WeakReference;

/**
 * Created by jiongfang on 2018/5/7.
 */
public class SlideActivity extends SlideBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    //handler 引用案例：
    private UIHandler mUiHandler;

    private Handler getUIHandler() {
        if (mUiHandler == null) {
            mUiHandler = new UIHandler(Looper.getMainLooper(), this);
        }
        return mUiHandler;
    }

    //ui线程handler 静态内部类
    private static class UIHandler extends Handler {
        private final WeakReference<SlideActivity> reference;

        public UIHandler(Looper looper, SlideActivity activity) {
            super(looper);
            reference = new WeakReference<SlideActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if ((reference == null) || (reference.get() == null)) {
                return;
            }
            SlideActivity slideActivity = reference.get();
            switch (msg.what) {

            }
        }
    }

    private WorkHandler workHandler;

    private WorkHandler getWorkHandler() {
        if (workHandler == null) {
            workHandler = new WorkHandler(getWorkLooper(), this);
        }
        return workHandler;
    }

    //后台线程workhandler 静态内部类
    private static class WorkHandler extends Handler {
        private final WeakReference<SlideActivity> reference;

        public WorkHandler(Looper looper, SlideActivity reference) {
            super(looper);
            this.reference = new WeakReference<SlideActivity>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (reference == null || reference.get() == null) {
                return;
            }
            SlideActivity slideActivity = reference.get();
            switch (msg.what) {

            }
        }
    }

    /***
     * 如果 要 获取 非主线程的 Looper 使用方式：
     * 1. new Handler(getWorkLooper())
     * 2. 退出时 OnDestory 需要调用OnWorkLooperDestory();
     */
    protected HandlerThread mWorker;

    public Looper getWorkLooper() {
        if (mWorker == null) {
            mWorker = new HandlerThread("class.name", getWorkLooperThreadPriority());
            mWorker.start();
        }
        return mWorker.getLooper();
    }

    protected int getWorkLooperThreadPriority() {
        return Process.THREAD_PRIORITY_BACKGROUND;
    }

    private void OnWorkLooperDestory() {
        // 停止后台线程
        if (mWorker != null)
            mWorker.quit();
    }

}
