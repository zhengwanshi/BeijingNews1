package com.example.beijingnews1.base;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zhengyg on 2018/2/8.
 */

public abstract class MenuDetailBasePager {
   public final Context context;
    /**
     * 代表各个页面的视图
     */
    public View rootView;


    /**
     * 抽象方法，强制孩子实现该方法
     * @param context
     */
    public MenuDetailBasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    /**
     * 子页面需要绑定数据，联网请求数据的时候，重写该方法
     * @return
     */
   public abstract View initView();
    public void initData(){

    }
}
