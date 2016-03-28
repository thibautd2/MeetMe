package com.mti.meetme;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback, LocationListener {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_settings:
                // Comportement du bouton "Paramètres"
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.clear();
        GetCurrentLocation();
        getAllUSerPosition();


    }

    private void GetCurrentLocation() {

        mMap.setMyLocationEnabled(true);
      /*  double latitude = mMap.getMyLocation().getLatitude();
        double longitude= mMap.getMyLocation().getLongitude();
*/
       double[] d = getlocation();
        LatLng pos = new LatLng(d[0], d[1]);
        FacebookUser.getInstance().setLatitude(pos.latitude);
        FacebookUser.getInstance().setLongitude(pos.longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pos.latitude, pos.longitude), 17));
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

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            Log.e("POSITION CAHNGED", "POSITION CHANGED");
        }
    };
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
        Log.e("Provider ENABLE", "PROVIDER ENABLE");
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
        final String currentuser_id = FacebookUser.getInstance().getUid();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mMap.clear();
                all_user.clear();
                int i = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User u = postSnapshot.getValue(User.class);

                    if (u.getLatitude() != null && u.getLongitude() != null && u.getUid().compareTo(currentuser_id)!=0)
                    {    if (u.getGender().compareTo("male") == 0)
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
                int age = user.convertBirthdayToAge();
                dialog.setTitle(user.getName()+"  "+age+" ans");
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
                TextView distance = (TextView) v.findViewById(R.id.distance);
                String dist = String.valueOf((int)getDistance(new LatLng(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude()), new LatLng(u.getLatitude(), u.getLongitude())));
                distance.setText(dist +" m");
                name.setText(u.getName());
                age.setText(String.valueOf(u.convertBirthdayToAge()));
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
