package com.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.List;

public class SystemUtil {
    private static final String EXTRA_DEF_KEYBOARDHEIGHT = "DEF_KEYBOARDHEIGHT";
    /** 键盘默认高度 (dp) */
    private static int sDefKeyboardHeight = 300;

    public static int getDefKeyboardHeight(Context context) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        int height = settings.getInt(EXTRA_DEF_KEYBOARDHEIGHT, 0);
        if (height > 0 && sDefKeyboardHeight != height) {
            SystemUtil.setDefKeyboardHeight(context,height);
        }
        return sDefKeyboardHeight;
    }

    public static void setDefKeyboardHeight(Context context, int height) {
        if(sDefKeyboardHeight != height){
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            settings.edit().putInt(EXTRA_DEF_KEYBOARDHEIGHT, height).commit();
        }
        SystemUtil.sDefKeyboardHeight = height;
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * 获取进程ID
     *
     * @return
     */
    public static int myPid() {
        return android.os.Process.myPid();
    }

    /**
     * 是否在wifi网络中
     *
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        NetworkInfo netInfo = null;
        try {
            if (context == null) {
                return false;
            }
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * 是否连网
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConected(Context context) {
        try {
            if (context != null) {
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                return (netInfo != null && netInfo.isConnected());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 获取IMEI码
     */
    public static String getImei(Context context) {
        if (context == null) {
            return "0";
        }
        String imei = "0";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(imei)) {
            imei = "0";
        }
        return imei;
    }



    /**
     * 获取系统型号
     *
     * @return
     */
    public static String getSysModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机屏幕密度
     *
     * @return
     */
    public static float getDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
    }

    /**
     * 获取系统版本号
     *
     * @return
     */
    public static String getSysVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取系统sdk版本号
     *
     * @return
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取当前app版本号
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionCode;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * 获取当前app版本号
     *
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionName;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getAppName(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.applicationInfo.name;
        } catch (Exception e) {
            return "";
        }
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取系统状态栏的高度 px 一般都是50px
     *
     * @return
     */
    public static int getStatusHeight(Activity context) {
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;

    }



    /**
     * 检查摄像头是否可用
     *
     * @param context
     * @return true：可用，false：不可用
     */
    public static boolean isCameraAvailable(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) // check
                                                                                          // camera
                                                                                          // hardware
            return false;

        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            return false; // 摄像头不可用，包括Camera对象未被释放
        }

        if (c != null)
            c.release();

        return true;
    }


    /**
     * 创建progressbar换肤的LayerDrawable
     *
     * @return LayerDrawable
     */
    public static Drawable getProgressBarBgDrawable() {
        return getProgressBarBgDrawable(Color.parseColor("#66ffffff"), Color.parseColor("#24ffffff"),4);
    }


    public static Drawable getProgressBarBgDrawable(int frontColor, int backColor, float cornerRadius) {
        GradientDrawable progress = new GradientDrawable();
        progress.setCornerRadius(cornerRadius);
        progress.setColor(frontColor);
        ClipDrawable clipDrawable = new ClipDrawable(progress, Gravity.LEFT,
                ClipDrawable.HORIZONTAL);
        GradientDrawable back = new GradientDrawable();
        back.setCornerRadius(cornerRadius);
        back.setColor(backColor);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                back, clipDrawable
        });
        layerDrawable.setId(0, android.R.id.background);
        layerDrawable.setId(1, android.R.id.progress);
        return layerDrawable;
    }




    /**
     * 检测耳机是否插入 插入了耳机，就返回true,否则false; 需要权限： <uses-permission
     * android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
     *
     * @param context
     * @return
     */
    public static boolean isWiredHeadsetOn(Context context) {
        AudioManager localAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        return localAudioManager.isWiredHeadsetOn();
    }

    /**
     * 获取设备屏幕大小
     *
     * @param context
     * @return 0 width,1 height
     */
    public static int[] getScreenSize(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        return new int[] {
                screenWidth, screenHeight
        };
    }

    /**
     * 获取屏幕尺寸，单位为英寸
     * @param context
     * 根据分辨率和像素计算屏幕尺寸
     * @return
     * @deprecated
     */
    public static double getScreenSizeInInch(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        dm=context.getApplicationContext().getResources().getDisplayMetrics();
        int height = dm.heightPixels;
        int width = dm.widthPixels ;
        double screenSize = Math.sqrt(height*height
                + width*width);
        double screenSizeInInch = screenSize/(dm.densityDpi);
        Log.d("计算尺寸","高度："+height+" 宽度："+width+" 对角线："+screenSize+" 分辨率："+dm.densityDpi+" 英寸："+screenSizeInInch+" 高："+dm.xdpi+"宽："+dm.ydpi);
        return screenSizeInInch;
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            return true;
        }
        return false;
    }

    public static int getNavigationBarHeight(Context context) {
        try {
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0 && !hasMenuKey) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 是否有navigationBar
     * @param context
     * @return
     */
    public static boolean hasNavBar(Context context) {
        boolean hasNav = false;
        try {
            int id = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNav = true;
            }
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNav = false;
            } else if ("0".equals(navBarOverride)) {
                hasNav = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNav;
    }

    /**
     *
     * @param num 需格式化数
     * @param decimalCount  小数点后位数
     * @param isRound 是否四舍五入
     * @return
     */
    public static String formateDecimal(double num, int decimalCount, boolean isRound) {
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(decimalCount);
        formater.setGroupingSize(0);
        if (!isRound) {
            formater.setRoundingMode(RoundingMode.FLOOR);
        }else{
            formater.setRoundingMode(RoundingMode.HALF_UP);
        }
        return formater.format(num);
    }

    /**
     * 显示软键盘
     * @param context
     * @param edit
     */
    public static void showSoftInput(Context context, final View edit) {
        try{
            final InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null && inputManager.isActive(edit)) {
                inputManager.showSoftInput(edit, 0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void showSoftInputDelay(Context context, final View edit, long delay) {
        try{
            final InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                if (!inputManager.isActive(edit)) {
                    edit.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (inputManager.isActive(edit)) {
                                inputManager.showSoftInput(edit, 0);
                            }
                        }
                    }, delay);
                } else {
                    if (inputManager.isActive(edit)) {
                        inputManager.showSoftInput(edit, 0);
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 隐藏软键盘
     * @param context
     * @param edit
     */
    public static void hideSoftInput(Context context, View edit) {
        try{
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null && inputManager.isActive(edit)) {
                inputManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void hideSoftInput(Activity activity) {
        try{
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if(inputManager != null && inputManager.isActive() && view!=null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取当前系统每个app的内存等级，即最大使用内存
     * @param context
     * @return
     */
    public static int getMemoryClass(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getMemoryClass();
    }

    /**
     * 打印当前内存
     *
     * @return
     */
    public static void printCurMemery(Context context, String tag) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            //进程名称
            String packageName = info.processName;
            if (packageName == null || !packageName.contains(tag)) {
                continue;
            }
            //进程id
            int pid = info.pid;
            //获取进程占用的内存
            android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{pid});
            android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            long totalPrivateDirty = memoryInfo.getTotalPrivateDirty(); //KB

                Log.i(" memory", tag + "-getTaskInfos :" +
                        "\npackageName:" + packageName +
                        "\npid:" + pid +
                        "\n占用内存（kb）:" + totalPrivateDirty);

        }
    }
    /**
     * 获取进程名
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        if (context != null) {
            try {
                ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                    if (appProcess.pid == myPid()) {
                        return appProcess.processName;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 检测储存卡是否可用
     *
     * @return
     */
    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * dip转换px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转换dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getDisplayWidth(Context context) {
        if(context == null) {
            return 0;
        }
        int width = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        try {
            Class<?> cls = Display.class;
            Class<?>[] parameterTypes = { Point.class };
            Point parameter = new Point();
            Method method = cls.getMethod("getSize", parameterTypes);
            method.invoke(display, parameter);
            width = parameter.x;
        } catch (Exception e) {
            width = display.getWidth();
        }
        return width;
    }

    /**
     * 获取屏幕高
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getDisplayHeight(Context context) {
        if(context == null) {
            return 0;
        }
        int height = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        try {
            Class<?> cls = Display.class;
            Class<?>[] parameterTypes = { Point.class };
            Point parameter = new Point();
            Method method = cls.getMethod("getSize", parameterTypes);
            method.invoke(display, parameter);
            height = parameter.y;
        } catch (Exception e) {
            height = display.getHeight();
        }
        return height;
    }

    /**
     * 获取 虚拟按键的高度
     * @param context
     * @return
     */
    public static  int getBottomStatusHeight(Context context){
        int totalHeight = getDpi(context);
        int contentHeight = getDisplayHeight(context);
        return totalHeight  - contentHeight;
    }


    /**
     * 获取屏幕原始尺寸高度，包括虚拟功能键高度
     * @param context
     * @return
     */
    public static int getDpi(Context context){
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi=displayMetrics.heightPixels;
        }catch(Exception e){
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获取系统bar条高
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        if(activity == null) {
            return 0;
        }
        int statusHeight = 0;
        try {
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            statusHeight = frame.top;
            if (0 == statusHeight) {
                Class<?> localClass;

                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass
                        .getField("status_bar_height")
                        .get(localObject)
                        .toString());
                statusHeight = activity.getResources()
                        .getDimensionPixelSize(i5);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 判断当前程序是否前台进程
     *
     * @param context
     * @return
     */
    public static boolean isCurAppTop(Context context) {
        if(context == null) {
            return false;
        }
        String curPackageName = context.getPackageName();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            RunningTaskInfo info = list.get(0);
            String topPackageName = info.topActivity.getPackageName();
            String basePackageName = info.baseActivity.getPackageName();
            if (topPackageName.equals(curPackageName) && basePackageName.equals(curPackageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取手机IP地址
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return "";
    }

    /**
     * 获取系统相册路径, 耗时操作
     * @param context
     * @return
     */
    public static String getAlbumPath(Context context) {
        if(context == null) {
            return null;
        }
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Images.Media.TITLE, "title");
        values.put(Images.Media.DESCRIPTION, "description");
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        Uri url = cr.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
        // 查询系统相册数据
        Cursor cursor = null;
        try {
            cursor = Images.Media.query(cr, url, new String[]{MediaStore.Images.Media.DATA});
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        String albumPath = null;
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            albumPath = cursor.getString(column_index);
            try {
                cursor.close();
            } catch (Exception e) {
            }
        }
        cr.delete(url, null, null);
        if (albumPath == null) {
            return null;
        }

        File albumDir = new File(albumPath);
        if (albumDir.isFile()) {
            albumDir = albumDir.getParentFile();
        }
        // 如果系统相册目录不存在,则创建此目录
        if (!albumDir.exists()) {
            albumDir.mkdirs();
        }
        albumPath = albumDir.getAbsolutePath();
        return albumPath;
    }

    /**
     * 初始化一个空{@link Menu}
     *
     * @param context
     * @return
     */
    public static Menu newInstanceMenu(Context context) {
        try {
            Class menuBuilder = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Constructor constructor = menuBuilder.getConstructor(Context.class);
            return (Menu) constructor.newInstance(context);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断Intent是否有效
     *
     * @param context
     * @param
     * @return true 有效
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    public static boolean hasSmartBar() { // 禁止smartbar
        if ("mx3".equals(Build.DEVICE) && Build.VERSION.SDK_INT >= 19) { // Mx3安装MX4的os视频
            return false;
        }
        try {
            // 新型号可用反射调用Build.hasSmartBar()
            Method method = Class.forName("android.os.Build").getMethod(
                    "hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Exception e) {
        }
        // 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
        if (Build.DEVICE.equals("mx2")) {
            return true;
        } else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
            return false;
        }
        return false;
    }


    /**
     * 获取MAC地址
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        return getMac(context, true);
    }
    /**
     * 获取MAC地址
     * @param context
     * @param withHyphen 是否带"-"
     * @return
     */
    public static String getMac(Context context, boolean withHyphen) {
        String imei = "";
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null && mac.length > 0) {
                    imei = formatMac(mac, withHyphen);
                    break;
                }
            }
        } catch (Exception e) {
            imei = "";
        }
        return imei;
    }

    /**
     * 获取设备标识，使用串号加网卡地址
     * @param context
     * @return
     */
    public static String getDeviceLabel(Context context) {
        String imei = getImei(context);
        String mac = getMac(context, false);
        return imei + "$" + mac;
    }

    private static String formatMac(byte[] mac, boolean withHyphen) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            byte b = mac[i];
            int intValue = 0;
            if (b >= 0) {
                intValue = b;
            } else {
                intValue = 256 + b;
            }
            sb.append(Integer.toHexString(intValue));
            if (withHyphen && i != mac.length - 1) {
                sb.append("-");
            }
        }
        return sb.toString();
    }


    public static String transDate(long timeStamp) {
        long time = System.currentTimeMillis() - timeStamp * 1000;
        long min = 1000L * 60;
        long hour = min * 60;
        long day = hour * 24;
        long month = day * 30;
        if(time > month){
            return  "1个月前";
        }else if (time > day) {
            return time / day + "天前";
        } else if (time > hour) {
            return time / hour + "小时前";
        } else if (time > min) {
            return time / min + "分钟前";
        }
        return "";

    }

    public static byte[] copyOfRange(byte[] from, int start, int end) {
        int length = end - start;
        byte[] result = new byte[length];
        System.arraycopy(from, start, result, 0, length);
        return result;
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    private String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    private String getSDAvailableSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    private String getRomTotalSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    private String getRomAvailableSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }


}
