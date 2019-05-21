package com.utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.utils.LogcatFileHelper;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


/**
 * Created by jiongfang on 2018/9/21.
 * WebSocket封装 实用类
 * 一般使用WebSocket 可以导入  java-websocket.jar ，然后客户端使用 WebSocketClient就可以了
 * 但本类使用OkHttp 开源框架包的WebSocket 注意版本要>3.5
 * compile 'com.squareup.okhttp3:okhttp:3.9.0'
 * compile 'com.squareup.okio:okio:1.7.0'
 * <p>
 * <p>
 * 个人：notice,目前如果启动软件时 无网络或连接不到主机，使用过程中网络恢复的再发消息 首包会无效但会启动连接
 * 如果使用运行过程中 无网络或连接不到主机，使用过程中网络恢复 10s后有心跳定时器的 会恢复连接，但10s首包无效
 * ---- 可以考虑专门添加一个定时器恢复连接
 */
public class WebSocketHelper {
    public WebSocketHelper(Context context, String url) {
        mContext = context;
        wsUrl = url;
    }

    private final static String TAG = "WebSocketHelper";
    private static WebSocketHelper webSocketHelper;

    private Context mContext;

    private String wsUrl = "";
    private WebSocket mWebSocket;
    private OkHttpClient mOkHttpClient;
    private Request mRequest;
    private SocketListener listener;
    private ExecutorService sendExecutor;
    private Timer timer;
    private TimerTask task;
    private boolean isAliveHeart = false;
    private String aliveSendStr = "";
    private int aliveHeartTime = 10; //10s

    public int reconnectCount = 0;   //重连次数
    public int ConnectWebSocketState = -1;  //网络webSocket 连接转态
    public final static int NET_DISCONNECT = -1;  //没有网络状态
    public final static int WEBSOCKET_DISCONNECT = 0; // 未连接到webSocket或连接不上、已关闭
    public final static int WEBSOCKET_CONNECT = 1;   //已连接上webSocket

    private static int READ_TIMEOUT = 3;
    private static int WRITE_TIMEOUT = 3;
    private static int CONNECT_TIMEOUT = 3;

    class CODE {
        public final static int NORMAL_CLOSE = 1000;  //正常关闭
        public final static int ABNORMAL_CLOSE = 1001;  //异常关闭
    }

    class TIP {
        public final static String NORMAL_CLOSE = "normal close";//正常关闭
        public final static String ABNORMAL_CLOSE = "abnormal close"; //异常关闭
    }

    //单列模式
    public static synchronized WebSocketHelper getInstance(Context context, String url) {
        if (webSocketHelper == null) {
            webSocketHelper = new WebSocketHelper(context, url);
        }
        return webSocketHelper;
    }

    //数据接收监听接口
    public OnReadMsgListener onReadMsgListener;

    public interface OnReadMsgListener {
        void OnReadMsg(String text);
    }

    public void setOnReadMsgListener(OnReadMsgListener onReadMsgListener) {
        this.onReadMsgListener = onReadMsgListener;
    }

    //网络主机连接 打开 监听  用于网络连接后初始化发送
    public OnOpenConnectListener onOpenConnectListener;

    public interface OnOpenConnectListener {
        void OnOpenConnect(WebSocket webSocket);
    }

    public void setOnOpenConnectListener(OnOpenConnectListener onOpenConnectListener) {
        this.onOpenConnectListener = onOpenConnectListener;
    }


    //发送数据
    public boolean Send(final String sendStr) {
        if (isNetworkConnected(mContext)) {
            //先进行 网络判断
            if (ConnectWebSocketState == WEBSOCKET_CONNECT && mWebSocket != null && sendExecutor != null) {
                sendExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mWebSocket.send(sendStr);
                        LogcatFileHelper.i("Jiong>>" + TAG, "发送websocket字符：  " + sendStr);
                    }
                });
            } else {
                LogcatFileHelper.w("Jiong>>" + TAG, "发送数据包时 判断网络webSocket 断开！--准备重连接");
                //disconnect();  //清除连接
                buildWebSocketConnect(); //尝试重连接
            }
        } else {
            LogcatFileHelper.w("Jiong>>" + TAG, "Send 无网络");
        }
        return false;
    }

    public void hasAliveHeart(boolean isAlive, int aliveTime, String str) {
        this.isAliveHeart = isAlive;
        this.aliveHeartTime = aliveTime;
        this.aliveSendStr = str;
        //设置定时器心跳
        if (isAliveHeart) {
            LogcatFileHelper.i("Jiong>>" + TAG, "定时器 使能");
            //    newTask();
        }
    }

    private void newTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        task = new TimerTask() {
            @Override
            public void run() {
                //先进行 网络判断
                if (isNetworkConnected(mContext)) {
                    if (ConnectWebSocketState == WEBSOCKET_CONNECT && mWebSocket != null) {
                        if (isAliveHeart) {
                            LogcatFileHelper.d("Jiong>>" + TAG, "发送心跳：  " + aliveSendStr);
                            mWebSocket.send(aliveSendStr);
                        } else {
                            LogcatFileHelper.d("Jiong>>" + TAG, "定时器 --没发送心跳  ");
                        }
                    } else {//重连接
                        LogcatFileHelper.w("Jiong>>" + TAG, "定时器 判断网络webSocket 断开！--准备重连接");
                        // disconnect();  //清除连接
                        buildWebSocketConnect(); //尝试重连接
                    }
                } else {
                    LogcatFileHelper.w("Jiong>>" + TAG, "定时器 无网络");
                }
            }
        };
    }

    //初始化并连接WebSocket
    public boolean buildWebSocketConnect() {
        if (isNetworkConnected(mContext)) { //判断网络连接状态
            return initWebSocket(wsUrl);  //初始化WebSocket
        } else {
            Log.w(TAG, "Jiong>> 检查到目前没网络连接！");
        }
        return false;
    }


    //检查网络是否连接
    private boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                if (mNetworkInfo.isAvailable()) {
                    return true;
                }
            } else {
                Log.w(TAG, "Jiong>> mNetworkInfo == null");
            }
        } else {
            Log.w(TAG, "Jiong>> isNetworkConnected   context== null");
        }
        ConnectWebSocketState = NET_DISCONNECT;  //记录网络不能连接状态
        return false;
    }

    //初始化WebSocket
    private boolean initWebSocket(String wsUrl) {
        boolean ret = false;
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true) //允许失败重试
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                    .build();
        }
        if (mRequest == null) {
            mRequest = new Request.Builder()
                    .url(wsUrl)
                    .build();
        }
        if (listener == null) {
            listener = new SocketListener();
        }

        //清除 心跳定时器
        if (isAliveHeart) {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            timer = new Timer();
            if (task != null) {
                task.cancel();
                task = null;
            }
            newTask();
        }

        //去掉队里中或者带运行状态的所有接口call
        mOkHttpClient.dispatcher().cancelAll();
        try {
            //  mLock.lockInterruptibly();
            try {
                ConnectWebSocketState = WEBSOCKET_DISCONNECT; //记录网络webSocket未连接状态
                mOkHttpClient.newWebSocket(mRequest, listener);
                ret = true;
                reconnectCount++;  //重新连接次数  首次1
                LogcatFileHelper.i("Jiong>>" + TAG, "initWebSocket 初始化WebSocket成功！");
            } finally {
                //       mLock.unlock();
            }
        } catch (Exception e) {
            ret = false;
        }

        //mOkHttpClient.dispatcher().executorService().shutdown();  //清除并关闭线程池
        return ret;
    }

    public void disconnect() {
        LogcatFileHelper.w("Jiong>>" + TAG, "into disconnect!");
        // 清除 发送队列
        if (sendExecutor != null && !sendExecutor.isShutdown()) {
            sendExecutor.shutdown();
            sendExecutor = null;
        }
        //清除 心跳定时器
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }

        if (mWebSocket != null) {
            boolean isClosed = mWebSocket.close(CODE.NORMAL_CLOSE, TIP.NORMAL_CLOSE);
            //非正常关闭连接
            if (!isClosed) {
                if (listener != null) {
                    listener.onClosed(mWebSocket, CODE.ABNORMAL_CLOSE, TIP.ABNORMAL_CLOSE);
                    LogcatFileHelper.w("Jiong>>" + TAG, "into disconnect!   --flag3");
                }
            }
            if (mWebSocket != null) {
                //  mWebSocket.cancel();
                //   LogcatFileHelper.w("Jiong>>"+TAG,"into disconnect!   --flag5");
            }
        }
    }


    //webSocket 连接成功/失败、数据返回、连接中断、连接结束 响应监听
    private class SocketListener extends WebSocketListener {
        public SocketListener() {
            super();
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            ConnectWebSocketState = WEBSOCKET_DISCONNECT;//记录网络webSocket未连接状态
            Log.e(TAG, "Jiong>>onClosed   code=" + code + "    reason=" + reason);
            if (code == CODE.NORMAL_CLOSE) {
                Log.e(TAG, "Jiong>>onClosed   code=" + code + "    webSocket 正常关闭！");
            } else if (code == CODE.ABNORMAL_CLOSE) {
                Log.e(TAG, "Jiong>>onClosed   code=" + code + "    webSocket 非正常关闭！");
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            ConnectWebSocketState = WEBSOCKET_DISCONNECT;//记录网络webSocket未连接状态
            Log.e(TAG, "Jiong>>onClosing   code=" + code + "    reason=" + reason);
            ;
            webSocket.close(CODE.NORMAL_CLOSE, TIP.NORMAL_CLOSE);  //定义正常关闭  -->会调用onClosed
        }

        @Override // 连接主机失败/断网
        public void onFailure(WebSocket webSocket, Throwable t, @android.support.annotation.Nullable Response response) {
            super.onFailure(webSocket, t, response);
            if (t instanceof SocketTimeoutException) {//连接超时
                Log.e("onFailure 连接超时", t.getMessage());
            } else if (t instanceof UnknownHostException) {//服务器主机未找到
                Log.e("onFailure 主机未找到", t.getMessage());
            } else {//其他错误--eg:断网
                Log.e("onFailure 其他错误", t.getMessage());
                t.printStackTrace();
            }
            ConnectWebSocketState = WEBSOCKET_DISCONNECT;//记录网络webSocket未连接状态

            webSocket.close(CODE.ABNORMAL_CLOSE, TIP.ABNORMAL_CLOSE); //定义非正常关闭   -->会调用onClosed
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
            Log.d(TAG, "Jiong>>onMessage 1   回包：" + String.valueOf(bytes));
        }

        @Override //接收到主机返回数据
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            Log.d(TAG, "Jiong>>onMessage 2  回包：" + text);
            if (onReadMsgListener != null) {
                onReadMsgListener.OnReadMsg(text);
            }
        }

        @Override  //连接上主机 newWebSocket后响应
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            mWebSocket = webSocket;
            Log.d(TAG, "Jiong>>onOpen");
            ConnectWebSocketState = WEBSOCKET_CONNECT;//记录网络webSocket已连接状态

            if (sendExecutor == null || sendExecutor.isShutdown()) {
                sendExecutor = Executors.newSingleThreadExecutor();
            }
            //设置监听
            if (onOpenConnectListener != null) {
                onOpenConnectListener.OnOpenConnect(webSocket);
            }

            //判断启动心跳定时器
            if (isAliveHeart) {
                if (timer == null) {
                    timer = new Timer();
                }
                if (task == null) {
                    newTask();
                }
                timer.schedule(task, 1000 * 5, aliveHeartTime);
            }
        }

    }

/***
 * 个人备注：目前ws://pandora.service.kugou.com/v1/pandora?mid=888 这个主机收发超时30s.超时中断逻辑 OnMessage->Onclosing->Onclosed  回包op: notify
 * 使用demo:
 *  1. new WebSocketHelper
 *    webSocketHelper = new WebSocketHelper(mContext,WebSocketDemo.URL);  //或单例模式 getInstance();
 *  2.设置 连接监听  --发送初始化命令
 *  webSocketHelper.setOnOpenConnectListener(new WebSocketHelper.OnOpenConnectListener(){});
 *  3.设置接收监听
 *   webSocketHelper.setOnReadMsgListener(new WebSocketHelper.OnReadMsgListener() {});
 *  4. 设置心跳  ，如果不需要唱连接 可以不需要
 *   webSocketHelper.hasAliveHeart(true,10*1000, jsonObject.toString());
 *  5.初始化网络连接
 *   webSocketHelper.buildWebSocketConnect();
 *  6.通信不需要 断开连接
 *  webSocketHelper.disconnect();
 * */


}

