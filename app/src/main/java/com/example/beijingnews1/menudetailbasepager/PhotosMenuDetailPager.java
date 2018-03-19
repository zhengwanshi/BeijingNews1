package com.example.beijingnews1.menudetailbasepager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.beijingnews1.R;
//import com.example.beijingnews1.activity.ShowImageActivity;
import com.example.beijingnews1.base.BasePager;
import com.example.beijingnews1.base.MenuDetailBasePager;

import com.example.beijingnews1.domain.NewsCenterPagerBean;
import com.example.beijingnews1.domain.NewsCenterPagerBean2;
import com.example.beijingnews1.domain.PhotosMenuDetailPagerBean;
import com.example.beijingnews1.utils.CacheUtils;
import com.example.beijingnews1.utils.Constants;
import com.example.beijingnews1.utils.LogUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.Call;
import volley.VolleyManager;

/**
 * Created by zhengyg on 2018/2/8.
 * 图片详情页面
 */

public class PhotosMenuDetailPager extends MenuDetailBasePager {
    private final NewsCenterPagerBean.DataBean detailPagerData;
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
    private PhotosMenuDetailPagerAdapter adapter;
    //private ProgressDialog mProgressBar;

    public PhotosMenuDetailPager(Context context, NewsCenterPagerBean.DataBean detailPagerData) {
        super(context);
        this.detailPagerData = detailPagerData;
    }

    @Override
    public View initView() {
      View view = View.inflate(context, R.layout.photos_menudetail_pager,null);
        x.view().inject(this,view);
        //设置点击某条的Item的监听
        listview.setOnItemClickListener(new MyOnItemClickListener());
        gridview.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }
    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            PhotosMenuDetailPagerBean.DataBean.NewsBean newsBean= news.get(position);
            String imageUrl = Constants.BASE_URL+newsBean.getLargeimage();
            Intent intent = new Intent(context,ShowImageActivity.class);
            intent.putExtra("url",imageUrl);
            context.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL+detailPagerData.getUrl();
        String saveJson = CacheUtils.getString(context,url);
        if (!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }
        //getDataFromNetByVolley();
        getDataFromNetByOkHttpUtils();
    }

    private void getDataFromNetByOkHttpUtils() {

//        String url = "http://www.zhiyun-tech.com/App/Rider-M/changelog-zh.txt";
//        url="http://www.391k.com/api/xapi.ashx/info.json?key=bd_hyrzjjfb4modhj&size=10&page=1";
        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new MyStringCallback());
    }


    public class MyStringCallback extends StringCallback
    {
        @Override
        public void onBefore(okhttp3.Request request, int id)
        {

        }

        @Override
        public void onAfter(int id)
        {

        }

        @Override
        public void onError(Call call, Exception e, int id)
        {
            e.printStackTrace();
            LogUtil.e( "使用OkHttpUtils请求失败===="+e.getMessage());

        }

        @Override
        public void onResponse(String response, int id)
        {
            LogUtil.e( "使用OkHttpUtils请求成功：onResponse：complete"+response);
            processData(response);
            switch (id)
            {
                case 100:
                    Toast.makeText(context, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(context, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void inProgress(float progress, long total, int id)
        {
            LogUtil.e("inProgress:" + progress);
           // mProgressBar.setProgress((int) (100 * progress));
        }
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
        adapter = new PhotosMenuDetailPagerAdapter();
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
            adapter = new PhotosMenuDetailPagerAdapter();
            gridview.setAdapter(adapter);
            listview.setVisibility(View.GONE);
            //按钮显示——ListView
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_list_type);

        }else {
            isShowListView = true;
            //显示GridView,隐藏ListView
            listview.setVisibility(View.VISIBLE);
            adapter = new PhotosMenuDetailPagerAdapter();
            listview.setAdapter(adapter);
           gridview.setVisibility(View.GONE);
            //按钮显示——GridView
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_grid_type);

        }
    }

    class  PhotosMenuDetailPagerAdapter extends BaseAdapter{

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
            if (convertView == null){
                convertView = View.inflate(context,R.layout.item_photos_menudetail_pager,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.tv_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //根据位置得到对应的数据
            PhotosMenuDetailPagerBean.DataBean.NewsBean newsEntity = news.get(position);
            viewHolder.tv_title.setText(newsEntity.getTitle());
            String imageUrl = Constants.BASE_URL + newsEntity.getSmallimage();
            LogUtil.e("图片URl========="+imageUrl);
            //使用Volley请求图片
           loaderImager(viewHolder,imageUrl);
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
    private void loaderImager(final ViewHolder viewHolder, String imageurl) {

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
