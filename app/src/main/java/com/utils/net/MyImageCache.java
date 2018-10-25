package com.utils.net;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;


/***
 *
 * author:fjw0312@163.com
 * date:2017.7.29
 *
 * */
public class MyImageCache implements ImageCache {
    //LruCache
    private LruCache<String, Bitmap> myCache;
    int maxMemory = (int) (Runtime.getRuntime().maxMemory()); //
    int cacheSize = maxMemory / 8;//

    //
    public MyImageCache() {
        // TODO Auto-generated constructor stub
        myCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // TODO Auto-generated method stub   //
                //return bitmap.getByteCount()/1024;
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    //
    public static MyImageCache bitmapCache; //

    public static synchronized MyImageCache instance() {
        if (bitmapCache == null) {
            bitmapCache = new MyImageCache();
        }
        return bitmapCache;
    }

    @Override
    public Bitmap getBitmap(String url) {
        // TODO Auto-generated method stub
        return myCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        // TODO Auto-generated method stub
        myCache.put(url, bitmap);
    }

}
