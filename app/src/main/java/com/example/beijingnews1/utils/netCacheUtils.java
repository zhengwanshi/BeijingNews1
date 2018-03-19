package com.example.beijingnews1.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by zhengyg on 2018/3/2.
 */

public class NetCacheUtils {
    /**
     * 请求图片成功

     */
    public  static final int SUCESS = 1;
    public static  final int FAIL = 2;
    private  final Handler handler;
    private final MemoryCacheUtils memoryCacheUtils;
    //线程池
    private ExecutorService service;
    private LocalCacheUtils localCacheUtils;

    public NetCacheUtils(Handler handler, LocalCacheUtils localCacheUtils, MemoryCacheUtils menoryCacheUtils) {
        this.handler = handler;
        service= Executors.newFixedThreadPool(10);
        this.localCacheUtils = localCacheUtils;
        this.memoryCacheUtils = menoryCacheUtils;
    }


    public  void getBitmaoFromNet(String imageUrl, int position) {

        service.execute(new MyRunnable(imageUrl,position));
    }
    class MyRunnable implements Runnable {

        private final String imageUrl;
        private final int position;

        public MyRunnable(String imageUrl, int position) {
            this.imageUrl = imageUrl;
            this.position = position;
        }

        @Override
        public void run() {
            //在子线程中请求图片

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(4000);
                connection.setReadTimeout(4000);
                connection.connect();
                int code = connection.getResponseCode();
                if (code == 200) {//coed为200则请求成功
                    InputStream is = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    //显示到控件上
                    Message msg = Message.obtain();
                    msg.what = SUCESS;
                    msg.arg1= position;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                    //在内存中缓存一份
                    memoryCacheUtils.putBitmap(imageUrl,bitmap);
                    //在本地SD卡中缓存一份
                    localCacheUtils.putBitmap(imageUrl,bitmap);

                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
                Message msg = Message.obtain();
                msg.what = FAIL;
                msg.arg1= position;

                handler.sendMessage(msg);

            }


        }
    }
}
