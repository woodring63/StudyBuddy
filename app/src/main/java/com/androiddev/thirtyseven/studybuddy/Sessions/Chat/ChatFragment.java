package com.androiddev.thirtyseven.studybuddy.Sessions.Chat;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Sessions.Whiteboard.WhiteboardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class ChatFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
    }

}
