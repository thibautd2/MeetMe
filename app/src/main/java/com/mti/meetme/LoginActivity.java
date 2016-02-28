package com.mti.meetme;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

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
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.mti.meetme.Model.User;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    private MobileServiceClient mClient;
    private MobileServiceTable<User> mUsers;
    CallbackManager callbackManager;
    String fb_token, fb_fname, fb_lname, fb_img, fb_email, fb_birth_year;
    LoginResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext()); //Initialisation du facebook SDK
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_layout);
        setContentView(R.layout.activity_login);
        try {
            init_connection(); //Connexion au Mobile Service
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        callbackManager = CallbackManager.Factory.create(); // permet les callback
        LoginManager.getInstance().registerCallback(callbackManager, /// CONNEXION USING FACEBOOK SDK
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        result = loginResult;
                        get_facebook_data();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(), "Problème réseau", Toast.LENGTH_LONG).show();
                    }
                });
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends")); //permissions
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
        Log.e("activityresult", "activity result");
    }

    public class CreateUser extends AsyncTask<User, Void, Void> //Ajout d' utilisateur dans la table User
    {
        @Override
        protected Void doInBackground(User... params) {
            if(params!=null && params.length>0)
                mUsers.insert(params[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void authenticate() {
        String accessToken = result.getAccessToken().getToken();
        JsonObject body = new JsonObject();
        body.addProperty("access_token", result.getAccessToken().getToken());
        ListenableFuture<MobileServiceUser> mLogin = mClient.login(MobileServiceAuthenticationProvider.Facebook, body);
        Futures.addCallback(mLogin, new FutureCallback<MobileServiceUser>() {
            @Override
            public void onFailure(Throwable exc) {
                Toast.makeText(getApplication(), "L'authentification à échouée", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onSuccess(MobileServiceUser user) {
                user.getAuthenticationToken();
                Toast.makeText(getApplication(), "Connecté", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void get_facebook_data()
    {   fb_token = result.getAccessToken().getToken();
        GraphRequest request = GraphRequest.newMeRequest(
                result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response)
                    {
                        String[] splited = object.optString("name").split(" ");
                        fb_fname = splited[0];
                        if (splited.length > 1)
                            fb_lname = splited[1];
                        try {
                            fb_img = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            Log.e("IMAGE URL", fb_img);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        fb_email = object.optString("email");
                        fb_birth_year = object.optString("birthday");
                        authenticate(); /// AUTHENTICATE TO AZURE
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,picture.height(300),name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

        public void init_connection() throws MalformedURLException {
        mClient = new MobileServiceClient(
                "https://meetmee.azurewebsites.net",
                this);
        mUsers = mClient.getTable(User.class);
    }
}
