package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.Main.NavBase;
import com.androiddev.thirtyseven.studybuddy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuddyList extends NavBase {
    private ArrayList<Buddy> buddies;
    private BuddyListAdapter adapter;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_list);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String id = prefs.getString("id", "None");
        buddies = new ArrayList<Buddy>();
        BuddieAsync async = new BuddieAsync(null, "/users/buddies/"+id, buddies);
        Log.v("HEY2", "users/buddies/"+id);

        async.execute();

        adapter = new BuddyListAdapter(this, buddies);
        adapter.setNotifyOnChange(true);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getApplicationContext(), BuddyProfile.class);
                        Parcel parcel = Parcel.obtain();
                        Buddy bud = buddies.get(position);
                        bud.writeToParcel(parcel, 0);

                        i.putExtra("buddy", bud);

                        startActivity(i);
                    }
                }
        );
    }

    class BuddieAsync extends AsyncTask<Void, Void, ArrayList<Buddy>> {

        ServerConnection server;

        public BuddieAsync(String method, String params, ArrayList<Buddy> buddies)
        {
            server = new ServerConnection(params);
        }

        @Override
        protected ArrayList<Buddy> doInBackground(Void... params) {
            //ArrayList<Buddy> buddies = new ArrayList<Buddy>();
            try {
                JSONObject json = server.run();
                if(json != null) {
                    JSONArray jerry = (JSONArray) json.get("buddies");

                    Buddy temp;
                    for (int i = 0; i < jerry.length(); ++i) {
                        JSONObject bud = jerry.getJSONObject(i);
                        temp = new Buddy();
                        System.out.println(bud);
                        temp.setBuddies((String[]) bud.get("buddies").toString().replace("},{", "~").split("~"));
                        temp.setCourses((String[]) bud.get("courses").toString().replace("},{", "~").split("~"));
                        temp.setId((String) bud.get("_id"));
                        temp.setMajor((String) bud.get("major"));
                        temp.setName((String) bud.get("name"));
                        temp.setSessions((String[]) bud.get("sessions").toString().replace("},{", "~").split("~"));
                        temp.setUsername((String) bud.get("username"));
                        temp.setBio((String) bud.get("bio"));
                        buddies.add(temp);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
           //adapter.notifyDataSetChanged();
            return buddies;
        }

        public void onPostExecute(ArrayList buddies)
        {
            adapter.notifyDataSetChanged();

        }

    }


}
