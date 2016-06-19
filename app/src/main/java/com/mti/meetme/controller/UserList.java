package com.mti.meetme.controller;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.SortUserList;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.Map.CalculateDistance;
import com.mti.meetme.Tools.Network.Network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by thiba_000 on 12/04/2016.
 */
public class UserList {
    private static UserList ourInstance = new UserList();

    public static UserList getInstance() {
        return ourInstance;
    }


    public void sortListUser(ArrayList<User> users) {
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Double.compare(getDistToMe(o1), getDistToMe(o2));
            }
        });
    }

    public void sortListEvent(ArrayList<Event> events) {
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return Double.compare(getDistToMe(o1), getDistToMe(o2));
            }
        });
    }

    public double getDistToMe(User u1) {
        LatLng latLng = new LatLng(u1.getLatitude(), u1.getLongitude());

        User u2 = FacebookUser.getInstance();
        LatLng latLng2 = new LatLng(u2.getLatitude(), u2.getLongitude());

        return CalculateDistance.getDistance(latLng, latLng2);
    }

    public double getDistToMe(Event e1) {
        LatLng latLng = new LatLng(e1.getLatitude(), e1.getLongitude());

        User u2 = FacebookUser.getInstance();
        if(u2 != null) {
            LatLng latLng2 = new LatLng(u2.getLatitude(), u2.getLongitude());

            return CalculateDistance.getDistance(latLng, latLng2);
        }
        else // erreur
            return 10;
    }
}
