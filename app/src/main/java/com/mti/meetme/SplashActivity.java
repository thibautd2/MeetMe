package com.mti.meetme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.mti.meetme.Model.User;
import com.mti.meetme.controller.FacebookUser;

import org.json.JSONObject;

public class SplashActivity extends Activity implements Firebase.AuthResultHandler, ValueEventListener {

    Firebase ref;

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FacebookSdk.sdkInitialize(this);
        Firebase.setAndroidContext(this);
        ref = new Firebase("https://intense-fire-5226.firebaseio.com/");

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
        Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void getUserFromFirebase(String Uid)
    {
        Firebase ref = new Firebase("https://intense-fire-5226.firebaseio.com/users/" + Uid);
        ref.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        Toast.makeText(getApplicationContext(), "Attempting to read user", Toast.LENGTH_SHORT).show();
        Log.w("USER JSON", (snapshot.getValue(User.class)).toString());

        //Gson gson = new Gson();
        currentUser = snapshot.getValue(User.class);

        FacebookUser.setFacebookUser(currentUser);

        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    @Override
    public void onCancelled(FirebaseError firebaseError) {
        System.out.println("The read failed: " + firebaseError.getMessage());
    }
}
