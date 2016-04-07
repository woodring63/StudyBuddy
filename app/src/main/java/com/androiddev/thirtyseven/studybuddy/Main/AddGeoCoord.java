package com.androiddev.thirtyseven.studybuddy.Main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.androiddev.thirtyseven.studybuddy.Main.NavBase;
import com.androiddev.thirtyseven.studybuddy.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.Manifest;

public class AddGeoCoord extends NavBase implements OnMapReadyCallback {

    private double longitude;
    private double latitude;
    private boolean permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geo_coord);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        permission = true;
        if(Build.VERSION.SDK_INT >= 23) {
            permission = checkSelfPermission((Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED;

        }
        if(permission) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }



    }
    @Override
    public void onMapReady(GoogleMap map) {
        LatLng l = new LatLng(latitude, longitude);

        if(Build.VERSION.SDK_INT >= 23) {
            permission = checkSelfPermission((Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED;
            map.setMyLocationEnabled(true);


        }
        map.moveCamera(CameraUpdateFactory.newLatLng(l));
        map.animateCamera(CameraUpdateFactory.zoomTo(12));
        map.addMarker(new MarkerOptions()
                .position(l)
                .title("Marker"));

    }


}
