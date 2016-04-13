package com.mti.meetme;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.mti.meetme.Tools.CarousselPager;
import com.mti.meetme.Tools.FacebookHandler;
import com.mti.meetme.Tools.Network;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.UserList;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private ArrayList<User> all_user;
    private Circle searchCircle;
    LatLng latLngCenter;
    boolean dispo = true;
    GeoFire geoFire;
    FollowMeLocationSource followMeLocationSource;
    private Map<String,Marker> markers;
    private int rayon = 10000;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] optionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        markers = new HashMap<String, Marker>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geoFire = new GeoFire(Network.geofire);
        all_user = new ArrayList<>();
        followMeLocationSource = new FollowMeLocationSource();

        optionTitle = getResources().getStringArray(R.array.option_map);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, optionTitle));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        followMeLocationSource.getBestAvailableProvider();
        if(mMap!=null)
        mMap.setMyLocationEnabled(true);
       // setUpMapIfNeeded();

        /* Enable the my-location layer (this causes our LocationSource to be automatically activated.)
         * While enabled, the my-location layer continuously draws an indication of a user's
         * current location and bearing, and displays UI controls that allow a user to interact
         * with their location (for example, to enable or disable camera tracking of their location and bearing).*/
       // mMap.setMyLocationEnabled(true);
    }

    private void setUpMapIfNeeded() {

        if(mMap == null) {
        }
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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_list:
                Intent intent2 = new Intent(getApplicationContext(), ProfilsListActiity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                return true;
            case R.id.menu_settings:
                // Comportement du bouton "Paramètres"
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }


    private void selectItem(int position) {
        // update the main content by replacing fragments
       /* Fragment fragment = new MapsActivity.PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(MapsActivity.PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = currentActivity.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
*/
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
       // mDrawerList.setSelection(1);

        switch (optionTitle[position]) {
            case "1 km" :
                rayon = 1000;
                break;
            case "4 km" :
                rayon = 4000;
                break;
            case "10 km" :
                rayon = 10000;
                break;
            default:
                break;
        }

        updateMap();
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, getString(R.string.no_permission_granted), Toast.LENGTH_LONG);
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        if(mMap == null) {
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.setLocationSource(followMeLocationSource);
        }
        GetCurrentLocation();
        updateMap();
    }

    private void updateMap() {
        mMap.clear();

        all_user.clear();
        markers.clear();

        searchCircle = mMap.addCircle(new CircleOptions().center(latLngCenter).radius(rayon));
        searchCircle.setFillColor(Color.argb(95, 255, 255, 255));
        searchCircle.setStrokeWidth(4);
        searchCircle.setStrokeColor(Color.argb(60, 0, 0, 0));

        getAllUSerPosition();
    }

    private void GetCurrentLocation() {
        //mMap.setMyLocationEnabled(true);
      /*  double latitude = mMap.getMyLocation().getLatitude();
        double longitude= mMap.getMyLocation().getLongitude();
*/
        double[] d = getlocation();
        LatLng pos = new LatLng(d[0], d[1]);
        FacebookUser.getInstance().setLatitude(pos.latitude);
        FacebookUser.getInstance().setLongitude(pos.longitude);
        latLngCenter = pos;
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

    private void sendPosition() {
        dispo = false;
        Log.e("SEND NEW POSITION", "SEND NEW POSITION :" + FacebookUser.getInstance().getLatitude().toString() + " " + FacebookUser.getInstance().getLongitude().toString());
        Firebase ref = Network.find_user(FacebookUser.getInstance().getUid());
        Map<String, Object> pos = new HashMap<String, Object>();
        pos.put("latitude", String.valueOf(FacebookUser.getInstance().getLatitude()));
        pos.put("longitude", String.valueOf(FacebookUser.getInstance().getLongitude()));
        geoFire.setLocation(FacebookUser.getInstance().getUid(), new GeoLocation(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude()));
        ref.updateChildren(pos, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    {
                        dispo = true;
                    }
                } else {
                    dispo = true;
                }
            }
        });
    }

    private void getAllUSerPosition()
    {
        GeoLocation geoLocation = new GeoLocation(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude());
        GeoQuery geoQuery = geoFire.queryAtLocation(geoLocation, rayon / 1000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Firebase ref = Network.find_user(key);
                final String fKey = key;
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean userExist = false;
                        User u = snapshot.getValue(User.class);
                        final String uid = FacebookUser.getInstance().getUid();
                        if (u.getLatitude() != null && u.getLongitude() != null && u.getUid().compareTo(uid) != 0) {
                            if (u.getGender().compareTo("male") == 0) {
                                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(
                                        u.getLatitude(), u.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.hmarker)).snippet(String.valueOf(all_user.size())));
                                if (fKey != null && marker != null && markers.get(fKey) == null)
                                    markers.put(fKey, marker);
                            } else {
                                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(
                                        u.getLatitude(), u.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.fmarker)).snippet(String.valueOf(all_user.size())));
                                if (fKey != null && marker != null && markers.get(fKey) == null)
                                    markers.put(fKey, marker);
                            }
                            Log.e("NB_USEr", "NB8USER : " + all_user.size());
                            for (int i = 0; i < all_user.size(); i++) {
                                if (all_user.get(i).getUid().compareTo(u.getUid()) == 0) {
                                    userExist = true;
                                    all_user.set(i, u);
                                }
                            }
                            if (!userExist)
                                all_user.add(u);
                        }
                        init_infos_window();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }

            @Override
            public void onKeyExited(String key) {
                markers.remove(key);
                for (int i = 0; i <  all_user.size(); i++) {
                    if (all_user.get(i).getUid().compareTo(key) == 0)
                        all_user.remove(i);
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                final Marker marker = markers.get(key);
                if(marker!= null) {
                    final Handler handler = new Handler();
                    final long start = SystemClock.uptimeMillis();
                    final long DURATION_MS = 2000;
                    final Interpolator interpolator = new AccelerateDecelerateInterpolator();
                    final LatLng startPosition = marker.getPosition();
                    final double lat = location.latitude;
                    final double lng = location.longitude;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            float elapsed = SystemClock.uptimeMillis() - start;
                            float t = elapsed / DURATION_MS;
                            float v = interpolator.getInterpolation(t);
                            double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                            double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                            marker.setPosition(new LatLng(currentLat, currentLng));
                            // if animation is not finished yet, repeat
                            if (t < 1) {
                                handler.postDelayed(this, 16);
                            }
                        }
                    });
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(FirebaseError error) {
            }
        });

        }

    public void init_infos_window()
    {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final User user = all_user.get(Integer.parseInt(marker.getSnippet()));
                final Dialog dialog = new Dialog(MapsActivity.this);
                dialog.setContentView(R.layout.user_pop_up);

                int age = user.convertBirthdayToAge();
                dialog.setTitle(user.getName()+"  "+age+" ans");
                ImageView image = (ImageView) dialog.findViewById(R.id.user_img2);

                Picasso.with(MapsActivity.this).load(user.getPic1()).fit().centerCrop().into(image);
                TextView interessé = (TextView) dialog.findViewById(R.id.interessé);
                TextView pasinteressé = (TextView) dialog.findViewById(R.id.pasinteressé);
                interessé.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

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
            @Override
            public View getInfoWindow(Marker arg0) {
                int id = Integer.parseInt(arg0.getSnippet());
                Log.e("MARKER ID", "MARKER ID : "+ id);
                User u = all_user.get(id);
                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                ImageView img = (ImageView) v.findViewById(R.id.user_image);
                TextView name = (TextView) v.findViewById(R.id.user_name);
                TextView age = (TextView) v.findViewById(R.id.user_age);
                TextView distance = (TextView) v.findViewById(R.id.distance);
                String dist = String.valueOf((int)getDistance(new LatLng(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude()), new LatLng(u.getLatitude(), u.getLongitude())));
                distance.setText(dist +" m");
                name.setText(u.getName());
                age.setText(String.valueOf(u.convertBirthdayToAge()) +" ans");
                Picasso.with(MapsActivity.this)
                                .load(u.getPic1())
                                .transform(new RoundedPicasso())
                                .into(img, new InfoWindowRefresher(arg0));
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

    private class FollowMeLocationSource implements LocationSource, LocationListener {

        private OnLocationChangedListener mListener;
        private LocationManager locationManager;
        private final Criteria criteria = new Criteria();
        private String bestAvailableProvider;
        private final int minTime = 20000;     // minimum time interval between location updates, in milliseconds
        private final int minDistance = 1;    // minimum distance between location updates, in meters

        private FollowMeLocationSource() {
            // Get reference to Location Manager
            locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
            // Specify Location Provider criteria
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(true);
            criteria.setBearingRequired(true);
            criteria.setSpeedRequired(true);
            criteria.setCostAllowed(true);
        }
        private void getBestAvailableProvider() {
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
            sendPosition();
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

    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onError() {
            Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
        }

        @Override
        public void onSuccess() {
            if (markerToRefresh != null && markerToRefresh.isInfoWindowShown()) {
                markerToRefresh.hideInfoWindow();
                markerToRefresh.showInfoWindow();
            }
        }
    }
}
