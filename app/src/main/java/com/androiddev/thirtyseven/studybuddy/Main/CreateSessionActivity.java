package com.androiddev.thirtyseven.studybuddy.Main;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.Accounts.Buddy;
import com.androiddev.thirtyseven.studybuddy.Backend.ServerConnection;
import com.androiddev.thirtyseven.studybuddy.Backend.SessionSchema;
import com.androiddev.thirtyseven.studybuddy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SecureCacheResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CreateSessionActivity extends AppCompatActivity {
    private GregorianCalendar calendar;
    private EditText dateText;
    private EditText startText;
    private EditText endText;
    private EditText descText;
    private AutoCompleteTextView textView;
    private int startHour;
    private int endHour;
    private int startMin;
    private int endMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        descText =(EditText) findViewById(R.id.editTextDesc);
        setSupportActionBar(toolbar);
        createAutoComplete();
        createButtons();
        calendar = new GregorianCalendar();
        createDatePicker(calendar);
        createTimePickers(calendar);

    }

    protected void createAutoComplete(){
        textView = (AutoCompleteTextView) findViewById(R.id.editTextCourse);
        String[] courses = getResources().getStringArray(R.array.course_list);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, courses);
        textView.setAdapter(adapter);
    }
    protected void createButtons() {
        Button button = (Button) findViewById(R.id.buttonConfirm);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(!(dateText.getText().toString().equals("")) && !(startText.getText().toString().equals("")) && !(endText.getText().toString().equals("")) &&
                        !(textView.getText().toString().equals("")) && (startHour < endHour
                || (startHour == endHour && startMin < endMin))){
                    String[] date = dateText.getText().toString().split("-");
                    GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(date[2]) ,Integer.parseInt(date[0]) -1 ,Integer.parseInt(date[1]) , startHour,startMin);
                    GregorianCalendar gc2 = new GregorianCalendar(Integer.parseInt(date[2]),Integer.parseInt(date[0]) -1 ,Integer.parseInt(date[1]) , endHour,endMin);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String id = prefs.getString("id", "None");
                    SessionSchema schema = new SessionSchema(id);
                    schema.setBio(descText.getText().toString());
                    schema.setCourse(textView.getText().toString());
                    schema.setTimes(gc.getTimeInMillis(), gc2.getTimeInMillis());
                    CreateSessionAsync async = new CreateSessionAsync("/sessions/newsession",schema);
                    async.execute();
                    Toast.makeText(getApplicationContext(), "Session Created", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), HubActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(i);
            }
                else{
                   Toast.makeText(getApplicationContext(), "Either you forgot to fill out a field, or your start time " +
                           "came after your end time." , Toast.LENGTH_LONG).show();
                }


            }
        });
        Button button2 = (Button) findViewById(R.id.buttonNo);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HubActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }

    protected void createDatePicker(GregorianCalendar calendar) {
        dateText = (EditText) findViewById(R.id.editTextDate);
        final DatePickerDialog pick = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateText.setText(monthOfYear+1 + "-"
                                + dayOfMonth + "-" + year);
                    }
                }, calendar.get(GregorianCalendar.YEAR), calendar.get(GregorianCalendar.MONTH), calendar.get(GregorianCalendar.DAY_OF_MONTH));
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick.show();
            }
        });

    }

    protected void createTimePickers(GregorianCalendar calendar) {
        startText = (EditText) findViewById(R.id.editTextStart);
        endText = (EditText) findViewById(R.id.editTextEnd);


        final TimePickerDialog startPick = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startHour = hourOfDay;
                        startMin = minute;
                        String AMPM = "AM";
                        if (hourOfDay == 0) {
                            hourOfDay += 12;

                        } else if (hourOfDay == 12) {
                            AMPM = "PM";

                        } else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            AMPM = "PM";
                        }
                        if (minute < 10) {
                            startText.setText(hourOfDay + ":0" + minute + " " + AMPM);

                        } else {
                            startText.setText(hourOfDay + ":" + minute + " " + AMPM);
                        }
                    }
                }, calendar.get(GregorianCalendar.HOUR_OF_DAY), calendar.get(GregorianCalendar.MINUTE), false);
        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPick.show();
            }
        });

        final TimePickerDialog endPick = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String AMPM = "AM";
                        endHour = hourOfDay;
                        endMin = minute;

                        if (hourOfDay == 0) {
                            hourOfDay += 12;

                        } else if (hourOfDay == 12) {
                            AMPM = "PM";

                        } else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            AMPM = "PM";
                        }
                        if (minute < 10) {
                            endText.setText(hourOfDay + ":0" + minute + " " + AMPM);

                        } else {
                            endText.setText(hourOfDay + ":" + minute + " " + AMPM);
                        }
                    }
                }, calendar.get(GregorianCalendar.HOUR_OF_DAY), calendar.get(GregorianCalendar.MINUTE), false);
        endText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endPick.show();
            }
        });
    }
}


class CreateSessionAsync extends AsyncTask<Void, Void, Boolean> {

    ServerConnection server;

    public CreateSessionAsync(String params, SessionSchema sessionSchema)
    {
        server = new ServerConnection(sessionSchema,params);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        JSONObject obj = server.run();
        try {
            if (obj.getString("status").equals("success"))
            {
                return true;
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
