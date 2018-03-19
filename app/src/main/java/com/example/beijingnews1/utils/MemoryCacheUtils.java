package com.example.beijingnews1.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.example.beijingnews1.base.MenuDetailBasePager;

/**
 * Created by zhengyg on 2018/3/2.
 */
public class MemoryCacheUtils {
    private LruCache<String,Bitmap> lruCache;
    public MemoryCacheUtils(){
        //使用系统分配给应用程序的八分之一内存作为缓存大小
        int maxSize = (int ) (Runtime.getRuntime().maxMemory()/1024/8);
        lruCache = new LruCache<String,Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
//                return super.sizeOf(key, value);
                return (value.getRowBytes()*value.getHeight())/1024;
            }
        };
    }
    public void putBitmap(String imageUrl, Bitmap bitmap) {
            lruCache.put(imageUrl,bitmap);
    }

    public Bitmap getBitmapFromUrl(String imageUrl) {
        return lruCache.get(imageUrl);
    }
}
