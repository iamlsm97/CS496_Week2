package com.cs496.cs496_week2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.GpsStatus;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by rongrong on 2017-07-06.
 */

public class Tab4 extends Fragment implements OnMapReadyCallback{
    View view;
    GoogleMap googleMap;
    MapView mapView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab4, container, false);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        LatLng KAIST = new LatLng(36.372434, 127.360353);
        LatLng A = new LatLng(37.22, 126.93);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(KAIST);
        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(A);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        markerOptions2.title("2");
        markerOptions2.snippet("afa");
        map.setMyLocationEnabled(true);
        map.addMarker(markerOptions);
        map.addMarker(markerOptions2);

        map.moveCamera(CameraUpdateFactory.newLatLng(KAIST));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
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
