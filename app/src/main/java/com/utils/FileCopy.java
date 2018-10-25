package com.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by jiongfang on 2018/6/27.
 * 文件拷贝
 */
public class FileCopy {

    private String mSourceFile = "";
    private String mDescFile = "";
    private int mode = 0;  //copy 模式： 0 未知文件或路径   1：文件拷贝   2：文件夹拷贝

    public FileCopy(String sourceFile, String descFile) {
        mSourceFile = sourceFile;
        mDescFile = descFile;
    }

    /**
     * 文件 拷贝
     *
     * @param oldPath
     * @param newPath
     * @throws IOException
     */
    public void copyFile(String oldPath, String newPath) throws IOException {
        //先清除 已存在的文件
        File file = new File(newPath);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        //获取源文件大小
        File oldFile = new File(oldPath);
        long size = oldFile.length();
        long hasSize = 0;
        //初始化创建 目标文件
        //   File file = new File(newPath);
        FileHelper newFileHAL = new FileHelper(newPath);
        FileInputStream in = new FileInputStream(oldFile);
        // FileOutputStream out = new FileOutputStream(file);
        long time = System.currentTimeMillis();
        long newTime = 0;
        long i = 0;
        byte[] buffer = new byte[10 * 1024];  //10k
        int len = 0;
        while ((len = in.read(buffer)) != -1) {
            newFileHAL.write_byte(buffer, true, 0, len);
            //out.write(buffer);
            hasCpySize = hasCpySize + len;
            hasSize = hasSize + len;
            progressPer = (int) ((hasSize * 100) / size);
            if (mCopyFileListener != null) {
                if (i % 100 == 0) {
                    mCopyFileListener.OnProgress(newPath, hasCpySize, progressPer);  //拷贝 过程
                    Log.i("Jiong>>CopyFile", "---------进度通知！");
                }
                i++;
            }
            Log.i("Jiong>>CopyFile", "hasSize=" + hasSize + "    progressPer=" + progressPer + "             i=" + String.valueOf(i));
        }
        if (mCopyFileListener != null) {
            mCopyFileListener.OnProgress(newPath, hasCpySize, progressPer);  //拷贝 过程
        }
        Log.i("Jiong>>CopyFile", "hasCpySize=" + hasCpySize);
        fileNum++;
        if (in != null) {
            in.close();
        }
    }

    public void copyDir(String oldPath, String newPath) throws IOException {
        File file = new File(oldPath);
        String[] filePath = file.list();

        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdir();
        }
        String sourcePath = oldPath;
        String path = newPath;

        for (int i = 0; i < filePath.length; i++) {
            if ((new File(sourcePath + file.separator + filePath[i])).isDirectory()) {
                copyDir(sourcePath + file.separator + filePath[i], path + file.separator + filePath[i]);
            }

            if (new File(sourcePath + file.separator + filePath[i]).isFile()) {
                copyFile(sourcePath + file.separator + filePath[i], path + file.separator + filePath[i]);
            }

        }
    }

    public void startCopy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";

                // 目标 文件目录判断
                File descFile = new File(mDescFile);
                if (descFile.isDirectory()) {
                    //判断 目标目录是否最后 带/
                    int a = 0;
                    int mDescFileLastIndex = mDescFile.length();
                    char c = mDescFile.charAt(mDescFileLastIndex - 1);
                    if (c == '/') {
                        a = 1;
                    }
                    int lastIndex = mSourceFile.lastIndexOf("/");
                    String fileName = mSourceFile.substring(lastIndex + a);
                    mDescFile = mDescFile + fileName;
                }
               /*   文件 路径不存在会创建
                else if(!descFile.exists()){
                    result = "目标 文件不存在";
                    if( mCopyFileListener != null ){
                        mCopyFileListener.OnError(result);
                    }
                    return;
                }
                if(!descFile.canWrite()){
                    result = "目标文件不可写";
                    if( mCopyFileListener != null ){
                        mCopyFileListener.OnError(result);
                    }
                    return;
                }
                */
                Log.i("Jiong>>CopyFile", "mDescFile=" + mDescFile);
                // 文件 或 文件夹判断
                File file = new File(mSourceFile);
                if (!file.exists()) {
                    mode = 0;
                    result = "源文件不存在";
                    if (mCopyFileListener != null) {
                        mCopyFileListener.OnError(result);
                    }
                    return;
                }
                if (!file.canRead()) {
                    mode = 0;
                    result = "源文件不可读";
                    if (mCopyFileListener != null) {
                        mCopyFileListener.OnError(result);
                    }
                    return;
                }
                if (file.isDirectory()) {
                    mode = 2;
                    try {
                        TotalSize = file.length();
                        Log.i("Jiong>>CopyFile", "TotalSize=" + TotalSize);
                        if (mCopyFileListener != null) {
                            mCopyFileListener.OnStart(TotalSize);  //开始拷贝
                        }
                        copyDir(mSourceFile, mDescFile);
                        result = "文件夹拷贝";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (mCopyFileListener != null) {
                        mCopyFileListener.OnCopyComplete(result, fileNum, mDescFile);
                    }
                } else if (file.isFile()) {
                    mode = 1;
                    try {
                        //                       TotalSize = file.length();   //此值不对 待优化
                        Log.i("Jiong>>CopyFile", "TotalSize=" + TotalSize);
                        if (mCopyFileListener != null) {
                            mCopyFileListener.OnStart(TotalSize);  //开始拷贝
                        }
                        copyFile(mSourceFile, mDescFile);
                        result = "文件拷贝";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (mCopyFileListener != null) {
                        mCopyFileListener.OnCopyComplete(result, fileNum, mDescFile);
                    }
                } else {
                    mode = 0;
                    result = "路径非 文件或目录类型";
                    if (mCopyFileListener != null) {
                        mCopyFileListener.OnError(result);
                    }
                    return;
                }
            }
        }).start();
    }

    private int fileNum = 0;  //拷贝的文件个数
    private long TotalSize = 0;  //拷贝的 所有文件总大小
    private long hasCpySize = 0;  //已拷贝的 总大小
    private int progressPer = 0;  //拷贝的当前文件总大小

    public CopyFileListener mCopyFileListener;

    public interface CopyFileListener {
        void OnError(String result);  //错误 调用方法

        void OnStart(long TotalSize);  //开始拷贝

        void OnCopyComplete(String result, int fileNum, String descPath); //拷贝结束调用方法   fileNum:文件拷贝个数

        void OnProgress(String file, long hasCpySize, int progressPer);  //拷贝 过程
    }

    public void setCopyFileListener(CopyFileListener copyFileListener) {
        mCopyFileListener = copyFileListener;
    }


    /**
     *  使用方式：
     * 1. FileCopy fileCopy = new FileCopy(FilePath, usb_storage);
     2. fileCopy.startCopy();
     3. fileCopy.setCopyFileListener(new FileCopy.CopyFileListener() {});
     *
     *
     */
}
