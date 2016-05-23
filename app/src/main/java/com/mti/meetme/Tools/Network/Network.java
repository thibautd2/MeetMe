package com.mti.meetme.Tools.Network;

import com.firebase.client.Firebase;

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
}
