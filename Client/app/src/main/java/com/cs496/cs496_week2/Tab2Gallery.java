package com.cs496.cs496_week2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;

/**
 * Created by rongrong on 2017-07-06.
 */

public class Tab2Gallery extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_gallery, container, false);

        /*
        if (FacebookUserInfo.isLoggedIn()) {
            try {
                String accessUrl = "https://graph.facebook.com/me?access_token=";
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(accessUrl + AccessToken.getCurrentAccessToken().getToken())
                        .build();
                Response response = client.newCall(request).execute();
                Log.d("RESPONSE", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */

        return view;
    }
}
