package com.mti.meetme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.Profil.CarousselPager;
import com.mti.meetme.Tools.FacebookHandler;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import com.pubnub.api.*;

public class ProfileActivity extends AppCompatActivity{

    private LinearLayout likesLayout;
    private LinearLayout friendsLayout;
    private ViewPager pager;

    private TextView  nameTextView;
    private TextView  ageTextView;
    private TextView  likesTextView;
    private TextView  friendsTextView;
    private TextView  descriptionTextView;

    private Menu menu;
    private MenuItem editDescItem;

    private User user;
    private User currentUser;

    private int idLikesCount = 0;
    private int idFriendsCount = 0;

    public Pubnub pubnub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = (User) getIntent().getSerializableExtra("User");

        pubnub = new Pubnub(getResources().getString(R.string.PublishKey), getResources().getString(R.string.PublishKey));

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

        if (user == null) {
            menu.findItem(R.id.menu_edit).setVisible(true);
            menu.findItem(R.id.menu_deco).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.menu_message).setVisible(true);
            menu.findItem(R.id.menu_heart).setVisible(true);
        }

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
            case R.id.menu_friends:
                Intent intent2 = new Intent(getApplicationContext(), FriendsListActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                return true;
            case R.id.menu_edit:
                displayEditDescription();
                return true;
            case R.id.menu_heart:
                //Fais tes bails ici thibaut
                return true;
            case R.id.menu_message:
                Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);

                Bundle bundle = new Bundle();
                bundle.putParcelable("User", user);
                chatIntent.putExtras(bundle);

                startActivity(chatIntent);
                return true;
            case R.id.menu_deco:
                Network.bdd_connexion.unauth();
                Intent decoIntent = new Intent(this, LoginActivity.class);
                startActivity(decoIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void displayEditDescription()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getResources().getString(R.string.description_popup_title));

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                FacebookUser.getInstance().setDescription(input.getText().toString());

                Firebase ref = Network.find_user(FacebookUser.getInstance().getUid());

                Map<String, Object> desc = new HashMap<>();
                desc.put("description", input.getText().toString());

                ref.updateChildren(desc, null);

                descriptionTextView.setText(input.getText().toString());
            }});


        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
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

        if (user == null)
        {
            likesTextView.setText(getString(R.string.likes_title));
            friendsTextView.setText(getString(R.string.friends_title));
        }
        else
        {
            likesTextView.setText(getString(R.string.likes_common_title));
            friendsTextView.setText(getString(R.string.friends_common_title));

            if (currentUser.getLikesID().size() == 0)
            {
                likesTextView.setVisibility(View.GONE);
                likesLayout.setVisibility(View.GONE);
            }

            if (currentUser.getFriendsID().size() == 0)
            {
                friendsTextView.setVisibility(View.GONE);
                friendsLayout.setVisibility(View.GONE);
            }
        }

        descriptionTextView.setText(currentUser.getDescription());

        if (currentUser.getLikesID().size() > 0)
            getLikesPictures();

        if (currentUser.getMeetMeFriends() != null)
            getFriendsPictures();


        if (user == null || FacebookUser.getInstance().haveThisFriend(currentUser.getUid()))
            findViewById(R.id.add_friends_btn).setVisibility(View.INVISIBLE);
        else
            addFriendButtonActivation();
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
        descriptionTextView = (TextView) findViewById(R.id.description_text);


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

                            String url = response.getJSONObject().getJSONObject("data").getString("url");
                            Picasso.with(ProfileActivity.this).load(url).transform(new RoundedPicasso()).into(newItem);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.profile_activity_icone), (int)getResources().getDimension(R.dimen.profile_activity_icone));
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
     //   Bundle params = new Bundle();
     //   params.putBoolean("redirect", false);
        if (currentUser.receiveMeetMeFriendsTab() == null)
            return;

        Log.e("profileActy", "getFriendsPictures, nb friends: " + currentUser.receiveMeetMeFriendsTab().size());

        friendsLayout.removeAllViews();

        for (String id : currentUser.receiveMeetMeFriendsTab()) {
            Firebase ref = Network.find_user(id);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("profileActy", "onDataChange");
                    ImageView newItem = new ImageView(ProfileActivity.this);
                    User u = dataSnapshot.getValue(User.class);


                    Log.e("profileActy", "onDataChange: " +  (int)getResources().getDimension(R.dimen.profile_activity_icone));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.profile_activity_icone), (int)getResources().getDimension(R.dimen.profile_activity_icone));
                    params.setMargins(10, 0, 10, 0);
                    friendsLayout.addView(newItem, params);
                    Picasso.with(ProfileActivity.this).load(u.getPic1()).placeholder(R.drawable.anonyme).transform(new RoundedPicasso()).into(newItem);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e("profileActy", "onCancelled");
                }
            });
        }
        /*
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + currentUser.receiveMeetMeFriendsTab().get(idFriendsCount) + "/picture",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback()
                {
                    public void onCompleted(GraphResponse response) {
                        try {
                        ImageView newItem = new ImageView(ProfileActivity.this);
                        String url = response.getJSONObject().getJSONObject("data").getString("url");
                        if(url != null) {
                            Picasso.with(ProfileActivity.this).load(url).transform(new RoundedPicasso()).into(newItem);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.height = friendsLayout.getHeight();
                            params.width = params.height;
                            params.setMargins(10, 0, 10, 0);
                            friendsLayout.addView(newItem, params);
                            idFriendsCount++;
                        }
                        if (idFriendsCount < currentUser.receiveMeetMeFriendsTab().size()) {
                            getFriendsPictures();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    }
                }
        ).executeAsync();
    */}

    private void addFriendButtonActivation() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.add_friends_btn);

        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addFriend(FacebookUser.getInstance(), currentUser);
                addFriend(currentUser, FacebookUser.getInstance());

                findViewById(R.id.add_friends_btn).setVisibility(View.INVISIBLE);
                //todo should clear the list before
                getFriendsPictures();
            }

            private void addFriend(User user_a, User user_b) {
                String str = user_a.getMeetMeFriends();
                if (str == null)
                    str = "";
                str += user_b.getUid() + ";";

                user_a.setMeetMeFriends(str);

                Firebase ref = Network.find_user(user_a.getUid());

                Map<String, Object> desc = new HashMap<>();
                desc.put("meetMeFriends", str);

                ref.updateChildren(desc, null);
            }
        });
    }
}
