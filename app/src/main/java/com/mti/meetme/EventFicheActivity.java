package com.mti.meetme;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.pubnub.api.Pubnub;

import org.json.JSONException;

/**
 * Created by thiba_000 on 16/06/2016.
 */

public class EventFicheActivity extends AppCompatActivity {

    TextView owner, title, adresse, date, hour, description, participants;
    ImageView image;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_fiche);

        Firebase.setAndroidContext(this);

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
    }

    private  void populateViews()
    {
        owner.setText(event.getUsername());
        title.setText(event.getName());
        adresse.setText(event.getAdresse());
        description.setText(event.getDescription());
        participants.setText("(15)");

    }
}
