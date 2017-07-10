package com.cs496.cs496_week2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by rongrong on 2017-07-06.
 */

public class Tab4 extends Fragment {
    View view;
    String name;
    String email;
    String img;
    Bitmap bitmap;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab4, container, false);

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.d("LOGLOG", object.toString());
                    img = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    Log.d("img url", img);
                    name = object.getString("name");
                    email = object.getString("email");

                    TextView textView = (TextView) view.findViewById(R.id.asdf);
                    TextView textView2 = (TextView) view.findViewById(R.id.asdf2);

                    ImageView imageView = (ImageView) view.findViewById(R.id.asdf3);
                    if (FacebookUserInfo.isLoggedIn()) {
                        Thread thread = new Thread() {
                            public void run() {
                                try {
                                    bitmap = BitmapFactory.decodeStream(new URL(img).openStream());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                        textView.setText(name);
                        textView2.setText(email);
                        thread.join();
                        imageView.setBackground(new ShapeDrawable(new OvalShape()));
                        imageView.setClipToOutline(true);
                        imageView.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.type(large)");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

        return view;
    }
}