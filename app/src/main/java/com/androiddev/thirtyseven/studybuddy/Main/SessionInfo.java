package com.androiddev.thirtyseven.studybuddy.Main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by enclark on 3/30/2016.
 */
public class SessionInfo extends NavBase{

    private JSONObject sessionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView course = (TextView) findViewById(R.id.courseName);
        TextView sessionDate = (TextView) findViewById(R.id.date);
        TextView startTime = (TextView) findViewById(R.id.startTime);
        TextView endTime = (TextView) findViewById(R.id.endTime);
        TextView desc = (TextView) findViewById(R.id.desc);
        Button join = (Button) findViewById(R.id.joinBtn);
        Button leave = (Button) findViewById(R.id.leaveBtn);



        try {
            sessionInfo = new JSONObject((String)getIntent().getSerializableExtra("session"));

            String datePattern = "MM/dd/yyyy";
            String timePattern = "hh:mm";

            SimpleDateFormat format = new SimpleDateFormat(datePattern);
            Date date = new Date(Long.parseLong(sessionInfo.getString("startTime")));
            sessionDate.setText(format.format(date));
            format = new SimpleDateFormat(timePattern);
            startTime.setText(format.format(date));
            date = new Date(Long.parseLong(sessionInfo.getString("endTime")));
            endTime.setText(format.format(date));

            course.setText(sessionInfo.getString("course"));
            desc.setText(sessionInfo.getString("bio"));

            JSONArray jArr = sessionInfo.getJSONObject("loc").getJSONArray("coordinates");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                JoinSessionAsyncTask async = null;
                try {
                    async = new JoinSessionAsyncTask(true,prefs.getString("id","None"),sessionInfo.getString("_id"));
                    async.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(getApplicationContext(), HubActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(getApplicationContext(), "You have joined the study session", Toast.LENGTH_LONG).show();
                startActivity(i);
            }
        });


        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                JoinSessionAsyncTask async = null;
                try {
                    async = new JoinSessionAsyncTask(false,prefs.getString("id","None"),sessionInfo.getString("_id"));
                    async.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(getApplicationContext(), HubActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(getApplicationContext(), "You have left the study session", Toast.LENGTH_LONG).show();
                startActivity(i);
            }
        });
    }
}

class JoinSessionAsyncTask extends AsyncTask<Void, Void, Boolean>
{
    private boolean join;
    private String id;
    private String sessionid;

    public JoinSessionAsyncTask(boolean join, String id, String sessionid)
    {
        this.join = join;
        this.id = id;
        this.sessionid = sessionid;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String url = join ? "/users/joinsession" : "/users/leavesession";

        url += "/" + id + "/" + sessionid;
        ServerConnection server = new ServerConnection(null,"PUT",url);
        JSONObject json = server.run();
        try {
            return ("success".equals(json.getString("status")))? true:false;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}