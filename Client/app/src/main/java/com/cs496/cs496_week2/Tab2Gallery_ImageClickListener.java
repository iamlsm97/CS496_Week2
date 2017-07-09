package com.cs496.cs496_week2;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.io.File;

/**
 * Created by rongrong on 2017-07-10.
 */

public class Tab2Gallery_ImageClickListener implements View.OnClickListener {

    Context context;

    File imageID;

    public Tab2Gallery_ImageClickListener(Context context, File imageID) {
        this.context = context;
        this.imageID = imageID;
    }

    public void onClick(View v) {

        Intent intent = new Intent(context, Tab2Gallery_ImageActivity.class);
        intent.putExtra("image ID", imageID);
        context.startActivity(intent);
    }
}