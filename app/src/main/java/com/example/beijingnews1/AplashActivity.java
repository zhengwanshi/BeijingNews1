package com.example.beijingnews1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.example.beijingnews1.activity.GuideActivity;
import com.example.beijingnews1.activity.MainActivity;
import com.example.beijingnews1.utils.CacheUtils;

public class AplashActivity extends Activity {

    private RelativeLayout rl_splash_root;
    public static final String START_MAIN="start_main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aplash);
        rl_splash_root = (RelativeLayout) findViewById(R.id.rl_splash_root);

        AlphaAnimation aa = new AlphaAnimation(0,1);
        aa.setFillAfter(true);

        ScaleAnimation sa = new ScaleAnimation(0,1,0,1,ScaleAnimation.RELATIVE_TO_SELF,ScaleAnimation.RELATIVE_TO_SELF);
        sa.setFillAfter(true);

        RotateAnimation ra = new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        ra.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.addAnimation(ra);
        set.addAnimation(aa);
        set.addAnimation(sa);
        set.setDuration(2000);

        rl_splash_root.startAnimation(set);

        set.setAnimationListener(new MyAnimationListener());
    }

    private class MyAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }



        @Override
        public void onAnimationEnd(Animation animation) {

                    boolean isStartMain = CacheUtils.getBoolean(AplashActivity.this,START_MAIN);
            Intent intent;
            if (isStartMain){
                intent = new Intent(AplashActivity.this,MainActivity.class);
            }else{
                intent = new Intent(AplashActivity.this,GuideActivity.class);
            }
            startActivity(intent);
            finish();
        }

        /**
         * 当动画重复播放的时候回调这个方法
         * @param animation
         */
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
