package com.mti.meetme.Tools;

import com.firebase.client.Firebase;

import java.net.URI;

/**
 * Created by thiba_000 on 15/03/2016.
 */
public class Network {
    static String URL = "https://intense-fire-5226.firebaseio.com/";
    public static Firebase connexion_user = new Firebase(URL+"Users/");
    public static Firebase bdd_connexion = new Firebase(URL);

    public static  Firebase find_user(String Uid)
    {
        return new Firebase(connexion_user+Uid);
    }

}
