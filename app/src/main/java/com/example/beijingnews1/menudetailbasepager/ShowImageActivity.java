package com.example.beijingnews1.menudetailbasepager;

import android.app.Activity;
import android.os.Bundle;

import com.example.beijingnews1.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by zhengyg on 2018/3/4.
 */
public class ShowImageActivity extends Activity{
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showimage);
        url = getIntent().getStringExtra("url");
        final PhotoView photoView = (PhotoView) findViewById(R.id.iv_photo);

        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

        Picasso.with(this)
//                .load("http://pbs.twimg.com/media/Bist9mvIYAAeAyQ.jpg")
                .load(url)
                .into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        attacher.update();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }
}
