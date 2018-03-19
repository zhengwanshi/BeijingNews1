package com.example.beijingnews1.base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.beijingnews1.R;
import com.example.beijingnews1.activity.MainActivity;

/**
 * Created by zhengyg on 2018/2/7.
 */

public class BasePager {
   public final Context context;



   public final View rootView;
    //显示标题
    public TextView tv_title;
    public ImageButton ib_menu;
    public FrameLayout fl_content;
    public ImageButton ib_swich_list_grid;

    public BasePager(Context context){
        this.context = context;
        rootView = initView();

    }

    private View initView() {
        View  view=View.inflate(context, R.layout.base_pager,null);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        ib_menu = (ImageButton) view.findViewById(R.id.ib_menu);
        ib_swich_list_grid= (ImageButton) view.findViewById(R.id.ib_swich_list_grid);
        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();
            }
        });
        fl_content = (FrameLayout) view.findViewById(R.id.fl_content);
        return view;
    }

    /**
     * 孩子初始化数据的时候调用，重写
     */
    public void initData(){

    }
}
