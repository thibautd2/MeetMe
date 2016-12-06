package com.mti.meetme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.FriendsListAdapter;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.NewFriendsListAdapter;
import com.mti.meetme.controller.FacebookUser;

import java.util.ArrayList;

/**
 * Created by thiba_000 on 12/04/2016.
 */
public class FriendsListActivity extends Fragment {
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView mRecyclerView;
    RecyclerView mRecyclerViewNFriend;
    ArrayList<User> friends;
    ArrayList<User> newfriends;
    FriendsListAdapter adapter;
    NewFriendsListAdapter adapterNFriend;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        friends = new ArrayList<>();
        newfriends = new ArrayList<>();

        //todo list with sending invitation
        //todo title of user lists
        getfriends();
        bindViews();
        populate();
        getNewfriends();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_friends_list, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void bindViews() {
        mLinearLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.list_item2);
        mRecyclerViewNFriend = (RecyclerView) getView().findViewById(R.id.list_item);
    }

    public void populate() {

        //friendList
        Log.e("friendlisactivity", "friends size: " + friends.size());
        adapter = new FriendsListAdapter(friends, getActivity());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adapter.getSimpleItemTouchCallback());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        adapter.notifyDataSetChanged();

        //newFriendList
        adapterNFriend = new NewFriendsListAdapter(newfriends, getActivity());
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(adapterNFriend.getSimpleItemTouchCallback());
        LinearLayoutManager manager2 = new LinearLayoutManager(getActivity());
        mRecyclerViewNFriend.setLayoutManager(manager2);
        mRecyclerViewNFriend.setAdapter(adapterNFriend);
        itemTouchHelper2.attachToRecyclerView(mRecyclerViewNFriend);
        adapterNFriend.notifyDataSetChanged();

        if (FacebookUser.getInstance().getFriendRequestReceived().equals("")) {
            ((TextView) getView().findViewById(R.id.demande)).setVisibility(View.GONE);
            ((TextView) getView().findViewById(R.id.friendsTxt)).setVisibility(View.GONE);
        } else {
            ((TextView) getView().findViewById(R.id.demande)).setVisibility(View.VISIBLE);
            if (FacebookUser.getInstance().getMeetMeFriends().equals(""))
                ((TextView) getView().findViewById(R.id.friendsTxt)).setVisibility(View.GONE);
            else
                ((TextView) getView().findViewById(R.id.friendsTxt)).setVisibility(View.VISIBLE);
        }


    }

    public void getfriends() {
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

    public void getNewfriends() {
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



                            if (friends.size() != 0) {
                                ((TextView) getView().findViewById(R.id.friendsTxt)).setVisibility(View.VISIBLE);
                                ((TextView) getView().findViewById(R.id.demande)).setVisibility(View.VISIBLE);
                            }
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
}
