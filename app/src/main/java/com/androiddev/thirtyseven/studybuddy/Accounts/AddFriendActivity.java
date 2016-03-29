package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.R;

import org.json.JSONObject;

public class AddFriendActivity extends AppCompatActivity {

    private JSONObject j;
    private JSONObject j2;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText usernameField = (EditText) (findViewById(R.id.editText));
        String friendName = usernameField.getText().toString();
        final String my_params = "/users/username/"+ friendName;
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
        }
        catch(Exception e){

        }
        final String my_params2 = "/users/updatefriends;

        a = new AsyncTask<Object, Void, Void>() {


            @Override
            protected Void doInBackground(Object... params) {

                ServerConnection s = new ServerConnection(my_params2);
                j2 = s.run();

                return null;
            }

        };
        try {
            j = (JSONObject) a.execute().get();
        }
        catch(Exception e){

        }


    }

}
