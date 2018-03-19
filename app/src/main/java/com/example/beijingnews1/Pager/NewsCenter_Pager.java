package com.example.beijingnews1.Pager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.beijingnews1.activity.MainActivity;
import com.example.beijingnews1.base.BasePager;
import com.example.beijingnews1.base.MenuDetailBasePager;
//import com.example.beijingnews1.domain.NewsCenterPagerBean;
import com.example.beijingnews1.domain.NewsCenterPagerBean;
import com.example.beijingnews1.domain.NewsCenterPagerBean2;
import com.example.beijingnews1.fragment.LeftmenuFragment;
import com.example.beijingnews1.menudetailbasepager.InteracMenuDetailPager;
import com.example.beijingnews1.menudetailbasepager.NewsMenuDetailPager;
import com.example.beijingnews1.menudetailbasepager.PhotosMenuDetailPager;
import com.example.beijingnews1.menudetailbasepager.TopicMenuDetailPager;
import com.example.beijingnews1.utils.CacheUtils;
import com.example.beijingnews1.utils.Constants;
import com.example.beijingnews1.utils.LogUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import volley.VolleyManager;


/**
 * Created by zhengyg on 2018/2/7.
 */

public class NewsCenter_Pager extends BasePager {
    //左侧菜单对应的数据
    //private List<NewsCenterPagerBean2.DetailPagerData> data;
    //详情页面的集合
   private ArrayList<MenuDetailBasePager>detaiBasePagers;
    private List<NewsCenterPagerBean.DataBean> data;

    public NewsCenter_Pager(Context context) {
        super(context);
    }

    /**
     * 根据位置切换不同详情页面
     */
    public void swichPager(int position){
        if (position<detaiBasePagers.size()){
            //设置标题
            tv_title.setText(data.get(position).getTitle());
            //移除之前内容
            fl_content.removeAllViews();
            //添加新内容
            MenuDetailBasePager detailBasePager = detaiBasePagers.get(position);
            View rootView= detailBasePager.rootView;
            detailBasePager.initData();//初始化数据

            fl_content.addView(rootView);

            if (position == 2){
                //图组详情页面
                ib_swich_list_grid.setVisibility(View.VISIBLE);
                //设置点击事件
                ib_swich_list_grid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //得到图组详情页面对象
                        PhotosMenuDetailPager detailPager = (PhotosMenuDetailPager) detaiBasePagers.get(2);
                        //调用图组对象切换ListView和GridView的方法
                        detailPager.swichListAndGrid(ib_swich_list_grid);
                    }
                });
            }else  if (position == 3){
                //互动详情页面
                ib_swich_list_grid.setVisibility(View.VISIBLE);
                //设置点击事件
                ib_swich_list_grid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //得到图组详情页面对象
                        InteracMenuDetailPager detailPager = (InteracMenuDetailPager) detaiBasePagers.get(3);
                        //调用图组对象切换ListView和GridView的方法
                        detailPager.swichListAndGrid(ib_swich_list_grid);
                    }
                });
            }else {
                ib_swich_list_grid.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void initData() {
        super.initData();
        ib_menu.setVisibility(View.VISIBLE);
        tv_title.setText("新闻中心");
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        fl_content.addView(textView);
        textView.setText("新闻中心内容");
        //得到缓存数据
        String saveJson = CacheUtils.getString(context,Constants.NEWSCENTER_PAGER_URL);

        if (!TextUtils.isEmpty(saveJson)){
           processData(saveJson);
        }
        //网络请求数据
     // getDataFromNet();
        //使用Volley联网请求数据
        getDataFromNetByVolley();
    }

    private void getDataFromNetByVolley() {
        //请求队列
        RequestQueue queue = Volley.newRequestQueue(context);
        //String请求
         StringRequest request = new StringRequest(Request.Method.GET, Constants.NEWSCENTER_PAGER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                LogUtil.e("使用Volley请求数据成功===="+ result);
                CacheUtils.putString(context,Constants.NEWSCENTER_PAGER_URL,result);
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
        queue.add(request);
       // VolleyManager.getRequestQueue().add(request);
    }

    private void getDataFromNet() {
        RequestParams params= new RequestParams(Constants.NEWSCENTER_PAGER_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("使用xUtils3联网请求成功"+result);
                processData(result);
                //缓存数据
                CacheUtils.putString(context,Constants.NEWSCENTER_PAGER_URL,result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                LogUtil.e("使用xUtils3联网请失败"+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

                LogUtil.e("使用xUtils3_onCancelled"+cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("使用xUtils3_onFinished");
            }
        });
    }

    /**
     * 解析数据
     * @param json
     */
    private void processData(String json) {
        NewsCenterPagerBean bean = parsedjson(json);
         String title= bean.getData().get(0).getChildren().get(1).getTitle();
        LogUtil.e("gson解析json"+title);


//        NewsCenterPagerBean2 bean = parsedjson2(json);
//      String title2= bean.getData().get(0).getChildren().get(1).getTitle();
//      LogUtil.e("gson解析json----NewsCenterPagerBean2 =="+title2);
        //左侧菜单数据
        data = bean.getData();
        MainActivity mainActivity = (MainActivity) context;
        LeftmenuFragment leftmenuFragment=mainActivity.getLeftmenuFragment();

        //添加详情页面
        detaiBasePagers =new ArrayList<>();
        detaiBasePagers.add(new NewsMenuDetailPager(context,data.get(0)));
        detaiBasePagers.add(new TopicMenuDetailPager(context,data.get(0)));
        detaiBasePagers.add(new PhotosMenuDetailPager(context,data.get(2)));
        detaiBasePagers.add(new InteracMenuDetailPager(context,data.get(2)));
        //把数据传到左侧菜单
        leftmenuFragment.setData(data);

    }

    /**
     * 使用Android系统自带的API解析json数据
     *
     * @param json
     * @return
     */
    private NewsCenterPagerBean2 parsedjson2(String json) {

        NewsCenterPagerBean2 bean2 = new NewsCenterPagerBean2();
        try {
            JSONObject object = new JSONObject(json);

            int retcode = object.optInt("retcode");
            bean2.setRetcode(retcode);

            JSONArray data = object.optJSONArray("data");
            if (data != null && data.length()>0){
                List<NewsCenterPagerBean2.DetailPagerData> detailPagerDatas = new ArrayList<>();
                //设置列表数据
                bean2.setData(detailPagerDatas);
                //for循环，解析每条数据
                for (int i = 0;i<data.length();i++){
                    JSONObject jsonObject = (JSONObject) data.get(i);

                    NewsCenterPagerBean2.DetailPagerData  detailPagerData= new NewsCenterPagerBean2.DetailPagerData();
                    //添加到集合数据
                    detailPagerDatas.add(detailPagerData);

                    int id = jsonObject.optInt("id");
                    detailPagerData.setId(id);
                    int type = jsonObject.optInt("type");
                    detailPagerData.setType(type);
                    String title = jsonObject.optString("title");
                    detailPagerData.setTitle(title);
                    String url = jsonObject.optString("url");
                    detailPagerData.setUrl(url);
                    String url1 = jsonObject.optString("url1");
                    detailPagerData.setUrl1(url1);
                    String dayurl =jsonObject.optString("dayurl");
                    detailPagerData.setDayurl(dayurl);
                    String excurl = jsonObject.optString("excurl");
                    detailPagerData.setExcurl(excurl);
                    String weekurl = jsonObject.optString("weekurl");
                    detailPagerData.setWeekurl(weekurl);

                    JSONArray children = jsonObject.optJSONArray("children");
                    if (children != null&&children.length() >0){

                        List<NewsCenterPagerBean2.DetailPagerData.ChildrenData>childrenDatas = new ArrayList<>();

                        //设置集合——ChildrenData
                        detailPagerData.setChildren(childrenDatas);

                        for (int j = 0;j< children.length();j++){
                            JSONObject childrenitem = (JSONObject) children.get(j);

                            NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData = new NewsCenterPagerBean2.DetailPagerData.ChildrenData();
                            //添加到集合中
                            childrenDatas.add(childrenData);

                            int childId = childrenitem.optInt("id");
                            childrenData.setId(childId);
                            String childTitle = childrenitem.optString("title");
                            childrenData.setTitle(childTitle);
                            String childUrl = childrenitem.optString("url");
                            childrenData.setUrl(childUrl);
                            int childType = childrenitem.optInt("type");
                            childrenData.setType(childType);

                        }
                    }


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bean2;
    }
    /**
     * 解析json数据：1,使用系统的API解析json；2,使用第三方框架解析json数据，例如Gson,fastjson
     *
     * @param json
     * @return
     */
    private NewsCenterPagerBean parsedjson(String json) {
        Gson gson = new Gson();
        NewsCenterPagerBean bean = gson.fromJson(json,NewsCenterPagerBean.class);

        return bean;
    }
}
