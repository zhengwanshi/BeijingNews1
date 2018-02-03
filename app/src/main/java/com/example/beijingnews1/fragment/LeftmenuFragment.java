package com.example.beijingnews1.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.beijingnews1.utils.LogUtil;

/**
 * Created by zhengyg on 2018/2/3.
 */
public class LeftmenuFragment extends com.example.beijingnews1.fragment.BaseFragment {


    private TextView mTextView;
    @Override
    public View initView() {
        mTextView = new TextView(context);
        mTextView.setTextSize(23);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(Color.RED);

        return mTextView;
    }

    @Override
    protected void initData() {
        super.initData();
        LogUtil.e("hehehe-------------------------");
        mTextView.setText("左侧菜单的页面.hehhe");

    }
}
