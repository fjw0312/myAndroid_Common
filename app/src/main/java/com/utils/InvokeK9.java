package com.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.MyApplication;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jiongfang on 2018/6/28.
 * 反射 调用k9 方法
 */
public class InvokeK9 {

    public final static String PACKAGENAME = "com.kugouk9";
    public final static String K9_USBHID = "com.usb.KugouUsbHidInterface";
    public final static String SET_MIC = "processMicVolume";
    public final static String FIELD = "UsbHidMode";
    public final static int MaxMIC = 15;
    public final static int USBHID_MODE = 1;

    //该 方法中 还有其他调用，代码能执行  达不到预期效果  ---- 最好 该技术 使用于static方法调用 或 整个类逻辑的实现

    public static void invoke(Context context, Handler handler) throws
            ClassNotFoundException, NoSuchMethodException, PackageManager.NameNotFoundException,
            IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchFieldException {
        invoke(context, handler, K9_USBHID, SET_MIC, MaxMIC, FIELD, USBHID_MODE);
    }

    public static void invoke(Context context, Handler handler,
                              String className, String methodName, int arg1, String fieldName, int arg2) throws
            ClassNotFoundException, NoSuchMethodException, PackageManager.NameNotFoundException,
            IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchFieldException {


        Context c = MyApplication.getContext().createPackageContext(PACKAGENAME, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        Class aClass = c.getClassLoader().loadClass(className);
        //       Class aClass = Class.forName(className);
        Method method = aClass.getMethod(methodName, int.class);
        Field field = aClass.getDeclaredField(fieldName);
        method.setAccessible(true);//设置能获取私有方法
        field.setAccessible(true); //设置能获取私有成员
        //       Object obj = aClass.newInstance();//获取无构造参数对象
        Constructor constructor = aClass.getConstructor(Context.class, Handler.class);//获取有参构造
        Object obj = constructor.newInstance(context, handler);//获取有构造参数对象
        field.set(obj, arg2);//给成员变量复制
        method.invoke(obj, arg1);//执行
    }


}
