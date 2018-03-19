package com.example.beijingnews1.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zhengyg on 2018/2/10.
 */

public class HorizontalScrollViewPager extends ViewPager {
    public HorizontalScrollViewPager(Context context) {
        super(context);
    }

    public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * 起始坐标
     */
    private float startX;
    private float startY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                //请求父层视图不拦截，当前控件的事件
                getParent().requestDisallowInterceptTouchEvent(true);//由当前控件拦截
                //记录起始坐标
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //来到新的坐标
                float endX = ev.getX();
                float endY = ev.getY();
                //计算偏移量
                float distanceX = endX - startX;
                float distanceY = endY - startY;
                //判断滑动方向
                if (Math.abs(distanceX)>Math.abs(distanceY)){
                    //水平滑动
                    //当滑动到ViewPager的第0个页面，并且是从左往右滑动
                    if (getCurrentItem()==0 && distanceX>0){
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else if ((getCurrentItem() == (getAdapter().getCount()-1))&&distanceX<0){
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else{
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }else{
                    //竖直滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
