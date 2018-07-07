package com.utils.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.MyApplication;
import com.utils.LogcatFileHelper;
import com.utils.MyBroadcastReceiver;

import java.util.HashMap;
import java.util.Iterator;

/****
 * android usb 通信处理类
 * made by ：fjw0312
 * date : 2017.12.19
 *
 * */
public class UsbHAL {

	public UsbHAL() {
		// TODO Auto-generated constructor stub
	}


	public  static UsbManager usbManager;    // usb管理器对象
	public static UsbDevice mydevice;       // usb设备对象
	UsbInterface intf;        // usb接口对象
	UsbDeviceConnection connection; // usb设备连接对象
	UsbEndpoint epOut, epIn;        // 输入、输出 端点 对象

	public static boolean UsbRunFlag = false;
	public static int  SendError = 0;
	public static int  ReceiveError = 0;

	/***
	 * 函数名：void get_usb_info()
	 * 功能：初始化   获得 usb设备的初始化信息
	 * 传入已知变量：mydevice usbManager 经行赋值变量：connection intf  epOut  epIn
	 * 使用：  可以在广播 获取到usb设备插入后调用 该usb初始化函数
	 * **/
	//初始化   获得 usb设备的初始化信息
	public int get_usb_info(){ //先传入mydevice
		LogcatFileHelper.i("UsbHAL->get_usb_info", "into ！ ");
		//1. 打开usb 设备
		connection = usbManager.openDevice(mydevice); // 打开usb设备
		if (connection == null) { // 不成功，则退出    usbManager.openDevice error
			MyBroadcastReceiver.sendBroad_Error_HAL("Usb打开失败！");
			LogcatFileHelper.w("UsbHAL->get_usb_info", "Usb openDevice Error！ ");
			return -1;
		}
		//2.判断 usb接口设备 数量
		if (mydevice.getInterfaceCount() != 1) {  // 接口数量
			MyBroadcastReceiver.sendBroad_Error_HAL("Usb接口数量异常！");
			LogcatFileHelper.w("UsbHAL->get_usb_info", "Usb Interface fault！ ");
			return -2;
		}
		//3.获得 第一个接口
		intf = mydevice.getInterface(0); // 获取第一个接口
		if (intf == null) {
			MyBroadcastReceiver.sendBroad_Error_HAL("Usb接口异常null");
			LogcatFileHelper.w("UsbHAL->get_usb_info", "Usb Interface null ");
			return -3;
		}
		//4. 独占接口
		connection.claimInterface(intf, true); // 独占接口

		//5. 获取端点 endpoint
		int cnt = intf.getEndpointCount(); // 获取端点数
		if (cnt < 1) {
			MyBroadcastReceiver.sendBroad_Error_HAL("Usb接口端点获取失败！");
			LogcatFileHelper.w("UsbHAL->get_usb_info", "Usb Endpoint Error ");
			return -4;
		}
		//获得 输出端点  输入端点
		for (int index = 0; index < cnt; index++) {
			UsbEndpoint ep = intf.getEndpoint(index);
			if ((ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)
					&& (ep.getDirection() == UsbConstants.USB_DIR_OUT)) {
				epOut = ep; // 针对主机而言，从主机 输出到设备,获取到 bulk的输出端点 对象
			}
			else if ((ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)
					&& (ep.getDirection() == UsbConstants.USB_DIR_IN)) {
				epIn = ep; // 针对主机而言，从设备 输出到主机 ,获取到 bulk的输入端点 对象
			}
		}

		//	MyBroadcastReceiver.sendBroad_MSG_HAL("Usb设备打开初始化 正常！");
		LogcatFileHelper.i("UsbHAL->get_usb_info", "Usb open and init UsbDevice Info OK! ");
		return 0;
	}


	//搜寻设备
	public int UsbSerch(final PendingIntent mPermissionIntent) {
		//1.获得usb 管理服务
		usbManager = (UsbManager) MyApplication.getContext().getSystemService(Context.USB_SERVICE); // 获取usb服务
		if (usbManager == null) {
			MyBroadcastReceiver.sendBroad_Error_HAL("获取USB服务失败！");
			LogcatFileHelper.e("UsbHAL->UsbSerch", "get UsbManager Error！ ");
			return -1;
		}
		//2. 搜索所有的usb设备   获得usb设备UsbDevice
		HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		while (deviceIterator.hasNext()) {
			UsbDevice device = deviceIterator.next();
			if (device.getVendorId() == 0x0471&& device.getProductId() == 0x5002) {
				mydevice = device;
				MyBroadcastReceiver.sendBroad_MSG_HAL("获得Usb设备！");
				LogcatFileHelper.i("UsbHAL->UsbSerch", "get Usb Device！ ");
				break;
			}
		}
		//3. 判断 是否没有找到 所要的设备
		if (mydevice == null) {
			MyBroadcastReceiver.sendBroad_Error_HAL("未找到USB Device！");
			LogcatFileHelper.e("UsbHAL->UsbSerch", "not find USB Device , device=null");
			return -2;
		}

		//4. 判读 usb 拥有权限  并初始化usb信息
		if (usbManager.hasPermission(mydevice)) { // 判断是否有权限 使用usb设备
			get_usb_info(); // 获取usb相关信息
			//	vib.vibrate(100); // 震动

		} else {  //usb 没有获得权限  No permission
			// 没有权限询问用户是否授予权限
			usbManager.requestPermission(mydevice, mPermissionIntent); // 该代码执行后，系统弹出一个对话框，
			// 询问用户是否授予程序操作USB设备的权限
		}
		UsbRunFlag = true;
		return 0;
	}

	//销毁 释放usb 端口
	public void OnDestory(){
		if(connection!=null){
			connection.releaseInterface(intf);
			connection = null;
		}
	}

	//接收  usb 数据  -- 需要在 子线程调用
	public byte[] UsbRead(byte[] InBuffer,int length) {
		if(InBuffer==null){
			MyBroadcastReceiver.sendBroad_Error_HAL("接收数组InBuffer=null！ ");
			return null;
		}
		int timeout = 5000;
		if(connection==null || epIn==null) return null;
		int cnt = connection.bulkTransfer(epIn, InBuffer, length, timeout); // 接收bulk数据
		if (cnt < 0) { // 没有接收到数据，则继续循环
			ReceiveError++;
			if(ReceiveError>100) ReceiveError = 0;
			return null;
		}

		//打印  接收到 的数据
		//String msg = MyUtils.printfBs(InBuffer);
		//MyBroadcastReceiver.sendBroad_MSG_HAL("接收到数据   "+msg);

		return InBuffer;
	}

	//发送  usb 数据  -- 需要在 子线程调用
	public boolean UsbSend(byte[] bs, int length) {
		if(bs==null){
			MyBroadcastReceiver.sendBroad_Error_HAL("发送数组bs=null！ ");
			return false;
		}
		int timeout = 1000;
		if(connection==null || epOut==null) return false;
		int cnt = connection.bulkTransfer( epOut, bs, length,timeout); // 发送bulk 数据给下位机
		if(cnt >=0){ //发送成功
		//	MyBroadcastReceiver.sendBroad_MSG_HAL("发送返回参数 "+String.valueOf(cnt));
		}else{  //发送失败
			SendError++;
			if(SendError>100) SendError = 0;
			MyBroadcastReceiver.sendBroad_Error_HAL("请求数据失败！ ");
			return false;
		}
		return true;
	}
	/*** 使用方式：
	 * 先注册 usb 广播及权限
	 *         //注册  usb授权 广播
	 mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
	 IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
	 registerReceiver(mUsbReceiver, filter); //设置广播接收器

	 <intent-filter>
	 <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"/>
	 <action android:name="android.hardware.usb.action.USB_DEVICE_DETACHED"/>
	 </intent-filter>
	 <meta-data android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" android:resource="@xml/device_filter"/>
	 *
	 *
	 *搜索 usb 设备
	 * usbHAL = new UsbHAL();
	     int result = usbHAL.UsbSerch(mPermissionIntent);
	 if(result ==0){
	 		//进行数据收发  具有阻塞作用 超时见代码！
	 		usbHAL.UsbSend(bs, bs.length);
	 		usbHAL.UsbRead(InBuffer, length);
	 }
	 //usb关闭
	 usbHAL.OnDestory();
	 *
	 * */
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		// usb 设备操作 授权
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			//LogcatFileHAL.d("mUsbReceiver", action+"USB_PERMISSION Broadcast onReceive广播授权成功!");
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (device != null) {
							// call method to set up device communication
							UsbHAL.this.mydevice = device;
							UsbHAL.this.get_usb_info();
							MyBroadcastReceiver.sendBroad_MSG_HAL("USB权限授权成功&初始化");
							//LogcatFileHAL.d("mUsbReceiver", "USB_PERMISSION Broadcast OK 广播授权成功&初始化usb ");
						}
					} else {
						//LogcatFileHAL.w("mUsbReceiver", "usb 授权Error !");
						MyBroadcastReceiver.sendBroad_MSG_HAL("usbUSB_PERMISSION 授权Error!");
						return;
					}
				}
			}
		}
	};

}
