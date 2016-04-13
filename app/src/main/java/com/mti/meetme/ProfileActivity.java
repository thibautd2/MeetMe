package com.mti.meetme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.menu.MenuView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.CarousselPager;
import com.mti.meetme.Tools.FacebookHandler;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProfileActivity extends ActionBarActivity{

    private LinearLayout likesLayout;
    private LinearLayout friendsLayout;
    private ViewPager pager;

    private TextView  nameTextView;
    private TextView  ageTextView;
    private TextView  likesTextView;
    private TextView  friendsTextView;

    private User user;
    private User currentUser;

    private int idLikesCount = 0;
    private int idFriendsCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = (User) getIntent().getSerializableExtra("User");

        try {
            bindViews();
            populateViews();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

    private void populateViews() throws JSONException, InterruptedException {
        if (user == null)
            currentUser = FacebookUser.getInstance();
        else {
            FacebookHandler handler = new FacebookHandler(user);
            currentUser = handler.loadUserCommonData();
        }

        CarousselPager adapter = new CarousselPager(getSupportFragmentManager());
        adapter.setUser(currentUser);
        pager.setAdapter(adapter);

        nameTextView.setText(currentUser.getName() + ",");
        ageTextView.setText("" + currentUser.convertBirthdayToAge());
        likesTextView.setText(getString(R.string.likes_title));
        friendsTextView.setText(getString(R.string.friends_title));

        getLikesPictures();
        getFriendsPictures();
    }


    private void bindViews()
    {
        nameTextView = (TextView) findViewById(R.id.name_textview);
        ageTextView = (TextView) findViewById(R.id.age_textview);
        likesTextView = (TextView) findViewById(R.id.likes_textview);
        friendsTextView = (TextView) findViewById(R.id.friends_textview);
        likesLayout = (LinearLayout) findViewById(R.id.likes_layout);
        friendsLayout = (LinearLayout) findViewById(R.id.friends_layout);
        pager = (ViewPager) findViewById(R.id.user_img_list);
    }

    /*********************************************************************
     *
     *        FACEBOOK PICTURES REQUEST
     *
     *********************************************************************/

   private void getLikesPictures()
    {
        Bundle params = new Bundle();
        params.putBoolean("redirect", false);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + currentUser.getLikesID().get(idLikesCount) + "/picture",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback(){
                        public void onCompleted(GraphResponse response) {try {
                            ImageView newItem = new ImageView(ProfileActivity.this);
                            Picasso.with(ProfileActivity.this).load(response.getJSONObject().getJSONObject("data").getString("url")).transform(new RoundedPicasso()).into(newItem);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.height = likesLayout.getHeight();
                            params.width = params.height;
                            params.setMargins(10, 0, 10, 0);
                            likesLayout.addView(newItem, params);

                            idLikesCount++;

                            if (idLikesCount < currentUser.getLikesID().size()) {
                                getLikesPictures();
                            }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
            ).executeAsync();

   }

    private void getFriendsPictures()
    {
        Bundle params = new Bundle();
        params.putBoolean("redirect", false);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + currentUser.getFriendsID().get(idFriendsCount) + "/picture",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback()
                {
                    public void onCompleted(GraphResponse response) {try {
                        ImageView newItem = new ImageView(ProfileActivity.this);
                        Picasso.with(ProfileActivity.this).load(response.getJSONObject().getJSONObject("data").getString("url")).transform(new RoundedPicasso()).into(newItem);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.height = likesLayout.getHeight();
                        params.width = params.height;
                        params.setMargins(10, 0, 10, 0);
                        friendsLayout.addView(newItem, params);

                        idFriendsCount++;

                        if (idFriendsCount < currentUser.getFriendsID().size()) {
                            getFriendsPictures();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }

                }
        ).executeAsync();
    }
}
