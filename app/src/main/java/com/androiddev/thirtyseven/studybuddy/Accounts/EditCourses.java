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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.Main.HubActivity;
import com.androiddev.thirtyseven.studybuddy.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class EditCourses extends AppCompatActivity {
    private AutoCompleteTextView textView;
    private SharedPreferences prefs;
    private String id;
    private JSONObject j;
    private JSONObject jObj;
    private String[] allCourses;
    private ArrayList<String> userCourses;
    private JSONObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_courses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        id = prefs.getString("id", "None");
        textView = (AutoCompleteTextView) findViewById(R.id.editText);
        createAutoComplete();
        userCourses = new ArrayList<String>();
        final String my_params = "/users/id/" + id;
        AsyncTask a = new AsyncTask<Object, Void, Void>() {


            @Override
            protected Void doInBackground(Object... params) {
                ServerConnection s = new ServerConnection(my_params);
                j = s.run();
                try {
                    for (int i = 0; i < j.getJSONObject("user").getJSONArray("courses").length(); i++) {
                        userCourses.add(j.getJSONObject("user").getJSONArray("courses").get(i).toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;

            }
        };
        a.execute();


        createButtons();


    }

    //Creates the autocompletetextview
    protected void createAutoComplete() {
        allCourses = getResources().getStringArray(R.array.course_list);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, allCourses);
        textView.setAdapter(adapter);
    }

    protected void createButtons() {

        //Button for adding a course
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String course = textView.getText().toString();
                if (userCourses.contains(course)) {
                    Toast.makeText(getApplicationContext(), "You are enrolled in that course.", Toast.LENGTH_LONG).show();
                } else {
                    if (Arrays.asList(allCourses).contains(course)) {
                        String[] courses = {course};
                        jObj = new JSONObject();
                        try {
                            jObj.put("courses", new JSONArray(courses));
                        } catch (Exception e) {

                        }

                        final String my_params = "/users/addcourses/" + id;

                        AsyncTask a = new AsyncTask<Object, Void, Void>() {


                            @Override
                            protected Void doInBackground(Object... params) {

                                ServerConnection s = new ServerConnection(jObj, "PUT", my_params);
                                j = s.run();
                                return null;

                            }

                        };
                        a.execute();
                        Intent i = new Intent(getApplicationContext(), UserCourses.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(), "That is not a course offered at ISU", Toast.LENGTH_LONG).show();
                    }
                }


            }

        });

        //Button for removing a course
        Button removeButton = (Button) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String course = textView.getText().toString();
                if (!userCourses.contains(course)) {
                    Toast.makeText(getApplicationContext(), "You are not enrolled in that course.", Toast.LENGTH_LONG).show();
                } else {
                    if (Arrays.asList(allCourses).contains(course)) {
                        String[] courses = {course};
                        jObj = new JSONObject();
                        try {
                            jObj.put("courses", new JSONArray(courses));
                        } catch (Exception e) {

                        }

                        final String my_params = "/users/deletecourses/" + id;
                        Log.v("MYTEST", id);

                        AsyncTask a = new AsyncTask<Object, Void, Void>() {


                            @Override
                            protected Void doInBackground(Object... params) {

                                ServerConnection s = new ServerConnection(jObj, "PUT", my_params);
                                j = s.run();
                                return null;

                            }

                        };
                        a.execute();
                        Intent i = new Intent(getApplicationContext(), UserCourses.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(), "That is not a course offered at ISU", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(getApplicationContext(), UserProfile.class);
        main.addCategory(Intent.CATEGORY_HOME);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main);
        finish();


    }

}
