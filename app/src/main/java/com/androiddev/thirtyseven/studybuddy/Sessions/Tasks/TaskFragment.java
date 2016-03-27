package com.androiddev.thirtyseven.studybuddy.Sessions.Tasks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Sessions.Whiteboard.WhiteboardView;

import java.util.ArrayList;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class TaskFragment extends Fragment {

    private ListView listView;
    private TaskListViewAdapter adapter;
    private ArrayList<String> tasks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tasks, container, false);

        try {
            listView = (ListView) rootView.findViewById(R.id.tasks_list_view);
            adapter = new TaskListViewAdapter(getContext(), R.layout.item_task, tasks);
            listView.setAdapter(adapter);
        } catch (NullPointerException e) {
            // rip in pieces
        }

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        // TODO load tasks from server for the session
        // Here are some dummy tasks
        tasks = new ArrayList<>();
        tasks.add("get a puppy");
        tasks.add("get a kitty");
        tasks.add("potato potato potato potato");
    }

}
