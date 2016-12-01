package com.mti.meetme.Tools.Network;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.core.Context;
import com.mti.meetme.Model.Event;
import com.mti.meetme.controller.MyGame;

import java.net.URI;

/**
 * Created by thiba_000 on 15/03/2016.
 */
public class Network {
    static String URL = "https://intense-fire-5226.firebaseio.com/";
    public static Firebase connexion_to_user = new Firebase(URL+"users");
    public static Firebase geofire = new Firebase(URL+"geofire");
    public static Firebase bdd_connexion = new Firebase(URL);
    public static Firebase getAlluser = connexion_to_user;
    public static Firebase connexion_to_event = new Firebase(URL+"events");

    public static  Firebase find_user(String Uid)
    {
        return new Firebase(connexion_to_user+"/"+Uid);
    }

    public static Firebase find_MeetmeFriends(String Uid) {
        return new Firebase(connexion_to_user+"/"+Uid + "/" + "meetMeFriends");
    }

    public static Firebase find_FriendRequestReceived(String Uid) {
        return new Firebase(connexion_to_user+"/"+Uid + "/" + "friendRequestReceived");
    }

    public static  Firebase create_event(String event)
    {
        return new Firebase(connexion_to_event+"/"+event);
    }

    public static  Firebase find_event(String event)
    {
        return new Firebase(connexion_to_event+"/"+event);
    }

    public static Firebase find_user_properties(String Uid, String properties) {
        return new Firebase(connexion_to_user+"/"+Uid + "/" + properties);
    }

    public static Firebase find_ParticipantsToMyGame() {
        Event mygame = MyGame.getInstance().getGame();
        Log.e("Network", "find_ParticipantsToMyGame: " + mygame.receiveEventId());
        return new Firebase(connexion_to_event + "/" + mygame.receiveEventId() + "/participants");
    }

    public static boolean isConnectedToInternet(Activity context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting(); //maybe just isConnected instead of isConnectedorConnecting
    }
}
