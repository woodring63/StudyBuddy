package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;
import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.Main.NavBase;
import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Main.HubActivity;

import java.util.ArrayList;

public class BuddyList extends NavBase {
    private ArrayList<Buddy> buddies = new ArrayList<Buddy>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_list);

        BuddyAsync async = new BuddyAsync(null, "/buddies/56c91d1d28c61d543b355647", buddies);
        async.execute();

        BuddyListAdapter adapter = new BuddyListAdapter(this, buddies);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getApplicationContext(), UserProfile.class);
                        Parcel parcel = Parcel.obtain();
                        Buddy bud = buddies.get(position);
                        bud.writeToParcel(parcel, 0);

                        i.putExtra("buddy", bud);

                        startActivity(i);
                    }
                }
        );
    }


}
