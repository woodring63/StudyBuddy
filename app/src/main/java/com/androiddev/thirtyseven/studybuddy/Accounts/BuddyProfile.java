package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.Main.NavBase;
import com.androiddev.thirtyseven.studybuddy.R;

import org.json.JSONObject;

public class BuddyProfile extends NavBase {
    private JSONObject j;
    private SharedPreferences prefs;
    private String id;
    private String budId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_profile);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        id = prefs.getString("id", "None");

        final Buddy buddy = getIntent().getParcelableExtra("buddy");
        budId = buddy.getId();

        TextView userNameView = (TextView) findViewById(R.id.userNameView);
        TextView userMajorView = (TextView) findViewById(R.id.userMajorView);
        TextView userBioView = (TextView) findViewById(R.id.userAboutMeText);

        userNameView.setText(buddy.getName());
        userMajorView.setText(buddy.getMajor());
        userBioView.setText(buddy.getBio());


        Button coursesButton = (Button) findViewById(R.id.coursesButton);
        coursesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BuddyCourses.class);
                Parcel parcel = Parcel.obtain();
                buddy.writeToParcel(parcel, 0);

                i.putExtra("buddy", buddy);

                startActivity(i);
            }
        });

        Button endFriend = (Button) findViewById(R.id.endFriend);
        endFriend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {





                final String my_params = "/users/deletefriend/" + id + "/" + budId;
                AsyncTask a = new AsyncTask<Object, Void, Void>() {

                    @Override
                    protected Void doInBackground(Object... params) {
                        ServerConnection s = new ServerConnection(null, "PUT", my_params);
                        j = s.run();
                        return null;
                    }
                };
                a.execute();
                Toast.makeText(getApplicationContext(), "Friendship Over", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), BuddyList.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }
}


