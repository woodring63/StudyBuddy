package com.androiddev.thirtyseven.studybuddy.Sessions.Tasks;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
                        try {
                            if(!data.getString("session").equals(sessionId))
                            {
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    private Emitter.Listener onUpdateTask = new Emitter.Listener() {

        //For when an the android recieves a message from
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject data = (JSONObject) args[0];
                        Log.e("data", data.toString());
                        try {
                            if(!data.getString("session").equals(sessionId))
                            {
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // add the message to view
                        updateTask(data,true);
                    }
                });
            }catch(NullPointerException e)
            {
                Log.d("onUpdateTask", "Null pointer exception (Task) getActivity.runOnUiThread.");
            }
        }
    };

    private Emitter.Listener onRemoveTask = new Emitter.Listener() {

        //For when an the android recieves a message from
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject data = (JSONObject) args[0];
                        try {
                            if(!data.getString("session").equals(sessionId))
                            {
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // add the message to view
                        updateTask(data,false);
                    }
                });
            }catch(NullPointerException e)
            {
                Log.d("onUpdateTask", "Null pointer exception (Task) getActivity.runOnUiThread.");
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tasks, container, false);

        try {
            thisTaskFragment = this;
            listView = (ListView) rootView.findViewById(R.id.tasks_list_view);
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
                                    "session:\""+sessionId+"\"," +
                                    "startTime:"+Calendar.getInstance().getTimeInMillis()+"}");
                            mSocket.emit("add task", json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        etText.setText("");
                    }
                }
            });
        } catch (NullPointerException e) {
            // rip in pieces
            e.printStackTrace();
        }
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        tasks = new ArrayList<>();
        // TODO load tasks from server for the session
        sessionId = ((SessionActivity) getActivity()).getSessionId();
        mSocket.on("new task", onNewTask);
        mSocket.on("update task", onUpdateTask);
        mSocket.on("remove task", onRemoveTask);
        mSocket.connect();


        adapter = new TaskListViewAdapter(getContext(), R.layout.item_task, tasks, mSocket,sessionId);
        TaskAsync async = new TaskAsync(sessionId,tasks,adapter);
        async.execute();
    }

    private void addTask(JSONObject jTask)
    {

//        Task task = new Task(etText.getText().toString(), false
//                , new Date(Calendar.getInstance().getTimeInMillis()));
        Task task = null;
        try {
            task = new Task(jTask.getString("name"), false
                    , new Date(jTask.getLong("startTime")),null);
            tasks.add(task);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    private synchronized void updateTask(JSONObject json, boolean update)
    {
        if(update) {
            try {
                long startTime = json.getLong("startTime");
                for (int i = 0; i < tasks.size(); i++) {
                    Task task = tasks.get(i);
                    if (task.getCreatedDate().getTime() == startTime) {
                        task.setDone(json.getBoolean("completed"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else
        {
            try {
                long startTime = json.getLong("startTime");
                for(Task task : tasks)
                {
                    if(task.getCreatedDate().getTime() == startTime)
                    {
                        adapter.remove(task);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("new task", onNewTask);
    }
}

class TaskAsync extends AsyncTask<Void, Void, JSONArray> {

    private String id;
    private ArrayList<Task> tasks;
    private ArrayAdapter adapter;

    public TaskAsync(String sessionid, ArrayList<Task> tasks,ArrayAdapter adapter) {
        this.id = sessionid;
        this.tasks = tasks;
        this.adapter = adapter;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        ServerConnection server = new ServerConnection("/sessions/tasks/" + id);
        JSONObject json = server.run();
        JSONArray arr = null;
        try {
            if(json!=null)
            arr = json.getJSONArray("tasks");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr;
    }

    @Override
    protected void onPostExecute(JSONArray params)
    {
        if(params == null)
        {
            return;
        }
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
        adapter.notifyDataSetChanged();

    }
}