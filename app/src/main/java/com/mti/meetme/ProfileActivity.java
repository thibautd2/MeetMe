package com.mti.meetme;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.mti.meetme.Model.User;
import com.mti.meetme.controller.FacebookUser;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {

    ImageView profilePic;
    TextView  nameTextView;
    TextView title;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_layout);
        setContentView(R.layout.activity_profile);

        user = (User) getIntent().getSerializableExtra("User");

        setContentView(R.layout.activity_profile);
        bindViews();
        populateViews();
    }

    private void populateViews()
    {
        User currentUser;

        if(user == null)
            currentUser = FacebookUser.getInstance();
        else
            currentUser = user;
            Picasso.with(this).load(currentUser.getPic1()).fit().centerCrop().into(profilePic);
            nameTextView.setText(currentUser.getName());

        title.setText(R.string.profile_title);
    }

    private void bindViews()
    {
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        nameTextView = (TextView) findViewById(R.id.name_textview);
        title = (TextView) findViewById(R.id.ActionBarLoginTitle);
    }

}
