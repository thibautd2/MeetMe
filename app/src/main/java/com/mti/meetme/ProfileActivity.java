package com.mti.meetme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.Profil.CarousselPager;
import com.mti.meetme.Tools.FacebookHandler;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.notifications.NotificationSender;
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

    private User user;
    private User currentUser;

    private int idLikesCount = 0;
    private int idFriendsCount = 0;

    public Pubnub pubnub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Firebase.setAndroidContext(this);

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
    public void onResume() {
        super.onResume();
        Firebase.setAndroidContext(this);
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
            case R.id.menu_edit:
                displayEditDescription();
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
            public void onClick(DialogInterface dialog, int whichButton) {
                FacebookUser.getInstance().setDescription(input.getText().toString());

                Firebase ref = Network.find_user(FacebookUser.getInstance().getUid());

                Map<String, Object> desc = new HashMap<>();
                desc.put("description", input.getText().toString());

                ref.updateChildren(desc, null);

                descriptionTextView.setText(input.getText().toString());
            }
        });


        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

  /*  private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        ActionBar.LayoutParams lp1 = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View customNav = LayoutInflater.from(this).inflate(R.layout.actionbar_custom_profile, null); // layout which contains your button.

        actionBar.setCustomView(customNav, lp1);
    }
*/
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

        if (user == null) {
            likesTextView.setText(getString(R.string.likes_title));
            friendsTextView.setText(getString(R.string.friends_title));
        } else {
            likesTextView.setText(getString(R.string.likes_common_title));
            friendsTextView.setText(getString(R.string.friends_common_title));

            if (currentUser.getLikesID().size() == 0) {
                likesTextView.setVisibility(View.GONE);
                likesLayout.setVisibility(View.GONE);
            }

            if (currentUser.getFriendsID().size() == 0) {
                friendsTextView.setVisibility(View.GONE);
                friendsLayout.setVisibility(View.GONE);
            }
        }

        descriptionTextView.setText(currentUser.getDescription());

        if (currentUser.getLikesID().size() > 0)
            getLikesPictures();

        if (currentUser.getMeetMeFriends() != null)
            getFriendsPictures();

        setFriendBtn();
    }

    public void setFriendBtn() {
        Firebase ref = Network.find_user(FacebookUser.getInstance().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);

                ImageButton imageButton = (ImageButton) findViewById(R.id.add_friends_btn);
                imageButton.setOnClickListener(null);

                if (u.getUid().equals(currentUser.getUid()))
                    imageButton.setVisibility(View.INVISIBLE);
                else if (u.haveThisFriend(currentUser.getUid()))
                    imageButton.setBackground(getResources().getDrawable(R.drawable.valide));
                else if (u.haveThisFriendRequestReceived(currentUser.getUid())) {
                    imageButton.setBackground(getResources().getDrawable(R.drawable.demande));
                    acceptInvitationBtn();
                } else if (u.haveThisFriendRequestSend(currentUser.getUid()))
                    imageButton.setBackground(getResources().getDrawable(R.drawable.intero));
                else
                    sendInvitationBtn();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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
                            String url = "";
                            if(response != null) {
                                 url = response.getJSONObject().getJSONObject("data").getString("url");
                            }
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
        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        if(idFriendsCount < currentUser.getFriendsID().size())
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + currentUser.getFriendsID().get(idFriendsCount) + "/picture",
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
                        if (idFriendsCount < currentUser.getFriendsID().size()) {
                            getFriendsPictures();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    }
                }
        ).executeAsync();
    }


    /*********************************************************************
     *
     *        FRIENDS HANDLING
     *
     *********************************************************************/

    private void sendInvitationBtn() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.add_friends_btn);

        assert imageButton != null;
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                friendRequest(FacebookUser.getInstance(), currentUser);
                Toast.makeText(getApplicationContext(), "Nouvelle demande d'ami envoyée à " + currentUser.getName(), Toast.LENGTH_LONG).show();
               // findViewById(R.id.add_friends_btn).setVisibility(View.INVISIBLE);
                getFriendsPictures();
               // setFriendBtn();
            }

            private void friendRequest(User user_a, User user_b) {
                String str = user_b.getFriendRequestReceived() + user_a.getUid() + ";";
                String str2 = user_a.getFriendRequestSend() + user_b.getUid() + ";";

                user_b.setFriendRequestReceived(str);
                user_a.setFriendRequestSend(str2);

                new NotificationSender().execute(user_b.getFcmID(), "Nouvelle demande d'ami reçue !", user_a.getName() + " vous a envoyé une demande");

                Firebase ref = Network.find_user(user_a.getUid());
                Map<String, Object> desc = new HashMap<>();
                desc.put("friendRequestSend", str2);
                ref.updateChildren(desc, null);

                Firebase ref2 = Network.find_user(user_b.getUid());
                Map<String, Object> desc2 = new HashMap<>();
                desc2.put("friendRequestReceived", str);
                ref2.updateChildren(desc2, null);
            }
        });
    }

    private void acceptInvitationBtn() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.add_friends_btn);

        assert imageButton != null;
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addFriend(FacebookUser.getInstance(), currentUser);
                addFriend(currentUser, FacebookUser.getInstance());

                FacebookUser.getInstance().removeFriendRequestReceived(currentUser.getUid());
                currentUser.removeFriendRequestSend(FacebookUser.getInstance().getUid());

                Toast.makeText(getApplicationContext(), "Nouvel amis ajouté", Toast.LENGTH_LONG).show();
                //findViewById(R.id.add_friends_btn).setVisibility(View.INVISIBLE);
                getFriendsPictures();
                //setFriendBtn();
            }
            private void addFriend(User user_a, User user_b) {
                String str = user_a.getMeetMeFriends();
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
