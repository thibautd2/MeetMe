package com.mti.meetme;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
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
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.Tools.Network;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements FacebookCallback<LoginResult>,
                                                                GraphRequest.GraphJSONObjectCallback,
                                                                Firebase.AuthResultHandler
{
    //Facebook stuff
    CallbackManager callbackManager;
    String fb_token, fb_name, fb_img, fb_email, fb_birthday, fb_age_range, fb_gender;
    LoginButton loginButton;
    Button map;
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

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_layout);

        //Firebase + Facebook initialization
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        ref = Network.bdd_connexion;

        //UI handling
        setContentView(R.layout.activity_login);
        bindViews();
        populateViews();

        //Facebook login handling (see method implementations)
        LoginManager.getInstance().registerCallback(callbackManager, this);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends, user_likes"));
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
        loginButton = (LoginButton) findViewById(R.id.login_button);
        title = (TextView) findViewById(R.id.ActionBarLoginTitle);
        loginButton = (LoginButton) findViewById(R.id.login_button);
    }

    private void populateViews()
    {
        title.setText(getResources().getText(R.string.app_name));

        img_user = (ImageView)findViewById(R.id.imageView);
        Picasso.with(getApplication()).load(R.drawable.chut).fit().centerCrop().transform(new RoundedPicasso()).into(img_user);
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
        parameters.putString("fields", "id,picture.height(300),name,email,gender,birthday,age_range");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCompleted(JSONObject object, GraphResponse response)
    {
        try {
            fb_img = object.getJSONObject("picture").getJSONObject("data").getString("url");
            fb_age_range = object.getJSONObject("age_range").getString("min");
            Log.w("LIKES", response.getJSONObject().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fb_name = object.optString("name").split(" ")[0];
        fb_email = object.optString("email");
        fb_birthday = object.optString("birthday");
        fb_gender = object.optString("gender");

        currentUser = new User(fb_age_range, null, fb_name, fb_birthday, "Trololo", fb_email, fb_img, fb_gender);

        if (progress != null)
            progress.dismiss();
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
        if(progress!=null)
            progress.dismiss();

        currentUser.setUid(authData.getUid());
        saveToFirebase(currentUser);

        getUserLikes();
        FacebookUser.setFacebookUser(currentUser);

        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    @Override
    public void onAuthenticationError(FirebaseError firebaseError) {
        if(progress!=null)
            progress.dismiss();
    }

    private void saveToFirebase(User user)
    {
        Firebase ref = Network.getAlluser;

        Firebase userRef = ref.child(user.getUid());

        userRef.setValue(user);
    }

    public void Progress(String text)
    {
        progress = new ProgressDialog(this);
        progress.setMessage(text);
        progress.setCancelable(false);
        progress.show();
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
                        currentUser.setLikes(response.getJSONObject());
                    }
                }
        ).executeAsync();

    }
}
