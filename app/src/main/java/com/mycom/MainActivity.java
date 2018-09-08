package com.mycom;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.fragment.testfragement.Fragment1;
import com.fragment.testfragement.Fragment2;
import com.fragment.testfragement.Fragment3;
import com.utils.FullScreenUI;
import com.utils.LogcatFileHelper;
import com.utils.SystemUtil;
import com.views.HeaderView;
import com.views.PullListView;
import com.views.TranslucentScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/*****
 * author:fjw0312
 * date: 2018.3.13
 * 功能说明： 个人 公共使用模块 整合 迭代工程
 */
public class MainActivity extends AppCompatActivity {


    Button Bn_start;

    String[] str_s = {"微信","QQ","陌陌","来往","探探",
            "爱奇艺","优酷","腾讯视频","乐视","bilibili",
            "凤凰","头条","网易","虎扑","天行","美团","携程","滴滴","京东","百度","腾讯","阿里" };

    private List<Fragment> mFragments;
    private String[] mTabTitles = {"消息", "好友", "动态"};
    ListView listView;
    HeaderView pullHeaderView;
    PullListView pullList;
    private TranslucentScrollView translucentScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FullScreenUI.FullScreenUI(this);
  //      listView = (ListView)findViewById(R.id.lv);
  //      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,str_s);
  //      listView.setAdapter(adapter);

        pullHeaderView  = (HeaderView)findViewById(R.id.pullHeader);

        pullList = (PullListView)findViewById(R.id.pullList);
       //
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,str_s);
        pullList.setAdapter(adapter);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.pp);
        pullList.setSlideEnable(false);
        pullList.addHeaderView(imageView);

  //      translucentScrollView = (TranslucentScrollView)findViewById(R.id.pullzoom_scrollview);
        //设置透明度变化监听
       // translucentScrollView.setTranslucentChangedListener(this);
        //关联需要渐变的视图
       // translucentScrollView.setTransView(actionBar);

        //关联伸缩的视图
 //       translucentScrollView.setPullZoomView(pullHeaderView);


/*
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

*/
        //1.获取系统android 版本
        String sysModel = SystemUtil.getSysModel(); //获取系统型号
        String sysVersion = SystemUtil.getSysVersion(); //获取系统版本
        int SDKVersion = SystemUtil.getSDKVersion(); //获取sdk 版本
        LogcatFileHelper.i("Jiong","系统型号:"+sysModel+"     系统版本:"+sysVersion+"   sdk 版本:"+SDKVersion);
        //2.获取屏幕尺寸 分辨率 density
        int[] screen = SystemUtil.getScreenSize(getBaseContext());
        float density = SystemUtil.getDensity(getBaseContext());
        int height = SystemUtil.getDpi(getBaseContext());
        LogcatFileHelper.i("Jiong","系统屏幕尺寸:"+screen[0]+"    "+screen[1]+"     density:"+density+"   height:"+height);
        //3.获取 内存 sdcard 容量  以及 剩余容量
        String ramTotalSize = SystemUtil.getRomTotalSize(getBaseContext());
        String ramAvaSize = SystemUtil.getRomAvailableSize(getBaseContext());
        String sdcardSize = SystemUtil.getSDTotalSize(getBaseContext());
        String sdcardAvaSize = SystemUtil.getSDAvailableSize(getBaseContext());
        LogcatFileHelper.i("Jiong","ramTotalSize:"+ramTotalSize+"     ramAvaSize:"+ramAvaSize);
        LogcatFileHelper.i("Jiong","sdcardSize:"+sdcardSize+"     sdcardAvaSize:"+sdcardAvaSize);
        //3.是否能执行 root & exec 代码

    }



    /**
     * 初始化toolBar
     */
    private void initToolBar() {
    //    Toolbar toolbar = (Toolbar) findViewById(R.id.tb_main);
        // 指定ToolBar的标题
    //    toolbar.setTitle("CoordinatorLayout");
        // 将toolBar和actionBar进行关联
//        setSupportActionBar(toolbar);

    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        Fragment1 msgFragment = new Fragment1();
        Fragment2 friendFragment = new Fragment2();
        Fragment3 foundFragment = new Fragment3();
        // 将三个fragment放入List里面管理，方便使用
        mFragments = new ArrayList<>();
        mFragments.add( msgFragment);
        mFragments.add(friendFragment);
        mFragments.add(foundFragment);
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private Context context;

        public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            // 获取指定位置的Fragment对象
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            // ViewPager管理页面的数量
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // 设置indicator的标题（TabLayout中tab的标题）
            return mTabTitles[position];
        }
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
