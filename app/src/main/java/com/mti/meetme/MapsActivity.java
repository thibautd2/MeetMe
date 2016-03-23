package com.mti.meetme;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
//import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.mti.meetme.Tools.CarousselPager;
import com.mti.meetme.Tools.Network;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.RoundedPicasso;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    private ArrayList<User> all_user;
    public static CarousselPager mpager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        all_user = new ArrayList<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        getAllUSerPosition();
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
    public void onLocationChanged(Location location){

        Log.e("POSITION CAHNGED", "POSITION CHANGED");

        FacebookUser.getInstance().setLatitude(location.getLatitude());
        FacebookUser.getInstance().setLongitude(location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
        sendPosition();
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("STATUS CHANGED", "STATUS CHANGER");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("Provider ENABLE", "PROVIDER ENABLE");
        GetCurrentLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void sendPosition()
    {
        Log.e("SEND NEW POSITION", "SEND NEW POSITION :"+ FacebookUser.getInstance().getLatitude().toString()+" "+FacebookUser.getInstance().getLongitude().toString());
        Firebase ref = Network.find_user(FacebookUser.getInstance().getUid());
        ref.child("latitude").setValue(String.valueOf(FacebookUser.getInstance().getLatitude()));
        ref.child("longitude").setValue(String.valueOf(FacebookUser.getInstance().getLongitude()));
    }

    private void getAllUSerPosition()
    {
        Firebase ref = Network.getAlluser;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User u = postSnapshot.getValue(User.class);
                    if(u.getLatitude()!= null && u.getLongitude() != null) {
                        if(u.getGender().compareTo("male")==0)
                        mMap.addMarker(new MarkerOptions().position(new LatLng(
                                u.getLatitude(), u.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.hmarker)).snippet(String.valueOf(i)));
                        else
                            mMap.addMarker(new MarkerOptions().position(new LatLng(
                                    u.getLatitude(), u.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.fmarker)).snippet(String.valueOf(i)));

                        all_user.add(u);
                        i++;
                    }
                }
                init_infos_window();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void init_infos_window()
    {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String r = marker.getId().substring(1);
                final User user = all_user.get(Integer.parseInt(r));
                final Dialog dialog = new Dialog(MapsActivity.this);
                dialog.setContentView(R.layout.user_pop_up);
                dialog.setTitle(user.getName()+"  "+user.convertBirthdayToAge() +" ans");
                ImageView image = (ImageView) dialog.findViewById(R.id.user_img);
                Picasso.with(MapsActivity.this).load(user.getPic1()).fit().centerCrop().into(image);
                TextView interessé = (TextView) dialog.findViewById(R.id.interessé);
                TextView pasinteressé = (TextView) dialog.findViewById(R.id.pasinteressé);
                interessé.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("User", user);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                });
                pasinteressé.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        GoogleMap.InfoWindowAdapter adapt = new GoogleMap.InfoWindowAdapter() {
            Boolean not_first_time_showing_info_window = false;

            @Override
            public View getInfoWindow(Marker arg0) {
                int id = Integer.parseInt(arg0.getSnippet());
                User u =  all_user.get(id);
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
