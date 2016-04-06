package com.androiddev.thirtyseven.studybuddy.Sessions.Description;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.Main.HubActivity;
import com.androiddev.thirtyseven.studybuddy.R;
import com.androiddev.thirtyseven.studybuddy.Sessions.MySessions;
import com.androiddev.thirtyseven.studybuddy.Sessions.Whiteboard.WhiteboardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joseph Elliott on 2/28/2016.
 */
public class DescriptionFragment extends Fragment {

    private JSONObject sessionInfo;
    private SharedPreferences prefs;
    private String id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_description, container, false);

        TextView course = (TextView) rootView.findViewById(R.id.courseName);
        TextView sessionDate = (TextView) rootView.findViewById(R.id.date);
        TextView startTime = (TextView) rootView.findViewById(R.id.startTime);
        TextView endTime = (TextView) rootView.findViewById(R.id.endTime);
        TextView desc = (TextView) rootView.findViewById(R.id.desc);
        Button leave = (Button) rootView.findViewById(R.id.leaveBtn);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        id = prefs.getString("id", "None");


        try {
            sessionInfo = new JSONObject((String)getActivity().getIntent().getSerializableExtra("session"));

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

            final String my_params = "/users/leavesession/" + id + "/" + sessionInfo.getString("_id");

            leave.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    AsyncTask a = new AsyncTask<Object, Void, Boolean>(){
                        @Override
                        protected Boolean doInBackground(Object...params){
                            ServerConnection server = new ServerConnection(null,"PUT",my_params);
                            JSONObject json = server.run();
                            try {
                                return ("success".equals(json.getString("status")))? true:false;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return null;
                            }

                        }
                    };
                    a.execute();
                    Intent i = new Intent(getActivity().getApplicationContext(), MySessions.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(getActivity().getApplicationContext(), "You have left the study session", Toast.LENGTH_LONG).show();
                    startActivity(i);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
    }

}
