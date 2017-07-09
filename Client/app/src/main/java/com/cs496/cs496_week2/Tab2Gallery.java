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


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.os.Parcelable;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static android.R.attr.data;
import static android.R.attr.galleryItemBackground;
import static android.R.attr.layout_gravity;
import static android.app.Activity.RESULT_OK;

/**
 * Created by rongrong on 2017-07-06.
 */

public class Tab2Gallery extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 530;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 531;

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

//        for (String string : result) {
//            Log.i("PhotoSelectActivity.java | getPathOfAllImages", "|" + string + "|");
//        }
        return result;
    }

    GridView gridview;
    View view;
    public static ArrayList<File> galleryId = new ArrayList<>();
    ArrayList<String> paths = new ArrayList<>();
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;

    public void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_CAMERA) {
            if (data != null) {
                Uri uri = data.getData();
                Cursor c = getActivity().getContentResolver().query(Uri.parse(uri.toString()), null, null, null, null);
                c.moveToNext();
                String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                uri = Uri.fromFile(new File(path));
                c.close();

                if (uri != null) {
                    File photo = new File(uri.getPath());
                    galleryId.add(photo);
                    gridview.setAdapter(new galleryAdapter(getActivity()));
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab2_gallery, container, false);

        ImageButton picture = (ImageButton) view.findViewById(R.id.camera);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA);

                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.CAMERA)) {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
                        dialog.setTitle("Permission Check")
                                .setMessage("[설정] > [개인정보 보호 및 안전] > [앱 권한] 에서 권한을 요청하여야 합니다")
                                .setPositiveButton("수락", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(
                                                new String[]{android.Manifest.permission.CAMERA},
                                                MY_PERMISSIONS_REQUEST_CAMERA
                                        );
                                    }
                                })
                                .setNegativeButton("거절", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getActivity(), "요청이 거부되었습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }).create().show();
                    } else {
                        requestPermissions(
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA
                        );
                    }
                } else {
                    doTakePhotoAction();
                }
            }
        });

        if (galleryId.size() == 0) {
            int permissionCheck = ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
                    dialog.setTitle("Permission Check")
                            .setMessage("[설정] > [개인정보 보호 및 안전] > [앱 권한] 에서 권한을 요청하여야 합니다")
                            .setPositiveButton("수락", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(
                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                                    );
                                }
                            })
                            .setNegativeButton("거절", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(), "요청이 거부되었습니다", Toast.LENGTH_SHORT).show();
                                }
                            }).create().show();
                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                    );
                }
            } else {
                paths = getPathOfAllImages();
                for (int i = 0; i < paths.size(); i++) {
                    File imgfile = new File(paths.get(i));
                    if (imgfile.exists()) galleryId.add(imgfile);
                }
                gridview = (GridView) view.findViewById(R.id.gridview);
                gridview.setAdapter(new galleryAdapter(getActivity()));
            }
        }

        return view;
    }

    private class galleryAdapter extends BaseAdapter {
        private Context mContext;
        LayoutInflater inflater;

        public galleryAdapter(Context c) {
            mContext = c;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return galleryId.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0, 0, 0, 0);
            } else {
                imageView = (ImageView) convertView;
            }

            //imageView.setImageBitmap(galleryId.get(position));
            Glide.with(getActivity())
                    .load(galleryId.get(position))
                    .into(imageView);
            imageView.setOnClickListener(new Tab2Gallery_ImageClickListener(mContext, galleryId.get(position)));
            return imageView;
        }
    }
    /*
    public void doTakeAlbumAction() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent, "Complete"), PICK_FROM_GALLERY);
    }
    */

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    paths = getPathOfAllImages();
                    for (int i = 0; i < paths.size(); i++) {
                        File imgfile = new File(paths.get(i));
                        if (imgfile.exists()) galleryId.add(imgfile);
                    }
                    gridview = (GridView) view.findViewById(R.id.gridview);
                    gridview.setAdapter(new galleryAdapter(getActivity()));
                } else {
                    Toast.makeText(getActivity(), "요청이 거부되었습니다", Toast.LENGTH_SHORT).show();
                }
                return;

            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doTakePhotoAction();
                } else {
                    Toast.makeText(getActivity(), "요청이 거부되었습니다", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}