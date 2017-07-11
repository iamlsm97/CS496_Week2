package com.cs496.cs496_week2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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

public class Tab0HttpTest extends Fragment {

    File proimg = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab0_httptest, container, false);

        Button httptest = (Button) view.findViewById(R.id.httptest);
        Button getimage = (Button) view.findViewById(R.id.getimage);
        Button delimage = (Button) view.findViewById(R.id.delimage);
        final EditText httpmethod = (EditText) view.findViewById(R.id.httpmethod);
        final EditText httpurl = (EditText) view.findViewById(R.id.httpurl);
        final EditText reqbody = (EditText) view.findViewById(R.id.reqbody);
        final EditText addname = (EditText) view.findViewById(R.id.addname);
        final EditText addnum = (EditText) view.findViewById(R.id.addnum);


        httptest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String method = httpmethod.getText().toString();
                final String urltext = httpurl.getText().toString();
                final String body = reqbody.getText().toString();
                final String name = addname.getText().toString();
                final String number = addnum.getText().toString();

                if (method.equals("GET")) {
                    final GetExample example = new GetExample();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String response = null;
                            try {
                                response = example.run("http://13.124.143.15:8080" + urltext);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Runnable r = new textThread(response);
                            getActivity().runOnUiThread(r);
                        }
                    }).start();

                } else if (method.equals("POST")) {
                    final PostExample example = new PostExample();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String response = null;
                            try {
                                response = example.post("http://13.124.143.15:8080" + urltext, body);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Runnable r = new textThread(response);
                            getActivity().runOnUiThread(r);
                        }
                    }).start();

                } else if (method.equals("PUT")) {
                    final PutExample example = new PutExample();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String response = null;
                            try {
                                response = example.put("http://13.124.143.15:8080" + urltext, proimg, name, number);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Runnable r = new textThread(response);
                            getActivity().runOnUiThread(r);
                        }
                    }).start();

                } else if (method.equals("IMG")) {
                    final ImagePost example = new ImagePost();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String response = null;
                            try {
                                response = example.post("http://13.124.143.15:8080" + urltext, proimg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Runnable r = new textThread(response);
                            getActivity().runOnUiThread(r);
                        }
                    }).start();
                }
            }
        });

        getimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 566);
            }
        });

        delimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proimg = null;
                Toast.makeText(getContext(), "Delete Selected Image", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 566 && resultCode == RESULT_OK && data != null) {

            Uri uri = data.getData();
            proimg = new File(uri.getPath());

            Toast.makeText(getContext(), "Image Selected", Toast.LENGTH_SHORT).show();

            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(uri, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            cursor.close();

            proimg = new File(imagePath);
        }

    }


    public class GetExample {
        OkHttpClient client = new OkHttpClient();

        String run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();

        }
    }

    public class PostExample {
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

    public class ImagePost {
        OkHttpClient client = new OkHttpClient();

        String post(String url, File file) throws IOException {
            RequestBody formBody = null;
            String filenameArray[] = file.getName().split("\\.");
            String ext = filenameArray[filenameArray.length - 1];
            formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/" + ext), file))
                    .build();

            Request request = new Request.Builder().url(url).put(formBody).build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }

    public class PutExample {
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

    public class textThread implements Runnable {
        String restext = null;

        public textThread(String text) {
            restext = text;
        }

        @Override
        public void run() {
            TextView textresult = (TextView) getActivity().findViewById(R.id.textresult);
            textresult.setText(restext);
        }
    }

}
