package com.mti.meetme;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.FacebookHandler;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.controller.TodayDesire;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class LoginActivity extends Activity implements FacebookCallback<LoginResult>,
                                                                GraphRequest.GraphJSONObjectCallback,
                                                                Firebase.AuthResultHandler
{
    //Facebook stuff
    CallbackManager callbackManager;
    String fb_token, fb_name, fb_img, fb_email, fb_birthday, fb_age_range, fb_gender, fb_id;
    LoginButton loginButton;

    //UI elements
    ImageView img_user;
    ProgressDialog progress;
    TextView title;
    //Current User
    User currentUser;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Firebase + Facebook initialization
        FacebookSdk.sdkInitialize(this);
        Firebase.setAndroidContext(this);
        callbackManager = CallbackManager.Factory.create();
        ref = Network.bdd_connexion;

        //UI handling
        setContentView(R.layout.activity_login);
        bindViews();
        populateViews();

        //Facebook login handling (see method implementations)
        LoginManager.getInstance().registerCallback(callbackManager, this);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends, user_likes, user_photos, user_events"));
    }

    //Facebook login callbacks
    @Override
    public void onSuccess(LoginResult loginResult) {

        Progress(getString(R.string.progress_status_connect));
        getFacebookData(loginResult);
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onError(FacebookException exception) {
        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
    }
    //UI Handling
    private void bindViews()
    {
        title = (TextView) findViewById(R.id.ActionBarLoginTitle);
        loginButton = (LoginButton) findViewById(R.id.login_button);
    }

    private void populateViews()
    {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void getFacebookData(LoginResult result)
    {
        fb_token = result.getAccessToken().getToken();
        GraphRequest request = GraphRequest.newMeRequest(result.getAccessToken(), this);

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,picture.height(300),name,email,gender,birthday,age_range,events");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCompleted(JSONObject object, GraphResponse response)
    {
        try {
            fb_img = object.getJSONObject("picture").getJSONObject("data").getString("url");
            fb_age_range = object.getJSONObject("age_range").getString("min");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fb_name = object.optString("name").split(" ")[0];
        fb_email = object.optString("email");
        fb_birthday = object.optString("birthday");
        fb_gender = object.optString("gender");
        fb_id = object.optString("id");
        currentUser = new User(fb_age_range, null, fb_name, fb_birthday, "", fb_email, fb_img, fb_gender, TodayDesire.Desire.Everything, FirebaseInstanceId.getInstance().getToken());

        onFacebookAccessTokenChange(AccessToken.getCurrentAccessToken());
    }

    private void onFacebookAccessTokenChange(AccessToken token) {
        Progress(getString(R.string.progress_status_retrieving));

        if (token != null) {
            ref.authWithOAuthToken("facebook", token.getToken(), this);
        }
        else {
            ref.unauth();
        }
    }

    @Override
    public void onAuthenticated(AuthData authData)
    {
        if(progress != null)
            progress.dismiss();

        Log.w("uid", authData.getUid());

        if (authData.getUid().split(":").length == 1)
            currentUser.setUid(authData.getUid());
        else
            currentUser.setUid(authData.getUid().split(":")[1]);

        FacebookUser.setFacebookUser(currentUser);

        FacebookHandler handler = new FacebookHandler(this);
        handler.loadFacebookDataForCurrentUser();
    }

    @Override
    public void onAuthenticationError(FirebaseError firebaseError) {
        if(progress != null)
            progress.dismiss();
    }

    public void Progress(String text)
    {
        progress = new ProgressDialog(this);
        progress.setMessage(text);
        progress.setCancelable(false);
        progress.show();
    }
}
