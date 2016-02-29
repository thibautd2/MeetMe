package com.mti.meetme.controller;

/**
 * Created by W_Corentin on 29/02/2016.
 */
public class FacebookUser {

    private static FacebookUser ourInstance = new FacebookUser();

    public static FacebookUser getInstance() {
        return ourInstance;
    }

    private FacebookUser()
    {
    }
}
