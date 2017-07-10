package com.cs496.cs496_week2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by q on 2017-07-10.
 */

public class Tab2Facebook extends Fragment {
    ArrayList<FacebookUserInfo.fbContact> ContactArrList;
    ArrayList<FacebookUserInfo.fbContact> items;
    ArrayList<FacebookUserInfo.fbContact> displayitems = new ArrayList<>();
    Bitmap bitmap;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (FacebookUserInfo.isLoggedIn()) {
            view = inflater.inflate(R.layout.tab2_facebook, null);
            ContactArrList = FacebookUserInfo.getContactArrayList();

            final CustomAdapter adapter = new CustomAdapter(this.getActivity(), R.layout.tab2_contacts_layout, ContactArrList);

            final EditText searchText = (EditText) view.findViewById(R.id.fb_text_search);
            searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String search_text = s.toString();
                    adapter.filter(search_text);
                }
            });

            ListView listview = (ListView) view.findViewById(R.id.fb_list_view);
            if (listview != null)
                listview.setAdapter(adapter);
        } else {
            view = inflater.inflate(R.layout._logout, null);
        }
        return view;
    }

    private class CustomAdapter extends ArrayAdapter<FacebookUserInfo.fbContact> {
        public void filter(String searchText) {
            searchText = searchText.toLowerCase(Locale.getDefault());
            displayitems.clear();
            if (searchText.length() == 0) {
                displayitems.addAll(items);
            }
            else {
                for (FacebookUserInfo.fbContact item : items) {
                    if (item.name.contains(searchText)) {
                        displayitems.add(item);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<FacebookUserInfo.fbContact> objects) {
            super(context, textViewResourceId, objects);
            displayitems = objects;
            items = new ArrayList<>();
            items.addAll(displayitems);
        }

        @Override
        public int getCount() {
            return displayitems.size();
        }

        @Override
        public FacebookUserInfo.fbContact getItem(int position) {
            return displayitems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.tab2_contacts_layout, null);
            }

            ImageView imageView = (ImageView)v.findViewById(R.id.profile_img);
            Thread thread = new Thread() {
                public void run() {
                    try {
                        bitmap = BitmapFactory.decodeStream(new URL(displayitems.get(position).img_src).openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            imageView.setBackground(new ShapeDrawable(new OvalShape()));
            imageView.setClipToOutline(true);
            imageView.setImageBitmap(bitmap);

            TextView textView1 = (TextView)v.findViewById(R.id.fb_textView);
            textView1.setText(displayitems.get(position).name);
            return v;
        }
    }
}
