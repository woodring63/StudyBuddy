package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Main.HubActivity;

public class BuddyList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_buddies);

        final String[] names = {"Joey Elliot", "Evan Woodring", "Erika Clark", "Nathan Gilbert"};
        final int[] imgIds = {R.drawable.joey, R.drawable.evan, R.drawable.erika, R.drawable.nathan};

        BuddyListAdapter adapter = new BuddyListAdapter(this, names, imgIds);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(BuddyList.this, UserProfile.class);

                        String resultName = names[position];
                        int resultImgId = imgIds[position];
                        i.putExtra("userName", resultName);
                        i.putExtra("userImgId", resultImgId);

                        startActivity(i);
                    }
                }
        );

        setContentView(R.layout.activity_buddy_list);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(getApplicationContext(), HubActivity.class);
                startActivity(i);
            }
        });
    }

}
