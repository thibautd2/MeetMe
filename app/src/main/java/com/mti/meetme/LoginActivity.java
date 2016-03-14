package com.mti.meetme;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.RoundedPicasso;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    String fb_token, fb_name, fb_img, fb_email, fb_birthday, fb_age_range, fb_gender;

    ProgressDialog progress;
    User currentUser;
    ImageView img_user;

    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindViews();


        FacebookSdk.sdkInitialize(this); //Initialisation du facebook SDK
        Firebase.setAndroidContext(this); //Init Firebase

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_layout);

        TextView title = (TextView) findViewById(R.id.ActionBarLoginTitle);
        title.setText(getResources().getText(R.string.app_name));

        setContentView(R.layout.activity_login);
        img_user = (ImageView)findViewById(R.id.imageView);
        Picasso.with(getApplication()).load(R.drawable.chut).fit().centerCrop().transform(new RoundedPicasso()).into(img_user);

        callbackManager = CallbackManager.Factory.create(); // permet les callback
        LoginManager.getInstance().registerCallback(callbackManager, /// CONNEXION USING FACEBOOK SDK
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        if (getUserFromFirebase(loginResult.getAccessToken().getToken()) == null)
                            getFacebookData(loginResult);
                        else
                            Toast.makeText(getApplicationContext(), "Your name is " + currentUser.getName(), Toast.LENGTH_SHORT).show();

                        Progress(getString(R.string.progress_status_connect));

                    }
                    @Override
                    public void onCancel() {
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(), "Problème réseau", Toast.LENGTH_LONG).show();
                    }
                });

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends")); //permissions
    }

    private void bindViews()
    {
        loginButton = (LoginButton) findViewById(R.id.login_button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
        Log.e("activityresult", "activity result");
    }

    public void getFacebookData(final LoginResult result)
    {
        fb_token = result.getAccessToken().getToken();
        GraphRequest request = GraphRequest.newMeRequest(
                result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response)
                    {
                        Log.i("JSON Result", object.toString());
                        String[] splited = object.optString("name").split(" ");
                        fb_name = splited[0];

                        try {
                            fb_img = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            Log.e("IMAGE URL", fb_img);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        fb_email = object.optString("email");
                        fb_birthday = object.optString("birthday");
                        fb_gender = object.optString("gender");

                        try {
                            fb_age_range = object.getJSONObject("age_range").getString("min");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        currentUser = new User(fb_age_range, fb_token, fb_name, fb_birthday, "", fb_email, fb_img, fb_gender);

                        FacebookUser.setFacebookUser(currentUser);

                        if (progress != null)
                            progress.dismiss();

                        saveToFirebase(currentUser);
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,picture.height(300),name,email,gender,birthday,age_range");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private User getUserFromFirebase(String fbToken)
    {
        Firebase ref = new Firebase("https://intense-fire-5226.firebaseio.com/users/" + fbToken);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentUser = (User) snapshot.getValue();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        return currentUser;
    }

    private void saveToFirebase(User user)
    {
        Firebase ref = new Firebase("https://intense-fire-5226.firebaseio.com/");

        Firebase userRef = ref.child("users").child(user.getFacebookToken());

        userRef.setValue(user);
    }

    public int dateToAge(String date)
    {
        DateFormat df = new SimpleDateFormat("MM/DD/yyyy", Locale.US);
        Date birthday = null;

        try {
            birthday = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 23;
    }

    public void Progress(String text)
    {
        progress = new ProgressDialog(this);
        progress.setMessage(text);
        progress.setCancelable(false);
        progress.show();
    }

}
