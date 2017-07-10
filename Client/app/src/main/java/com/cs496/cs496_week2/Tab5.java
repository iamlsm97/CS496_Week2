package com.cs496.cs496_week2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by rongrong on 2017-07-11.
 */

public class Tab5 extends Fragment {

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
        for (String string : result) {
            Log.e("for loop result", "|" + string + "|");
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab5, container, false);
        seekBar = view.findViewById(R.id.gall_seekbar);
        seekText = view.findViewById(R.id.gall_seekcnt);

        if (galleryId.size() == 0) {
            int permissionCheck = ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE);

            paths = getPathOfAllImages();
            for (int i = 0; i < paths.size(); i++) {
                File imgfile = new File(paths.get(i));
                if (imgfile.exists()) galleryId.add(imgfile);
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
            return galleryId.size();
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


//            ImageView imageView;
//            if (convertView == null) {
//                imageView = new ImageView(mContext);
//                imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setPadding(0, 0, 0, 0);
//            } else {
//                imageView = (ImageView) convertView;
//            }

            //imageView.setImageBitmap(galleryId.get(position));
            Glide.with(getActivity())
                    .load(galleryId.get(position))
                    .into(imageview);
//            imageView.setOnClickListener(new Tab3Gallery_ImageClickListener(mContext, galleryId.get(position)));
//            return imageView;


            linear.addView(imageview);
            return linear;

        }
    }


//    GridView gv;
//    GalleryGridAdapter gAdapter;
//
//    FloatingActionButton FABAddImg;
//    SeekBar seekBar;
//    TextView seekText;
//    ArrayList<String> storedImgPath = new ArrayList<String>();
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.tab5, container, false);
//
//        loadImageFromStorage();
//
//        FABAddImg = rootView.findViewById(R.id.fab_add);
//        gv = rootView.findViewById(R.id.galleryGridView);
//        gAdapter = new GalleryGridAdapter(getContext());
//        gv.setAdapter(gAdapter);
//        seekBar = rootView.findViewById(R.id.gall_seekbar);
//        seekText = rootView.findViewById(R.id.gall_seekcnt);
//
//
//        FABAddImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, 0);
//            }
//        });
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                Integer seek_cnt = seekBar.getProgress() + 1;
//                String seek_text = String.valueOf(seek_cnt) + " in a row";
//                seekText.setText(seek_text);
//                gv.setNumColumns(seek_cnt);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        return rootView;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
//
//            Toast.makeText(getContext(), "Added Image to Gallery", Toast.LENGTH_LONG).show();
////            Toast.makeText(getContext(), "Added Image to Gallery", Toast.LENGTH_LONG).show();
//
//            // Let's read picked image data - its URI
//            Uri uri = data.getData();
//            // Let's read picked image path using content resolver
//            String[] filePath = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContext().getContentResolver().query(uri, filePath, null, null, null);
//            cursor.moveToFirst();
//            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
//            cursor.close();
//
//            Bitmap image = BitmapFactory.decodeFile(imagePath);
//            storeImage(image);
//            gAdapter.notifyDataSetChanged();
//        }
//
//
//    }
//
//    private String getInternalPath() {
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//                + "/Android/data/"
//                + getContext().getApplicationContext().getPackageName()
//                + "/Files/tabB");
//
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                return null;
//            }
//        }
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
//        String mImageName = "ChanRong_" + timeStamp + ".jpg";
//        return mediaStorageDir.getPath() + File.separator + mImageName;
//    }
//
//    /* Create a File for saving an image or video */
//    private File getOutputMediaFile(String path) {
//        if (path == null) {
//            return null;
//        }
//        File mediaFile;
//        mediaFile = new File(path);
//        return mediaFile;
//    }
//
//    private void storeImage(Bitmap image) {
//        String internalPath = getInternalPath();
//        File pictureFile = getOutputMediaFile(internalPath);
//
//        if (pictureFile == null) {
//            Log.d("TAG", "Error creating media file, check storage permissions: ");// e.getMessage());
//            return;
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(pictureFile);
//            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
//            fos.close();
//            storedImgPath.add(internalPath);
//        } catch (FileNotFoundException e) {
//            Log.d("TAG", "File not found: " + e.getMessage());
//        } catch (IOException e) {
//            Log.d("TAG", "Error accessing file: " + e.getMessage());
//        }
//    }
//
//    public void loadImageFromStorage() {
//        String storagePath = Environment.getExternalStorageDirectory()
//                + "/Android/data/"
//                + getContext().getApplicationContext().getPackageName()
//                + "/Files/tabB";
//        File[] ImgFiles = (new File(storagePath).listFiles());
//        if (ImgFiles != null) {
//            for (int i = 0; i < ImgFiles.length; i++) {
//                if (!ImgFiles[i].isDirectory()) {
//                    storedImgPath.add(String.valueOf(ImgFiles[i]));
//                }
//            }
//        }
//    }
//
//    public class GalleryGridAdapter extends BaseAdapter {
//        Context context;
//
//        public GalleryGridAdapter(Context c) {
//            context = c;
//        }
//
//        public int getCount() {
//            return storedImgPath.size();
//        }
//
//        public Object getItem(int arg0) {
//            return null;
//        }
//
//        public long getItemId(int arg0) {
//            return 0;
//        }
//
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            LinearLayout linear = new LinearLayout(context);
//            linear.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 500));
//            linear.setPadding(10, 10, 10, 10);
//
//            ImageView imageview = new ImageView(context);
//            imageview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//            imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//            Bitmap bitmap = BitmapFactory.decodeFile(storedImgPath.get(position));
//
////            imageview.setImageBitmap(bitmap);
//
//            Glide.with(getActivity())
//                    .load(storedImgPath.get(position))
//                    .into(imageview);
//
//
////            imageview.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    final String[] actions = new String[]{"Show Image", "Delete Image"};
////                    AlertDialog.Builder selectAct = new AlertDialog.Builder(getContext());
////                    selectAct.setTitle("Select Action");
////                    selectAct.setNegativeButton("Cancel", null);
////                    selectAct.setItems(actions, new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialogInterface, int i) {
////                            if (i == 0) {
//////                                Intent showIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(new File(storedImgPath.get(position))));
//////                                startActivity(showIntent);
////
////                                Toast.makeText(getContext(), storedImgPath.get(position), Toast.LENGTH_LONG).show();
////
////                                Intent galleryIntent = new Intent(Intent.ACTION_VIEW);
////                                galleryIntent.setDataAndType(Uri.fromFile(new File(storedImgPath.get(position))), "image/*");
////                                galleryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                startActivity(galleryIntent);
////
////                            } else {
////                                File delFile = new File(storedImgPath.get(position));
////                                if (delFile.delete()) {
////                                    Toast.makeText(getContext(), "Deleted Image from Gallery", Toast.LENGTH_LONG).show();
////                                }
////                                storedImgPath.remove(position);
////                                gAdapter.notifyDataSetChanged();
////                            }
////                        }
////                    });
////                    selectAct.show();
////
////                }
////            });
//
//            linear.addView(imageview);
//            return linear;
//        }
//    }
}
