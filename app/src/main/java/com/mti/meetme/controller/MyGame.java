package com.mti.meetme.controller;

import com.firebase.client.Firebase;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.Network.Network;

import java.text.DateFormat;
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

    public void finishTheGame() {
        if (isOwner()) {
            Event myEvent = MyGame.getInstance().getGame();
            Firebase ref = Network.find_event(myEvent.receiveEventId());

            Date date = new Date();

            Map<String, Object> desc = new HashMap<>();
            desc.put("endDate", dateFormat.format(date).toString());
            ref.updateChildren(desc, null);

            MyGame.getInstance().setGame(null);
         }
        else
        {
            //todo Firebase -> not anymore participant
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
}
