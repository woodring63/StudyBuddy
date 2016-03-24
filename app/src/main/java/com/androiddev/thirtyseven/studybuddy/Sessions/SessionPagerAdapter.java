package com.androiddev.thirtyseven.studybuddy.Sessions;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class SessionPagerAdapter extends FragmentPagerAdapter {

    private static final int numOfTabs = 3;

    private DocumentFragment documentFragment;
    private TaskFragment taskFragment;
    private WhiteboardFragment whiteboardFragment;

    public SessionPagerAdapter(FragmentManager fm) {
        super(fm);
        documentFragment = new DocumentFragment();
        taskFragment = new TaskFragment();
        whiteboardFragment = new WhiteboardFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return documentFragment;
            case 1:
                return taskFragment;
            case 2:
                return whiteboardFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
