package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;
import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.Main.NavBase;
import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Main.HubActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuddyList extends NavBase {
    private ArrayList<Buddy> buddies;
    private BuddyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_list);

        buddies = new ArrayList<Buddy>();
        BuddieAsync async = new BuddieAsync(null, "/buddies/56c91d1d28c61d543b355647", buddies);
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
                        Intent i = new Intent(getApplicationContext(), UserProfile.class);
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
