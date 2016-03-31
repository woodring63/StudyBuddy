package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.Main.HubActivity;
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
        setContentView(R.layout.activity_user_courses);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        name = prefs.getString("name", "None");
        id = prefs.getString("id", "None");

        Button editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EditCourses.class);
                startActivity(i);
                finish();
            }
        });

        TextView nameText = (TextView) findViewById(R.id.coursesHeader);
        nameText.setText(name + "'s Courses");
        final String my_params = "/users/id/" + id;

        //Gets the user's courses
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
        //show courses in ListView
        courses = new String[jArray.length()];
        try {
            for (int i = 0; i < jArray.length(); i++) {
                courses[i] = jArray.getString(i);
            }
            ListView courseslist = (ListView) findViewById(R.id.coursesList);
            for(int i = 0; i < courses.length; i++){
                courses[i] = "- " + courses[i];
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,courses);
            adapter.setNotifyOnChange(true);
            courseslist.setAdapter(adapter);

        }
        catch(JSONException f){

        }


    }
    @Override
    public void onBackPressed(){
        Intent main = new Intent(getApplicationContext(), UserProfile.class);
        main.addCategory(Intent.CATEGORY_HOME);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main);
        finish();


    }

}
