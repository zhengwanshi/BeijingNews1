package com.example.beijingnews1.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.beijingnews1.R;
import com.example.beijingnews1.utils.LogUtil;

/**
 * Created by zhengyg on 2018/2/3.
 */
public class ContentFragment extends com.example.beijingnews1.fragment.BaseFragment {
    public static final String HEHEHE = "hehehe";
    private ViewPager viewPager;
    private RadioGroup rg_main;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.content_fragment,null);
        viewPager= (ViewPager) view.findViewById(R.id.viewpager);
        rg_main = (RadioGroup) view.findViewById(R.id.rg_main);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        LogUtil.e(HEHEHE);
        rg_main.check(R.id.rb_home);


    }
}
