package com.cs496.cs496_week2;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

import okhttp3.OkHttpClient;

/**
 * Created by q on 2017-07-07.
 */

public class FacebookUserInfo {
    private static String userlist;
    private static String name = new String();
    private static String email = new String();
    private static String id = new String();
    private static String img;
    private static ArrayList<fbContact> fbcontactlist = new ArrayList<>();
    public static class fbContact {
        String img_src = "Default";
        String name = "Default";
    }

    public static ArrayList<fbContact> getfbContactList() {
        return fbcontactlist;
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

    public static void Login() throws JSONException {
        HttpCall.setMethodtext("GET");
        HttpCall.setUrltext("/api/userlist");
        userlist = HttpCall.getResponse();

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.d("Profile information", object.toString());
                    img = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    name = object.getString("name");
                    email = object.getString("email");
                    if (!userlist.contains("\"email\":\""+email+"\"")) {
                        HttpCall.setMethodtext("POST");
                        HttpCall.setUrltext("/api/adduser");
                        HttpCall.setBodytext("{\"email\":\""+email+"\"}");
                        HttpCall.getResponse();
                        Log.e("not in userlist", "email is "+ email);

                        uploadFacebookContact(email);
                    } else {
                        Log.e("already joined", "!");
                        try {
                            HttpCall.setMethodtext("GET");
                            HttpCall.setUrltext("/api/"+email+"/facebook");
                            JSONArray fbJSONArray = new JSONArray(HttpCall.getResponse());
                            for (int j=0;j<fbJSONArray.length();j++) {
                                fbContact new_ele = new fbContact();
                                new_ele.name = fbJSONArray.getJSONObject(j).getString("name");
                                new_ele.img_src = "http://13.124.143.15:8080/"+fbJSONArray.getJSONObject(j).getString("profile_image");
                                fbcontactlist.add(new_ele);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        //fbcontactlist = getContactList();
    }

    public static void Logout() {
        fbcontactlist = new ArrayList<>();
    }

    static File f;

    public static void uploadFacebookContact(final String email) {
        final GraphRequest graphRequest2 = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "/me/taggable_friends", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                JSONObject json = response.getJSONObject();
                Log.d("request success", ".");
                try {
                    if (json != null) {
                        final JSONArray jsonArray = json.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final int k = i;
                            HttpCall.setMethodtext("fbPUT");
                            HttpCall.setUrltext("/api/"+email+"/addfacebook");
                            HttpCall.setIdtext(jsonArray.getJSONObject(i).getString("id"));
                            HttpCall.setNametext(jsonArray.getJSONObject(i).getString("name"));
                            f = null;
                            Thread thread = new Thread() {
                                public void run() {
                                    try {
                                        Log.d("file is", jsonArray.getJSONObject(k).getJSONObject("picture").getJSONObject("data").getString("url"));
                                        String tDir = System.getProperty("java.io.tmpdir");
                                        f = new File(tDir+"tmp"+".jpg");
                                        FileUtils.copyURLToFile(new URL(jsonArray.getJSONObject(k).getJSONObject("picture").getJSONObject("data").getString("url")), f);
                                        if (f != null) {
                                            HttpCall.setProimgfile(f);
                                            HttpCall.getResponse();
                                        }
                                        else {
                                            Log.d("WHATTHE","AFSDDFASASFD");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            if (f == null) Log.d("!@#$!@#$","ASDFASFD");

                            thread.start();
                            thread.join();

                            Log.d("IMG_SOURCE", jsonArray.getJSONObject(i).getJSONObject("picture").getJSONObject("data").getString("url"));
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture.type(large)");
        graphRequest2.setParameters(parameters);
        graphRequest2.executeAsync();
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
                            File f = new File(jsonArray.getJSONObject(i).getJSONObject("picture").getJSONObject("data").getString("url"));
                            contact_list.add(contact_ele);
                            Log.d("IMG_SOURCE", jsonArray.getJSONObject(i).getString("id"));
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
