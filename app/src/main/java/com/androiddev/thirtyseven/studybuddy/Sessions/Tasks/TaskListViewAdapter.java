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
public class TaskListViewAdapter extends ArrayAdapter<Task> {

    private Context context;
    private ArrayList<Task> tasks = new ArrayList<>();

    public TaskListViewAdapter(Context context, int layoutResourceId, ArrayList<Task> tasks) {
        super(context, layoutResourceId, tasks);
        this.context = context;
        this.tasks = tasks;
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
                    markTaskAsComplete(view);
                } else {
                    markTaskAsIncomplete(view);
                }
                tasks.get(finalPosition).toggleDone();
            }
        });

        EditText editText = (EditText) view.findViewById(R.id.item_task_edit_text);
        editText.setText(tasks.get(position).getTask());

        if (tasks.get(position).getDone()) {
            markTaskAsComplete(view);
            checkBox.setChecked(true);
        } else {
            markTaskAsIncomplete(view);
        }

        return view;
    }

    public void clear() {
        tasks = new ArrayList<>();
    }

    private void markTaskAsComplete(View view) {
        view.setAlpha(0.05f);
    }

    private void markTaskAsIncomplete(View view) {
        view.setAlpha(1f);
    }

}
