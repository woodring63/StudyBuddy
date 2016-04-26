package com.androiddev.thirtyseven.studybuddy.Main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.jar.JarEntry;

import javax.xml.transform.Result;

public class HubActivity extends NavBase implements OnMapReadyCallback {
    JSONObject json;
    ListView sessionsList;
    ArrayList<String> sessions;
    private boolean permission;
    private String bundle;
    JSONArray JArray;
    public ArrayList<Marker> markers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sessions = new ArrayList<>();
        super.onCreate(savedInstanceState);
        bundle = (String) getIntent().getSerializableExtra("sessions");
        //setupSessions(bundle);
        markers = new ArrayList<Marker>();
        setContentView(R.layout.activity_hub);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        permission = true;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddGeoCoord.class);
                startActivity(i);
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    public void setupSessions(String bundle) {

        try {
            if (bundle != null) {
                json = new JSONObject(bundle.toString());
                if (json != null) {
                    JArray = json.getJSONArray("sessions");
                    for (int i = 0; i < JArray.length(); i++) {
                        String pattern = "MM/dd/yyyy";
                        SimpleDateFormat format = new SimpleDateFormat(pattern);
                        Date date = new Date(Long.parseLong(JArray.getJSONObject(i).getString("startTime")));
                        sessions.add(JArray.getJSONObject(i).getString("course") + " " + format.format(date));

                    }
                }
            } else {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String email = prefs.getString("email", "None");
                // FindSessionsAsync async = new FindSessionsAsync(email, sessions, JArray);
                //JArray = async.execute().get();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void onMapReady(GoogleMap map) {
        LatLng ISU = new LatLng(42.026334, -93.646361);
        LatLng curtissHall = new LatLng(42.026201, -93.644804);
        LatLng jischkeHall = new LatLng(42.027170, -93.644537);
        LatLng pearsonHall = new LatLng(42.025935, -93.649923);
        LatLng atanasoffHall = new LatLng(42.028135, -93.649730);
        LatLng cooverHall = new LatLng(42.028334, -93.650953);
        LatLng gilmanHall = new LatLng(42.029027, -93.648604);
        LatLng leBaronHall = new LatLng(42.028533, -93.647574);
        LatLng udcc = new LatLng(42.024772, -93.651500);
        LatLng hooverHall = new LatLng(42.026668, -93.651350);
        LatLng howeHall = new LatLng(42.026828, -93.652627);
        LatLng gerdinHall = new LatLng(42.025471, -93.644410);
        LatLng memorialUnion = new LatLng(42.023886, -93.645897);
        LatLng physicsHall = new LatLng(42.029139, -93.647370);
        LatLng designHall = new LatLng(42.028631, -93.653086);
        LatLng carverHall = new LatLng(42.025225, -93.648345);
        LatLng library = new LatLng(42.028015, -93.648958);
        LatLng musicHall = new LatLng(42.024634, -93.648202);
        if (Build.VERSION.SDK_INT >= 23) {
            permission = checkSelfPermission((Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED;
            map.setMyLocationEnabled(true);

        }
        map.moveCamera(CameraUpdateFactory.newLatLng(ISU));
        map.animateCamera(CameraUpdateFactory.zoomTo(16));
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
        map.addMarker(new MarkerOptions().position(gerdinHall).title("Gerdin Business Building").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(memorialUnion).title("Memorial Union").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(physicsHall).title("Physics Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(designHall).title("Design Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(carverHall).title("Carver Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(musicHall).title("Music Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        map.addMarker(new MarkerOptions().position(library).title("Parks Library").icon(BitmapDescriptorFactory.fromResource(R.drawable.cramschool)));
        try {
            if (bundle != null) {
                json = new JSONObject(bundle.toString());
                if (json != null) {
                    JArray = json.getJSONArray("sessions");
                    Log.v("X", JArray.length() + "");
                    for (int i = 0; i < JArray.length(); i++) {
                        JSONArray coordinates = JArray.getJSONObject(i).getJSONObject("loc").getJSONArray("coordinates");
                        Log.v("Test", coordinates.toString() + "");

                        double lat = (Double) coordinates.get(0);
                        double lon = (Double) coordinates.get(1);
                        LatLng l = new LatLng(lat, lon);

                        Marker m = map.addMarker(new MarkerOptions().position(l).snippet(i + ""));
                        markers.add(m);

                    }
                }
            } else {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String email = prefs.getString("email", "None");
                FindSessionsAsync async = new FindSessionsAsync(email, map, JArray, markers);
                JArray = async.execute().get();

            }

        } catch (Exception e) {

        }
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (markers.contains(marker)) {
                    int position = Integer.parseInt(marker.getSnippet());
                    Intent i = new Intent(getApplicationContext(), SessionInfo.class);
                    Bundle bundle = new Bundle();
                    try {
                        if (JArray != null) {
                            bundle.putSerializable("session", JArray.getJSONObject(position).toString());
                            i.putExtras(bundle);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    startActivity(i);
                    return true;
                }
                else{
                    marker.showInfoWindow();
                    return true;
                }
            }
        });
    }
}

class FindSessionsAsync extends AsyncTask<Void, Void, JSONArray> {

    private String username;
    private GoogleMap map;
    private JSONArray JArray;
    private ArrayList<Marker> markers;
    //private JArrayCallback callback;

    public FindSessionsAsync(String username, GoogleMap map, JSONArray JArray, ArrayList<Marker> markers) {
        this.username = username;
        this.map = map;
        this.JArray = JArray;
        this.markers = markers;
        // this.callback
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        ServerConnection server = new ServerConnection("/users/username/" + username);
        JSONObject json = server.run();
        try {
            JArray = json.getJSONArray("newSessions");
        } catch (JSONException e) {
            e.printStackTrace();
        }catch(NullPointerException e)
        {
            //do nothing
        }
        return JArray;
    }

    @Override
    protected void onPostExecute(JSONArray params) {
        // callback.onCompleted(params);
        if(JArray == null)
        {
            return;
        }
        Log.v("Y", JArray.length() + "");

        for (int i = 0; i < JArray.length(); i++) {
            try {
                JSONArray coordinates = JArray.getJSONObject(i).getJSONObject("loc").getJSONArray("coordinates");
                double lat = (Double) coordinates.get(0);
                double lon = (Double) coordinates.get(1);
                Log.v("Test", lat + "");
                LatLng l = new LatLng(lat, lon);

                Marker m = map.addMarker(new MarkerOptions().position(l).snippet(i + ""));
                markers.add(m);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            /*try {
                String pattern = "MM/dd/yyyy";
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                Date date = new Date(Long.parseLong(JArray.getJSONObject(i).getString("startTime")));
                sessions.add(JArray.getJSONObject(i).getString("course") + " " + format.format(date));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }
}

