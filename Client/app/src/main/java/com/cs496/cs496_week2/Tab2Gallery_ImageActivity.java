package com.cs496.cs496_week2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by rongrong on 2017-07-10.
 */

public class Tab2Gallery_ImageActivity extends Activity {
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab2_gallery_image_slide);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        PhotoViewAttacher mAttacher;
        setImage(imageView);
        mAttacher = new PhotoViewAttacher(imageView);
        mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mAttacher.update();

    }

    private void setImage(ImageView imageView) {
        Intent receivedIntent = getIntent();
        File imageID = (File)receivedIntent.getExtras().get("image ID");
        Glide.with(this).load(imageID).into(imageView);
    }
}
