package com.example.beijingnews1.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by zhengyg on 2018/3/2.
 */
public class LocalCacheUtils {


    private final MemoryCacheUtils memoryCacheUtils;

    public LocalCacheUtils(MemoryCacheUtils menoryCacheUtils) {
        this.memoryCacheUtils = menoryCacheUtils;
    }

    public Bitmap getBitmapFromUrl(String imageUrl) {
        //判断sdcard是否挂载
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
        {
            try {
                String fileName = MD5Encoder.encode(imageUrl);
                File file = new File( Environment.getExternalStorageDirectory() +"/beijingnews",fileName);


                if (file.exists()){
                    FileInputStream is = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    //把图片缓存到内存中
                    if (bitmap!= null){
                        memoryCacheUtils.putBitmap(imageUrl,bitmap);
                    }
                    return bitmap;
                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("图片本地获取失败");
            }

        }
        return null;
    }

    public void putBitmap(String imageUrl, Bitmap bitmap) {
        //判断sdcard是否挂载
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //保存图片在/mnt/sdcard/beijingnews/http://192.168.21.165:8080/xsxxxx.png
            //保存图片在/mnt/sdcard/beijingnews/llkskljskljklsjklsllsl
            try {
                String fileName = MD5Encoder.encode(imageUrl);//llkskljskljklsjklsllsl

                ///mnt/sdcard/beijingnews/llkskljskljklsjklsllsl
//                File file = new File(Environment.getExternalStorageDirectory()+"/beijingnews",fileName);
                File file = new File(Environment.getExternalStorageDirectory()+"/beijingnews",fileName);
                LogUtil.e("图片本地缓。。。。");
                File parentFile =  file.getParentFile();//mnt/sdcard/beijingnews
                if(!parentFile.exists()){
                    //创建目录
                    parentFile.mkdirs();
                }


                if(!file.exists()){
                    LogUtil.e("111111111");
                    file.createNewFile();
                    LogUtil.e("444444444");
                }
                //保存图片
           bitmap.compress(Bitmap.CompressFormat.PNG,100,new FileOutputStream(file));
                LogUtil.e("图片本地缓存成功");

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("图片本地缓存失败");
            }
        }

    }
}
