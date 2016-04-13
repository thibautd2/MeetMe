package com.mti.meetme.controller;

import com.mti.meetme.Model.User;

import java.util.ArrayList;

/**
 * Created by thiba_000 on 12/04/2016.
 */
public class UserList {
    private static ArrayList<User> _all_users;
    public static ArrayList<User> getInstance() {
        return _all_users;
    }
    public static void setAll_users(ArrayList<User> users) { _all_users = users;}
}
