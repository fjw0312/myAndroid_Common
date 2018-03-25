package com;

import android.app.Application;
import android.content.Context;

import com.utils.CrashHandler;

/***
 * 全局 Application  Context 获取
 * author:fjw0312
 * date:2017.7.12
 * notice: need to AndroidMainfest.xml add:
 * <application
 *     android:name="com.MyApplication"
 * use: MyApplication.getContext();
 * */
public class MyApplication extends Application{  
	private static Context context;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = getApplicationContext();
		//初始化crash 异常捕获
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(getApplicationContext());
	}

	//获取 context
	public static Context getContext(){
		return context;
	}

}
