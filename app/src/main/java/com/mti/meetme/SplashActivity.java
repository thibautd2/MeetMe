package com.mti.meetme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.FacebookHandler;
import com.mti.meetme.Tools.Network;
import com.mti.meetme.controller.FacebookUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends Activity implements Firebase.AuthResultHandler, ValueEventListener {

    Firebase ref;
    User currentUser;

    private JSONObject fullLikes;

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
        ref.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        currentUser = snapshot.getValue(User.class);

        if (currentUser != null) {
            FacebookUser.setFacebookUser(currentUser);

            FacebookHandler handler = new FacebookHandler(this);
            handler.loadFacebookDataForCurrentUser();
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

    /*public void getUserLikes(String next)
    {
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/likes",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            if (fullLikes == null)
                                fullLikes = response.getJSONObject();
                            else {
                                JSONArray array = response.getJSONObject().getJSONArray("data");

                                for (int i = 0; i < array.length(); i++)
                                    fullLikes.getJSONArray("data").put(response.getJSONObject().getJSONArray("data").get(i));

                            }

                            if (!response.getJSONObject().getJSONObject("paging").isNull("next"))
                                 getUserLikes(response.getJSONObject().getJSONObject("paging").getJSONObject("cursors").getString("after"));
                            else {
                                FacebookUser.getInstance().setLikes(fullLikes);

                                Intent intent = new Intent(SplashActivity.this, MapsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        if (next == null)
        {
            request.executeAsync();
        }
        else {
            Bundle parameters = new Bundle();
            parameters.putString("after", next);
            request.setParameters(parameters);
            request.executeAsync();
        }
    }*/

    /*public void getUserFriends()
    {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.i("Friends Request", response.toString());
                        FacebookUser.getInstance().setFriends(response.getJSONObject());
                    }
                }
        ).executeAsync();
    }*/
}
