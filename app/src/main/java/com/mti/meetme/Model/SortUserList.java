package com.mti.meetme.Model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mti.meetme.Tools.Map.CalculateDistance;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.TodayDesire;

/**
 * Created by Alex on 14/06/2016.
 */

public class SortUserList {
    public int distanceToSearch;
    public boolean displayMen;
    public boolean displayWomen;

    private static SortUserList ourInstance = new SortUserList();

    public static SortUserList getInstance() {
        return ourInstance;
    }

    private SortUserList() {
        distanceToSearch = 10000;
        displayMen = true;
        displayWomen = true;
    }

    //todo should be on controller
    public boolean user_correspond(User u)
    {
        if (getDistToMe(u) < distanceToSearch / 1000 && (getGenderStr().equals("all") || u.getGender().equals(getGenderStr())) &&
                (FacebookUser.getInstance().getEnvie().equals(TodayDesire.Desire.Everything.toString()) ||
                        u.getEnvie().equals(TodayDesire.Desire.Everything.toString()) || u.getEnvie().equals(FacebookUser.getInstance().getEnvie())))
            return true;

        Log.e("SorlListUser", "user_correspond: " + FacebookUser.getInstance().getEnvie());
        return false;
    }

    public double getDistToMe(User u1) {
        LatLng latLng = new LatLng(u1.getLatitude(), u1.getLongitude());

        User u2 = FacebookUser.getInstance();
        LatLng latLng2 = new LatLng(u2.getLatitude(), u2.getLongitude());

        return CalculateDistance.getDistance(latLng, latLng2);
    }

    public String getGenderStr() {
        if (displayMen && displayWomen)
            return "all";
        else if (displayMen)
            return "male";

        return "female";
    }
}
