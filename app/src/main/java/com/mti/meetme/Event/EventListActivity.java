package com.mti.meetme.Event;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.R;
import com.mti.meetme.Tools.Event.EventAdapter;
import com.mti.meetme.Tools.Map.CalculateDistance;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.MyGame;
import com.mti.meetme.controller.TodayDesire;
import com.mti.meetme.controller.UserList;

import java.util.ArrayList;
import java.util.Arrays;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by thiba_000 on 13/06/2016.
 */

public class EventListActivity extends Fragment {
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView mRecyclerView;
    ArrayList<Event> events;
    EventAdapter adapter;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        simpleItemTouchCallback = getNewItemTocuh();
        events = new ArrayList<>();
        getall_event();
        bindViews();
        populate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profils_list, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void bindViews()
    {
        //mLinearLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.list_item);
    }

    public void populate()
    {
        adapter = new EventAdapter(events, getActivity());
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
                for (DataSnapshot postSnapshot : snapshot.getChildren())
                {
                    Event event = postSnapshot.getValue(Event.class);

                    if(checkEventVisibility(event)) {
                        events.add(event);}
                }

                UserList.getInstance().sortListEvent(events);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    //todo fucking doublon on MapsActivity -> put it on a eventTools or something
    private boolean checkEventVisibility(Event event) {
        if (event == null || event.visibility == null)
            return false;

        if (!MyGame.getInstance().isNotFinished(event) && event.endDate.compareTo("")!=0)
            return false;

        if (event.getOwnerid().equals(FacebookUser.getInstance().getUid()))
            return true;

        if (FacebookUser.getInstance().getEnvie() != TodayDesire.Desire.Everything.toString() && !event.getCategorie().equals(FacebookUser.getInstance().getEnvie()))
            return false;

        ArrayList<String> visibilityList;
        if (event.getVisibility().contains(";")) {
            String visible[] = event.getVisibility().split(";");
            visibilityList = new ArrayList<>(Arrays.asList(visible));
        }
        else {
            visibilityList = new ArrayList<>();
            visibilityList.add(event.getVisibility());
        }

        if ((visibilityList.get(0).equals("friend") || visibilityList.get(0).equals("friends")) && FacebookUser.getInstance().haveThisFriend(event.getOwnerid())) {
            if (visibilityList.size() == 1)
                return true;
            if (visibilityList.size() > 1 && (visibilityList.get(1).equals(FacebookUser.getInstance().receiveGoodGender()) ||  visibilityList.get(1).equals("all")))
                return true;
            return false;
        }

        if (visibilityList.get(0).equals("friend_selection") && event.getInvited() != null && event.getInvited().contains(FacebookUser.getInstance().getUid()))
            return true;

        if (visibilityList.get(0).equals("all")) {
            if (visibilityList.size() == 1 || visibilityList.get(1).equals("all"))
                return true;
            if (visibilityList.size() > 1 && visibilityList.get(1).equals(FacebookUser.getInstance().getGender()))
                return true;
            return false;
        }

        return false;
    }

    private double getDistToMe(User u1) {
        LatLng latLng = new LatLng(u1.getLatitude(), u1.getLongitude());

        User u2 = FacebookUser.getInstance();
        LatLng latLng2 = new LatLng(u2.getLatitude(), u2.getLongitude());

        return CalculateDistance.getDistance(latLng, latLng2);
    }
}

