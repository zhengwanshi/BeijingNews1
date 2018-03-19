package com.example.ok_http_simple;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;



import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final int POST = 2;
    private static final int GET =1 ;
    private Button btn_get;
    private Button btn_post;
    private TextView tv_result;
    private OkHttpClient  client = new OkHttpClient();

    private Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET:
                    tv_result.setText("GET+++++====="+msg.obj.toString());
                    break;
                case POST:
                    tv_result.setText("POST+++++====="+msg.obj.toString());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_get = (Button) findViewById(R.id.btn_get);
        btn_post = (Button) findViewById(R.id.btn_post);
        tv_result = (TextView) findViewById(R.id.tv_result);

        //设置点击事件
        btn_get.setOnClickListener(this);
        btn_post.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get:
                getDataFromByGet();

                break;
            case R.id.btn_post:
                getDataFromByPost();
                break;
        }
    }

    private void getDataFromByPost() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                String result;
                try {
                    result = post("http://api.m.mtime.cn/PageSubArea/TrailerList.api","");
                    Message msg =Message.obtain();
                    msg.what =POST;
                    msg.obj=result;
                    hander.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    private void getDataFromByGet() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                String result;
                try {
                    result = Get("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
                    Message msg =Message.obtain();
                    msg.what =GET;
                    msg.obj=result;
                    hander.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }
    String Get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
