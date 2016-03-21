package com.mti.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mti.meetme.Model.User;
import com.mti.meetme.controller.FacebookUser;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {

    ImageView profilePic;
    TextView  nameTextView;
    TextView  title;
    TextView  ageTextView;
    TextView  likesTextView;

    Button map;
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

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        if(user == null)
            currentUser = FacebookUser.getInstance();
        else
            currentUser = user;
            Picasso.with(this).load(currentUser.getPic1()).fit().centerCrop().into(profilePic);
            nameTextView.setText(currentUser.getName() + ",");
            ageTextView.setText("" + currentUser.convertBirthdayToAge());
            likesTextView.setText(getString(R.string.likes_title));


        title.setText(R.string.profile_title);
    }

    private void bindViews()
    {
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        nameTextView = (TextView) findViewById(R.id.name_textview);
        title = (TextView) findViewById(R.id.ActionBarLoginTitle);
        ageTextView = (TextView) findViewById(R.id.age_textview);
        likesTextView = (TextView) findViewById(R.id.likes_textview);
        map = (Button) findViewById(R.id.map);
    }

}
