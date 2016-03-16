package com.mti.meetme;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.mti.meetme.Tools.Network;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.RoundedPicasso;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    public MobileServiceList<User> all;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //new GetAllUsersPosition().execute();
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
        sendPosition();
    }

    public double[] getlocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location location = null;
        for (int i = 0; i < providers.size(); i++) {
            location = locationManager.getLastKnownLocation(providers.get(i));
            if (location != null)
                break;
        }
        double[] gps = new double[2];
        if (location != null) {
            gps[0] = location.getLatitude();
            gps[1] = location.getLongitude();
        }
        providers.removeAll(locationManager.getAllProviders());
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

    private void sendPosition()
    {
        Firebase ref = Network.find_user(FacebookUser.getInstance().getUid());
        ref.child("Latitude").setValue(String.valueOf(FacebookUser.getInstance().getLatitude()));
        ref.child("Longitude").setValue(String.valueOf(FacebookUser.getInstance().getLongitude()));
    }

    /*public class GetAllUsersPosition extends AsyncTask<Void, Void, Void> // A MODIFIER UTILISER UN RAYON
    {

        @Override
        protected Void doInBackground(Void... params) {
            try {
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
            int i =0;
            if(all!=null) {
                for (User u : all) {
                    if(u.getLongitude()!=null && u.getLatitude()!=null)
                        if(u.getGender()!=null && u.getGender()==true)
                            mMap.addMarker(new MarkerOptions().position(new LatLng(
                                              u.getLatitude(),u.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.hmarker)).snippet(String.valueOf(i)));
                        else
                            mMap.addMarker(new MarkerOptions().position(new LatLng(
                                    u.getLatitude(),u.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.fmarker)).snippet(String.valueOf(i)));
                    i++;
                }
            }
            init_infos_window();
        }

    }*/

    public void init_infos_window()
    {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String r = marker.getId().substring(1);
                User user = all.get(Integer.parseInt(r));
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("User", user);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        GoogleMap.InfoWindowAdapter adapt = new GoogleMap.InfoWindowAdapter() {
            Boolean not_first_time_showing_info_window = false;

            @Override
            public View getInfoWindow(Marker arg0) {
                int id = Integer.parseInt(arg0.getSnippet());
                User u =  all.get(id);
                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                ImageView img = (ImageView) v.findViewById(R.id.user_image);
                TextView name = (TextView) v.findViewById(R.id.user_name);
                TextView age = (TextView) v.findViewById(R.id.user_age);
                name.setText(u.getName());
                if (not_first_time_showing_info_window)
                {
                    Picasso.with(MapsActivity.this)
                            .load(u.getPic1())
                            .transform(new RoundedPicasso())
                            .into(img);
                }
                else
                {
                    not_first_time_showing_info_window = true;
                        Picasso.with(MapsActivity.this)
                                .load(u.getPic1())
                                .transform(new RoundedPicasso())
                                .into(img, new InfoWindowRefresher(arg0));
                }
                return v;
            }
            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        };
        mMap.setInfoWindowAdapter(adapt);
    }
    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError() {}
    }
}
