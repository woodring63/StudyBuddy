package com.androiddev.thirtyseven.studybuddy.Main;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.androiddev.thirtyseven.studybuddy.R;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CreateSessionActivity extends AppCompatActivity {
    private GregorianCalendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createAutoComplete();
        createButtons();
        calendar = new GregorianCalendar();
        createDatePicker(calendar);
        createTimePickers(calendar);

    }

    protected void createAutoComplete(){
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.editTextCourse);
        String[] courses = getResources().getStringArray(R.array.course_list);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, courses);
        textView.setAdapter(adapter);
    }
    protected void createButtons() {
        Button button = (Button) findViewById(R.id.buttonConfirm);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HubActivity.class);
                startActivity(i);
            }
        });
        Button button2 = (Button) findViewById(R.id.buttonNo);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HubActivity.class);
                startActivity(i);
            }
        });
    }

    protected void createDatePicker(GregorianCalendar calendar) {
        final EditText dateText = (EditText) findViewById(R.id.editTextDate);
        final DatePickerDialog pick = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateText.setText(monthOfYear + "-"
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
        final EditText startText = (EditText) findViewById(R.id.editTextStart);
        final EditText endText = (EditText) findViewById(R.id.editTextEnd);


        final TimePickerDialog startPick = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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


