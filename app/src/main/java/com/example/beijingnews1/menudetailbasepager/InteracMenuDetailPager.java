package com.example.beijingnews1.menudetailbasepager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.beijingnews1.R;
import com.example.beijingnews1.base.MenuDetailBasePager;
import com.example.beijingnews1.domain.NewsCenterPagerBean;
import com.example.beijingnews1.domain.PhotosMenuDetailPagerBean;
import com.example.beijingnews1.utils.BitmapCacheUtils;
import com.example.beijingnews1.utils.CacheUtils;
import com.example.beijingnews1.utils.Constants;
import com.example.beijingnews1.utils.LogUtil;
import com.example.beijingnews1.utils.NetCacheUtils;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.List;

import java.util.logging.LogRecord;

import volley.VolleyManager;

/**
 * Created by zhengyg on 2018/2/8.
 * 互动详情页面
 */

public class InteracMenuDetailPager extends MenuDetailBasePager {
    private final NewsCenterPagerBean.DataBean detailPagerData;
    private Handler handler =new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NetCacheUtils.SUCESS:
                    int position = msg.arg1;
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (listview.isShown()){
                        ImageView iv_icon = (ImageView) listview.findViewWithTag(position);
                        if (iv_icon != null && bitmap != null){
                            iv_icon.setImageBitmap(bitmap);
                        }
                    }
                    if (gridview.isShown()){
                        ImageView iv_icon = (ImageView) gridview.findViewWithTag(position);
                        if (iv_icon != null && bitmap != null){
                            iv_icon.setImageBitmap(bitmap);
                        }
                    }
                    LogUtil.e("请求图片成功++==="+ position);
                    break;
                case NetCacheUtils.FAIL:
                    position = msg.arg1;
                    LogUtil.e("请求图片失败==="+position);
                    break;
        }
        }
    } ;
    /**
     * 抽象方法，强制孩子实现该方法
     *
     * @param context
     */

    @ViewInject(R.id.listview)
    private ListView listview;

    @ViewInject(R.id.gridview)
    private GridView gridview;
    //    private NewsCenterPagerBean2.DetailPagerData detailPagerData;
    private String url;
    private List<PhotosMenuDetailPagerBean.DataBean.NewsBean> news;
    private InteracMenuDetailPager.PhotosMenuDetailPagerAdapter adapter;

    public InteracMenuDetailPager(Context context, NewsCenterPagerBean.DataBean detailPagerData) {
        super(context);
        this.detailPagerData = detailPagerData;
        bitmapCacheUtils = new BitmapCacheUtils(handler);
    }

    /**
     * 三级缓存工具类
     * @return
     */
    private BitmapCacheUtils bitmapCacheUtils;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.photos_menudetail_pager,null);
        x.view().inject(this,view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL+detailPagerData.getUrl();
        String saveJson = CacheUtils.getString(context,url);
        if (!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }
        getDataFromNetByVolley();
    }

    private void getDataFromNetByVolley() {
        //请求队列
        // RequestQueue queue = Volley.newRequestQueue(context);
        //String请求
        StringRequest request = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                LogUtil.e("使用Volley请求数据成功===="+ result);
                CacheUtils.putString(context,url,result);
                processData(result);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("使用Volley请求数据失败===" + volleyError.getMessage());
            }
        })
        {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String parsed = new String(response.data,"UTF-8");
                    LogUtil.e("解析啦啦啦啦啦啦啦啦啦");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };
        //添加到队列
        //queue.add(request);
        VolleyManager.getRequestQueue().add(request);
    }

    private void processData(String json) {
        PhotosMenuDetailPagerBean bean = parsedJson(json);
        LogUtil.e("图组解析成功-===="+ bean.getData().getNews().get(0).getTitle());
        isShowListView = true;
        //设置适配器
        news = bean.getData().getNews();
        adapter = new InteracMenuDetailPager.PhotosMenuDetailPagerAdapter();
        listview.setAdapter(adapter);


    }

    /**
     * true ,显示ListView,隐藏GridView
     * false 显示GridView,隐藏
     * @param ib_swich_list_grid
     */
    private boolean isShowListView = true;
    public void swichListAndGrid(ImageButton ib_swich_list_grid) {
        if (isShowListView){
            isShowListView = false;
            //显示GridView,隐藏ListView
            gridview.setVisibility(View.VISIBLE);
            adapter = new InteracMenuDetailPager.PhotosMenuDetailPagerAdapter();
            gridview.setAdapter(adapter);
            listview.setVisibility(View.GONE);
            //按钮显示——ListView
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_list_type);

        }else {
            isShowListView = true;
            //显示GridView,隐藏ListView
            listview.setVisibility(View.VISIBLE);
            adapter = new InteracMenuDetailPager.PhotosMenuDetailPagerAdapter();
            listview.setAdapter(adapter);
            gridview.setVisibility(View.GONE);
            //按钮显示——GridView
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_grid_type);

        }
    }

    class  PhotosMenuDetailPagerAdapter extends BaseAdapter {
        private DisplayImageOptions options;
        public PhotosMenuDetailPagerAdapter(){

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.home_scroll_default)
                    .showImageForEmptyUri(R.drawable.home_scroll_default)
                    .showImageOnFail(R.drawable.home_scroll_default)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new RoundedBitmapDisplayer(10))//设置矩形圆角
                    .build();
        }

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
           InteracMenuDetailPager.ViewHolder viewHolder;
            if (convertView == null){
                convertView = View.inflate(context,R.layout.item_photos_menudetail_pager,null);
                viewHolder = new InteracMenuDetailPager.ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.tv_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (InteracMenuDetailPager.ViewHolder) convertView.getTag();
            }
            //根据位置得到对应的数据
            PhotosMenuDetailPagerBean.DataBean.NewsBean newsEntity = news.get(position);
            viewHolder.tv_title.setText(newsEntity.getTitle());
            String imageUrl = Constants.BASE_URL + newsEntity.getSmallimage();
            LogUtil.e("图片URl========="+imageUrl);
            //使用Volley请求图片
           // loaderImager(viewHolder,imageUrl);
            //使用三级缓存请求图片
//            viewHolder.iv_icon.setTag(position);
//            Bitmap bitmap = bitmapCacheUtils.getBitmap(imageUrl,position);
//            if (bitmap !=null){
//                viewHolder.iv_icon.setImageBitmap(bitmap);//从本地或者内存中读取到的图片
//
//            }

            //2.使用Picasso请求网络图片
//            Picasso.with(context)
//                    .load(imageUrl)
//                    .placeholder(R.drawable.home_scroll_default)
//                    .error(R.drawable.home_scroll_default)
//                    .into(viewHolder.iv_icon);
            //2.使用Picasso请求网络图片
            //3.使用Glide请求图片

//            Glide.with(context)
//                    .load(imageUrl)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.home_scroll_default)//真正加载的默认图片
//                    .error(R.drawable.home_scroll_default)//失败的默认图片
//                    .into(viewHolder.iv_icon);

            //使用Image_Loader
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imageUrl, viewHolder.iv_icon, options);

            return convertView;
        }
    }


    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_title;
    }

    /**
     *
     * @param viewHolder
     * @param imageurl
     */
    private void loaderImager(final PhotosMenuDetailPager.ViewHolder viewHolder, String imageurl) {

        //设置tag
        viewHolder.iv_icon.setTag(imageurl);
        //直接在这里请求会乱位置
        //   RequestQueue queue = Volley.newRequestQueue(context);
        ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                LogUtil.e("获取图片成功///////////////////////");
                if (imageContainer != null) {
                    LogUtil.e("获取图片成功11111///////////////////////");
                    if (viewHolder.iv_icon != null) {
                        if (imageContainer.getBitmap() != null) {
                            //设置图片
                            viewHolder.iv_icon.setImageBitmap(imageContainer.getBitmap());

                        } else {
                            //设置默认图片
                            viewHolder.iv_icon.setImageResource(R.drawable.home_scroll_default);
                            LogUtil.e("获取图片成功2222222///////////////////////");
                        }
                    }else {
                        LogUtil.e("viewHolder.iv_icon ======= null///////////////////////");
                    }
                }
            }
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //如果出错，则说明都不显示（简单处理），最好准备一张出错图片
                viewHolder.iv_icon.setImageResource(R.drawable.home_scroll_default);
                LogUtil.e("获取图片失败///////////////////////");
            }
        };
        VolleyManager.getImageLoader().get(imageurl, listener);
        LogUtil.e("获取图片///////////////////////");
    }

    private PhotosMenuDetailPagerBean parsedJson(String json) {
        return new Gson().fromJson(json,PhotosMenuDetailPagerBean.class);
    }
}
