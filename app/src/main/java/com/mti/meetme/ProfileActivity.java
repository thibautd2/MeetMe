package com.mti.meetme;

import android.app.ActionBar;

import android.content.Intent;

import android.database.CursorIndexOutOfBoundsException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.model.User;

import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {

    ImageView profilePic;
    TextView  nameTextView;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_layout);
        setContentView(R.layout.activity_profile);
        user = (User)getIntent().getSerializableExtra("User");
        TextView title = (TextView) findViewById(R.id.ActionBarLoginTitle);
        title.setText(R.string.profile_title);
        setContentView(R.layout.activity_profile);
        bindViews();
        populateViews();
    }

    private void populateViews()
    {
        User Currentuser;
        if(user == null)
            Currentuser = FacebookUser.getInstance();
        else
            Currentuser = user;
            Picasso.with(this).load(Currentuser.getPic1()).fit().centerCrop().into(profilePic);
            nameTextView.setText(Currentuser.getName());
    }

    private void bindViews()
    {
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        nameTextView = (TextView) findViewById(R.id.name_textview);
    }

}
