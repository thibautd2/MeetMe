package com.mti.meetme.controller;

import android.hardware.camera2.params.Face;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.R;
import com.mti.meetme.Tools.Network.Network;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 18/06/2016.
 */
public class MyGame {
    private Event game;
    private User owner;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private static MyGame ourInstance = new MyGame();

    public static MyGame getInstance() {
        return ourInstance;
    }

    private MyGame() {
    }

    public Event getGame() {
        return game;
    }

    public void setGame(Event game) {
        this.game = game;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void initMyGame() {
        User u = FacebookUser.getInstance();
        if (u == null || u.getParticipateTo() == null || u.getParticipateTo() == "")
            return;

        String participate[] = u.getParticipateTo().split(";");

        for (String participe : participate) {
            Firebase ref = Network.find_event(participe);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Event event = dataSnapshot.getValue(Event.class);
                    if ((event.getType().equals("compass") || event.getType().equals("warmNcold"))  && isNotFinished(event)) {
                        game = event;

                        Firebase ref = Network.find_user(event.getOwnerid());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User u = dataSnapshot.getValue(User.class);
                                if (u != null)
                                    owner = u;
                                else
                                    game = null;
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
    }

    public void finishTheGame() {
        if (game == null)
            return;

        if (isOwner()) {
            Event myEvent = MyGame.getInstance().getGame();
            Firebase ref = Network.find_event(myEvent.receiveEventId());

            Date date = new Date();

            Map<String, Object> desc = new HashMap<>();
            desc.put("endDate", dateFormat.format(date).toString());
            ref.updateChildren(desc, null);

            MyGame.getInstance().setGame(null);
        } else {
                if (game.participants == null || game.participants == "")
                    return;

                String participate[] = game.participants.split(";");
                String result = "";

                for (String s: participate)
                    if (!s.equals(FacebookUser.getInstance().getUid()))
                        result += s + ";";

                if (game.participants != result) {
                    game.participants = result;

                    Firebase refEvent = Network.find_event(game.receiveEventId());
                    Map<String, Object> descEvent = new HashMap<>();
                    descEvent.put("participants", result);
                    refEvent.updateChildren(descEvent, null);
                }

            game = null;
            owner = null;
        }
    }

    public boolean isOwner() {
        return game.getOwnerid().equals(FacebookUser.getInstance().getUid());
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public boolean isNotFinished(Event e) {
        try {
            Date now = new Date();
            Date eventFinish = null;
            eventFinish = MyGame.getInstance().getDateFormat().parse(e.getEndDate());
            if (eventFinish == null || eventFinish.before(now))
                return false;
        } catch (ParseException e1) {
            e1.printStackTrace();
            return false;
        }

        return true;
    }
}
