package com.cs496.cs496_week2;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by rongrong on 2017-07-10.
 */

public class HttpCall extends Activity{
    private static GetExample getexample = new GetExample();
    private static PostExample postexample = new PostExample();
    private static PutExample putexample = new PutExample();

    private static File proimg = null;
    private static String method;
    private static String urltext;
    private static String body;
    private static String name;
    private static String number;
    private static String response;

    public static void setMethodtext(String s) {
        method = s;
    }

    public static void setUrltext(String s) {
        urltext = s;
    }

    public static void setBodytext(String s) {
        body = s;
    }

    public static void setNametext(String s) {
        name = s;
    }

    public static void setNumbertext(String s) {
        number = s;
    }

    public static void setProimgfile(File f) {
        proimg = f;
    }

    public static class GetExample {
        OkHttpClient client = new OkHttpClient();

        String run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }

    public static class PostExample {
        public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

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

    public static class PutExample {
        //        public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        String put(String url, File file, String name, String number) throws IOException {
            RequestBody formBody;
            if (file != null) {
                String filenameArray[] = file.getName().split("\\.");
                String ext = filenameArray[filenameArray.length - 1];
                formBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name", name)
                        .addFormDataPart("number", number)
                        .addFormDataPart("profile_image", file.getName(), RequestBody.create(MediaType.parse("image/" + ext), file))
                        .build();
            } else {
                formBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name", name)
                        .addFormDataPart("number", number)
                        .build();
            }

            Request request = new Request.Builder().url(url).put(formBody).build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }

    public static String getResponse() {
        Log.d("METHOD", method);
        Log.d("URL", urltext);
        if (method.equals("GET")) {
           getexample = new GetExample();
            response = null;

            getThread mThread = new getThread();
            mThread.start();
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mThread.getResponse();

        } else if (method.equals("POST")) {
            postexample = new PostExample();
            response = null;

            postThread mThread = new postThread();
            mThread.start();
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mThread.getResponse();

        } else if (method.equals("PUT")) {
            putexample = new PutExample();
            response = null;

            putThread mThread = new putThread();
            mThread.start();
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mThread.getResponse();

        }
        return null;
    }

    public static class putThread extends Thread {
        static String response;

        @Override
        public void run() {
            try {
                response = putexample.put("http://13.124.143.15:8080" + urltext, proimg, name, number);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getResponse() {
            return response;
        }
    }

    public static class postThread extends Thread {
        static String response;

        @Override
        public void run() {
            try {
                response = postexample.post("http://13.124.143.15:8080" + urltext, body);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getResponse() {
            return response;
        }
    }

    public static class getThread extends Thread {
        static String response;

        @Override
        public void run() {
            try {
                response = getexample.run("http://13.124.143.15:8080" + urltext);
                Log.d("RESPONSE", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getResponse() {
            return response;
        }
    }
}
