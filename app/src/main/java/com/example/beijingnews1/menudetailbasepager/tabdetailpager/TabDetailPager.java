package com.example.beijingnews1.menudetailbasepager.tabdetailpager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beijingnews1.R;
import com.example.beijingnews1.activity.NewsDetailActivity;
import com.example.beijingnews1.base.MenuDetailBasePager;
import com.example.beijingnews1.domain.NewsCenterPagerBean;
import com.example.beijingnews1.domain.NewsCenterPagerBean2;
import com.example.beijingnews1.domain.TabDetailPagerBean;
import com.example.beijingnews1.utils.CacheUtils;
import com.example.beijingnews1.utils.Constants;
import com.example.beijingnews1.utils.LogUtil;
import com.example.beijingnews1.view.HorizontalScrollViewPager;

import com.example.refreshlistview.RefreshListview;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;


/**
 * Created by zhengyg on 2018/2/9.
 */

public class TabDetailPager extends MenuDetailBasePager {
    private final NewsCenterPagerBean.DataBean.ChildrenBean childrenData;
//    private final NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData;

    private String url;
    private HorizontalScrollViewPager viewPager;
    private TextView tv_title;
    private LinearLayout ll_point_group;
    private RefreshListview listView;
    private TabDetailPagerListAdapter adapter;
    //顶部新闻的数据
    private List<TabDetailPagerBean.DataBean.TopnewsBean> topnews;
    private int prePosition;
    private ImageOptions imageOptions;
    private List<TabDetailPagerBean.DataBean.NewsBean>  news;
    //加载更多的地址
    private String  moreUrl;
    private boolean isLoadMore = false;
    public static final String READ_ARRAY_ID= "read_array_id";
    private InternalHandler internalHandler;

    /**
     * 抽象方法，强制孩子实现该方法
     *
     * @param context
     */



    public TabDetailPager(Context context, NewsCenterPagerBean.DataBean.ChildrenBean childrenData) {
        super(context);
        this.childrenData = childrenData;
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(100), DensityUtil.dip2px(100))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.news_pic_default)
                .setFailureDrawableId(R.drawable.news_pic_default)
                .build();

    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tabdetail_pager,null);
        listView = (RefreshListview) view.findViewById(R.id.list_view_1);


        View topNewsView = View.inflate(context,R.layout.topnews,null);
        viewPager= (HorizontalScrollViewPager) topNewsView .findViewById(R.id.viewpager3);
        tv_title= (TextView) topNewsView .findViewById(R.id.tv_title_1);
        ll_point_group = (LinearLayout) topNewsView .findViewById(R.id.ll_point_group_1);

       //把轮播图部分加入ListView中
       // listView.addHeaderView(topNewsView);
        listView.addTopNewsView(topNewsView);

        //设置监听下拉刷新
        listView.setOnRefreshListener(new MyOnRefreshListener());

        //设置ListView的点击事件
        listView.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int realPosition = position - 1;
            TabDetailPagerBean.DataBean.NewsBean newsData = news.get(realPosition);
            LogUtil.e("newsData_id =="+newsData.getId()+",newsData_title =="+newsData.getTitle()+",url=="+newsData.getUrl());
            //1，取出保存的ID集合
            String idArray = CacheUtils.getString(context,READ_ARRAY_ID);
            //2，判断是否存在，如果不存在，才保存，并且更新适配器
            if (!idArray.contains(newsData.getId()+"")){

                CacheUtils.putString(context,READ_ARRAY_ID,idArray+newsData.getId()+",");
                adapter.notifyDataSetChanged();//getCount->get
            }
            //跳转到新闻浏览页面
            Intent intent = new Intent(context,NewsDetailActivity.class);
            intent.putExtra("url",Constants.BASE_URL+newsData.getUrl());
            context.startActivity(intent);

        }
    }
    class MyOnRefreshListener implements RefreshListview.OnRefreshListener{
        @Override
        public void onPullDownRefresh() {
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {
           // Toast.makeText(context, "hehheh", Toast.LENGTH_SHORT).show();

        if (TextUtils.isEmpty(moreUrl)){
            Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
            listView.onRefreshFinish(false);
        }else {
            getMoreDataFromNet();
        }
        }
    }

    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("加载更多成功==="+result);
                listView.onRefreshFinish(false);
                //把这个放在前面
                isLoadMore = true;
                //解析数据
                processData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("加载更多失败==="+ex.getMessage());
                listView.onRefreshFinish(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
               LogUtil.e("加载更多onCancelled=="+cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("加载更多onFinished");
            }
        });
    }

    @Override
    public void initData() {
        super.initData();

        url = Constants.BASE_URL+ childrenData.getUrl();
        LogUtil.e(childrenData.getTitle()+"的联网Url=="+url);

        //把之前缓存的数据取出
        String saveJson = CacheUtils.getString(context,url);
        if (!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }
        getDataFromNet();

    }

    private void getDataFromNet() {
        prePosition = 0;
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //缓存数据
                CacheUtils.putString(context,url,result);
                LogUtil.e(childrenData.getTitle() + "____页面数据请求成功=="+result);
                processData(result);

                //隐藏下拉刷新控件,重新数据，更新时间
                listView.onRefreshFinish(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            LogUtil.e(childrenData.getTitle()+"--页面数据请求失败"+ex.getMessage());
                //隐藏下拉刷新控件,重新数据，更新时间
                listView.onRefreshFinish(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e(childrenData.getTitle() + "-页面数据请求onCancelled==" + cex.getMessage());

            }

            @Override
            public void onFinished() {
                LogUtil.e(childrenData.getTitle() + "-onFinished==");
            }
        });
    }

    private void processData(String json) {
        TabDetailPagerBean bean = parsedJson(json);
        LogUtil.e(childrenData.getTitle()+"解析成功"+bean.getData().getNews().get(0).getTitle());

        moreUrl = "";
        if (TextUtils.isEmpty(bean.getData().getMore())){
            moreUrl = "";
        }else{
            moreUrl = Constants.BASE_URL+bean.getData().getMore();
        }
        LogUtil.e("加载更多的地址=="+moreUrl);
        //默认和加载更多
        if (!isLoadMore){
            //默认
            //顶部新闻的数据
            topnews=bean.getData().getTopnews();
            //设置ViewPager的适配器
            viewPager.setAdapter(new TabDetailPagerTopNewsAdapter());
            //添加红点
            addPoint();


            //监听页面的改变，设置文本和点的变化
            viewPager.addOnPageChangeListener(new MyOnPagerChangeListener());
            tv_title.setText(topnews.get(0).getTitle());
            //准备ListView数据集合
            news = bean.getData().getNews();
            //设置ListView的适配器
            adapter = new TabDetailPagerListAdapter();
            listView.setAdapter(adapter);
        }else{
            //加载更多
            isLoadMore = false;
            //添加到原来的集合中
            news.addAll(bean.getData().getNews());
            //刷新适配器
            adapter.notifyDataSetChanged();
        }
        //发消息每隔4000ms切换一次ViewPager页面
        if (internalHandler==null){
            internalHandler = new InternalHandler();
        }
        //是把消息队列所有的消息和回调移除
        internalHandler.removeCallbacksAndMessages(null);
        internalHandler.postDelayed(new MyRunnable(),4000);


    }
    class InternalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //切换ViewPager的下一个页面
            int item = (viewPager.getCurrentItem()+1)%topnews.size();
            viewPager.setCurrentItem(item);
            internalHandler.removeCallbacksAndMessages(null);
            internalHandler.postDelayed(new MyRunnable(),4000);

        }
    }

    private void addPoint() {
        ll_point_group.removeAllViews();
        for (int i= 0;i<topnews.size();i++){
            ImageView imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.point_selector);

            LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(DensityUtil.dip2px(5),DensityUtil.dip2px(5));

            if (i==0){
                imageView.setEnabled(true);
            }else{
                imageView.setEnabled(false);
                params.leftMargin= DensityUtil.dip2px(8);
            }
            imageView.setLayoutParams(params);
            ll_point_group.addView(imageView);


        }
    }

    class TabDetailPagerListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return news.size();
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
           ViewHolder viewHolder;
            if (convertView==null){
                convertView = View.inflate(context,R.layout.item_tabdetail_pager,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

                convertView.setTag(viewHolder);
            }else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据位置得到数据
            TabDetailPagerBean.DataBean.NewsBean newsData = news.get(position);
            String imageUrl = Constants.BASE_URL+ newsData.getListimage();
            x.image().bind(viewHolder.iv_icon,imageUrl,imageOptions);
            viewHolder.tv_title.setText(newsData.getTitle());
            viewHolder.tv_time.setText(newsData.getPubdate());

            //设置字体
            String idArray = CacheUtils.getString(context,READ_ARRAY_ID);
            if (idArray.contains(newsData.getId()+"")){
                viewHolder.tv_title.setTextColor(Color.GRAY);
            }else {
                viewHolder.tv_title.setTextColor(Color.BLACK);
            }
            return convertView;
        }
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_time;

    }

    class MyOnPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //设置文本，对应红点高亮
            tv_title.setText(topnews.get(position).getTitle());
            ll_point_group.getChildAt(prePosition).setEnabled(false);
            ll_point_group.getChildAt(position).setEnabled(true);
            prePosition = position;
        }

        private boolean isDragging = false;

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING){
                isDragging = true;
                LogUtil.e("拖拽");
                internalHandler.removeCallbacksAndMessages(null);
            }else  if (state == ViewPager.SCROLL_STATE_SETTLING&&isDragging){
                isDragging = false;
                LogUtil.e("惯性");
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(),4000);

            }else  if (state == ViewPager.SCROLL_STATE_IDLE&&isDragging){
                isDragging = false;
                LogUtil.e("静止");
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(),4000);

            }

        }
    }

    class  TabDetailPagerTopNewsAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view ==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            //设置默认图片
            imageView.setBackgroundResource(R.drawable.home_scroll_default);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(imageView);

            TabDetailPagerBean.DataBean.TopnewsBean topnewsData = topnews.get(position);
            String imageUrl = Constants.BASE_URL+topnewsData.getTopimage();
//联网请求图片
            x.image().bind(imageView,imageUrl);
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            LogUtil.e("按下");
                            internalHandler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP:
                            LogUtil.e("松手");
                            internalHandler.removeCallbacksAndMessages(null);
                            internalHandler.postDelayed(new MyRunnable(),4000);
                            break;
                    }
                    return true;
                }
            });



            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View) object);
        }
    }

    private TabDetailPagerBean parsedJson(String json) {

        return new Gson().fromJson(json,TabDetailPagerBean.class);
    }

    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            internalHandler.sendEmptyMessage(0);
        }
    }
}
