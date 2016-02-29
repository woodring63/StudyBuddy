package com.androiddev.thirtyseven.studybuddy.Sessions;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.androiddev.thirtyseven.studybuddy.R;

/**
 * Created by Joseph Elliott on 2/2/2016.
 */
public class SessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        // Initialize toolbar stuff
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize ViewPager stuff
        final NonSwipeViewPager viewPager = (NonSwipeViewPager) findViewById(R.id.session_pager);
        final SessionPagerAdapter adapter = new SessionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // Initialize tab stuff
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Document"));
        tabLayout.addTab(tabLayout.newTab().setText("Tasks"));
        tabLayout.addTab(tabLayout.newTab().setText("Whiteboard"));

        // Enable moving between fragments with the tabs
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Unused
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Unused
            }
        });

        // Initialize the tabs to be in the center position
        try {
            viewPager.setCurrentItem(tabLayout.getTabCount() / 2);
            tabLayout.getTabAt(tabLayout.getTabCount() / 2).select();
        } catch (NullPointerException e) {
            // rip in pieces
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
