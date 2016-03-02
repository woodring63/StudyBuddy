package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        final Buddy buddy = getIntent().getParcelableExtra("buddy");

        TextView userNameView = (TextView) findViewById(R.id.userNameView);
        TextView userMajorView = (TextView) findViewById(R.id.userMajorView);

        userNameView.setText(buddy.getName());
        userMajorView.setText(buddy.getMajor());

        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BuddyList.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        Button coursesButton = (Button) findViewById(R.id.coursesButton);
        coursesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), UserCourses.class);
                Parcel parcel = Parcel.obtain();
                buddy.writeToParcel(parcel, 0);

                i.putExtra("buddy", buddy);

                startActivity(i);
            }
        });
    }


}