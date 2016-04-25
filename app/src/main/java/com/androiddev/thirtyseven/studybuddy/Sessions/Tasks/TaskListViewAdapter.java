package com.androiddev.thirtyseven.studybuddy.Sessions.Tasks;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.R;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Joseph Elliott on 3/26/2016.
 */
public class TaskListViewAdapter extends ArrayAdapter<Task> {

    private Context context;
    private ArrayList<Task> tasks = new ArrayList<>();
    private Socket mSocket;
    private String sessionId;

    public TaskListViewAdapter(Context context, int layoutResourceId, ArrayList<Task> tasks, Socket mSocket, String sessionId) {
        super(context, layoutResourceId, tasks);
        this.context = context;
        this.tasks = tasks;
        this.mSocket = mSocket;
        this.sessionId = sessionId;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.item_task, null);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_task_check_box);
        final int finalPosition = position;
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tasks.get(finalPosition).getDone()) {
                    markTaskAsComplete(view, tasks.get(finalPosition));
                } else {
                    markTaskAsIncomplete(view, tasks.get(finalPosition));
                }
                tasks.get(finalPosition).toggleDone();
                try {
                    mSocket.emit("update task",new JSONObject("{session : " + sessionId +
                            ", startTime :" + tasks.get(finalPosition).getCreatedDate().getTime() +
                            ",completed:"+tasks.get(finalPosition).getDone()+"}"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                notifyDataSetChanged();
            }
        });

        final EditText editText = (EditText) view.findViewById(R.id.item_task_edit_text);
        editText.setText(tasks.get(position).getTask());

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                tasks.get(finalPosition).setTask(editText.getText().toString());
                return false;
            }
        });

        if (tasks.get(position).getDone()) {
            markTaskAsComplete(view, tasks.get(position));
            checkBox.setChecked(true);
        } else {
            markTaskAsIncomplete(view, tasks.get(position));
        }

        return view;
}

    public void clear() {
        tasks = new ArrayList<>();
    }


    private void markTaskAsComplete(View view, Task task) {
        view.setAlpha(0.05f);
        try {
            EditText et = (EditText) view.findViewById(R.id.item_task_edit_text);
            et.setPaintFlags(et.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } catch (Exception e) {

        }
        Calendar cal = Calendar.getInstance();
        task.setCheckedDate(new Date(cal.getTimeInMillis()));
        cal.add(Calendar.SECOND, 10); // TIME UNTIL IT'S DELETED
        task.setTerminationDate(new Date(cal.getTimeInMillis()));
    }

    private void markTaskAsIncomplete(View view, Task task) {
        view.setAlpha(1f);
        try {
            EditText et = (EditText) view.findViewById(R.id.item_task_edit_text);
            et.setPaintFlags(et.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        } catch (Exception e) {

        }
        task.setCheckedDate(null);
        task.setTerminationDate(null);
    }

    @Override
    public void notifyDataSetChanged() {
        // sort the tasks by alphabet / done or not done
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTerminationDate() != null) {
                if (tasks.get(i).getTerminationDate().getTime() < Calendar.getInstance().getTimeInMillis()
                        && tasks.get(i).getDone()) {
                    try {

                        mSocket.emit("remove task", new JSONObject("{startTime:" + tasks.get(i).getCreatedDate().getTime() + ",session:" + sessionId + "}"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    tasks.remove(i);
                }
            }
        }
        Collections.sort(tasks);
        super.notifyDataSetChanged();
    }

}
