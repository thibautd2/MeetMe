package com.mti.meetme.Event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.R;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thiba_000 on 16/06/2016.
 */

public class EventFicheActivity extends AppCompatActivity {

    TextView owner, title, adresse, date, hour, description, participants;
    ImageView image, userimage;
    Event event;
    User creator;
    LinearLayout imageParticipants;
    Button participateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_fiche);
        event = (Event) getIntent().getSerializableExtra("Event");
        bindViews();
        populateViews();
    }

    private void bindViews()
    {
        owner = (TextView) findViewById(R.id.event_fiche_owner);
        title = (TextView) findViewById(R.id.event_fiche_titre);
        adresse = (TextView) findViewById(R.id.event_fiche_adresse);
        description = (TextView) findViewById(R.id.event_fiche_description);
        image = (ImageView) findViewById(R.id.event_fiche_img);
        participants = (TextView) findViewById(R.id.event_fiche_nb_participants);
        userimage = (ImageView) findViewById(R.id.event_fiche_user_photo);
        date = (TextView) findViewById(R.id.event_fiche_date);
        imageParticipants = (LinearLayout) findViewById(R.id.event_fiche_layout);
        participateBtn = (Button) findViewById(R.id.button2);
    }

    private  void populateViews()
    {
        owner.setText(event.getUsername());
        title.setText(event.getName());
        adresse.setText(event.getAdresse());
        description.setText(event.getDescription());
        participants.setText("(15)");
        image.setBackgroundResource(R.drawable.allfine);
        if(event!=null && event.getType()!=null) {
            if (event.getType().compareTo("sport") == 0)
                image.setBackgroundResource(R.drawable.finesport);
            if (event.getType().compareTo("party") == 0)
                image.setBackgroundResource(R.drawable.soiree2fine);
            if (event.getType().compareTo("drink") == 0)
                image.setBackgroundResource(R.drawable.drinkfine);
        }
        date.setText(event.getDate());
        Firebase ref = Network.find_user(event.getOwnerid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              if(getBaseContext() != null) {
                  creator = dataSnapshot.getValue(User.class);
                  Picasso.with(getBaseContext()).load(creator.getPic1()).fit().centerCrop().transform(new RoundedPicasso()).into(userimage);
              }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        if(event != null && event.getInvited()!= null && event.getInvited().length()>1)
            getParticipants();

        //todo make invisible if already participant
        if (event.ownerid.equals(FacebookUser.getInstance().getUid()))
            participateBtn.setVisibility(View.INVISIBLE);
        else {
            participateBtn.setVisibility(View.VISIBLE);
            participateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Firebase ref = Network.find_event(event.receiveEventId());
                    String str = event.getParticipants() + FacebookUser.getInstance().getUid() + ";";
                    Map<String, Object> desc = new HashMap<>();
                    desc.put("participants", str);
                    ref.updateChildren(desc, null);
                }
            });
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
       // if(event != null && event.getInvited()!= null && event.getInvited().length()>1)
         //   getParticipants();

    }


    public void getParticipants()
    {
        String[] participent = event.getInvited().split(";");
        for (String e : participent) {
            Firebase user = Network.find_user(e);
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);
                    ImageView newItem = new ImageView(EventFicheActivity.this);
                    Picasso.with(getApplicationContext()).load(u.getPic1()).fit().centerCrop().transform(new RoundedPicasso()).into(newItem);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.height = imageParticipants.getHeight();
                    params.width = params.height;
                    params.setMargins(10, 0, 10, 0);
                    imageParticipants.addView(newItem, params);

                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

}
