package com.mti.meetme.Event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.Event.EventCreation.CreateEventManager;
import com.mti.meetme.Event.Game.GameCompassActivity;
import com.mti.meetme.Event.Game.GameWarmNColdActivity;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.R;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.MyGame;
import com.mti.meetme.controller.TodayDesire;
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
        if(event.getInvited()!= null && event.getInvited().length() >0)
        participants.setText("("+event.getInvited().split(";").length+") participants");
        if(participants.getText().length() < 1)
            participants.setText("("+event.participants+") participants");
        image.setBackgroundResource(R.drawable.allfine);
        if(event!=null && event.getType()!=null) {
            if (event.getCategorie().compareTo(TodayDesire.Desire.Sport.toString()) == 0)
                image.setBackgroundResource(R.drawable.finesport);
            if (event.getCategorie().compareTo(TodayDesire.Desire.party.toString()) == 0)
                image.setBackgroundResource(R.drawable.soiree2fine);
            if (event.getCategorie().compareTo(TodayDesire.Desire.Drink.toString()) == 0)
                image.setBackgroundResource(R.drawable.drinkfine);
            if (event.getCategorie().compareTo(TodayDesire.Desire.play.toString()) == 0)
                image.setBackgroundResource(R.drawable.finegames);
        }
        if(event.baniere != null && event.baniere.compareTo("")!=0)
            Picasso.with(this).load(event.baniere).fit().centerCrop().into(image);
        date.setText(event.getDate());
        Firebase.setAndroidContext(getApplicationContext());
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

        participateBtn.setVisibility(View.INVISIBLE);

        if(event != null && event.getInvited()!= null && event.getInvited().length()>1) {
            getParticipants();
        }

            if (!event.ownerid.equals(FacebookUser.getInstance().getUid()) && !event.getParticipants().contains(FacebookUser.getInstance().getUid())) {
                participateBtn.setVisibility(View.VISIBLE);
                participateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!FacebookUser.getInstance().isParticipatingTo(event.receiveEventId())) {
                            Firebase ref = Network.find_event(event.receiveEventId());
                            String strEvent = event.getParticipants() + FacebookUser.getInstance().getUid() + ";";
                            Map<String, Object> descEvent = new HashMap<>();
                            descEvent.put("participants", strEvent);
                            ref.updateChildren(descEvent, null);
                            FacebookUser.getInstance().addParticipateTo(event.receiveEventId());
                            Toast.makeText(getApplicationContext(), "Vous étes inscrit à l'évènement !", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        }

                        if (event.type.equals("compass")) {
                            MyGame.getInstance().setGame(event);
                            MyGame.getInstance().setOwner(creator);
                            Intent intent = new Intent(EventFicheActivity.this, GameCompassActivity.class);
                            startActivity(intent);
                        } else if (event.type.equals("warmNcold")) {
                            MyGame.getInstance().setGame(event);
                            MyGame.getInstance().setOwner(creator);
                            Intent intent = new Intent(EventFicheActivity.this, GameWarmNColdActivity.class);
                            startActivity(intent);
                        }
                  /*  else
                    {
                        Toast.makeText(this, "Vous participez mainenant a cet event", Toast.LENGTH_LONG);
                        Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }
*/
                    }
                });
            }

    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    public void getParticipants() {
        String[] guests = event.getInvited().split(";");

        Log.w("GUESTS", event.getInvited());

        for (String e : guests) {
            Firebase user = Network.find_user(e);
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User userInvited = dataSnapshot.getValue(User.class);

                    ImageView newItem = new ImageView(EventFicheActivity.this);
                    Picasso.with(getApplicationContext()).load(userInvited.getPic1()).fit().centerCrop().transform(new RoundedPicasso()).into(newItem);

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
}
