package com.mti.meetme;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;

import android.app.ActionBar;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.sql.Date;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

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
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.model.User;
import com.mti.meetme.tools.RoundedPicasso;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private MobileServiceClient mClient;
    private MobileServiceTable<User> mUsers;

    CallbackManager callbackManager;
    String fb_token, fb_fname, fb_lname, fb_img, fb_email;
    int fb_age;

    ProgressDialog progress;
    User currentUser;
    ImageView img_user;

    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindViews();

        FacebookSdk.sdkInitialize(getApplicationContext()); //Initialisation du facebook SDK

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_layout);
        TextView title = (TextView) findViewById(R.id.ActionBarLoginTitle);
        title.setText(getResources().getText(R.string.app_name));

        setContentView(R.layout.activity_login);
        img_user = (ImageView)findViewById(R.id.imageView);
        Picasso.with(getApplication()).load(R.drawable.chut).fit().centerCrop().transform(new RoundedPicasso()).into(img_user);

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
                        authenticate(loginResult.getAccessToken().getToken());
                        Progress(getString(R.string.progress_status_connect));
                        getFacebookData(loginResult);
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

        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            Progress(getString(R.string.progress_status_connect));
            authenticate(AccessToken.getCurrentAccessToken().getToken());
        }

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

    @Override
    public void onClick(View v)
    {

    }

    public class CreateUser extends AsyncTask<User, Void, User>
    {

        @Override
        protected User doInBackground(User... params) {
            if(params!=null && params.length>0) {
                mUsers.insert(params[0]);
            }
            return params[0];
        }
        @Override
        protected void onPostExecute(User us) {
            super.onPostExecute(us);
            if(progress!=null)
            progress.dismiss();
            if(us!=null) {
                currentUser = us;

                FacebookUser.setFacebookUser(currentUser);

                Toast.makeText(getApplicationContext(), "Create Connect", Toast.LENGTH_LONG).show();
                Picasso.with(getApplicationContext()).load(currentUser.getPic1()).fit().centerCrop().transform(new RoundedPicasso()).into(img_user);

            }
            else
                Toast.makeText(getApplication(), "Echec de la connexion", Toast.LENGTH_LONG).show();
        }
    }
    public class FindUser extends AsyncTask<User, Void, User> //Retrouve l'utilisateur
    {
        User create;
        @Override
        protected User doInBackground(User... params) {
            User current = null;
            if(params!=null && params.length>0) {
                create = params[0];
                ListenableFuture<MobileServiceList<User>> test  = mUsers.where().field("AzureID").eq(params[0].getAzureID()).execute();
                try {
                    if(test.get().size()>0)
                        current = test.get().get(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            if(current!= null)
                currentUser = current;
            return currentUser;
        }
        @Override
        protected void onPostExecute(User current) {
            super.onPostExecute(current);
            if (progress!=null)
                progress.dismiss();
            if(current == null)
            {
                Progress("Creation de compte");
                new CreateUser().execute(create);
            }
            else
            {
                FacebookUser.setFacebookUser(current);

                Picasso.with(getApplicationContext()).load(currentUser.getPic1()).fit().centerCrop().transform(new RoundedPicasso()).into(img_user);

                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(intent);
            }
        }
    }

    private void authenticate(String token) {
        JsonObject body = new JsonObject();
        body.addProperty("access_token", token);
        ListenableFuture<MobileServiceUser> mLogin = mClient.login(MobileServiceAuthenticationProvider.Facebook, body);
        Futures.addCallback(mLogin, new FutureCallback<MobileServiceUser>() {
            @Override
            public void onFailure(Throwable exc) {
                Toast.makeText(getApplication(), "L'authentification à échouée", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onSuccess(MobileServiceUser user) {
                User new_user = new User(fb_fname, fb_age, "", user.getUserId(), fb_email, fb_img);

                new FindUser().execute(new_user);
            }
        });
    }

    public void getFacebookData(LoginResult result)
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
                        fb_age = 21;
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
    public void Progress(String text)
    {
        progress = new ProgressDialog(this);
        progress.setMessage(text);
        progress.setCancelable(false);
        progress.show();
    }

}
