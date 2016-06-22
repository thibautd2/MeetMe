package com.mti.meetme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.FacebookHandler;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.MyGame;

import org.json.JSONObject;

public class SplashActivity extends Activity implements Firebase.AuthResultHandler, ValueEventListener {
    private Firebase ref;
    private User currentUser;
    private boolean launcher;

    private JSONObject fullLikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FacebookSdk.sdkInitialize(this);
        Firebase.setAndroidContext(this);

        ref = Network.bdd_connexion;
        launcher = false;

        onFacebookAccessTokenChange(AccessToken.getCurrentAccessToken());
    }

    private void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            ref.authWithOAuthToken("facebook", token.getToken(), this);
        }
        else {
            ref.unauth();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onAuthenticated(AuthData authData) {
        if (authData.getUid().split(":").length == 1)
            getUserFromFirebase(authData.getUid());
        else
            getUserFromFirebase(authData.getUid().split(":")[1]);
    }

    @Override
    public void onAuthenticationError(FirebaseError firebaseError) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void getUserFromFirebase(String Uid)
    {
        Firebase ref = Network.find_user(Uid);
        ref.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        if (snapshot != null)
        currentUser = snapshot.getValue(User.class);

        if (currentUser != null && launcher == false) {
            FacebookUser.setFacebookUser(currentUser);
            MyGame.getInstance().initMyGame();

            launcher = true;

            FacebookHandler handler = new FacebookHandler(this);
            handler.loadFacebookDataForCurrentUser();
        }
        else if (currentUser == null)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

    }
    @Override
    public void onCancelled(FirebaseError firebaseError) {
        System.out.println("The read failed: " + firebaseError.getMessage());
    }
}
