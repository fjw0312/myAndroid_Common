package com.utils.NodeInfo;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.utils.LogcatFileHelper;
import com.utils.MyBroadcastReceiver;

import java.util.List;

/**
 * Created by jiongfang on 2018/3/1.
 * 无障碍 服务 使用时 资源文件必须注册
 */
public class RobService extends AccessibilityService {

    private final static String TAG = "RobService";
    private Context mContext;

    /**
     * 该辅助功能开关是否打开了
     * @param accessibilityServiceName：指定辅助服务名字
     * @param context：上下文
     * @return
     */
    private boolean isAccessibilitySettingsOn(String accessibilityServiceName, Context context) {
        int accessibilityEnable = 0;
        String serviceName = context.getPackageName() + "/" +accessibilityServiceName;
        LogcatFileHelper.i("Jiong"+TAG,"into isAccessibilitySettingsOn"+serviceName);
        try {
            accessibilityEnable = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        } catch (Exception e) {
            LogcatFileHelper.e("Jiong"+TAG, "get accessibility enable failed, the err:" + e.getMessage());
        }
        if (accessibilityEnable == 1) {
             TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
                        LogcatFileHelper.i("Jiong"+TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        }else {
            LogcatFileHelper.d(TAG,"Accessibility service disable");
        }
        return false;
    }
    private boolean isServiceEnabled() {  //判断无障碍服务是否开启
        AccessibilityManager accessibilityManager = (AccessibilityManager)getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList( AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            String thisClassName = this.getClass().getSimpleName();
            if (info.getId().contains(thisClassName)) {
                LogcatFileHelper.i("Jiong>>","判断无障碍服务是否开启  开启"+thisClassName);
                return true;
            }
        }
        LogcatFileHelper.i("Jiong>>","判断无障碍服务是否开启  --- 未开启");
        return false;
    }
    private void enableRobService() { //跳转到无障碍服务设置页面
        Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        accessibleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(accessibleIntent);
    }



    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        MyBroadcastReceiver.sendBroad_MSG_HAL("无障碍服务 被KiLL ！");
        LogcatFileHelper.e("Jiong","RobService->onDestroy !  无障碍服务 被KiLL");
        super.onDestroy();
    }
    @Override
    public void onInterrupt() {
    }

    //  可以  不用在页面中  就捕获到按键 事件
    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        LogcatFileHelper.i("Jiong>."+TAG,"RobService onKeyEvent========KeyCode="+event.getKeyCode());
        return super.onKeyEvent(event);
    }

    @Override  //捕获到界面的点击事件
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType(); //获得 事件类型
       //  Log.d("Jiong","onAccessibilityEvent !  "+eventType);
     //   AccessibilityNodeInfo noteInfo = event.getSource();  //获得点击的对象
    }

    boolean IsServiceConnected = false;
    MyNodeInfoUtil nodeInfoUtil;
    String getMsg = "";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getBaseContext();
        //注册 广播
        registerReciver(mContext);


        //跑一心跳 打印线程
        new MyThread().start();

        if(isServiceEnabled() ) {
            LogcatFileHelper.i("Jiong","isServiceEnabled !");
            //实例化 一个MyNodeInfoUtil
            nodeInfoUtil = new MyNodeInfoUtil(this);
            IsServiceConnected = true;
        }else{
            if(isAccessibilitySettingsOn(this.getClass().getName(), mContext)){
                //延时 再判断
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isServiceEnabled() ){
                            LogcatFileHelper.i("Jiong"," 延时在判断 isServiceEnabled !");
                            //实例化 一个MyNodeInfoUtil
                            nodeInfoUtil = new MyNodeInfoUtil(RobService.this);
                            IsServiceConnected = true;
                            onServiceConnected();
                        }
                    }
                },2000);
            }else{  //未打开 无障碍 开关 跳到打开设置页面
                String msgContent = "请打开无障碍服务!";
                LogcatFileHelper.i("Jiong",msgContent);
                MyBroadcastReceiver.sendBroad_MSG_HAL(msgContent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enableRobService();
                    }
                },500);
            }
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         //判断确认 无障碍服务 服务和功能启动  后  进入 业务处理
         //业务处理
        return super.onStartCommand(intent, flags, startId);
    }

    @Override   //只会  手动打开手机服务时进入
    protected void onServiceConnected() {
        super.onServiceConnected();
        //实例化 一个MyNodeInfoUtil
        nodeInfoUtil = new MyNodeInfoUtil(this);
        IsServiceConnected = true;
        LogcatFileHelper.i("Jiong>>","into onServiceConnected");
    }



    //心跳 检测服务 线程
    private class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while (true) {
                    Thread.sleep(5*1000);
                    LogcatFileHelper.i("Jiong>>","RobService alive!");
                }
            }catch (InterruptedException e){
                LogcatFileHelper.e("Jiong>>","TAG--Die!");
            }
        }
    }
    /**
     * 校验某个服务是否还活着
     * serviceName :传进来的服务的名称
     */
    public static boolean isServiceRunning(Context context,String serviceName){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo info : infos){
            String name = info.service.getClassName();
            //String packagename = info.service.getPackageName();  // com.kugouk9
           // Log.d("RobService","获得的服务包名称:"+packagename);
            if(name.equals(serviceName)){
                return true;
            }
        }
        return false;
    }

    //广播接收器  注册
    RobBroadcastReceiver receiver;
    public final static  String ACTION_MSG_START_CHECK_UPDATE = "com.mycom.Robstvice.Test"; //检测更新
    private void registerReciver(Context context){
        receiver = new RobBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MSG_START_CHECK_UPDATE);

        context.registerReceiver(receiver,intentFilter);
    }
    private class RobBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogcatFileHelper.i("Jiong>>"+TAG,"接收到广播："+action);

        }
    }



}
