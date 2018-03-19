package com.example.beijingnews1.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.beijingnews1.base.BasePager;

import java.util.ArrayList;

/**
 * Created by zhengyg on 2018/2/7.
 */

public class ContentFramentAdapter extends PagerAdapter {
    private final ArrayList<BasePager> basePagers;

    public ContentFramentAdapter(ArrayList<BasePager> basePagers){
        this.basePagers = basePagers;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BasePager basePager =basePagers.get(position);
        View rootView = basePager.rootView;
        // basePager.initData();
        container.addView(rootView);
        return rootView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return basePagers.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == object;
    }

}
