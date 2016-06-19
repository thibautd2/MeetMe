package com.mti.meetme.controller;

import com.firebase.client.Firebase;
import com.mti.meetme.Model.Event;
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

    public void finishTheGame() {
        Event myEvent = MyGame.getInstance().getGame();
        Firebase ref = Network.find_event(myEvent.receiveEventId());

        Date date = new Date();

        Map<String, Object> desc = new HashMap<>();
        desc.put("endDate", dateFormat.format(date).toString());
        ref.updateChildren(desc, null);

        MyGame.getInstance().setGame(null);
        //Toast.makeText(getApplicationContext(), "Jeux terminé !", Toast.LENGTH_LONG).show();
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
}
