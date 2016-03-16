package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.androiddev.thirtyseven.studybuddy.Main.HubActivity;
import com.androiddev.thirtyseven.studybuddy.R;

public class NoValidAccount extends AppCompatActivity {

    private MultiAutoCompleteTextView textView;
    private EditText nameText;
    private EditText majorText;
    private EditText bioText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_valid_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createAutoComplete();
        createButton();

    }

    protected void createAutoComplete() {
        textView = (MultiAutoCompleteTextView) findViewById(R.id.coursesText);
        textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        String[] courses = getResources().getStringArray(R.array.course_list);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, courses);
        textView.setAdapter(adapter);
    }

    protected void createButton() {
        Button button = (Button) findViewById(R.id.button);
        nameText = (EditText) findViewById(R.id.nameText);
        majorText = (EditText) findViewById(R.id.majorText);
        bioText = (EditText) findViewById(R.id.bioText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(textView.getText().toString().equals("")) &&!(nameText.getText().toString().equals("")) && !(majorText.getText().toString().equals("")) && !(bioText.getText().toString().equals(""))) {
                    Intent i = new Intent(getApplicationContext(), HubActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please Fill Out All Fields"
                            , Toast.LENGTH_LONG).show();
                }

            }
        });

    }


}
