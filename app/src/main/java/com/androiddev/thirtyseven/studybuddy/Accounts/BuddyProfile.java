package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.R;

public class BuddyProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_profile);

        final Buddy buddy = getIntent().getParcelableExtra("buddy");

        TextView userNameView = (TextView) findViewById(R.id.userNameView);
        TextView userMajorView = (TextView) findViewById(R.id.userMajorView);
        TextView userBioView = (TextView) findViewById(R.id.userAboutMeText);

        userNameView.setText(buddy.getName());
        userMajorView.setText(buddy.getMajor());
        userBioView.setText(buddy.getBio());

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