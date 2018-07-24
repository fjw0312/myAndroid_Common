package com.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by fjw0312 on 2018/4/17.
 * 捕获网络 变化广播   --具体待完善
 *
 * <receiver android:name="com.receiver.NetworkReceiver"
     android:enabled="true"
     android:exported="true">
     <intent-filter>
         <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
     </intent-filter>
 </receiver>
 */

public class NetworkReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG,"action:" + action);
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            Log.d(TAG, "网络状态已经改变");
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info != null && info.isAvailable()) {
                String name = info.getTypeName();
                Log.d(TAG, "当前网络名称：" + name);
            } else {
                Log.d(TAG, "没有可用网络");

            }
        }
    }

    //完整的网络 判断
    /*
    //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

        //获得ConnectivityManager对象
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //获取ConnectivityManager对象对应的NetworkInfo对象
        //获取WIFI连接的信息
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //获取移动数据连接的信息
        NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
            Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
        } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
            Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
        } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
            Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
        }
//API大于23时使用下面的方式进行网络监听
    }else {

        System.out.println("API level 大于23");
        //获得ConnectivityManager对象
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //获取所有网络连接的信息
        Network[] networks = connMgr.getAllNetworks();
        //用于存放网络连接信息
        StringBuilder sb = new StringBuilder();
        //通过循环将网络信息逐个取出来
        for (int i=0; i < networks.length; i++){
            //获取ConnectivityManager对象对应的NetworkInfo对象
            NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
            sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
        }
        Toast.makeText(context, sb.toString(),Toast.LENGTH_SHORT).show();
    }
    */
}
