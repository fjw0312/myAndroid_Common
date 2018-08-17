package com.utils.downFile;

import com.utils.LogcatFileHelper;

import java.io.File;

/**
 * Created by jiongfang on 2018/6/6.
 * 检测 下载文件完整性
 */
public class CheckDownloadFile {
    private static  final String TAG = "Jiong>>CheckDownloadFile";

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
}
