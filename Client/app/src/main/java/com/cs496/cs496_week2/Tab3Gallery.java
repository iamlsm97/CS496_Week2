package com.cs496.cs496_week2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by rongrong on 2017-07-11.
 */

public class Tab3Gallery extends Fragment {

    private ArrayList<String> getPathOfAllImages() {
        ArrayList<String> result = new ArrayList<>();
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
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
        return result;
    }


    View view;
    GridView gridview;
    GalleryGridAdapter gAdapter;
    SeekBar seekBar;
    TextView seekText;
    public static ArrayList<File> galleryId = new ArrayList<>();
    ArrayList<String> paths = new ArrayList<>();
    ArrayList<URL> galleryurl = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (!FacebookUserInfo.isLoggedIn()) {
            view = inflater.inflate(R.layout._logout, null);
        } else {
            if (view == null) {
                view = inflater.inflate(R.layout.tab3_gallery, container, false);
                seekBar = view.findViewById(R.id.gall_seekbar);
                seekText = view.findViewById(R.id.gall_seekcnt);

                FloatingActionButton FABAddImg = view.findViewById(R.id.fab_add);
                FloatingActionButton FABCamera = view.findViewById(R.id.fab_cam);

                FABAddImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, 621);
                    }
                });

                FABCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, 622);
                    }
                });

                if (galleryId.size() == 0) {
                    paths = getPathOfAllImages();
                    for (int i = 0; i < paths.size(); i++) {
                        File imgfile = new File(paths.get(i));
                        if (imgfile.exists()) galleryId.add(imgfile);
                    }

                    JSONArray DBGallery = null;

                    CustomThread thread = new CustomThread();
                    thread.start();

                    try {
                        thread.join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    DBGallery = thread.getResult();
                    for (int i = 0; i < DBGallery.length(); i++) {
                        JSONObject single = null;
                        URL imageUrl = null;
                        File singleFile = null;
                        try {
                            single = DBGallery.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            imageUrl = new URL("http://13.124.143.15:8080/" + single.getString("image"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        galleryurl.add(imageUrl);
                    }

                    gridview = (GridView) view.findViewById(R.id.galleryGridView);
                    gAdapter = new GalleryGridAdapter(getContext());
                    gridview.setAdapter(gAdapter);

                }


                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        Integer seek_cnt = seekBar.getProgress() + 1;
                        String seek_text = String.valueOf(seek_cnt) + " in a row";
                        seekText.setText(seek_text);
                        gridview.setNumColumns(seek_cnt);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            } else {
                ViewGroup pVG = (ViewGroup) view.getParent();
                if (pVG != null) {
                    pVG.removeView(view);
                }
            }
        }

        return view;
    }

    private class GalleryGridAdapter extends BaseAdapter {
        private Context mContext;
        LayoutInflater inflater;

        public GalleryGridAdapter(Context c) {
            mContext = c;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return galleryurl.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout linear = new LinearLayout(mContext);
            linear.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 500));
            linear.setPadding(10, 10, 10, 10);

            ImageView imageview = new ImageView(mContext);
            imageview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(getActivity())
                    .load(galleryurl.get(position))
                    .into(imageview);

            linear.addView(imageview);
            return linear;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 621 && resultCode == RESULT_OK && data != null) {

            Toast.makeText(getContext(), "Added Image to Gallery", Toast.LENGTH_LONG).show();

            // Let's read picked image data - its URI
            Uri uri = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(uri, filePath, null, null, null);
            cursor.moveToFirst();
            final String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            cursor.close();

            final PostImage example = new PostImage();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String response = null;
                    try {
                        response = example.post("http://13.124.143.15:8080/api/" + FacebookUserInfo.getEmail() + "/addimage", new File(imagePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (requestCode == 622 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            Cursor c = getActivity().getContentResolver().query(Uri.parse(uri.toString()), null, null, null, null);
            c.moveToNext();
            String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
            uri = Uri.fromFile(new File(path));
            c.close();

            if (uri != null) {
                File photo = new File(uri.getPath());
                galleryId.add(photo);
                gAdapter.notifyDataSetChanged();
            }
        }


    }

    public class GetGallery {
        OkHttpClient client = new OkHttpClient();

        String get(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();

        }
    }

    public static class PostImage {
        OkHttpClient client = new OkHttpClient();

        String post(String url, File file) throws IOException {
            RequestBody formBody = null;
            String filenameArray[] = file.getName().split("\\.");
            String ext = filenameArray[filenameArray.length - 1];
            formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/" + ext), file))
                    .build();

            Request request = new Request.Builder().url(url).post(formBody).build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }

    public class CustomThread extends Thread {
        JSONArray dbGallery = null;
        final GetGallery example = new GetGallery();

        public CustomThread() {
            dbGallery = null;
        }

        @Override
        public void run() {
            String response = null;
            try {
                response = example.get("http://13.124.143.15:8080/api/" + FacebookUserInfo.getEmail() + "/gallery");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                dbGallery = new JSONArray(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public JSONArray getResult() {
            return dbGallery;
        }

    }

    public class GalleryThread extends Thread {

        URL imageurl = null;
        File file = null;

        public GalleryThread(URL url) {
            imageurl = url;
        }

        @Override
        public void run() {
            Log.e("url in thread", imageurl.toString());
            InputStream in = null;
            try {
                URLConnection urlConn = imageurl.openConnection();
                HttpURLConnection httpConn = (HttpURLConnection) urlConn;
                httpConn.connect();
                in = httpConn.getInputStream();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                IOUtils.copy(in, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public File getFile() {
            return file;
        }


    }

}