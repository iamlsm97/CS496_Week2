package com.cs496.cs496_week2;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by q on 2017-07-07.
 */

public class FacebookUserInfo {
    private static String name = new String();
    private static String email = new String();
    private static String id = new String();
    private static String img;
    private static ArrayList<fbContact> contactArrayList = new ArrayList<>();
    public static class fbContact {
        String img_src = "Default";
        String name = "Default";
    }

    public static ArrayList<fbContact> getContactArrayList() {
        return contactArrayList;
    }

    public static String getName() {
        return name;
    }

    public static String getEmail() {
        return email;
    }

    public static String getId() {
        return id;
    }

    public static String getImg() {
        return img;
    }

    public static boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public static void Login() {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.d("Profile information", object.toString());
                    img = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    name = object.getString("name");
                    email = object.getString("email");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.type(large)");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

        contactArrayList = getContactList();
    }

    public static void Logout() {
        contactArrayList = new ArrayList<>();
    }

    public static ArrayList<fbContact> getContactList() {
        final ArrayList<fbContact> contact_list = new ArrayList<>();

        final GraphRequest graphRequest2 = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "/me/taggable_friends", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                JSONObject json = response.getJSONObject();
                Log.d("request success", ".");
                try {
                    if (json != null) {
                        JSONArray jsonArray = json.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            fbContact contact_ele = new fbContact();
                            contact_ele.name = jsonArray.getJSONObject(i).getString("name");
                            contact_ele.img_src = jsonArray.getJSONObject(i).getJSONObject("picture").getJSONObject("data").getString("url");
                            contact_list.add(contact_ele);
                            Log.d("IMG_SOURCE", contact_ele.img_src);
                        }
                        GraphRequest nextRequest = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
                        if (nextRequest != null) {
                            nextRequest.setCallback(this);
                            nextRequest.executeAsync();
                        }
                    } else {
                        Log.d("request fail", ".");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        graphRequest2.executeAsync();

        return contact_list;
    }
}
