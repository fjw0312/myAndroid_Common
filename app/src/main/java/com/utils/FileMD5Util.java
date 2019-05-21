package com.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by  on 2018/4/17.
 */

public class FileMD5Util {
    private static FileMD5Util mSelf = null;
    private static MessageDigest md5 = null;
    private static boolean isStop;
    private static final char[] hexChar = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static synchronized FileMD5Util getInstance() {
        if (mSelf == null) {
            mSelf = new FileMD5Util();
        }
        if (md5 == null) {
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
//                KGLog.d("test", e.toString());
            }
        }
        return mSelf;
    }

    public String getFileHash(File file) {
        String hash;
        try {
            FileInputStream in = new FileInputStream(file);
            isStop = false;
            hash = getStreamHash(in);
            in.close();
        } catch (Exception e) {
            return null;
        }
        return hash;
    }

    private String getStreamHash(InputStream in) {
        String hash = null;
        byte[] buffer = new byte[1024];
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(in, 8192);
            int numRead;
            while ((numRead = bufferedInputStream.read(buffer)) > 0) {
                if (isStop) {
                    return "";
                }
                md5.update(buffer, 0, numRead);
            }
            bufferedInputStream.close();
            hash = toHexString(md5.digest());
        } catch (Exception e) {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (Exception ex) {
//                    KGLog.d("test", ex.toString());
                }
            }
//            KGLog.d("test", e.toString());
        }
        return hash;
    }

    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }
}
