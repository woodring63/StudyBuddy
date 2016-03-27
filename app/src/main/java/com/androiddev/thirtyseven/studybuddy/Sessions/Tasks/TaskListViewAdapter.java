package com.androiddev.thirtyseven.studybuddy.Sessions.Tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;

import com.androiddev.thirtyseven.studybuddy.R;

import java.util.ArrayList;

/**
 * Created by Joseph Elliott on 3/26/2016.
 */
public class TaskListViewAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> tasks = new ArrayList<>();

    public TaskListViewAdapter(Context context, int layoutResourceId, ArrayList<String> tasks) {
        super(context, layoutResourceId, tasks);
        this.context = context;
        this.tasks = tasks;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView != null) {
            return convertView;
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.item_task, null);

            CheckBox checkBox = (CheckBox) v.findViewById(R.id.item_task_check_box);
            EditText editText = (EditText) v.findViewById(R.id.item_task_edit_text);
            editText.setText(tasks.get(position));

            return v;
        }
    }

    public void clear() {
        tasks = new ArrayList<>();
    }

}
