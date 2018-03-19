package com.example.beijingnews1.activity;


import android.support.v4.app.FragmentManager;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.example.beijingnews1.fragment.ContentFragment;
import com.example.beijingnews1.R;
import com.example.beijingnews1.fragment.LeftmenuFragment;
import com.example.beijingnews1.utils.DensityUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

    public int screeWidth;
    public int screeHeight;
    public static final String MAIN_CONTENT_TAG="main_content_tag";
    public static final String LEFTMENU_TAG = "leftmenu_tag";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        initSlidingMenu();
        initFragment();

    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
       FragmentTransaction ft= fm.beginTransaction();

        //FragmentManager fm = getFragmentManager();
      //  FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.fl_main_content,new ContentFragment(),MAIN_CONTENT_TAG);
        ft.replace(R.id.fl_leftmenu,new LeftmenuFragment(),LEFTMENU_TAG);
        ft.commit();
    }

    private void initSlidingMenu() {
        setContentView(R.layout.activity_main2);
        setBehindContentView(R.layout.activity_leftmenu);
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffset(DensityUtil.dip2px(MainActivity.this,250));
    }

    public LeftmenuFragment getLeftmenuFragment() {
        return  (LeftmenuFragment) getSupportFragmentManager().findFragmentByTag(LEFTMENU_TAG);
    }

    /**
     * 得到正文Fragment
     * @return
     */
    public ContentFragment getContentFragment() {
        return (ContentFragment) getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT_TAG);
    }
}
