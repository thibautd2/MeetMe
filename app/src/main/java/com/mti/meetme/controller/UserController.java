package com.mti.meetme.controller;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.snapshot.IndexedNode;
import com.firebase.client.snapshot.Node;
import com.mti.meetme.Model.SortUserList;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.FacebookHandler;
import com.mti.meetme.Tools.Network.Network;

import java.util.ArrayList;

/**
 * Created by Alex on 14/06/2016.
 */

public class UserController {
    private static UserController ourInstance = new UserController();

    public static UserController getInstance() {
        return ourInstance;
    }

    private UserController() {
    }

    public User getUser(String id) {
        Firebase ref = Network.find_user(id);

        //DataSnapshot ds = new DataSnapshot(ref, new IndexedNode(new Node(), 0));
        //final User friend = ds.getValue(User.class);
        final ArrayList<User> theUser = new ArrayList<>();


            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    theUser.add(snapshot.getValue(User.class));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e("UserController", "onCancelled: " + firebaseError.getMessage());
                }
            });

            return theUser.get(0);
    }
}
