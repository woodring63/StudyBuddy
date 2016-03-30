package com.androiddev.thirtyseven.studybuddy.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.androiddev.thirtyseven.studybuddy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class HubActivity extends NavBase{
    JSONObject json;
    ListView sessionsList;
    ArrayList<String> sessions;
    @Override
    public void onCreate(Bundle savedInstanceState){
        sessions = new ArrayList<>();

        try {
            if((String)getIntent().getSerializableExtra("sessions") != null){


                json = new JSONObject((String)getIntent().getSerializableExtra("sessions").toString());
                if(json != null)
                {

                    JSONArray JArray = json.getJSONArray("sessions");

                    for(int i = 0; i < JArray.length(); i ++)
                    {
                        String pattern = "MM/dd/yyyy";
                        SimpleDateFormat format = new SimpleDateFormat(pattern);
                        Date date = new Date(Long.parseLong(JArray.getJSONObject(i).getString("startTime")));
                        sessions.add(JArray.getJSONObject(i).getString("course") +" "+ format.format(date));
                        System.out.println(JArray.getString(i));
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,sessions);
        adapter.setNotifyOnChange(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);
        sessionsList = (ListView) findViewById(R.id.sessionListView);
        sessionsList.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CreateSessionActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

}
/*
class SessionObject{

    public String id;
    public String startTime;
    public String endTime;
    public String course;

    public SessionObject(JSONObject json)
    {
        try {
            id = json.getString("_id");
            startTime = json.getString("startTime");
            endTime = json.getString("endTime");
            course = json.getString("course");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

class MyAdapter extends ArrayAdapter<SessionObject> {
    ArrayList<SessionObject> sessions;
    Context context;
    int layout;

    public MyAdapter(Context context, int resource, ArrayList<SessionObject> objects) {
        super(context, resource, objects);
        context = context;
        sessions = objects;
        layout = resource;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= ((Activity) context).getLayoutInflater();
        View row=inflater.inflate(layout,parent,false);
        return super.getView(position, convertView, parent);

    }
}*/
