package com.cs496.cs496_week2;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import org.json.JSONObject;

import java.net.URL;

/**
 * Created by q on 2017-07-07.
 */

public class FacebookUserInfo {
    private static String name = new String();
    private static String email = new String();
    private static String id = new String();
    private static String img;

    public static void init() {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.d("LOGLOG", object.toString());
                    img = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    name = object.getString("name");
                    email = object.getString("email");
                    id = object.getString("id");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    public static String getName() {
        return name;
    }

    public static void setName(String s) {
        name = s;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String s) {
        email = s;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String s) {
        id = s;
    }

    public static String getImg() {
        return img;
    }

    public static boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
}
