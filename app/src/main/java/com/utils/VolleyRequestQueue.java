package com.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by jiongfang on 2018/3/28.
 * 采用Volley 网络请求框架
 */
public class VolleyRequestQueue {

    public static RequestQueue mQueue;

    public static synchronized RequestQueue instance(Context context) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(context);
        }
        return mQueue;
    }

    //注bug 使用静态jsonObject 存在 多次请求不及时响应问题！！！！！！！！！
    public static JSONObject jsonObject = new JSONObject();

    // 获取  Http Get方法 JSONObject
    public static JSONObject getJsonObject_Volley_Get(Context context, String strUrl) {
        //1.创建 RequestQueue
        RequestQueue mQueue = VolleyRequestQueue.instance(context); //  RequestQueue mQueue = Volley.newRequestQueue(context);
        //2.创建 JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strUrl, jsonObject,
                new myRequestListener(), new myRequestErrorListener());
        //3. 请求数据对象 添加到Queue
        mQueue.add(jsonObjectRequest);

        return jsonObject;
    }

    private static class myRequestListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            jsonObject = response;
        }
    }

    private static class myRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            LogcatFileHelper.e("VolleyRequestQueue>", "getJsonObject_Volley_Get>接收异常！");
        }
    }

}
