package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androiddev.thirtyseven.studybuddy.R;

public class UserCourses extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_courses);

        final Buddy buddy = getIntent().getParcelableExtra("buddy");

        ListView coursesList = (ListView) findViewById(R.id.coursesList);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, buddy.getCourses());
        coursesList.setAdapter(adapter);

    }

}
