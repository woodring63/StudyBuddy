package com.androiddev.thirtyseven.studybuddy.Sessions.Tasks;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Sessions.Whiteboard.WhiteboardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class TaskFragment extends Fragment {

    private ListView listView;
    private TaskListViewAdapter adapter;
    private Button btnAdd;
    private EditText etText;
    private ArrayList<Task> tasks;
    private TaskFragment thisTaskFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tasks, container, false);

        try {
            thisTaskFragment = this;
            listView = (ListView) rootView.findViewById(R.id.tasks_list_view);
            adapter = new TaskListViewAdapter(getContext(), R.layout.item_task, tasks);
            listView.setAdapter(adapter);
            etText = (EditText) rootView.findViewById(R.id.tasks_add_edit_text);
            btnAdd = (Button) rootView.findViewById(R.id.tasks_add_button);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etText.getText().toString().length() == 0) {
                        Snackbar.make(thisTaskFragment.getView(), "Oops! You tried to make a blank task."
                                , Snackbar.LENGTH_SHORT).show();
                    } else {
                        Task task = new Task(etText.getText().toString(), false
                                , new Date(Calendar.getInstance().getTimeInMillis()));
                        tasks.add(task);
                        adapter.notifyDataSetChanged();
                        etText.setText("");
                    }
                }
            });
        } catch (NullPointerException e) {
            // rip in pieces
        }

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);

        // TODO load tasks from server for the session
        tasks = new ArrayList<>();
    }

}

class TaskAsync extends AsyncTask<Void, Void, JSONArray> {

    private String id;

    public TaskAsync(String sessionid) {
        this.id = sessionid;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        ServerConnection server = new ServerConnection("/tasks/" + id);
        JSONObject json = server.run();
        JSONArray arr = null;
        try {
            arr = json.getJSONArray("tasks");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr;
    }
}