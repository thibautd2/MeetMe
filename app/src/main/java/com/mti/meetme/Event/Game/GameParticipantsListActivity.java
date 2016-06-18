package com.mti.meetme.Event.Game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.ChatActivity;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.Model.User;
import com.mti.meetme.R;
import com.mti.meetme.Tools.FriendsListAdapter;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.NewFriendsListAdapter;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.MyGame;
import com.mti.meetme.controller.UserList;

import java.util.ArrayList;

/**
 * Created by Alex on 17/06/2016.
 */

public class GameParticipantsListActivity extends AppCompatActivity{
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView mRecyclerView;
    ArrayList<User> particiants;
    ParticipantsListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_participants);

        particiants = new ArrayList<>();

        getParticipants();

        Log.e("gameParticipantActy", "onCreate: "+ particiants.size());
        bindViews();
        populate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        switch (item.getItemId()) {
            case R.id.menu_maps:
                startActivity(intent);
                return true;
            case R.id.menu_end:
                MyGame.getInstance().finishTheGame();
                Toast.makeText(this, "Partie terminée", Toast.LENGTH_LONG);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bindViews() {
        mLinearLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_item);
    }

    public void populate() {
        adapter = new ParticipantsListAdapter(particiants, MyGame.getInstance().getGame(),this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adapter.getSimpleItemTouchCallback());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        adapter.notifyDataSetChanged();
    }

    public void getParticipants() {
        Firebase ref = Network.find_ParticipantsToMyGame();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String s = snapshot.getValue(String.class);
                particiants.clear();

                Log.e("GameparticipantActy", "onDataChange: " + s );
                for (String id : s.split(";")) {
                    if (id.equals(""))
                        continue;

                    Log.e("GameparticipantActy", "onDataChange, id: " + id );

                    final Firebase refFriends = Network.find_user(id);
                    refFriends.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            particiants.add(dataSnapshot.getValue(User.class));
                            UserList.getInstance().sortListUser(particiants);
                            //adapter.update(friends);
                            adapter.notifyDataSetChanged();
                            Log.e("GameParticipantActy", "onDataChange: should work");
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
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
