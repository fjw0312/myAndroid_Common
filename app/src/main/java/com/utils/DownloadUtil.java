package com.utils;

import android.content.Context;
import android.util.Log;

import com.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by martinpeng on 2016/10/17.
 * 下载完 后 记得还要校验 一下md5.
 */
public class DownloadUtil {

    public final static  String UPDATE_ZIP_DIR_USBHID = MyApplication.SAVE_FILE_PATH+"USBVupdate/";
    public final static  String UPDATE_ZIP_FILE_USBHID = UPDATE_ZIP_DIR_USBHID+"usbHidUpdate.bin";

    public static boolean isDownloading = false;
    private static long oldDownloadPos = 0 ;
    private static String TAG = "DownloadUtil";
    public static int progress = -1;
    private static DownloadChangedListener listener ;



    /**
     * 通过url ，http方式下载文件
     *
     * @param httpUrl
     * @param saveFile 文件的临时存储路径
     * @return true:下载成功,false 失败
     */
    private static boolean httpDownload(String httpUrl, String saveFile) {
        // 下载网络文件
        int byteread = 0;
        LogcatFileHelper.i("Jiong"+TAG,"into httpDownload");
        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return false;
        }

        InputStream inStream = null;
        RandomAccessFile fs = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent","K9_UsbHid");
            conn.setConnectTimeout(5 * 1000); // 设置连接超时
            conn.setRequestProperty("Connection", "Keep-Alive");
            if(oldDownloadPos!= 0) {
                conn.setRequestProperty("Range", "bytes=" + oldDownloadPos + "-");
            }
            conn .setRequestProperty("Accept-Encoding", "identity");

            conn.connect();
            String length = conn.getHeaderField("content-length");
            double total = Long.valueOf(length)+oldDownloadPos;
            Log.d(TAG,"length:"+total);

            inStream = conn.getInputStream();
            File f = new File(saveFile);
            if(f == null && !f.exists()){
                f.createNewFile();
            }
            fs = new RandomAccessFile(saveFile,"rw");
            if(oldDownloadPos>0) {
                fs.seek(oldDownloadPos);
            }
            byte[] buffer = new byte[1024];
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
                int temp = (int)(100*fs.length()/total);
                if(temp != progress) {
                    progress = temp;
                    if(listener != null){
                        listener.onProgress(progress);
                    }
                }
                try {
                    Thread.sleep(1);
                }catch (InterruptedException e){

                }

            }
            StateInfoPersist.setDownloadMD5Info("");
            progress = -1 ;
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fs != null) {
                    fs.close();
                }
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            isDownloading = false;
        }

    }


    public static void deleteDownloadFile() {
        File dir = new File(UPDATE_ZIP_DIR_USBHID);

        if (!dir.exists()) {
            return;
        }

        if (dir.listFiles() != null && dir.listFiles().length > 0) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
        }
    }

    private static long calculateDownloadFileSize(){
        File file = new File(UPDATE_ZIP_FILE_USBHID);
        if(file != null && file.exists()){
            return file.length();
        }

        return 0 ;
    }

    /**
     * 下载固件的时间点，限定在0点-8点之间,如果有下载记录则续传
     */
    public static boolean canAutoDownload(){

        /*Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        if((hour>=0 && hour<=8) || !TextUtils.isEmpty(StateInfoPersist.getDownloadMD5Info())){
            return true;
        }
        return false ;*/

        //新版本默认自动下载
        return true;

    }


    /**
     * 开始下载
     *
     * @param url download url
     * @return download id
     */
    public synchronized static long startDownload(final String md5, final String url) {

        if (isDownloading) {
            return -1;
        }
        isDownloading = true;
        LogcatFileHelper.i("Jiong"+TAG,"into startDownload");
        String downloadMD5 = StateInfoPersist.getDownloadMD5Info();
        oldDownloadPos  = 0 ;
        if (downloadMD5 != null) {
            if (downloadMD5.equals(md5)){
                //这次下载的文件跟上次下载的一致，做断点续传
                oldDownloadPos = calculateDownloadFileSize();
            }else{
                deleteDownloadFile();
            }
        } else {
            deleteDownloadFile();
        }

        StateInfoPersist.setDownloadMD5Info(md5);
        new Thread() {
            @Override
            public void run() {
                File dir = new File(UPDATE_ZIP_DIR_USBHID);
                if(dir == null || !dir.exists()){
                    dir.mkdirs();
                }
                boolean result = httpDownload(url, UPDATE_ZIP_FILE_USBHID);
                if (listener != null) {
                    listener.onDonwloadComplete(result, UPDATE_ZIP_FILE_USBHID);
                }
                isDownloading = false;
            }
        }.start();

        return 0;
    }

    private static class StateInfoPersist {

        private static final String KEY_MD5_INFO = "key_download_usbHid_md5_info";

        public static void setDownloadMD5Info(String md5) {
            if (md5 == null) {
                return;
            }
            MyApplication.getContext().getSharedPreferences("k9_usbHid_downloadInfo", Context.MODE_PRIVATE).edit()
                    .putString(KEY_MD5_INFO, md5).commit();
        }

        public static String getDownloadMD5Info() {
            return MyApplication.getContext().getSharedPreferences("k9_usbHid_downloadInfo", Context.MODE_PRIVATE).getString(KEY_MD5_INFO, "");
        }
    }


    public interface DownloadChangedListener {
        void onDonwloadComplete(boolean result, String path);
        void onProgress(int progress);
    }

    public static void setListener(DownloadChangedListener listener) {
        DownloadUtil.listener = listener;
    }


    /**
     * 使用方式：
     * DownloadUtil.startDownload(updateBean.md5,updateBean.rew_url);
     * DownloadUtil.setListener(downloadChangedListener);
     *
     */
}
