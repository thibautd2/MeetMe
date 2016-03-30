package com.mti.meetme;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.CarousselPager;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProfileActivity extends ActionBarActivity{

    LinearLayout likesLayout;
    LinearLayout friendsLayout;
    public static ViewPager pager;

    ImageView profilePic;
    TextView  nameTextView;
    TextView  title;
    TextView  ageTextView;
    TextView  likesTextView;
    TextView  friendsTextView;

    User user;
    User currentUser;

    private ArrayList<String> resultLikes;
    private ArrayList<String> resultFriends;

    private int idLikesCount = 0;
    private int idFriendsCount = 0;
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = (User) getIntent().getSerializableExtra("User");

        bindViews();

        try {
            populateViews();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_maps:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            case R.id.menu_settings:
                displaySettings(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displaySettings(boolean visible) {
        MenuView.ItemView item = (MenuView.ItemView) findViewById(R.id.menu_settings);

        if (visible)
            item.getItemData().setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        else
            item.getItemData().setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    private void populateViews() throws JSONException {
        resultLikes = new ArrayList<>();
        resultFriends = new ArrayList<>();

        if (user == null)
            currentUser = FacebookUser.getInstance();
        else {
            populateViewsOther();
            return;
        }

        CarousselPager adapter = new CarousselPager(getSupportFragmentManager());
        adapter.setUser(currentUser);
        pager.setAdapter(adapter);

//      Picasso.with(this).load(currentUser.getPic1()).fit().centerCrop().into(profilePic);
        nameTextView.setText(currentUser.getName() + ",");
        ageTextView.setText("" + currentUser.convertBirthdayToAge());
        likesTextView.setText(getString(R.string.likes_title));
        friendsTextView.setText(getString(R.string.friends_title));
//      title.setText(R.string.profile_title);

        getLikesPictures(currentUser.getLikes());
        getFriendsPictures(currentUser.getFriends());
    }


    private void populateViewsOther() throws JSONException {

      //Picasso.with(this).load(user.getPic1()).fit().centerCrop().into(profilePic);
        nameTextView.setText(user.getName() + ",");
        ageTextView.setText("" + user.convertBirthdayToAge());
        likesTextView.setText(getString(R.string.likes_common_title));
        friendsTextView.setText(getString(R.string.friends_common_title));
        getUserFriends();
        getUserLikes();

        CarousselPager adapter = new CarousselPager(getSupportFragmentManager());
        adapter.setUser(user);
        pager.setAdapter(adapter);
    }

    private void bindViews()
    {
        nameTextView = (TextView) findViewById(R.id.name_textview);
        title = (TextView) findViewById(R.id.ActionBarLoginTitle);
        ageTextView = (TextView) findViewById(R.id.age_textview);
        likesTextView = (TextView) findViewById(R.id.likes_textview);
        friendsTextView = (TextView) findViewById(R.id.friends_textview);
        likesLayout = (LinearLayout) findViewById(R.id.likes_layout);
        friendsLayout = (LinearLayout) findViewById(R.id.friends_layout);
        pager = (ViewPager) findViewById(R.id.user_img);
    }

    /*********************************************************************
     *
     *        FRIENDS HANDLING / I LIKE TO MAkE  ART BECAUSE I AM AN ARTIST
     *
     *********************************************************************/

    private void getFriendsPictures(JSONObject friends) throws JSONException {

        if(friends!=null) {
            JSONArray friendsArray = friends.getJSONArray("data");
            idFriendsCount = friendsArray.length();

            ArrayList<String> friendsId = new ArrayList<String>();

            for (int i = 0; i < friendsArray.length(); i++) {
                friendsId.add(friendsArray.getJSONObject(i).getString("id"));
            }
            getFriendsPicturesURL(friendsId);
        }
    }

    private void getFriendsPicturesURL(final ArrayList<String> likesId)
    {
        for (int i = 0; i < likesId.size(); i++) {
            Bundle params = new Bundle();
            params.putBoolean("redirect", false);

            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + likesId.get(i) + "/picture",
                    params,
                    HttpMethod.GET,
                    new GraphRequest.Callback()
                    {
                        public void onCompleted(GraphResponse response) {
                            try {
                                resultFriends.add(response.getJSONObject().getJSONObject("data").getString("url"));

                                if (resultFriends.size() == idFriendsCount)
                                {
                                    for (int i = 0; i < resultFriends.size(); i++)
                                    {
                                        ImageView newItem = new ImageView(ProfileActivity.this);

                                        Picasso.with(ProfileActivity.this).load(resultFriends.get(i)).transform(new RoundedPicasso()).into(newItem);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        params.height = friendsLayout.getHeight();
                                        params.width = params.height;
                                        params.setMargins(10, 0, 10, 0);
                                        friendsLayout.addView(newItem, params);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
            ).executeAsync();
        }
    }

    private void getFriendsInCommonPictures(JSONObject friends) throws JSONException {

        if(friends!= null) {
            JSONArray friendsArray = friends.getJSONArray("data");

            ArrayList<String> friendsId = new ArrayList<String>();
            ArrayList<String> friendsIdCurrent = new ArrayList<String>();

            for (int i = 0; i < friendsArray.length(); i++) {
                friendsId.add(friendsArray.getJSONObject(i).getString("id"));
            }

            for (int i = 0; i < FacebookUser.getInstance().getFriends().length(); i++) {
                friendsIdCurrent.add(FacebookUser.getInstance().getFriends().getJSONArray("data").getJSONObject(i).getString("id"));
            }

            friendsId.retainAll(friendsIdCurrent);
            idFriendsCount = friendsId.size();
            getFriendsPicturesURL(friendsId);
        }
    }

    public void getUserFriends() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + user.getUid().split(":")[1] + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        user.setFriends(response.getJSONObject());
                        try {
                            getFriendsInCommonPictures(user.getFriends());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }
    /*********************************************************************
     *
     *                  LIKES HANDLING // WITH PASSION
     *
     *********************************************************************/

    private void getLikesPictures(JSONObject likes) throws JSONException {

        if (likes != null)
        {
        JSONArray likesArray = likes.getJSONArray("data");
        idLikesCount = likesArray.length();

        ArrayList<String> likesId = new ArrayList<String>();

        for (int i = 0; i < likesArray.length(); i++) {
            likesId.add(likesArray.getJSONObject(i).getString("id"));
        }
            this.likesId = likesId;
            getLikesPicturesURL();
        }
    }

    private void getLikesInCommonPictures(JSONObject likes) throws JSONException {

        JSONArray likesArray = likes.getJSONArray("data");

        ArrayList<String> likesId = new ArrayList<String>();
        ArrayList<String> likesIdCurrent = new ArrayList<String>();

        for (int i = 0; i < likesArray.length(); i++)
        {
            likesId.add(likesArray.getJSONObject(i).getString("id"));
        }

        for (int i = 0; i < FacebookUser.getInstance().getLikes().length(); i++)
        {
            likesIdCurrent.add(FacebookUser.getInstance().getLikes().getJSONArray("data").getJSONObject(i).getString("id"));
        }

        likesId.retainAll(likesIdCurrent);

        idLikesCount = likesId.size();
        this.likesId = likesId;
        getLikesPicturesURL();
    }

    ArrayList<String> likesId = new ArrayList<>();
    int count = 0;

    private void getLikesPicturesURL()//final ArrayList<String> likesId)
    {
        if (likesId.size() == 0)
            return;

            Bundle params = new Bundle();
            params.putBoolean("redirect", false);

            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + likesId.get(count) + "/picture",
                    params,
                    HttpMethod.GET,
                    new GraphRequest.Callback()
                    {
                        public void onCompleted(GraphResponse response) {
                            try {
                                //resultLikes.add(response.getJSONObject().getJSONObject("data").getString("url"));
                                ImageView newItem = new ImageView(ProfileActivity.this);
                                Picasso.with(ProfileActivity.this).load(response.getJSONObject().getJSONObject("data").getString("url")).transform(new RoundedPicasso()).into(newItem);

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.height = likesLayout.getHeight();
                                params.width = params.height;
                                params.setMargins(10, 0, 10, 0);
                                likesLayout.addView(newItem, params);
                                count++;

                                if (count < idLikesCount) {
                                    getLikesPicturesURL();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
            ).executeAsync();

   }

    public void getUserLikes()
    {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + user.getUid().split(":")[1] + "/likes",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        user.setLikes(response.getJSONObject());
                        try {
                            getLikesInCommonPictures(user.getLikes());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }
}
