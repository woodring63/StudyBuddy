package com.androiddev.thirtyseven.studybuddy.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.transform.Result;

public class HubActivity extends NavBase{
    JSONObject json;
    ListView sessionsList;
    ArrayList<String> sessions;
    JSONArray JArray;
    @Override
    public void onCreate(Bundle savedInstanceState){
        sessions = new ArrayList<>();
        super.onCreate(savedInstanceState);
        String bundle = (String)getIntent().getSerializableExtra("sessions");
        setupSessions(bundle);
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

    public void setupSessions(String bundle)
    {

        try {
            if(bundle != null){
                json = new JSONObject(bundle.toString());
                if(json != null)
                {
                    JArray = json.getJSONArray("sessions");
                    for(int i = 0; i < JArray.length(); i ++)
                    {
                        String pattern = "MM/dd/yyyy";
                        SimpleDateFormat format = new SimpleDateFormat(pattern);
                        Date date = new Date(Long.parseLong(JArray.getJSONObject(i).getString("startTime")));
                        sessions.add(JArray.getJSONObject(i).getString("course") + " " + format.format(date));
                    }
                }
            }else
            {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String email = prefs.getString("email","None");
                FindSessionsAsync async = new FindSessionsAsync(email, sessions, JArray);
                JArray = async.execute().get();

            }

        }catch (Exception e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,sessions);
        adapter.setNotifyOnChange(true);
        setContentView(R.layout.activity_hub);
        sessionsList = (ListView) findViewById(R.id.sessionListView);
        sessionsList.setAdapter(adapter);
        //When a session is clicked on, send them to a new
        sessionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), SessionInfo.class);
                Bundle bundle = new Bundle();
                try {
                    if(JArray != null) {
                        bundle.putSerializable("session", JArray.getJSONObject(position).toString());
                        i.putExtras(bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(i);
            }
        });
    }

    public void fillingTheArrayList()
    {
        for(int i = 0; i < JArray.length(); i ++)
        {
            try {
                String pattern = "MM/dd/yyyy";
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                Date date = new Date(Long.parseLong(JArray.getJSONObject(i).getString("startTime")));
                sessions.add(JArray.getJSONObject(i).getString("course") + " " + format.format(date));
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
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

class FindSessionsAsync extends AsyncTask<Void, Void, JSONArray>
{

    private String username;
    private ArrayList sessions;
    private JSONArray JArray;
    //private JArrayCallback callback;

    public FindSessionsAsync(String username, ArrayList sessions, JSONArray JArray)
    {
        this.username = username;
        this.sessions = sessions;
        this.JArray = JArray;
       // this.callback = callback;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        ServerConnection server = new ServerConnection("/users/username/" + username);
        JSONObject json = server.run();
        try {
            JArray = json.getJSONArray("newSessions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JArray;
    }

    @Override
    protected void onPostExecute(JSONArray params)
    {
       // callback.onCompleted(params);
        for(int i = 0; i < JArray.length(); i ++)
        {
            try {
                String pattern = "MM/dd/yyyy";
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                Date date = new Date(Long.parseLong(JArray.getJSONObject(i).getString("startTime")));
                sessions.add(JArray.getJSONObject(i).getString("course") + " " + format.format(date));
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}


interface JArrayCallback
{
    public void onCompleted(JSONArray jArray);
}