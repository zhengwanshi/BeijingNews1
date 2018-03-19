package com.example.beijingnews1.utils;

import android.graphics.Bitmap;
import android.os.Handler;

/**
 * Created by zhengyg on 2018/3/2.
 */

public class BitmapCacheUtils {


    private  NetCacheUtils netCacheUtils;

    //本地缓存工具类
    private LocalCacheUtils localCacheUtils;

    private MemoryCacheUtils menoryCacheUtils;

    public BitmapCacheUtils(Handler handler) {
        menoryCacheUtils = new MemoryCacheUtils();

        localCacheUtils = new LocalCacheUtils(menoryCacheUtils);
        netCacheUtils = new NetCacheUtils(handler,localCacheUtils,menoryCacheUtils);
    }

    public Bitmap getBitmap(String imageUrl, int position) {
        //从内存中去图片
        if (menoryCacheUtils != null){
            Bitmap bitmap = menoryCacheUtils.getBitmapFromUrl(imageUrl);
            if (bitmap != null){
                LogUtil.e("内存加载图片成功"+position);
                return bitmap;
            }
        }
        //从本地取图片
        if (localCacheUtils != null){
            Bitmap bitmap = localCacheUtils.getBitmapFromUrl(imageUrl);
            if (bitmap != null){
                LogUtil.e("本地加载图片成功"+position);
                return bitmap;
            }
        }

        //请求网络图片
        netCacheUtils.getBitmaoFromNet(imageUrl,position);
        return null;
    }
}
