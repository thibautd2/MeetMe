package com.mti.meetme.controller;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
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
    private static ArrayList<User> _all_users = new ArrayList<>();

    private static UserList ourInstance = new UserList();

    public static UserList getInstance() {
        return ourInstance;
    }

    public static ArrayList<User> get_all_users() {
        return _all_users;
    }

    public static void setAll_users(ArrayList<User> users) { _all_users = users;}

    public void updateUserList(DataSnapshot snapshot) {
        _all_users.clear();
        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
            User u = postSnapshot.getValue(User.class);
            if(u != null && u.getUid() != null && u.getUid().compareTo(FacebookUser.getInstance().getUid())!=0 && SortUserList.getInstance().user_correspond(u))
                _all_users.add(u);
        }

        sortList();
    }

    public void sortList() {
        Collections.sort(_all_users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
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
}
