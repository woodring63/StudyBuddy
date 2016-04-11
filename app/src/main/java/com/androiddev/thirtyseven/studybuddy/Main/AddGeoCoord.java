package com.androiddev.thirtyseven.studybuddy.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.androiddev.thirtyseven.studybuddy.Main.NavBase;
import com.androiddev.thirtyseven.studybuddy.R;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.Manifest;
import android.widget.Button;

public class AddGeoCoord extends NavBase implements OnMapReadyCallback {

    private double longitude;
    private double latitude;
    private boolean permission;
    private SharedPreferences.Editor editor;
    private MarkerOptions userMarker;


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
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        Button button = (Button) findViewById(R.id.buddyButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng position = userMarker.getPosition();
                editor.putString("Latitude", String.valueOf(position.latitude));
                editor.putString("Longitude", String.valueOf(position.longitude));
                Intent i = new Intent(getApplicationContext(), CreateSessionActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(i);

            }
        });




    }
    @Override
    public void onMapReady(GoogleMap map) {
        LatLng l = new LatLng(latitude, longitude);
        LatLng ISU = new LatLng(42.026334, -93.646361);
        LatLng curtissHall = new LatLng(42.026208, -93.645023);
        LatLng jischkeHall = new LatLng(42.027223, -93.644535);
        LatLng pearsonHall = new LatLng(42.025935, -93.649923);
        LatLng atanasoffHall = new LatLng(42.028135, -93.649730);
        LatLng cooverHall = new LatLng(42.028334, -93.650953);
        LatLng gilmanHall = new LatLng(42.029027, -93.648604);
        LatLng leBaronHall = new LatLng(42.028533, -93.647574);
        LatLng udcc = new LatLng(42.024772, -93.651500);
        LatLng hooverHall = new LatLng(42.026668, -93.651350);
        LatLng howeHall = new LatLng(42.026828, -93.652627);
        LatLng gerdinHall = new LatLng(42.025274, -93.644612);
        LatLng memorialUnion = new LatLng(42.024134, -93.645964);
        LatLng physicsHall = new LatLng(42.029139, -93.647370);
        LatLng designHall = new LatLng(42.028342, -93.652981);

        if(Build.VERSION.SDK_INT >= 23) {
            permission = checkSelfPermission((Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED;
            map.setMyLocationEnabled(true);

        }
        map.moveCamera(CameraUpdateFactory.newLatLng(ISU));
        map.animateCamera(CameraUpdateFactory.zoomTo(16));
        userMarker = new MarkerOptions().position(ISU).title("Drag this pin to where you want your Study Session to be!").draggable(true);
        map.addMarker(userMarker);
        map.addMarker(new MarkerOptions().position(curtissHall).title("Curtiss Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(jischkeHall).title("Jischke Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(pearsonHall).title("Pearson Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(atanasoffHall).title("Atanasoff Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(cooverHall).title("Coover Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(gilmanHall).title("Gilman Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(leBaronHall).title("LeBaron Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(udcc).title("Union Drive Community Center").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(hooverHall).title("Hoover Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(howeHall).title("Howe Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(gerdinHall).title("Gerdin BusinessBuilding").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(memorialUnion).title("Memorial Union").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(physicsHall).title("Physics Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(designHall).title("Design Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));



    }


}
