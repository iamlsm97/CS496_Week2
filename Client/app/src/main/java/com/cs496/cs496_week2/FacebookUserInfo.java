package com.cs496.cs496_week2;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    private static ArrayList<Contact> contactlist = new ArrayList<>();
    private static ArrayList<Cafe> cafelist = new ArrayList<>();
    private static ArrayList<String> cafenamelist = new ArrayList<>();
    public static class Cafe {
        String name = new String();
        String time = new String();
        double lat;
        double lng;
        String roastery = new String();
        String engname = new String();
    }
    public static class fbContact {
        String img_src = "Default";
        String name = "Default";
    }
    public static class Contact {
        String img_src = "Default";
        String number = "Default";
        String name = "Default";
    }

    public static ArrayList<fbContact> getfbContactList() {
        return fbcontactlist;
    }

    public static ArrayList<Contact> getContactList() {
        return contactlist;
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

    public static void Login(final Context context) throws JSONException {
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

                        uploadContact(email, context);
                        uploadGallery(email, context);
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

                            HttpCall.setMethodtext("GET");
                            HttpCall.setUrltext("/api/"+email+"/contact");
                            JSONArray contactJSONArray = new JSONArray(HttpCall.getResponse());
                            for (int j=0;j<contactJSONArray.length();j++) {
                                Contact new_ele = new Contact();
                                new_ele.name = contactJSONArray.getJSONObject(j).getString("name");
                                new_ele.number = contactJSONArray.getJSONObject(j).getString("number");
//                                if (contactJSONArray.getJSONObject(j).getString("profile_image") == null)
//                                    new_ele.img_src = null;
//                                else new_ele.img_src = contactJSONArray.getJSONObject(j).getString("profile_image");
                                contactlist.add(new_ele);
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
        contactlist = new ArrayList<>();
        email = null;
        name = null;
        id = null;
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

    public static void uploadContact(String email, Context context) throws IOException {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone._ID
        };
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " asc";
        String condition = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
        String[] selectionArgs = null;

        Cursor contactCursor = context.getContentResolver().query(
                uri,
                projection,
                condition,
                selectionArgs,
                sortOrder
        );

        while (contactCursor.moveToNext()) {
            HttpCall.setMethodtext("PUT");
            HttpCall.setUrltext("/api/"+email+"/addcontact");
            HttpCall.setNumbertext(contactCursor.getString(0));
            HttpCall.setNametext(contactCursor.getString(1));
            Uri img_uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactCursor.getString(2)));
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), img_uri);
            if (input != null) {
                Log.d("there is photo", "!!!!!!!!");
                String tDir = System.getProperty("java.io.tmpdir");
                f = new File(tDir + "tmp" + ".jpg");
                OutputStream output = new FileOutputStream(f);
                IOUtils.copy(input, output);
                output.close();
                HttpCall.setProimgfile(f);
            }
            HttpCall.getResponse();
        }
        contactCursor.close();
    }

    private static ArrayList<String> getPathOfAllImages(Context context) {
        ArrayList<String> result = new ArrayList<>();
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int columnDisplayname = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);

        int lastIndex;
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(columnIndex);
            String nameOfFile = cursor.getString(columnDisplayname);
            lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile);
            lastIndex = lastIndex >= 0 ? lastIndex : nameOfFile.length() - 1;

            if (!TextUtils.isEmpty(absolutePathOfImage)) {
                result.add(absolutePathOfImage);
            }
        }
        for (String string : result) {
            Log.e("for loop result", "|" + string + "|");
        }
        return result;
    }

    public static class PostImage {
        OkHttpClient client = new OkHttpClient();

        String post(String url, File file) throws IOException {

            Log.e("post url", url);
            Log.e("post file", file.toString());
            RequestBody formBody = null;
            String filenameArray[] = file.getName().split("\\.");
            String ext = filenameArray[filenameArray.length - 1];
            formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/" + ext), file))
                    .build();

            Log.e("send req", "11111");

            Request request = new Request.Builder().url(url).post(formBody).build();
            Log.e("send req", "22222");
            Response response = client.newCall(request).execute();
            Log.e("send req", "3333");
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            Log.e("send req", "4444");
            return response.body().string();
        }
    }

    public static void uploadGallery(final String email, Context context) throws IOException {

        ArrayList<String> paths = new ArrayList<>();
        paths = getPathOfAllImages(context);
//        Log.e("pathlength", String.valueOf(paths.size()));
        for (int i = 0; i < paths.size(); i++) {
            Log.e("씨발!", "퇴근좀하자");
            final File imgfile = new File(paths.get(i));
            if (imgfile.exists()) {

                final PostImage example = new PostImage();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String response = null;
                        try {
                            Log.e("req file", imgfile.toString());
                            response = example.post("http://13.124.143.15:8080/api/"+email+"/addimage", imgfile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        }


    }


    public static ArrayList<fbContact> getContactListdirect() {
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

    public static ArrayList<Cafe> getCafeList() {
        return cafelist;
    }

    public static ArrayList<String> getCafeNameList() {
        return cafenamelist;
    }

    public static void uploadCafe() {
        HttpCall.setMethodtext("GET");
        HttpCall.setUrltext("/api/cafelist");
        JSONArray JSONcafe = new JSONArray();
        try {
            JSONcafe = new JSONArray(HttpCall.getResponse());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < JSONcafe.length(); i++) {
            Cafe cafe_ele = new Cafe();
            try {
                cafe_ele.name = JSONcafe.getJSONObject(i).getString("name");
                cafe_ele.engname = JSONcafe.getJSONObject(i).getString("engname");
                cafe_ele.time = JSONcafe.getJSONObject(i).getString("time");
                cafe_ele.lat = JSONcafe.getJSONObject(i).getDouble("lat");
                cafe_ele.lng = JSONcafe.getJSONObject(i).getDouble("lng");
                cafe_ele.roastery = JSONcafe.getJSONObject(i).getString("roastery");
                Log.d("add success", cafe_ele.name);
                cafelist.add(cafe_ele);
                cafenamelist.add(cafe_ele.name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
