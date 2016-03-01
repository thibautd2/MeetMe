package com.mti.meetme;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mti.meetme.controller.FacebookUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    ImageView profilePic;
    TextView  nameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_layout);
        TextView title = (TextView) findViewById(R.id.ActionBarLoginTitle);
        title.setText(R.string.profile_title);

        setContentView(R.layout.activity_profile);

        bindViews();
        populateViews();
    }

    private void populateViews()
    {
        Picasso.with(this).load(FacebookUser.getInstance().getPic1()).fit().centerCrop().into(profilePic);
        nameTextView.setText(FacebookUser.getInstance().getName());
    }

    private void bindViews()
    {
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        nameTextView = (TextView) findViewById(R.id.name_textview);
    }


}
