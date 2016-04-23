package com.androiddev.thirtyseven.studybuddy.Sessions.Tasks;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Sessions.SessionActivity;
import com.androiddev.thirtyseven.studybuddy.Sessions.Whiteboard.WhiteboardView;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
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
    private String sessionId;
    private Socket mSocket;
    {
        try {
            //Connects to a socket on the server
            mSocket = IO.socket(ServerConnection.IP + ":8000");
        } catch (URISyntaxException e) {}
    }

    private Emitter.Listener onNewTask = new Emitter.Listener() {

        //For when an the android recieves a message from
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject data = (JSONObject) args[0];
                        Log.e("data", data.toString());
                        // add the message to view
                        addTask(data);
                    }
                });
            }catch(NullPointerException e)
            {
                Log.d("onNewTask", "Null pointer exception (Task) getActivity.runOnUiThread.");
            }
        }
    };


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
                        try {
                            JSONObject json = new JSONObject("{name:\" " +etText.getText().toString() +"\"," +
                                    "completed:\""+false+"\"," +
                                    "session:\""+sessionId+"\"" +
                                    "startTime:"+Calendar.getInstance().getTimeInMillis()+"}");
                            Log.e("senddata",json.toString());
                            mSocket.emit("new message", json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        tasks = new ArrayList<>();
        // TODO load tasks from server for the session
        sessionId = ((SessionActivity) getActivity()).getSessionId();
        mSocket.on("message", onNewTask);
        mSocket.connect();

        TaskAsync async = new TaskAsync(sessionId,tasks);
        async.execute();
    }

    private void addTask(JSONObject jTask)
    {

//        Task task = new Task(etText.getText().toString(), false
//                , new Date(Calendar.getInstance().getTimeInMillis()));
        Task task = null;
        try {
            task = new Task(jTask.getString("name"), false
                    , new Date(jTask.getLong("startTime")),jTask.getString("_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tasks.add(task);
        adapter.notifyDataSetChanged();
    }

}

class TaskAsync extends AsyncTask<Void, Void, JSONArray> {

    private String id;
    private ArrayList<Task> tasks;

    public TaskAsync(String sessionid, ArrayList<Task> tasks) {
        this.id = sessionid;
        this.tasks = tasks;
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

    @Override
    protected void onPostExecute(JSONArray params)
    {
        for(int i = 0; i < params.length(); i++)
        {
            try {
                JSONObject obj = params.getJSONObject(i);
                Date start = new Date(obj.getLong("startTime"));
                Task task = new Task(obj.getString("name"),obj.getBoolean("completed"),start,obj.getString("_id"));
                tasks.add(task);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}