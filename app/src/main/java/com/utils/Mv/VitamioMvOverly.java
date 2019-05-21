package com.utils.Mv;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mycom.R;
import com.utils.LogcatFileHelper;
import com.utils.StatusBarCompat;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


/**
 * Created by jiongfang on 2018/9/29.
 * 点歌Mv浮窗   使用了 vitamio 画面不好，且 还是需要配置下载缓存 暂时 不实现了
 *     //多功能视频播放器 vitamio
    compile 'com.charonchui.vitamio:vitamio:4.2.2'
 */
public class VitamioMvOverly {
    public VitamioMvOverly(Context context, String mp4url, String song, String singer) {
        mContext = context;
        this.Mp4url = mp4url;
        this.song = song;
        this.singer = singer;
    }
    private static final String TAG = "MvOverly";
    private Context mContext;

    private LinearLayout popup_window;
    private View mView = null;
    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams params;
    public boolean isShown = false;
    public final int  SHOW_WINDOW_TIME = 1000*300;  //浮窗 显示 时间


    private TextView textTime;
    private ImageView dissView;
    private LinearLayout title_layout;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private String Mp4url = "";
    private String song = "";
    private String singer = "";
    private Timer timer;
    private ProgressBar progressBar;
    private long buffProcess = 0;
    private boolean isPlay = false;

    private long allTime = 0;
    private long currentTime = 0;
    private String strAllTime = "";

    private VideoView videoView;

    public void initView() {
        if (isShown) {
            return;
        }
        isShown = true;
        mView = setUpView(mContext);


        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        params  = new WindowManager.LayoutParams();
        //设置type.系统提示型窗口，TYPE_SYSTEM_ALERT一般都在应用程序窗口之上.
        if (Build.VERSION.SDK_INT >= 23) {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;    //sdk >= 23
            // params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }else{
          //  params.type = WindowManager.LayoutParams.TYPE_TOAST;            //sdk  <= 23
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        // 设置flag
     //   params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
     //   int flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
     //   params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    //    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;
          params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_FULLSCREEN; //设置flags.  响应应窗口， 响应应窗口外的点击，不传递窗口底下点击
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.format = PixelFormat.TRANSLUCENT;
     //   params.width = WindowManager.LayoutParams.WRAP_CONTENT;
     //   params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.width = 900;
        params.height = 700;
        int screen_x = mWindowManager.getDefaultDisplay().getWidth();
        int screen_y = mWindowManager.getDefaultDisplay().getHeight()- StatusBarCompat.getStatusBarHeight(mContext)- StatusBarCompat.getDaoHangHeight(mContext);
        params.x = (screen_x - params.width)/2;
        params.y = (screen_y - params.height)/2;
        LogcatFileHelper.i("Jiong>>"+TAG,"into initView params.y="+params.y+"   params.x="+params.x);
        mWindowManager.addView(mView, params);
    }

    private View setUpView(Context context) {
        //   View view = View.inflate(context,R.layout.activity_volume, null);
        View view = LayoutInflater.from(context).inflate(R.layout.overlay_mv, null);

        popup_window = (LinearLayout)view.findViewById(R.id.update_lay);
        dissView = (ImageView)view.findViewById(R.id.dissView);
        title_layout = (LinearLayout)view.findViewById(R.id.title_layout);
        dissView.setOnClickListener(l);
        textTime = (TextView)view.findViewById(R.id.time);
        videoView = (VideoView)view.findViewById(R.id.vvid);
       // surfaceView = (SurfaceView)view.findViewById(R.id.surfaceView);

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        statusBarHeight = getStatusBarHeight();

       // view.setBackgroundResource(R.color.mini_lyr_background_color);
        //获取 视图区域 点击处理
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                Rect rect = new Rect();
                popup_window.getGlobalVisibleRect(rect);
                if (!rect.contains(x, y)) {
                    LogcatFileHelper.i("Jiong>>"+TAG,"into setOnTouchListener 浮窗区域 外");
  //                  dissmissView();
                }else{
                    LogcatFileHelper.i("Jiong>>"+TAG,"into setOnTouchListener 浮窗区域 内");
                    dissmissViewDelay();
                }
                //UsbHidUtils.Log(TAG,"setOnTouchListener x="+x+" y="+y +" rect.width="+rect.width()+" rect.height="+rect.height());
                return false;
            }
        });

        //处理 back 键
        popup_window.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK){
                    dissmissView();
                    LogcatFileHelper.i("Jiong>>"+TAG,"into onKey KeyEvent.KEYCODE_BACK");
                }
                return false;
            }
        });
        //处理点击 窗口标题 拖动事件
        title_layout.setOnTouchListener(onTouchListener );

        return view;
    }
    // 系统状态栏的高度
    private static int statusBarHeight;
    // 记录当前手指位置在屏幕上的横坐标
    private float xInScreen;
    // 记录当前手指位置在屏幕上的纵坐标
    private float yInScreen;
    // 记录手指按下时在屏幕上的横坐标,用来判断单击事件
    private float xDownInScreen;
    // 记录手指按下时在屏幕上的纵坐标,用来判断单击事件
    private float yDownInScreen;
    // 记录手指按下时在小悬浮窗的View上的横坐标
    private float xInView;
    // 记录手指按下时在小悬浮窗的View上的纵坐标
    private float yInView;
    //处理点击 窗口标题 拖动事件
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                // 手指按下时记录必要的数据,纵坐标的值都减去状态栏的高度
                case MotionEvent.ACTION_DOWN:
                    // 获取相对与小悬浮窗的坐标
                    xInView = motionEvent.getX();
                    yInView = motionEvent.getY();
                    //Log.i("Jiong>>"+TAG,"按下的相对坐标xInView="+xInView+"   yInView="+yInView);
                    // 按下时的坐标位置，只记录一次
                    xDownInScreen = motionEvent.getRawX();
                    yDownInScreen = motionEvent.getRawY() - statusBarHeight;
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 时时的更新当前手指在屏幕上的位置
                    xInScreen = motionEvent.getRawX();
                    yInScreen = motionEvent.getRawY() - statusBarHeight;
                 //   Log.i("Jiong>>"+TAG,"手指在屏幕上的位置xInScreen="+xInScreen+"   yInScreen="+yInScreen);
                    // 手指移动的时候更新小悬浮窗的位置
                    updateViewPosition();
                    break;
                case MotionEvent.ACTION_UP:
                    // 如果手指离开屏幕时，按下坐标与当前坐标相等，则视为触发了单击事件
                    if (xDownInScreen == motionEvent.getRawX()
                            && yDownInScreen == (motionEvent.getRawY() - getStatusBarHeight())) {
                        //触发点击事件
                    }
                    break;
            }
            return true;
        }
    };
    /**
     * 获取状态栏的高度
     *
     * @return
     */
    private int getStatusBarHeight() {

        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            return mContext.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }





    //显示 当前 浮窗
    public void showView() {

        initView();

       // setAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initVideo();
            }
        },200);

       // dissmissViewDelay(); //最多显示300s
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            currentTime = videoView.getCurrentPosition();
            UIHandler.removeMessages(MSG_REFRESH_TIME);
            UIHandler.sendEmptyMessage(MSG_REFRESH_TIME);
            //等待缓存
            if(buffProcess != 0 && buffProcess!=99){
                if(buffProcess <= currentTime ){  //缓存不够 等待缓存
                    setPause();
                }else{  //缓存oK
                    if(!isPlay){ //是否暂停状态  开始播放
                        setPlay();
                    }
                }
            }

            Log.i("Jiong>>timerTask>>" + Thread.currentThread().getName() + "", TAG+"当前播放时间:"+currentTime);
        }
    };
    public void initVideo(){
        //一定要初始化
        Vitamio.initialize(mContext);

        videoView.setVideoURI(Uri.parse(Mp4url));//设置播放地址
        videoView.setMediaController(new MediaController(mContext));
        videoView.setVolume(0f,0f);

        //设置监听
        videoView.setOnPreparedListener(onPreparedListener);
        videoView.setOnErrorListener(onErrorListener);
        videoView.setOnCompletionListener(onCompletionListener);
        videoView.setOnBufferingUpdateListener(onBufferingUpdateListener);
        videoView.setOnInfoListener(onInfoListener);
    }
    //设置 缓存 监听
    private MediaPlayer.OnPreparedListener  onPreparedListener = new MediaPlayer.OnPreparedListener(){
        @Override
        public void onPrepared(MediaPlayer mp) {
            isPlay = false;
            //       seekToMv(InitDibbleHelper.songRunTime);

            float startTime = 0;

            startTime = startTime*1000+300; //延时500ms 开始播放
            Log.e("Jiong>>"+TAG, "mediaPlayer>>onPrepared  startTime="+startTime);
            UIHandler.removeMessages(MSG_START_MEDIAPLAY);
            UIHandler.sendEmptyMessageDelayed(MSG_START_MEDIAPLAY, (int)startTime); //mediaPlayer.start();
            allTime = videoView.getDuration();  //获取播放时长ms  0.001s
            Log.i("Jiong>>"+TAG, "mediaPlayer>>onPrepared  allTime="+allTime);
            int c_s = (int)(allTime/1000);
            String c_str1 = (c_s/60 > 9)? String.valueOf(c_s/60): "0"+ String.valueOf(c_s/60);
            String c_str2 = (c_s%60 > 9)? String.valueOf(c_s%60): "0"+ String.valueOf(c_s%60);
            strAllTime = c_str1+":"+c_str2;
            if (timer == null) {
                timer = new Timer();
                timer.schedule(timerTask, 0, 1000);
            }
        }
    };
    //设置 播放结束监听
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.e("Jiong>>"+TAG, "mediaPlayer>>onCompletion  播放结束！");
            if(timer != null){
                timer.cancel();
                timer = null;
            }
        }
    };
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e("Jiong>>"+TAG, "mediaPlayer>>onError  播放  what="+what +"   extra="+extra);
            return false;
        }
    };
    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            buffProcess = percent * (allTime/100);
            if(percent>= 99) buffProcess = allTime;
            Log.w("Jiong>>"+TAG, "mediaPlayer>>onBufferingUpdate  percent="+percent +"     buffProcess="+buffProcess);
        }
    };
    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                Log.w("Jiong>>"+TAG, "mediaPlayer>>onInfo  暂停播放开始缓冲更多数据");
            }else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                Log.w("Jiong>>"+TAG, "mediaPlayer>>onInfo  缓冲了足够的数据重新开始播放");
            }else if(what == MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED){
                Log.w("Jiong>>"+TAG, "mediaPlayer>>onInfo  MEDIA_INFO_DOWNLOAD_RATE_CHANGED");
            }
            return false;
        }
    };




    //处理Mv 同步
    public void seekToMv(long markTime){
     //   CheckMv  checkMv = CheckData.getCheckMv(song, singer);
        LogcatFileHelper.i("Jiong>>"+TAG,"into --- seekToMv  ");
            float time = 0;
            float seekTo = (int)markTime+time *1000;
            videoView.seekTo((int)seekTo);
            LogcatFileHelper.i("Jiong>>"+TAG,"快进退进度："+seekTo);

    }

    //处理暂停播放
    public void setPlayAndPause(int state){
        if(state == 1){
            setPlay();
        }else if(state == 0){
            setPause();
        }
    }

    //处理暂停
    public void setPause(){
        if(videoView != null){
            videoView.pause();
            isPlay = false;
        }
    }

    //处理播放
    public void setPlay(){
        if(videoView != null){
        //    videoView.resume();
            videoView.start();
            isPlay = true;
        }
    }
    //释放 player
    public void releasePlayer(){
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }

    private final static int MSG_DISS_WINDOW = 0;  //隐藏 浮窗
    private final static int MSG_DISS_DELAY_WINDOW = 1;        //延时隐藏 浮窗
    private final static int MSG_REFRESH_TIME = 2;        //刷新播放时间
    private final static int MSG_ANIMOTOR_OVERLY_POSITION = 100;  //更新浮窗  位置
    private final static int MSG_ANIMOTOR_OVERLY_LAYOUT = 101;   //更新浮窗  大小位置
    private final static int MSG_START_MEDIAPLAY = 155;        //开始播放
    private final static int MSG_UPDTATE_MEDIAPLAY = 156;        //刷新播放



    private Handler UIHandler  = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case MSG_DISS_WINDOW:
                    dissmissView();
                    break;
                case MSG_DISS_DELAY_WINDOW:
                    dissmissViewDelay();
                    break;
                case MSG_REFRESH_TIME:
                    int c_s = (int)(currentTime/1000);
                    String c_str1 = (c_s/60 > 9)? String.valueOf(c_s/60): "0"+ String.valueOf(c_s/60);
                    String c_str2 = (c_s%60 > 9)? String.valueOf(c_s%60): "0"+ String.valueOf(c_s%60);
                    textTime.setText(c_str1+":"+c_str2+"/"+strAllTime);
                    break;
                case MSG_ANIMOTOR_OVERLY_POSITION:
                    UIHandler.removeMessages(MSG_ANIMOTOR_OVERLY_POSITION);
                    mWindowManager.updateViewLayout(mView, params);
                    break;
                case MSG_ANIMOTOR_OVERLY_LAYOUT:
                    UIHandler.removeMessages(MSG_ANIMOTOR_OVERLY_LAYOUT);
                    mWindowManager.updateViewLayout(mView, params);
                    UIHandler.sendEmptyMessageDelayed(104, 200);
                    //mDismissHandler.postDelayed(mDismissRunnable, 1000);
                    break;
                case 104:
                    setAnimation();
                    mDismissHandler.postDelayed(mDismissRunnable, 1000);
                    break;
                case MSG_START_MEDIAPLAY:
                    setPlay(); //mediaPlayer.start();
                    progressBar.setVisibility(View.GONE);

                    LogcatFileHelper.i("Jiong>>"+TAG,"mediaPlayer.start()  ---------");
                    UIHandler.sendEmptyMessageDelayed(MSG_UPDTATE_MEDIAPLAY,1000);
                    break;
                case MSG_UPDTATE_MEDIAPLAY:
               //     KugouInfo.sendRequestKsingInfoBroadcast(103);//请求k歌进度
                    break;
                default:break;
            }
        }
    };

    //设置监听器
    private View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             if(view.getId() == R.id.dissView){
               dissmissView();
            }
        }
    };

    //隐藏浮窗
    public void dissmissViewDelay() {
        mDismissHandler.removeCallbacks(mDismissRunnable);
        mDismissHandler.postDelayed(mDismissRunnable, SHOW_WINDOW_TIME);
    }
    //隐藏浮窗
    public void dissmissView() {
        mDismissHandler.removeCallbacks(mDismissRunnable);
        mDismissHandler.post(mDismissRunnable);
    }
    private Handler mDismissHandler = new Handler();
    private Runnable mDismissRunnable = new Runnable() {
        @Override
        public void run() {
            if(animatorSet != null ){
                clearAnimation();
            }
            if (isShown && null != mView ) {
                if(mWindowManager != null){
                    if(timer != null){
                        timer.cancel();
                        timer = null;
                    }
                    releasePlayer();
                     mWindowManager.removeViewImmediate(mView);
                    isShown = false;
                    LogcatFileHelper.w("Jiong>>"+TAG,"mDismissRunnable  去除浮窗！");
                }

            }
        }
    };

    private void updateViewPosition() {
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = (int) (xInScreen - xInView);
        params.y = (int) (yInScreen - yInView);
        UIHandler.sendEmptyMessage(MSG_ANIMOTOR_OVERLY_POSITION);
    }
    private void updateViewWithHeight() {
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        UIHandler.sendEmptyMessage(MSG_ANIMOTOR_OVERLY_LAYOUT);
    }




    //清除动画
    private void clearAnimation(){
       // loadingImg.clearAnimation();
       // loadingImg.setVisibility(View.GONE);
        animatorSet.cancel();
    }

    AnimatorSet animatorSet;
    //属性动画
    private void setAnimation(){
        animatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(popup_window, "scaleX",0.9f,0.7f, 0.4f,0.2f,0.1f, 0.05f,0.02f, 0.01f);
        animator.setDuration(1000);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(popup_window, "scaleY",0.9f, 0.7f,0.2f,0.1f, 0.05f,0.02f, 0.01f);
        animator2.setDuration(1000);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(popup_window, "translationX", 0, 400);
        animator3.setDuration(1000);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(popup_window, "translationY", 0, 1000);
        animator4.setDuration(1000);
        animatorSet.play(animator).with(animator2).with(animator3).with(animator4);
        animatorSet.setInterpolator( new LinearInterpolator());
        animatorSet.start();
         /*
        tipsAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        tipsAnimation.setInterpolator(new LinearInterpolator());
        tipsAnimation.setDuration(1000);     //动画持续时间
        tipsAnimation.setFillAfter(true);   //动画结束后保持动画
        tipsAnimation.setRepeatCount(-1);//设置动画重复次数
        tipsAnimation.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
          */
        //开始动画
    //    loadingImg.startAnimation(tipsAnimation);

        /*
        ObjectAnimator animator = ObjectAnimator.ofFloat(loadingImg, "rotation", 0f, 360.0f);
        loadingImg.setPivotX(loadingImg.getWidth()/2);
        loadingImg.setPivotY(loadingImg.getHeight()/2);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());//不停顿
        animator.setRepeatCount(-1);//设置动画重复次数
        animator.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
        animator.start();//开始动画
        animator.pause();//暂停动画
        animator.resume();//恢复动画
        */
    }
}
