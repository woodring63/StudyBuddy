package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.os.AsyncTask;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by enclark on 2/28/2016.
 */
public class BuddyAsync extends AsyncTask<Void, Void, ArrayList<Buddy>> {

    ServerConnection server;

    public BuddyAsync(String method, String params, ArrayList<Buddy> buddies)
    {
        server = new ServerConnection(params);
    }

    @Override
    protected ArrayList<Buddy> doInBackground(Void... params) {
        ArrayList<Buddy> buddies = new ArrayList<Buddy>();
        try {
            JSONObject json = server.run();
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
                temp.setSessions((String[]) bud.get("sessions").toString().replace("},{","~").split("~"));
                temp.setUsername((String) bud.get("username"));
                buddies.add(temp);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return buddies;
    }
}
