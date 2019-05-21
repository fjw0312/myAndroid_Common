package com.utils.net.load;

import android.content.Context;
import android.util.Log;

import com.MyApplication;
import com.utils.FileMD5Util;
import com.utils.LogcatFileHelper;

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
 */
public class DownloadUtil {
    public DownloadUtil(String fileName) {
        UPDATAE_FILE_NAME = fileName;
        UPDATE_ZIP_FILE = UPDATE_ZIP_DIR + UPDATAE_FILE_NAME;
    }

    public final static String UPDATE_ZIP_DIR = MyApplication.SAVE_FILE_PATH+"DirUpdate/";
    public  static String UPDATAE_FILE_NAME = "xxx.dat";
    public  static String UPDATE_ZIP_FILE = UPDATE_ZIP_DIR + UPDATAE_FILE_NAME;

    public final static String USER_AGENT = "K9_update"; //网络连接参数
    public final static int  TIME_OUT = 5*1000;   //网络超时

    private static final String SaveMD5 = "k9_Save_downloadInfo"; //用于保存下载的md5
    private static final String SaveMD5Key = "k9_Save_downloadInfo_key_md5_info";
    private static final String PREFRENCE_FILE = "k9_downloadInfo"; //用于保存在下载过程中的md5 用于 断点续传
    private static final String KEY_MD5_INFO = "k9_downloadInfo_key_md5_info";

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
            conn.setRequestProperty("User-Agent",  USER_AGENT);
            conn.setConnectTimeout(TIME_OUT); // 设置连接超时
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

    // 删除下载文件
    public static void deleteDownloadFile() {
        File dir = new File(UPDATE_ZIP_DIR);
        if (!dir.exists()) {
            return;
        }

        if (dir.listFiles() != null && dir.listFiles().length > 0) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
        }
    }
    //计算 下载文件大小
    private static long calculateDownloadFileSize(){
        File file = new File(UPDATE_ZIP_FILE);
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


    //保存 md5
    public static String getSaveDownloadMD5(){
        String md5 = "";
        md5 = MyApplication.getContext().getSharedPreferences(SaveMD5, Context.MODE_PRIVATE).getString(SaveMD5Key, "");
        LogcatFileHelper.i("Jiong>>","@@@@@@@@@@@@@@@@@ 获取 MD5="+md5);
        return md5;
    }
    public static void setSaveDownloadMD5(String md5){
        if(md5==null) return;
        MyApplication.getContext().getSharedPreferences(SaveMD5, Context.MODE_PRIVATE).edit()
                .putString(SaveMD5Key, md5).commit();
        LogcatFileHelper.i("Jiong>>","@@@@@@@@@@@@@@@@@ 设置MD5="+md5);
    }
    //下载包状态信息 -- 做断点续传
    private static class StateInfoPersist {
        public static void setDownloadMD5Info(String md5) {
            if (md5 == null) {
                return;
            }
            MyApplication.getContext().getSharedPreferences(PREFRENCE_FILE, Context.MODE_PRIVATE).edit()
                    .putString(KEY_MD5_INFO, md5).commit();
        }

        public static String getDownloadMD5Info() {
            return MyApplication.getContext().getSharedPreferences(PREFRENCE_FILE, Context.MODE_PRIVATE).getString(KEY_MD5_INFO, "");
        }
    }

    /**
     * 检查文件完整性，并通知UI线程（如果有更新则保存配置）
     *
     * @param path
     * @param md5
     */
    public static boolean checkAndSaveUpdate(String path, String md5) {
        File file = new File(path);
        LogcatFileHelper.i(TAG, "checkAndSaveUpdate into ");
        if (file.exists() && file.isFile()) {
            String tmd5 = FileMD5Util.getInstance().getFileHash(file);
            LogcatFileHelper.i(TAG, "checkAndSaveUpdate into ");
            LogcatFileHelper.i(TAG, "文件md5值：" + md5 + "    计算的md5值:" + tmd5);
            boolean isOk = md5.equals(tmd5);
            if (isOk) {
                return true;
            } else {
                //如果校验不合法，则删掉已下载的文件
                LogcatFileHelper.i(TAG, "文件校验不通过，删除已下载文件...");
                file.delete();
            }
        }
        return false;
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
        setSaveDownloadMD5(md5);
        new Thread() {
            @Override
            public void run() {
                File dir = new File(UPDATE_ZIP_DIR);
                if(dir == null || !dir.exists()){
                    dir.mkdirs();
                }
                boolean result = httpDownload(url, UPDATE_ZIP_FILE);

                //检验 Md5 文件
                if(result){
                    String saveMd5 =  getSaveDownloadMD5();
                    result = checkAndSaveUpdate(UPDATE_ZIP_FILE,saveMd5);
                }

                if (listener != null) {
                    listener.onDonwloadComplete(result, UPDATE_ZIP_FILE);
                }
                isDownloading = false;
            }
        }.start();

        return 0;
    }


    public interface DownloadChangedListener {
        void onDonwloadComplete(boolean result, String path);
        void onProgress(int progress);
    }

    public static void setListener(DownloadChangedListener listener) {
        DownloadUtil.listener = listener;
    }

    /**
     * 使用demo
     * 1.实例化
     * DownloadUtils download = new DowloadUtils("xx.apk");
     * 2.设置下载结束监听
     * download.setListener();
     * 3.开始下载
     * download.startDownload();
     * */
}
