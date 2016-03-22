package com.mti.meetme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.Network;
import com.mti.meetme.controller.FacebookUser;

public class SplashActivity extends Activity implements Firebase.AuthResultHandler, ValueEventListener {

    Firebase ref;

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FacebookSdk.sdkInitialize(this);
        Firebase.setAndroidContext(this);
        ref = Network.bdd_connexion;

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
        getUserFromFirebase(authData.getUid());
    }

    @Override
    public void onAuthenticationError(FirebaseError firebaseError) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void getUserFromFirebase(String Uid)
    {
        Firebase ref = Network.find_user(Uid);
        ref.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        currentUser = snapshot.getValue(User.class);

        if (currentUser != null) {
            FacebookUser.setFacebookUser(currentUser);

            getUserFriends();
            getUserLikes();
        }
        else
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

    }
    @Override
    public void onCancelled(FirebaseError firebaseError) {
        System.out.println("The read failed: " + firebaseError.getMessage());
    }

    public void getUserLikes()
    {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/likes",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        FacebookUser.getInstance().setLikes(response.getJSONObject());

                        Intent intent = new Intent(SplashActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                }
        ).executeAsync();
    }

    public void getUserFriends()
    {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        FacebookUser.getInstance().setFriends(response.getJSONObject());
                    }
                }
        ).executeAsync();
    }
}
