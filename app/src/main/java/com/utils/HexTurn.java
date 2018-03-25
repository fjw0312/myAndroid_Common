package com.utils;

import android.util.Log;

// 数值进制 字符串 十六进制等转换

// 辅助工具类
public class HexTurn {
	//十进制 转换为 16进制字符串
	public static String Int_TrunToHex(int x){
		String hex = Integer.toHexString(x);
		return hex;
	}

	//十六进制字符串 转换为 10进制数值
	public static int Hex_TrunToInt(String hex){
		int x = Integer.parseInt(hex,16);
		return x;
	}

	//将 十六进制字符串 转换为  转化为byte
	public static byte Hex_TrunToByte(String hex){
		int x = Integer.parseInt(hex,16);
		byte y = (byte)(x&0xff);
		return y;
	}
	//将  byte 装换为 int
	public static int Byte_TrunToInt(byte x){
		char a = (char)(x&0xff);
		int s = a;
		return s;
	}
	//将 byte 转换为 Hex
	public static String Byte_TrunToHex(byte x){
		int a = Byte_TrunToInt(x);
		String hex = Int_TrunToHex(a);
		return hex;
	}
	//将字符串 十六进制hex 字符 转换为byte[]  eg："01 20 30 40"
	public static byte[] getBs(String str){
		if("".equals(str))  return null;
		String[] bs_str = str.split(" ");
		byte[]  bs = new byte[bs_str.length];
		for(int i=0;i<bs.length;i++){
			bs[i] =   Hex_TrunToByte(bs_str[i]);
		}
		return bs;
	}
	// 打印 byte[]  数组
	public  static String printfBs(byte[] bs){
		if(bs==null) return "";
		String[] str_s = new String[bs.length];
		StringBuffer str = new StringBuffer();
		for(int i=0;i<bs.length;i++){
			int x = Byte_TrunToInt(bs[i]);
			String hex = Int_TrunToHex(x);
			str_s[i] = hex;
			if(hex.length()==1){
				hex = "0x0"+hex;
			}else{
				hex = "0x"+hex;
			}

			str.append(hex+"  ");
		} 
		String tt = new String(str); 
		Log.i("数组：", tt);
	//	MyBroadcastReceiver.sendBroad_MSG_HAL(tt);
		return tt;
	}
	
	//CRC 校验
	public static int CRC16(byte[] buf, int len) {
		int i, j;
		int c, crc = 0xFFFF;
		for (i = 0; i < len; i++) {
			c = buf[i] & 0x00FF;
			crc ^= c;
			for (j = 0; j < 8; j++) {
				if ((crc & 0x0001) != 0) {
					crc >>= 1;
					crc ^= 0xA001;
				} else
					crc >>= 1;
			}
		}
		return (crc);
	}

	
}
