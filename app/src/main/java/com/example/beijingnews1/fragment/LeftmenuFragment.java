package com.example.beijingnews1.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.beijingnews1.Pager.NewsCenter_Pager;
import com.example.beijingnews1.R;
import com.example.beijingnews1.activity.MainActivity;
//import com.example.beijingnews1.domain.NewsCenterPagerBean;
import com.example.beijingnews1.domain.NewsCenterPagerBean;
import com.example.beijingnews1.domain.NewsCenterPagerBean2;
import com.example.beijingnews1.utils.DensityUtil;
import com.example.beijingnews1.utils.LogUtil;

import java.util.List;

/**
 * Created by zhengyg on 2018/2/3.
 */
public class LeftmenuFragment extends com.example.beijingnews1.base.BaseFragment {



//    private List<NewsCenterPagerBean2.DetailPagerData> data;
    private ListView listView;
    private LeftmenuFragmentAdaper adapter;
    private int prePosition;
    private List<NewsCenterPagerBean.DataBean> data;

    @Override
    public View initView() {
        listView = new ListView(context);
        listView.setPadding(0, DensityUtil.dip2px(context,40),0,0);
        listView.setDividerHeight(0);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setSelector(android.R.color.transparent);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            prePosition = position;
                adapter.notifyDataSetChanged();
                //2.把左侧菜单关闭
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();
                //3.切换对应的详情页面
               swichPager(prePosition);
            }
        });


        return listView;
    }

    private void swichPager(int prePosition) {
       MainActivity mainActivity = (MainActivity) context;
        ContentFragment contentFragment = mainActivity.getContentFragment();
        NewsCenter_Pager newsCenterPager = contentFragment.getNewsCenterPager();
        newsCenterPager.swichPager(prePosition);
    }

    @Override
    protected void initData() {
        super.initData();
       // LogUtil.e("hehehe-------------------------");


    }

    /**
     * 接收数据
     * @param data
     */
    public void setData(List<NewsCenterPagerBean.DataBean> data) {
        this.data = data;
        for (int i = 0;i<data.size();i++){
            LogUtil.e("title=="+data.get(i).getTitle());
        }
        //设置适配器
        adapter = new LeftmenuFragmentAdaper();
        listView.setAdapter(adapter);

        //设置默认页面
        swichPager(prePosition);
    }
    class LeftmenuFragmentAdaper extends BaseAdapter{

        @Override
        public int getCount() {
            LogUtil.e("调用了getCount方法=="+data.size());
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(context, R.layout.item_leftmenu,null);
            textView.setText(data.get(position).getTitle());
            textView.setEnabled(position ==prePosition);
            LogUtil.e("调用了getView方法==");
            return textView;
        }
    }
}
