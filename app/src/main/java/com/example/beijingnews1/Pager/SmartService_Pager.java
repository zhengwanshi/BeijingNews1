package com.example.beijingnews1.Pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.example.beijingnews1.base.BasePager;

/**
 * Created by zhengyg on 2018/2/7.
 */

public class SmartService_Pager extends BasePager {
    public SmartService_Pager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        tv_title.setText("智慧服务");
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        fl_content.addView(textView);
        textView.setText("智慧服务内容");
    }
}
