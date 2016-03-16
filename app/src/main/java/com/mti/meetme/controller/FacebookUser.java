package com.mti.meetme.controller;

import com.mti.meetme.Model.User;

/**
 * Created by W_Corentin on 29/02/2016.
 */
public class FacebookUser {

    private static User _facebookUser;
    public static User getInstance() {
        return _facebookUser;
    }
    public static void setFacebookUser(User facebookUser) { _facebookUser = facebookUser; }
}
