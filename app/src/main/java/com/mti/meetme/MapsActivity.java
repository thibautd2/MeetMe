package com.mti.meetme;

import android.app.ActionBar;
import android.content.Context;
import android.hardware.camera2.params.Face;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.Network;
import com.mti.meetme.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new GetAllUsersPosition().execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        GetCurrentLocation();
    }

    private void GetCurrentLocation() {
        double[] d = getlocation();
        LatLng pos = new LatLng(d[0], d[1]);
        FacebookUser.getInstance().setLatitude(pos.latitude);
        FacebookUser.getInstance().setLongitude(pos.longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d[0], d[1]), 17));
        new SendPosition().execute();
    }

    public double[] getlocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location l = null;
        for (int i = 0; i < providers.size(); i++) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null)
                break;
        }
        double[] gps = new double[2];
        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        providers.removeAll(lm.getAllProviders());
        return gps;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public class SendPosition extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            List<Pair<String, String>> modifs= new ArrayList<Pair<String, String> >();;
            modifs.add(new Pair<String, String>("Latitude", String.valueOf(FacebookUser.getInstance().getLatitude())));
            modifs.add(new Pair<String, String>("Longitude", String.valueOf(FacebookUser.getInstance().getLatitude())));
            Network.getUsers().update(FacebookUser.getInstance(), modifs);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            }
    }
    public class GetAllUsersPosition extends AsyncTask<Void, Void, Void> // A MODIFIER UTILISER UN RAYON
    {

        MobileServiceList<User> all;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                all = Network.getUsers().select("Longitude", "Latitude", "AzureID", "Name").execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(all!=null) {
                for (User u : all) {
                    if(u.getLongitude()!=null && u.getLatitude()!=null)
                         mMap.addMarker(new MarkerOptions().position(new LatLng(
                                           u.getLatitude(),u.getLongitude())).title(u.getName()));
                }
            }
        }
    }
}
