package com.example.beijingnews1.menudetailbasepager;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.beijingnews1.R;
import com.example.beijingnews1.activity.MainActivity;
import com.example.beijingnews1.base.MenuDetailBasePager;
import com.example.beijingnews1.domain.NewsCenterPagerBean;
import com.example.beijingnews1.domain.NewsCenterPagerBean2;
import com.example.beijingnews1.menudetailbasepager.tabdetailpager.TabDetailPager;
import com.example.beijingnews1.utils.LogUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengyg on 2018/2/8.
 * 新闻详情页面
 */

public class NewsMenuDetailPager extends MenuDetailBasePager {

    private final List<NewsCenterPagerBean.DataBean.ChildrenBean> children;
    @ViewInject(R.id.tabPagerIndicator)
    private TabPageIndicator tabPagerIndicator;
    @ViewInject(R.id.viewpager2)
    private ViewPager viewPager;
    @ViewInject(R.id.ib_tab_next)
    private ImageButton ib_tab_next;



    //页签页面数据集合
   // private List<NewsCenterPagerBean2.DetailPagerData.ChildrenData>children;

    private ArrayList<TabDetailPager> tabDetailPagers;


    public NewsMenuDetailPager(Context context, NewsCenterPagerBean.DataBean detailPagerData) {
        super(context);
         children = detailPagerData.getChildren();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.newsmenu_detail_pager,null);
        x.view().inject(NewsMenuDetailPager.this,view);
        ib_tab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("新闻详情页面数据被初始化了");
        //准备新闻详情页面
        tabDetailPagers = new ArrayList<>();
        for (int i = 0;i<children.size();i++){
            tabDetailPagers.add(new TabDetailPager(context,children.get(i)));
        }
      //设置VeiwPager的适配器
            viewPager.setAdapter(new MyNewsMenuDetailPagerAdapter());
        //ViewPager与TabPagerIndicator关联
        tabPagerIndicator.setViewPager(viewPager);

        //注意以后监听页面的变化，TabPagerIndicator监听页面的变化
        tabPagerIndicator.setOnPageChangeListener(new MyOnPagerChangeListener());

    }

    class MyOnPagerChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            if (position ==0){
                //SlidingMenu可以滑动
                isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);

            }else{
                //SlidingMenu不可以滑动
                isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    class MyNewsMenuDetailPagerAdapter extends PagerAdapter{
        @Override
        public CharSequence getPageTitle(int position) {
            return children.get(position).getTitle();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
           TabDetailPager tabDetailPager = tabDetailPagers.get(position);
            View rootView = tabDetailPager.rootView;
            tabDetailPager.initData();
            container.addView(rootView);
            return rootView;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
           container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return tabDetailPagers.size() ;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view ==object;
        }
    }
    /**
     根据传人的参数设置是否让SlidingMenu可以滑动
     */
    private void isEnableSlidingMenu(int touchmodeFullscreen) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }
}
