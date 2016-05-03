package com.mti.meetme.Tools.Map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by thiba_000 on 19/04/2016.
 */
public class CalculateDistance {

    public static double getDistance(LatLng oldPos, LatLng newPos) {
        double lat1 = oldPos.latitude;
        double lng1 = oldPos.longitude;
        double lat2 = newPos.latitude;
        double lng2 = newPos.longitude;
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        int meterConversion = 1609;
        return Double.valueOf(dist * meterConversion);
    }
}
