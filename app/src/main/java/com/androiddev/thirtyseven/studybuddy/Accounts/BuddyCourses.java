package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.R;

public class BuddyCourses extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_courses);

        final Buddy buddy = getIntent().getParcelableExtra("buddy");

        TextView coursesList = (TextView) findViewById(R.id.coursesList);
        TextView name = (TextView) findViewById(R.id.coursesHeader);
        name.setText(buddy.getName() + "'s Courses");
        String[] buddyCourses = buddy.getCourses();
        for(int i = 0; i < buddyCourses.length; i++){
                coursesList.append("\n" + buddyCourses[i] + "\n");
        }
    }

}
