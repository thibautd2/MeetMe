package com.mti.meetme;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mti.meetme.controller.FacebookUser;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_layout);
        TextView title = (TextView) findViewById(R.id.ActionBarLoginTitle);
        title.setText(FacebookUser.getInstance().getName());

        setContentView(R.layout.activity_profile);

    }
}
