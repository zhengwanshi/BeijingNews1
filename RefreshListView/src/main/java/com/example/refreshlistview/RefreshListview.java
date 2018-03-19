package com.example.refreshlistview;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;




import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhengyg on 2018/2/10.
 */

public class RefreshListview extends ListView {
    private LinearLayout headerView;

    //下拉刷新控件
    private View ll_pull_down_refresh;
    private ImageView iv_arrow;
    private ProgressBar pb_status;
    private TextView tv_status;
    private TextView tv_time;
    private int ll_PullDownRefresh;
    //三种状态
    private static  final  int   PULL_DOWN_REFRESH =  0;
    private static  final  int RELEASE_REGRESH =  1;
    private static  final  int REFRESHING=  2;
    private int currentStatus =PULL_DOWN_REFRESH;

    private Animation upAnimation;
    private Animation downAnimation;
    private View footerView;
    private int footerViewHeight;
    private boolean isLoadMore=false;
    /**
     * 顶部轮播图部分
     */
    private View topNewsView;
    private int listViewOnScreenY = -1;


    public RefreshListview(Context context) {
        this(context,null);
    }

    public RefreshListview(Context context, AttributeSet attrs) {
       this(context,attrs,0);
    }

    public RefreshListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderVeiw(context);
        initAnimation();
        initFooterView(context);
    }

    private void initFooterView(Context context) {
         footerView = View.inflate(context,R.layout.refresh_footer,null);
        footerView.measure(0,0);
       footerViewHeight= footerView.getMeasuredHeight();
        footerView.setPadding(0,-footerViewHeight,0,0);
        addFooterView(footerView);
        //监听ListView的滚动
        setOnScrollListener(new MyOnScrollListener());
    }

    public void addTopNewsView(View topNewsView) {
        if (topNewsView != null){
            this.topNewsView =topNewsView;
            headerView.addView(topNewsView);
        }
    }

    class MyOnScrollListener implements OnScrollListener{

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
            //当静止或者滚动的时候
        if (scrollState ==OnScrollListener.SCROLL_STATE_IDLE ||scrollState==OnScrollListener.SCROLL_STATE_FLING){
            //并且是最后一条可见
            if(getLastVisiblePosition()>=getCount()-1){
                //显示加载更多视图
                footerView.setPadding(8,8,8,8);
                //状态改变
                isLoadMore = true;
                //回调接口
                if (mOnRefreshListener != null){
                    mOnRefreshListener.onLoadMore();
                }

            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
    private void initAnimation() {
        upAnimation = new RotateAnimation(0,-180,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180,-360,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);
    }

    private void initHeaderVeiw(Context context) {
        headerView = (LinearLayout) View.inflate(context, R.layout.refresh_header,null);
       //下拉控件
        ll_pull_down_refresh= headerView.findViewById(R.id.ll_pull_down_refresh);
        iv_arrow = (ImageView) headerView.findViewById(R.id.iv_arrow);
        pb_status= (ProgressBar) headerView.findViewById(R.id.pb_status);
        tv_status = (TextView) headerView.findViewById(R.id.tv_status);
        tv_time= (TextView) headerView.findViewById(R.id.tv_time);
        //ListView 添加头
        addHeaderView(headerView);

        //测量高
        ll_pull_down_refresh.measure(0,0);
        ll_PullDownRefresh =ll_pull_down_refresh.getMeasuredHeight();

        //View.setPadding(0,-控件高,0,0);完全隐藏
        //View.setPadding(0,0,0,0);完全显示
        ll_pull_down_refresh.setPadding(0,-ll_PullDownRefresh,0,0);



    }
    private float startY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                //记录起始点位置
                startY = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                if (startY ==-1){startY = ev.getY();}
                //判断顶部轮播图是否完全显示，只有完全显示才会有下拉刷新
                boolean isDisplayTopNews = isDisplayTopNews();
                if (!isDisplayTopNews){

                    //加载更多
                 //   LogUtil.e("下载更多截取下拉刷新///////");
                    break;
                }

                //如果正在刷新则不再刷新
                if (currentStatus ==REFRESHING){
                    break;
                }

                float endY = ev.getY();
                //计算距离
                float distanceY = endY - startY;
                    if (distanceY > 0){
                        int paddingTop = (int) (-ll_PullDownRefresh+distanceY);
                       if (paddingTop <0 && currentStatus !=PULL_DOWN_REFRESH){
                           currentStatus = PULL_DOWN_REFRESH;
                           //更新状态
                           refreshViewStatus();
                       }else if (paddingTop > 0 && currentStatus !=RELEASE_REGRESH){
                           currentStatus = RELEASE_REGRESH;
                           //更新状态
                           refreshViewStatus();
                       }
                        ll_pull_down_refresh.setPadding(0,paddingTop,0,0);
                    }
                break;
            case MotionEvent.ACTION_UP:
                startY = -1;
                if (currentStatus == PULL_DOWN_REFRESH){
                    //完全隐蔽
                    ll_pull_down_refresh.setPadding(0,-ll_PullDownRefresh,0,0);

                }else if (currentStatus == RELEASE_REGRESH){
                    currentStatus = REFRESHING;
                    refreshViewStatus();
                    //完全显示
                    ll_pull_down_refresh.setPadding(0,0,0,0);
                    if (mOnRefreshListener !=null ){
                        mOnRefreshListener.onPullDownRefresh();
                    }


                }
                break;


        }
        return super.onTouchEvent(ev);
    }

    private boolean isDisplayTopNews() {
        if(topNewsView != null){
           //1.得到ListView在屏幕上的坐标
            int[] location = new int[2];
            if (listViewOnScreenY == -1){
                getLocationOnScreen(location);
                listViewOnScreenY = location[1];
            }
            //得到顶部轮播图在屏幕上的坐标
            topNewsView.getLocationOnScreen(location);
            int topNewsViewOnScreenY = location[1];
            return listViewOnScreenY <= topNewsViewOnScreenY;
        }else{
            return true;
        }
    }

    private void refreshViewStatus() {
        switch ( currentStatus){
            case PULL_DOWN_REFRESH:
                iv_arrow.startAnimation(downAnimation);
                tv_status.setText("下拉刷新...");
                pb_status.setVisibility(GONE);
                break;
            case RELEASE_REGRESH:
                iv_arrow.startAnimation(upAnimation);
                tv_status.setText("松手刷新...");
                pb_status.setVisibility(GONE);
                break;
            case REFRESHING:
                tv_status.setText("正在刷新...");
                pb_status.setVisibility(VISIBLE);
                iv_arrow.clearAnimation();
                iv_arrow.setVisibility(GONE);
                break;
        }
    }

    /**
     * 刷新成功更新时间
     * @param success
     */
    public void onRefreshFinish(boolean success) {
        if (isLoadMore){
            //加载更多
            isLoadMore = false;
            //隐藏加载更多布局
            footerView.setPadding(0,-footerViewHeight,0,0);
                    }else {
            tv_status.setText("下拉刷新...");
            currentStatus = PULL_DOWN_REFRESH;
            iv_arrow.clearAnimation();
            pb_status.setVisibility(GONE);
            iv_arrow.setVisibility(VISIBLE);

            ll_pull_down_refresh.setPadding(0,-ll_PullDownRefresh,0,0);
            if (success){
                //更新时间
                tv_time.setText("上次更新时间："+ getSystemTime());
            }
        }
        

    }

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * 监听控件的刷新
     */
    public interface  OnRefreshListener{
        //当下拉刷新时候回调这个方法
        public void onPullDownRefresh();
        public void onLoadMore();
    }
    private OnRefreshListener mOnRefreshListener;
    //设置监听刷新,由外界调用
    public void setOnRefreshListener(OnRefreshListener l){
        this.mOnRefreshListener = l;
    }

}
