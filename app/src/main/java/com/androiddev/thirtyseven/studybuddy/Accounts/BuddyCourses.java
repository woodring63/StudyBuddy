package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.Main.NavBase;
import com.androiddev.thirtyseven.studybuddy.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BuddyCourses extends NavBase {

    private String[] courses;
    ListView courseslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_courses);
        final Buddy buddy = getIntent().getParcelableExtra("buddy");

        /*TextView coursesList = (TextView) findViewById(R.id.coursesList);

        for(int i = 0; i < buddyCourses.length; i++){
                coursesList.append("\n" + buddyCourses[i] + "\n");
        }*/
        TextView name = (TextView) findViewById(R.id.coursesHeader);
        name.setText(buddy.getName() + "'s Courses");
        courses = buddy.getCourses();
        for(int i = 0; i < courses.length; i++){
            courses[i] = "- " + courses[i];
        }
        //Listview adapter of all of the courses the Buddy is in
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,courses);
        adapter.setNotifyOnChange(true);
        courseslist = (ListView) findViewById(R.id.coursesList);
        courseslist.setAdapter(adapter);
    }

}
