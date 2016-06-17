package com.mti.meetme.notifications;

import android.util.Log;

import com.firebase.client.Firebase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.controller.FacebookUser;

/**
 * Created by Corentin on 5/23/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        User updatedUser = FacebookUser.getInstance();
        if(refreshedToken != null && updatedUser != null)
        updatedUser.setFcmID(refreshedToken);

        Firebase ref = Network.getAlluser;
        Firebase userRef = ref.child(FacebookUser.getInstance().getUid());
        userRef.setValue(updatedUser);
    }
}
