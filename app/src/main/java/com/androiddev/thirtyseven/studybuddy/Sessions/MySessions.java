package com.androiddev.thirtyseven.studybuddy.Sessions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.Main.NavBase;
import com.androiddev.thirtyseven.studybuddy.Main.SessionInfo;
import com.androiddev.thirtyseven.studybuddy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySessions extends NavBase {

    private ListView sessions;
    private ArrayList<String> mySessions;
    private SharedPreferences prefs;
    private String email;
    private JSONObject j;
    private JSONArray JArray;
    private JSONArray joinedSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sessions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        email = prefs.getString("email", "None");
        String name = prefs.getString("name", "None");
        mySessions = new ArrayList<String>();


        final String my_params = "/users/username/" + email;
        AsyncTask a = new AsyncTask<Object, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(Object... params) {
                ServerConnection s = new ServerConnection(my_params);
                j = s.run();


                return j;
            }

        };
        try {
            j = (JSONObject) a.execute().get();
        } catch (Exception e) {

        }
        joinedSessions = null;
        JSONArray createdSessions = null;
        JArray = new JSONArray();

        try {
            joinedSessions = j.getJSONArray("joinedSessions");
            createdSessions = j.getJSONArray("createdSessions");
            String pattern = "MM/dd/yyyy";
            SimpleDateFormat format = new SimpleDateFormat(pattern);



            /*for (int j = 0; j < createdSessions.length(); j++) {
                Date date = new Date(Long.parseLong(createdSessions.getJSONObject(j).getString("startTime")));

                mySessions.add(createdSessions.getJSONObject(j).getString("course") + " " + format.format(date));
                JArray.put(createdSessions.getJSONObject(j));

            }*/
            for (int i = 0; i < joinedSessions.length(); i++) {
                Date date = new Date(Long.parseLong(joinedSessions.getJSONObject(i).getString("startTime")));

                mySessions.add(joinedSessions.getJSONObject(i).getString("course") + " " + format.format(date));
                JArray.put(joinedSessions.getJSONObject(i));

            }

        } catch (Exception e) {

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mySessions);
        adapter.setNotifyOnChange(true);


        sessions = (ListView) findViewById(R.id.sessionListView);

        sessions.setAdapter(adapter);

        sessions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), SessionActivity.class);
                Bundle bundle = new Bundle();
                try {
                    if (joinedSessions != null) {
                        bundle.putSerializable("session", JArray.getJSONObject(position).toString());
                        i.putExtras(bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(i);
            }
        });
        TextView t = (TextView) findViewById(R.id.header);
        t.setText(name + "'s Sessions");
    }


}
