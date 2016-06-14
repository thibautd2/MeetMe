package com.mti.meetme;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.FacebookHandler;
import com.mti.meetme.Tools.FriendsListAdapter;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.NewFriendsListAdapter;
import com.mti.meetme.Tools.Profil.ProfilsAdapter;
import com.mti.meetme.controller.FacebookUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thiba_000 on 12/04/2016.
 */
public class FriendsListActivity extends FragmentActivity {

    LinearLayoutManager mLinearLayoutManager;
    RecyclerView mRecyclerView;
    RecyclerView mRecyclerViewNFriend;
    ArrayList<User> friends;
    ArrayList<User> newfriends;
    FriendsListAdapter adapter;
    NewFriendsListAdapter adapterNFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        friends = new ArrayList<>();
        newfriends = new ArrayList<>();

        //todo list with sending invitation
        //todo title of user lists
        getfriends();
        getNewfriends();
        bindViews();
        populate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friend, menu);
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
            case R.id.menu_friends:
                Intent intent2 = new Intent(getApplicationContext(), EventUserFragmentActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bindViews()
    {
        mLinearLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_item2);
        mRecyclerViewNFriend = (RecyclerView) findViewById(R.id.list_item);
    }

    public void populate() {

        //friendList
        Log.e("friendlisactivity", "friends size: " + friends.size());
        adapter = new FriendsListAdapter(friends, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adapter.getSimpleItemTouchCallback());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        adapter.notifyDataSetChanged();

        //newFriendList
        adapterNFriend = new NewFriendsListAdapter(newfriends, this);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(adapterNFriend.getSimpleItemTouchCallback());
        LinearLayoutManager manager2 = new LinearLayoutManager(this);
        mRecyclerViewNFriend.setLayoutManager(manager2);
        mRecyclerViewNFriend.setAdapter(adapterNFriend);
        itemTouchHelper2.attachToRecyclerView(mRecyclerViewNFriend);
        adapterNFriend.notifyDataSetChanged();

        if (FacebookUser.getInstance().getFriendRequestReceived().equals("")) {
            ((TextView) findViewById(R.id.demande)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.friendsTxt)).setVisibility(View.GONE);
        }
        else {
            ((TextView) findViewById(R.id.demande)).setVisibility(View.VISIBLE);
            if (FacebookUser.getInstance().getMeetMeFriends().equals(""))
                ((TextView) findViewById(R.id.friendsTxt)).setVisibility(View.GONE);
            else
                ((TextView) findViewById(R.id.friendsTxt)).setVisibility(View.VISIBLE);
        }


    }

    public void getfriends()
    {
        Firebase ref = Network.find_MeetmeFriends(FacebookUser.getInstance().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String s = snapshot.getValue(String.class);
                friends.clear();

                for (String id : s.split(";")) {
                    if (id.equals(""))
                        continue;

                    final Firebase refFriends = Network.find_user(id);
                    refFriends.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            friends.add(dataSnapshot.getValue(User.class));
                            //adapter.update(friends);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getNewfriends()
    {
        Firebase ref = Network.find_FriendRequestReceived(FacebookUser.getInstance().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String s = snapshot.getValue(String.class);
                FacebookUser.getInstance().setFriendRequestReceived(s);

                newfriends.clear();

                for (String id : s.split(";")) {
                    if (id.equals(""))
                        continue;

                    final Firebase refFriends = Network.find_user(id);
                    refFriends.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newfriends.add(dataSnapshot.getValue(User.class));
                            adapterNFriend.notifyDataSetChanged();


                            ((TextView) findViewById(R.id.demande)).setVisibility(View.VISIBLE);
                            if (friends.size() != 0)
                                ((TextView) findViewById(R.id.friendsTxt)).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(FriendsListActivity.this, MapsActivity.class));
    }
}
