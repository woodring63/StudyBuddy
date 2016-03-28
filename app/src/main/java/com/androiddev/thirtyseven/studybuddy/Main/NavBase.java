package com.androiddev.thirtyseven.studybuddy.Main;
/**
 * Created By Evan
 * This class is designed for modularity of the navigation drawer
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.Accounts.BuddyList;
import com.androiddev.thirtyseven.studybuddy.Accounts.LoginActivity;
import com.androiddev.thirtyseven.studybuddy.Accounts.UserProfile;
import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Sessions.SessionActivity;

import java.util.ArrayList;

public class NavBase extends AppCompatActivity {
    private static String TAG = NavBase.class.getSimpleName();

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private SharedPreferences prefs;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();


    //We override the setContentView method so that each subclass can call onCreate AND onCreateDrawer
    @Override
    public void setContentView(@LayoutRes int layoutResID){
        super.setContentView(layoutResID);
        onCreateDrawer();
    }

    //This method will actually create the "drawer"
    protected void onCreateDrawer() {
        //We get the toolbar so that we can add the "hamburger" icon
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //SHARED PREFERENCE EXAMPLE
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String id = prefs.getString("id", "None");
        Log.v("HEY", id);


        //Added 3 activities, more to come/will change
        mNavItems.add(new NavItem("Buddies", "View Your Buddies", R.drawable.ic_accessibility_black_24dp));
        mNavItems.add(new NavItem("Profile", "View Your Profile", R.drawable.ic_play_light));
        mNavItems.add(new NavItem("Sessions", "View a Session", R.drawable.ic_android_black_24dp));
        mNavItems.add(new NavItem("Log Out", "Log Out of App", R.drawable.ic_3d_rotation_black_24dp));

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item Listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //Selects Item from drawer
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });
        //Drawer is now toggleable
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //Adds "hamburger" button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates menu; adds items to the action bar if present.
        getMenuInflater().inflate(R.menu.menu_hub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Selects item from drawer, starting relevant activity depending on which button
    //is pressed
    private void selectItemFromDrawer(int position) {

        Intent i;
        switch(position){
            case 0:
                i = new Intent(getApplicationContext(), BuddyList.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(getApplicationContext(), UserProfile.class);
                startActivity(i);
                break;
            case 2:
                i = new Intent(getApplicationContext(), SessionActivity.class);
                startActivity(i);
                break;
            case 3:
                i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                break;
        }



        mDrawerList.setItemChecked(position, true);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerPane);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
}

//Drawer item, which has a title, subtitle, and image
class NavItem {
    String mTitle;
    String mSubtitle;
    int mIcon;

    public NavItem(String title, String subtitle, int icon) {
        mTitle = title;
        mSubtitle = subtitle;
        mIcon = icon;
    }
}

//Adapter needed for drawer
class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<NavItem> mNavItems;

    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }
    //Methods must be overriden
    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            //Inflates all drawer items
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView subtitleView = (TextView) view.findViewById(R.id.subtitle);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        //Sets up relevant information
        titleView.setText( mNavItems.get(position).mTitle );
        subtitleView.setText( mNavItems.get(position).mSubtitle );
        iconView.setImageResource(mNavItems.get(position).mIcon);

        return view;
    }

}

