package com.mti.meetme.Tools.Map;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.LocationSource;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.controller.FacebookUser;

/**
 * Created by thiba_000 on 18/04/2016.
 */

public class FollowMeLocationSource implements LocationSource, LocationListener {

    private OnLocationChangedListener mListener;
    private LocationManager locationManager;
    private final Criteria criteria = new Criteria();
    private String bestAvailableProvider;
    private final int minTime = 20000;     // minimum time interval between location updates, in milliseconds
    private final int minDistance = 1;    // minimum distance between location updates, in meters

    public FollowMeLocationSource(Context context) {
        // Get reference to Location Manager
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        // Specify Location Provider criteria
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
    }
    public void getBestAvailableProvider() {
            /* The preffered way of specifying the location provider (e.g. GPS, NETWORK) to use
             * is to ask the Location Manager for the one that best satisfies our criteria.
             * By passing the 'true' boolean we ask for the best available (enabled) provider. */
        bestAvailableProvider = locationManager.getBestProvider(criteria, true);
    }

    /* Activates this provider. This provider will notify the supplied listener
     * periodically, until you call deactivate().
     * This method is automatically invoked by enabling my-location layer. */
    @Override
    public void activate(OnLocationChangedListener listener) {
        // We need to keep a reference to my-location layer's listener so we can push forward
        // location updates to it when we receive them from Location Manager.
        mListener = listener;

        // Request location updates from Location Manager
        if (bestAvailableProvider != null) {
            locationManager.requestLocationUpdates(bestAvailableProvider, minTime, minDistance, this);
        } else {
            // (Display a message/dialog) No Location Providers currently available.
        }
    }
    /* Deactivates this provider.
     * This method is automatically invoked by disabling my-location layer. */
    @Override
    public void deactivate() {
        // Remove location updates from Location Manager
        locationManager.removeUpdates(this);
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location) {
            /* Push location updates to the registered listener..
             * (this ensures that my-location layer will set the blue dot at the new/received location) */
        if (mListener != null) {
            mListener.onLocationChanged(location);
        }
        Log.e("VERRIFFF", "fezfe");
        FacebookUser.getInstance().setLongitude(location.getLongitude());
        FacebookUser.getInstance().setLatitude(location.getLatitude());
        MapsActivity.sendPosition();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    @Override
    public void onProviderEnabled(String s) {
    }
    @Override
    public void onProviderDisabled(String s) {
    }
}