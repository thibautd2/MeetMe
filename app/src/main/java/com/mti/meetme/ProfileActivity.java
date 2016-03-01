package com.mti.meetme;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.model.User;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_layout);
        setContentView(R.layout.activity_profile);
        User user = (User)getIntent().getSerializableExtra("User");
        TextView title = (TextView) findViewById(R.id.ActionBarLoginTitle);
        if(user == null)
            title.setText(FacebookUser.getInstance().getName());
        else
            title.setText(user.getName());
    }
}
