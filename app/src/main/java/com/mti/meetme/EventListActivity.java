package com.mti.meetme;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.Event.EventAdapter;
import com.mti.meetme.Tools.Map.CalculateDistance;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.Profil.ProfilsAdapter;
import com.mti.meetme.controller.FacebookUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by thiba_000 on 13/06/2016.
 */

public class EventListActivity extends FragmentActivity {
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView mRecyclerView;
    ArrayList<Event> events;
    EventAdapter adapter;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profils_list);

        simpleItemTouchCallback = getNewItemTocuh();

        events = new ArrayList<>();
        getall_event();
        bindViews();
        populate();
    }

    protected ItemTouchHelper.SimpleCallback getNewItemTocuh() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                adapter.remove(viewHolder.getAdapterPosition());
            }
        };
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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        //mLinearLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_item);
    }

    public void populate()
    {
        adapter = new EventAdapter(events, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(gaggeredGridLayoutManager);
        mRecyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        adapter.notifyDataSetChanged();
    }

    public void getall_event()
    {
        Firebase ref = Network.connexion_to_event;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                events.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Event event = postSnapshot.getValue(Event.class);
                    if(event.visibility.compareTo("all") == 0 || (event.getInvited()!=null && FacebookUser.getInstance().getMeetMeFriends().contains(event.ownerid)) || event.ownerid.compareTo(FacebookUser.getInstance().getUid()) == 0) {
                        events.add(event);}
                }
                adapter.notifyDataSetChanged();
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
        startActivity(new Intent(EventListActivity.this, MapsActivity.class));

    }

    private double getDistToMe(User u1) {
        LatLng latLng = new LatLng(u1.getLatitude(), u1.getLongitude());

        User u2 = FacebookUser.getInstance();
        LatLng latLng2 = new LatLng(u2.getLatitude(), u2.getLongitude());

        return CalculateDistance.getDistance(latLng, latLng2);
    }
}

