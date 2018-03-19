package com.example.beijingnews1.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.beijingnews1.Pager.Govaffair_Pager;
import com.example.beijingnews1.Pager.Home_Pager;
import com.example.beijingnews1.Pager.NewsCenter_Pager;
import com.example.beijingnews1.Pager.Setting_Pager;
import com.example.beijingnews1.Pager.SmartService_Pager;
import com.example.beijingnews1.R;
import com.example.beijingnews1.activity.MainActivity;
import com.example.beijingnews1.adapter.ContentFramentAdapter;
import com.example.beijingnews1.base.BasePager;
import com.example.beijingnews1.utils.LogUtil;
import com.example.beijingnews1.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by zhengyg on 2018/2/3.
 */
public class ContentFragment extends com.example.beijingnews1.base.BaseFragment {
    public static final String HEHEHE = "hehehe";
    @ViewInject(R.id.viewpager1)
    private NoScrollViewPager viewPager;

    @ViewInject(R.id.rg_main)
    private RadioGroup rg_main;
    private ArrayList<BasePager> basePagers;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.content_fragment,null);
       x.view().inject(ContentFragment.this,view);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        LogUtil.e(HEHEHE);
        //初始化五个页面，并且放入集合中
        basePagers = new ArrayList<>();
        basePagers.add(new Home_Pager(context));
        basePagers.add(new NewsCenter_Pager(context));
        basePagers.add(new SmartService_Pager(context));
        basePagers.add(new Govaffair_Pager(context));
        basePagers.add(new Setting_Pager(context));

        viewPager.setAdapter(new ContentFramentAdapter(basePagers));



        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        //默认选中首页
        rg_main.check(R.id.rb_home);
        basePagers.get(0).initData();
        //设置默认不可以滑动
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

    }

    /**
     * 得到新闻中心
     * @return
     */
    public NewsCenter_Pager getNewsCenterPager() {
        return (NewsCenter_Pager) basePagers.get(1);
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * 当某个页面被选中时回调
         * @param position
         */
        @Override
        public void onPageSelected(int position) {
            BasePager basePager = basePagers.get(position);
            basePager.initData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{

        /**
         * 点击切换页面
         * @param group
         * @param checkedId
         */
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            MainActivity mainActivity = (MainActivity) context;
            switch (checkedId){
                case R.id.rb_home:
                    viewPager.setCurrentItem(0,false);

                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                    break;
                case R.id.rb_newscenter:
                    viewPager.setCurrentItem(1,false);//false是没有动画的，要动画只要不加false就可以l
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    break;
                case R.id.rb_smartservice:
                    viewPager.setCurrentItem(2,false);
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                    break;
                case R.id.rb_govaffair:
                    viewPager.setCurrentItem(3,false);
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                    break;
                case R.id.rb_setting:
                    viewPager.setCurrentItem(4,false);
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                    break;



            }
        }
    }
//    class ContentFramentAdapter extends PagerAdapter{
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            BasePager basePager =basePagers.get(position);
//            View rootView = basePager.rootView;
//           // basePager.initData();
//            container.addView(rootView);
//            return rootView;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//
//        @Override
//        public int getCount() {
//            return basePagers.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//
//            return view == object;
//        }
//
//    }
}
