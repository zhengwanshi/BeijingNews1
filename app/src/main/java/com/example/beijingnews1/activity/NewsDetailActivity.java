package com.example.beijingnews1.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beijingnews1.R;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewsDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvTitle;
    private ImageButton ibMenu;
    private ImageButton ibBack;
    private ImageButton ibTextsize;
    private ImageButton ibShare;
    private WebView webview;
    private ProgressBar pbLoading;
    private String url;
    private WebSettings websettings;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-02-28 14:50:24 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        tvTitle = (TextView)findViewById( R.id.tv_title );
        ibMenu = (ImageButton)findViewById( R.id.ib_menu );
        ibBack = (ImageButton)findViewById( R.id.ib_back );
        ibTextsize = (ImageButton)findViewById( R.id.ib_textsize );
        ibShare = (ImageButton)findViewById( R.id.ib_share );
        webview = (WebView)findViewById( R.id.webview );
        pbLoading = (ProgressBar)findViewById( R.id.pb_loading );


        tvTitle.setVisibility(View.GONE);
        ibMenu.setVisibility(View.GONE);
        ibBack.setVisibility(View.VISIBLE);
        ibTextsize.setVisibility(View.VISIBLE);
        ibShare.setVisibility(View.VISIBLE);


        ibBack.setOnClickListener( this );
        ibTextsize.setOnClickListener( this );
        ibShare.setOnClickListener( this );
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-02-28 14:50:24 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
      if ( v == ibBack ) {
            // Handle clicks for ibBack
          finish();
        } else if ( v == ibTextsize ) {
            // Handle clicks for ibTextsize
          //Toast.makeText(this, "设置文字大小", Toast.LENGTH_SHORT).show();
          showChangeTextSizeDialog();
        } else if ( v == ibShare ) {
            // Handle clicks for ibShare
//          Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
          showShare();
        }
    }

    private int tempSize = 2;
    private int realSize = tempSize;
    private void showChangeTextSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置文字大小");
        String[] items = {"超大字体","大字体","正常字体","小字体","超小字体"};
        builder.setSingleChoiceItems(items, realSize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tempSize = which;

            }
        });
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realSize= tempSize;
                changeTextSize(realSize);
            }
        });
        builder.show();
    }

    private void changeTextSize(int realSize) {
        switch (realSize){
            case 0:
                //超大字体
                websettings.setTextZoom(200);
                break;
            case 1:
                //大字体
                websettings.setTextZoom(150);
                break;
            case 2:
                //正常字体
                websettings.setTextZoom(100);
                break;
            case 3:
                //小字体
                websettings.setTextZoom(75);
                break;
            case 4:
                //超小字体
                websettings.setTextZoom(50);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏

        setContentView(R.layout.activity_news_detail);
        findViews();
        getData();
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
       // oks.setTitleUrl("http://image.so.com/i?src=360pic_strong&z=1&i=0&cmg=a30421cf224f42b6c767d6e166b1aeeb&q=%E6%90%9E%E7%AC%91%E5%9B%BE%E7%89%87");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("多长的假期都有一个头");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://image.so.com/i?src=360pic_strong&z=1&i=0&cmg=a30421cf224f42b6c767d6e166b1aeeb&q=%E6%90%9E%E7%AC%91%E5%9B%BE%E7%89%87");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("哈哈哈");
        // site是分享此内容的网站名称，仅在QQ空间使用
        //oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
       // oks.setSiteUrl("http://image.so.com/i?src=360pic_strong&z=1&i=0&cmg=a30421cf224f42b6c767d6e166b1aeeb&q=%E6%90%9E%E7%AC%91%E5%9B%BE%E7%89%87");

        // 启动分享GUI
        oks.show(this);
    }

    private void getData() {
       url =  getIntent().getStringExtra("url");
         websettings = webview.getSettings();
        //设置支持JavaScript
        websettings.setJavaScriptEnabled(true);
        //设置双击变大变小
        websettings.setUseWideViewPort(true);
        //增加缩放按键
        websettings.setBuiltInZoomControls(true);
        //不让从当前页面跳转到系统的浏览器
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbLoading.setVisibility(View.GONE);
            }
        });

        webview.loadUrl(url);
    }
}
