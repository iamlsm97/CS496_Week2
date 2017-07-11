package com.cs496.cs496_week2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by rongrong on 2017-07-11.
 */

public class Tab4CafeMap extends Fragment implements OnMapReadyCallback {
    View view;
    GoogleMap googleMap;
    MapView mapView;
    ArrayList<FacebookUserInfo.Cafe> cafelist = new ArrayList<>();
    ArrayList<String> cafenamelist = new ArrayList<>();
    double lat = 36.372434;
    double lng = 127.360353;
    String name = "KAIST";
    String time = "do you want some coffee?";
    String roastery = "N";
    String engname = "kaist";
    Tab4CafeMap tmp;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab4_cafemap, container, false);
        cafelist = FacebookUserInfo.getCafeList();
        cafenamelist = FacebookUserInfo.getCafeNameList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, cafenamelist);
        final AutoCompleteTextView textView = (AutoCompleteTextView) view.findViewById(R.id.cafe_search);
        textView.setAdapter(adapter);

        final TextView nametext = (TextView) view.findViewById(R.id.cafe_name);
        final TextView timetext = (TextView) view.findViewById(R.id.cafe_time);
        final TextView typetext = (TextView) view.findViewById(R.id.cafe_type);
        nametext.setText(name);
        timetext.setText(time);
        typetext.setText("Search your favorite");

        tmp = this;
        Button selectbtn = (Button) view.findViewById(R.id.search_cafe_btn);
        selectbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View mview) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                String cafename = textView.getText().toString();
                Log.d("cafe clicked", cafename);
                if (cafenamelist.contains(cafename)) {
                    Log.d("cafe clicked", "fck");
                    for (int i=0;i<cafenamelist.size();i++) {
                        Log.d("cafe clicked( in loop )", cafenamelist.get(i));
                        if (cafenamelist.get(i).equals(cafename)) {
                            Log.d("cafe selected", name);
                            name = cafelist.get(i).name;
                            lat = cafelist.get(i).lat;
                            lng = cafelist.get(i).lng;
                            time = cafelist.get(i).time;
                            roastery = cafelist.get(i).roastery;
                            engname = cafelist.get(i).engname;

                            nametext.setText(name);
                            timetext.setText(time);
                            if (roastery.equals("N")) typetext.setText("Franchise");
                            else typetext.setText("Roastery");

                            mapView = (MapView) view.findViewById(R.id.map);
                            mapView.onCreate(savedInstanceState);
                            mapView.onResume();
                            mapView.getMapAsync(tmp);
                        } else {
                            Log.d("cafe not selected", name);
                        }
                    }
                    Log.d("loop end", "...");
                }
            }
        });

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        LatLng cafe = new LatLng(lat, lng);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cafe, 15);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(cafe);
        markerOptions.title(name);
        markerOptions.snippet(time);
        map.setMyLocationEnabled(true);
        map.addMarker(markerOptions);

        map.animateCamera(cameraUpdate);

//        map.moveCamera(CameraUpdateFactory.newLatLng(cafe));
//        map.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
