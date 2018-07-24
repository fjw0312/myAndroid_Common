package com.mycom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fragment.single.TestSignalFragmentActivity;
import com.utils.SystemUtil;

import java.lang.ref.WeakReference;

/*****
 * author:fjw0312
 * date: 2018.3.13
 * 功能说明： 个人 公共使用模块 整合 迭代工程
 */
public class MainActivity extends AppCompatActivity {


    Button Bn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bn_start = (Button)findViewById(R.id.Bn_start);
        Bn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.Bn_start){
                   // Intent intent = new Intent(MainActivity.this, SlideActivity.class);
                   // Intent intent = new Intent(MainActivity.this, SlideLayoutActivity.class);
                    Intent intent = new Intent(MainActivity.this, TestSignalFragmentActivity.class);
                    startActivity(intent);
                }
            }
        });

        //1.获取系统android 版本
        String sysModel = SystemUtil.getSysModel(); //获取系统型号
        String sysVersion = SystemUtil.getSysVersion(); //获取系统版本
        int SDKVersion = SystemUtil.getSDKVersion(); //获取sdk 版本
        //2.获取屏幕尺寸 分辨率 density
        int[] screen = SystemUtil.getScreenSize(getBaseContext());
        float density = SystemUtil.getDensity(getBaseContext());
        int height = SystemUtil.getDpi(getBaseContext());
        //3.是否能执行 root & exec 代码

    }


    /** Handler 为避免 非静态内部类 内存泄漏  使用规范：
     *  1.如下定义 UIHandler 类
     *  2.实例化  在OnCreate(){ mUIHandler = new UIHandler(Looper.getMainLooper(), this);   }   //碎片时 onActivityCreated()中
     *  3.在页面销毁 中 移除所有消息    mUIHandler.removeCallbacksAndMessages(null);
     *
     *  同理 可以使用于 碎片fragment中，将 Activity 换成 fragment
     */

    private UIHandler mUIHandler;
    private  static class UIHandler extends Handler {
        //定义 弱应用
        private WeakReference<Activity> weakReference;
        public UIHandler(Looper looper, Activity activity){
            super(looper);
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //判断 引用的 activity或 碎片 是否还存在
            if(weakReference == null && weakReference.get() == null){
                return;
            }
            Activity activity = weakReference.get();
            //开始 消息 业务判断
            switch(msg.what){
                case 1:
                    break;
                default:break;
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

    private void OnWorkLooperDestory(){
        // 停止后台线程
        if (mWorker != null)
            mWorker.quit();
    }


}
