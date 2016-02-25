package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.androiddev.thirtyseven.studybuddy.Main.NavBase;
import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Main.HubActivity;

public class BuddyList extends NavBase {
    private final String[] names = {"Joey Elliot", "Evan Woodring", "Erika Clark", "Nathan Gilbert"};
    private final int[] imgIds = {R.drawable.joey, R.drawable.evan, R.drawable.erika, R.drawable.nathan};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_list);




        BuddyListAdapter adapter = new BuddyListAdapter(this, names, imgIds);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getApplicationContext(), UserProfile.class);

                        String resultName = names[position];
                        int resultImgId = imgIds[position];
                        i.putExtra("userName", resultName);
                        i.putExtra("userImgId", resultImgId);

                        startActivity(i);
                    }
                }
        );
    }


}
