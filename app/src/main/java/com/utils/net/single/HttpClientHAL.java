package com.utils.net.single;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Queue;


//import MyApplicationInit.MyApplication;
import android.content.Intent;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.MyApplication;


/**
 * HttpClient 接口     不再使用
 * android 内嵌的apach 开源HttpClient  在android 6.0 就被舍弃了。
 * date:2017.5.24
 */
public class HttpClientHAL {

    public HttpClientHAL() {
        // TODO Auto-generated constructor stub
    }
/*
	//发送  GET 请求
	public static String sendReques_Get(String strUrl){
		String str_response = "";
		try{
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(strUrl);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			//接收返回响应
			if(httpResponse.getStatusLine().getStatusCode()==200){  //HTTP_OK=200
				HttpEntity httpEntity = httpResponse.getEntity();
				str_response = EntityUtils.toString(httpEntity,"utf-8");
			}
		}catch(Exception e){
			Log.e("HttpClientHAL" ,"sendReques_Get网络异常抛出！");
			//发送  广播通知
			Intent intent = new Intent("Fang.MyBroadcast.Error");
			intent.putExtra("fang", "HttpClientHAL-GET 网络异常");
			MyApplication.getContext().sendBroadcast(intent);
		}
		return str_response;
	}

	//上报    POST
	public static String sendReques_Post(String strUrl){
		String str_response = "";
		try{
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(strUrl);

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", "admin"));
			params.add(new BasicNameValuePair("password", "123456"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
			httpPost.setEntity(entity);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			//接收返回响应
			if(httpResponse.getStatusLine().getStatusCode()==200){
				HttpEntity httpEntity = httpResponse.getEntity();
				str_response = EntityUtils.toString(httpEntity,"utf-8");
			}
		}catch(Exception e){
			Log.e("HttpClientHAL","sendReques_Get网络异常抛出！");
			//发送  广播通知
			Intent intent = new Intent("Fang.MyBroadcast.Error");
			intent.putExtra("fang", "HttpClientHAL-GET 网络异常");
			MyApplication.getContext().sendBroadcast(intent);
		}
		return str_response;
	}
	*/
}
