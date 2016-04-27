package com.androiddev.thirtyseven.studybuddy.Sessions;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.androiddev.thirtyseven.studybuddy.Sessions.Chat.ChatFragment;
import com.androiddev.thirtyseven.studybuddy.Sessions.Description.DescriptionFragment;
import com.androiddev.thirtyseven.studybuddy.Sessions.Document.DocumentFragment;
import com.androiddev.thirtyseven.studybuddy.Sessions.Tasks.TaskFragment;
import com.androiddev.thirtyseven.studybuddy.Sessions.Whiteboard.WhiteboardFragment;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class SessionPagerAdapter extends FragmentPagerAdapter {

    private static final int numOfTabs = 4;

    private DocumentFragment documentFragment;
    private TaskFragment taskFragment;
    private WhiteboardFragment whiteboardFragment;
    private DescriptionFragment descriptionFragment;
    private ChatFragment chatFragment;

    public SessionPagerAdapter(FragmentManager fm) {
        super(fm);
        //documentFragment = new DocumentFragment();
        taskFragment = new TaskFragment();
        whiteboardFragment = new WhiteboardFragment();
        descriptionFragment = new DescriptionFragment();
        chatFragment = new ChatFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
//            case 0:
//                return documentFragment;
//            case 1:
//                return taskFragment;
//            case 2:
//                return whiteboardFragment;
//            case 3:
//                return chatFragment;
//            case 4:
//                return descriptionFragment;
            case 0:
                return taskFragment;
            case 1:
                return whiteboardFragment;
            case 2:
                return chatFragment;
            case 3:
                return descriptionFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
