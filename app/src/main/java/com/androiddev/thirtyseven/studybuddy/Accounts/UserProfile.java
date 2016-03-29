package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.Main.NavBase;
import com.androiddev.thirtyseven.studybuddy.R;

public class UserProfile extends NavBase {

    private SharedPreferences prefs;
    private String id;
    private String bio;
    private String name;
    private String major;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        id = prefs.getString("id", "None");
        bio = prefs.getString("bio", "None");
        name = prefs.getString("name", "None");
        major = prefs.getString("major", "None");
        TextView userNameView = (TextView) findViewById(R.id.userNameView);
        TextView userMajorView = (TextView) findViewById(R.id.userMajorView);
        TextView userBioView = (TextView) findViewById(R.id.userAboutMeText);

        userNameView.setText(name);
        userBioView.setText(bio);
        userMajorView.setText(major);

        }

        }
