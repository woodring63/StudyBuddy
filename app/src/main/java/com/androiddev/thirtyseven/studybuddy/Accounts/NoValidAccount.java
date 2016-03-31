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
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.Backend.User;
import com.androiddev.thirtyseven.studybuddy.Main.HubActivity;
import com.androiddev.thirtyseven.studybuddy.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class NoValidAccount extends AppCompatActivity {

    private MultiAutoCompleteTextView textView;
    private EditText nameText;
    private EditText majorText;
    private EditText bioText;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String email;
    private User user;
    private JSONObject j;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_valid_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        email = prefs.getString("email", "None");
        setSupportActionBar(toolbar);
        createAutoComplete();
        createButton();
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();


    }

    //creates the MultiAutoCompleteTextView for the user's courses

    protected void createAutoComplete() {
        textView = (MultiAutoCompleteTextView) findViewById(R.id.coursesText);
        textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        String[] courses = getResources().getStringArray(R.array.course_list);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, courses);
        textView.setAdapter(adapter);
    }

    protected void createButton() {
        Button button = (Button) findViewById(R.id.button);
        //get the entered text
        nameText = (EditText) findViewById(R.id.nameText);
        majorText = (EditText) findViewById(R.id.majorText);
        bioText = (EditText) findViewById(R.id.bioText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(textView.getText().toString().equals("")) &&!(nameText.getText().toString().equals("")) && !(majorText.getText().toString().equals("")) && !(bioText.getText().toString().equals(""))) {
                    Intent i = new Intent(getApplicationContext(), HubActivity.class);
                    //make a user object and set their info
                    user = new User();
                    user.setName(nameText.getText().toString());
                    user.setBio(bioText.getText().toString());
                    String[] myCourses = textView.getText().toString().split(",");
                    ArrayList<String> finalCourses = new ArrayList<String>();
                    for(int j = 0; j < myCourses.length; j++){
                        if(!myCourses[j].trim().isEmpty()){
                            myCourses[j] = myCourses[j].replaceAll("^\\s+", "");
                            finalCourses.add(myCourses[j]);
                        }
                    }
                    myCourses = finalCourses.toArray(new String[finalCourses.size()]);
                    user.setCourses(myCourses);
                    user.setMajor(majorText.getText().toString());
                    user.setUsername(email);

                    //Async task to add the user to the server

                    AsyncTask a = new AsyncTask<Object, Void, Void>() {


                        @Override
                        protected Void doInBackground(Object... params) {

                            ServerConnection s = new ServerConnection(user, "/users/newuser/");
                            s.run();
                            return null;

                        }


                    };
                    a.execute();

                    final String my_params = "/users/username/"+ email;

                    //Then, save all their info

                    AsyncTask a1 = new AsyncTask<Object, Void, Void>() {


                        @Override
                        protected Void doInBackground(Object... params) {

                            ServerConnection s = new ServerConnection(my_params);
                            j = s.run();

                            try {
                                Log.v("Login", j.getJSONObject("user").getString("name"));

                            } catch (Exception e) {
                                Log.v("Login", "NOPE");
                            }
                            try {
                                editor.putString("id", j.getJSONObject("user").getString("_id"));
                                editor.putString("name", j.getJSONObject("user").getString("name"));
                                editor.putString("bio", j.getJSONObject("user").getString("bio"));
                                editor.putString("major", j.getJSONObject("user").getString("major"));
                                editor.commit();

                            }
                            catch(Exception e){

                            }

                            return null;

                        }

                    };
                    a1.execute();



                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();


                }

                else{
                    Toast.makeText(getApplicationContext(), "Please Fill Out All Fields"
                            , Toast.LENGTH_LONG).show();
                }

            }
        });

    }


}
