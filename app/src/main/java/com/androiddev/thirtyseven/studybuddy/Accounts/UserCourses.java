package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserCourses extends AppCompatActivity {

    private SharedPreferences prefs;
    private String name;
    private String id;
    private String[] courses;
    private JSONObject j;
    private JSONArray jArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_courses);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        name = prefs.getString("name", "None");
        id = prefs.getString("id", "None");
        TextView coursesList = (TextView) findViewById(R.id.coursesList);
        TextView nameText = (TextView) findViewById(R.id.coursesHeader);
        nameText.setText(name + "'s Courses");
        final String my_params = "/users/id/" + id;
        AsyncTask a = new AsyncTask<Object, Void, JSONArray>() {


            @Override
            protected JSONArray doInBackground(Object... params) {

                ServerConnection s = new ServerConnection(my_params);
                j = s.run();

                try {
                    Log.v("Login", j.getJSONObject("user").getString("name"));
                    jArray = j.getJSONObject("user").getJSONArray("courses");

                } catch (Exception e) {
                    Log.v("Login", "NOPE");
                }
                return jArray;

            }


        };
        try{
            jArray = (JSONArray) a.execute().get();
        }
        catch(Exception e){

        }
        courses = new String[jArray.length()];
        try {
            for (int i = 0; i < jArray.length(); i++) {
                courses[i] = jArray.getString(i);
            }
            for (int i = 0; i < courses.length; i++) {
                coursesList.append("\n" + courses[i] + "\n");
            }
        }
        catch(JSONException f){

        }


    }

}
