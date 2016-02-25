package com.androiddev.thirtyseven.studybuddy.Accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androiddev.thirtyseven.studybuddy.R;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Bundle userData = getIntent().getExtras();
        if (userData == null)
            return;
        String userName = userData.getString("userName");
        int userImgId = userData.getInt("userImgId");

        TextView userNameView = (TextView) findViewById(R.id.userNameView);
        ImageView userImageView = (ImageView) findViewById(R.id.userImageView);

        userNameView.setText(userName);
        userImageView.setImageResource(userImgId);

        Button button = (Button) findViewById(R.id.backToSearchButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BuddyList.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
    }



}
