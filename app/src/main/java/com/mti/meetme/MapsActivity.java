package com.mti.meetme;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mti.meetme.Event.EventFicheActivity;
import com.mti.meetme.Event.EventUserFragmentActivity;
import com.mti.meetme.Event.EventCreation.CreateEventManager;
import com.mti.meetme.Event.Game.GameParticipantsListActivity;
import com.mti.meetme.Event.Game.GameWarmNColdActivity;
import com.mti.meetme.Interface.ContextDrawerAdapter;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.DrawerListAdapter;
import com.mti.meetme.Tools.Map.CalculateDistance;
import com.mti.meetme.Tools.Map.FollowMeLocationSource;
import com.mti.meetme.Tools.MenuSlideItem;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.MyGame;
import com.mti.meetme.controller.TodayDesire;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ContextDrawerAdapter {

    public GoogleMap mMap;
    public ArrayList<User> all_user;
    public ArrayList<Event> all_event;
    public Circle searchCircle;
    static LatLng latLngCenter;
    boolean dispo = true;
    public static GeoFire geoFire;
    FollowMeLocationSource followMeLocationSource;
    private WeakHashMap<String, Marker> markers;
    private int rayon = 10000;
    public static int backtwice = 0;

    private ImageButton settingsButton;
    private ImageButton profileButton;

    private FloatingActionButton eventButton;
    private FloatingActionButton listButton;
    private FloatingActionButton gameButton;

    private enum Gender {
        MEN,
        WOMEN,
        ALL
    };

    private Gender gender;

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private DrawerLayout mDrawerLayout;

    ArrayList<MenuSlideItem> MenuSlideItems = new ArrayList<MenuSlideItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Firebase.setAndroidContext(this);

        setupActionBar();

        gender = Gender.ALL;
        markers = new WeakHashMap<String, Marker>();
        super.onCreate(savedInstanceState);
        backtwice = 0;
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geoFire = new GeoFire(Network.geofire);
        all_user = new ArrayList<>();
        all_event = new ArrayList<>();

        followMeLocationSource = new FollowMeLocationSource(this);

        init_menu();
        init_envie_du_jour();

        bindViews();
        populateViews();
    }

    private void bindViews()
    {
        eventButton = (FloatingActionButton) findViewById(R.id.fab_event);
        listButton = (FloatingActionButton) findViewById(R.id.fab_list);
        gameButton = (FloatingActionButton) findViewById(R.id.fab_play_game);

        profileButton = (ImageButton) findViewById(R.id.mapsProfileButton);
        settingsButton = (ImageButton) findViewById(R.id.mapsSettingsButton);
    }

    private void populateViews()
    {
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, CreateEventManager.class);
                startActivity(intent);
            }
        });


        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MapsActivity.this, EventUserFragmentActivity.class);
               // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
            }
        });

        if (MyGame.getInstance().getGame() != null) {
            if (MyGame.getInstance().getGame().getOwnerid().equals(FacebookUser.getInstance().getUid()))
                gameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo go to list of game participants
                        Intent intent2 = new Intent(MapsActivity.this, GameParticipantsListActivity.class);
                        startActivity(intent2);
                    }
                });
         /*   else
                gameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo go to list of game participants
                        Intent intent3 = new Intent(MapsActivity.this, GameWarmNColdActivity.class);
                        startActivity(intent3);
                    }
                });*/
        }
        else
            gameButton.setVisibility(View.INVISIBLE);

        profileButton.getLayoutParams().height -= 30;
        profileButton.getLayoutParams().width -= 30;

        profileButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }});

        settingsButton.getLayoutParams().height -= 30;
        settingsButton.getLayoutParams().width -= 30;

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(mDrawerPane))
                    mDrawerLayout.closeDrawer(mDrawerPane);
                else
                    mDrawerLayout.openDrawer(mDrawerPane);
            }});
    }

    public void init_menu() {
        //todo singleton on drawerAdapter then us it in listacty
        MenuSlideItems.add(new MenuSlideItem("Distance", " km", R.drawable.radar, new MenuSlideItem.MySeekBar(0, 10, 10)));
        MenuSlideItems.add(new MenuSlideItem("Genre", R.drawable.gender, new MenuSlideItem.MyCheckBox("Men", true), new MenuSlideItem.MyCheckBox("Women", true), null, null));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, MenuSlideItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });
    }

    public void init_envie_du_jour() {
        final Dialog dialog = new Dialog(MapsActivity.this);
        dialog.setTitle("Envie du jour !");
        dialog.setContentView(R.layout.envie_du_jour);
        RelativeLayout party = (RelativeLayout) dialog.findViewById(R.id.party);
        RelativeLayout drink = (RelativeLayout) dialog.findViewById(R.id.drink);
        RelativeLayout meet = (RelativeLayout) dialog.findViewById(R.id.meet);
        RelativeLayout sport = (RelativeLayout) dialog.findViewById(R.id.sport);
        RelativeLayout play = (RelativeLayout) dialog.findViewById(R.id.play);
        RelativeLayout all = (RelativeLayout) dialog.findViewById(R.id.all);

        View.OnClickListener update_desire = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_TodayDesire(v, dialog);
            }
        };

        party.setOnClickListener(update_desire);
        drink.setOnClickListener(update_desire);
        meet.setOnClickListener(update_desire);
        sport.setOnClickListener(update_desire);
        play.setOnClickListener(update_desire);
        all.setOnClickListener(update_desire);
        dialog.show();
        dialog.setCancelable(false);
    }


    public void update_TodayDesire(View v, Dialog dialog) {
        Firebase ref = Network.find_user(FacebookUser.getInstance().getUid());
        String currentDesire = "";
        if (v.getId() == R.id.party)
            currentDesire = TodayDesire.Desire.party.toString();
        if (v.getId() == R.id.drink)
            currentDesire = TodayDesire.Desire.Drink.toString();
        if (v.getId() == R.id.meet)
            currentDesire = TodayDesire.Desire.Date.toString();
        if (v.getId() == R.id.sport)
            currentDesire = TodayDesire.Desire.Sport.toString();
        if (v.getId() == R.id.play)
            currentDesire = TodayDesire.Desire.play.toString();
        if (v.getId() == R.id.all)
            currentDesire = TodayDesire.Desire.Everything.toString();

        FacebookUser.getInstance().setEnvie(currentDesire);
        Map<String, Object> envie = new HashMap<String, Object>();
        envie.put("envie", currentDesire);
        ref.updateChildren(envie);
        dialog.dismiss();
    }


    @Override
    public void onResume() {
        super.onResume();

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, CreateEventManager.class);
                startActivity(intent);
            }
        });
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, EventUserFragmentActivity.class);
                startActivity(intent);
            }
        });

        if (MyGame.getInstance().getGame() != null) {
            gameButton.setVisibility(View.VISIBLE);
            gameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MapsActivity.this, GameParticipantsListActivity.class);
                    startActivity(intent);
                }
            });
        }
        else
            gameButton.setVisibility(View.INVISIBLE);

        Firebase.setAndroidContext(getApplicationContext());
        backtwice = 0;
        followMeLocationSource.getBestAvailableProvider();
        if (mMap != null)
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.no_permission_granted), Toast.LENGTH_LONG).show();
            } else
                mMap.setMyLocationEnabled(true);

        updateMap();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        ActionBar.LayoutParams lp1 = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View customNav = LayoutInflater.from(this).inflate(R.layout.actionbar_custom_maps, null); // layout which contains your button.

        actionBar.setCustomView(customNav, lp1);
    }

    private void selectItemFromDrawer(int position) {
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    @Override
    public void menuDrawerSeekBarListener(SeekBar seekBar, TextView textView, String btnName) {
        if (btnName.equals("Distance")) {
            rayon = 1000 * seekBar.getProgress();
        }
        updateMap();
    }

    @Override
    public void menuDrawerMultyChoiceListener(CheckBox checkBox, String btnName, boolean ischecked) {
        if (btnName.equals("Genre")) {
            boolean men = true;
            boolean women = true;
            if (checkBox.getText().equals("Men") && ischecked)
                men = true;
            else if (checkBox.getText().equals("Men") && !ischecked && gender != Gender.MEN) {
                men = false;
            } else if (checkBox.getText().equals("Men")) {
                checkBox.setChecked(true);
                women = false;
            }
            if (checkBox.getText().equals("Women") && ischecked)
                women = true;
            else if (checkBox.getText().equals("Women") && !ischecked && gender != Gender.WOMEN) {
                women = false;
            } else if (checkBox.getText().equals("Women")) {
                checkBox.setChecked(true);
                men = false;
            }
            gender = getGender(men, women);
        }
        updateMap();
    }

    private Gender getGender(boolean men, boolean women) {
        if (men && women)
            return Gender.ALL;
        if (men)
            return Gender.MEN;

        return Gender.WOMEN;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, getString(R.string.no_permission_granted), Toast.LENGTH_LONG);
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        if (mMap == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.no_permission_granted), Toast.LENGTH_LONG).show();
            } else {
                mMap = googleMap;
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.setLocationSource(followMeLocationSource);
            }
        }
        GetCurrentLocation();
        updateMap();
    }

    public void updateMap() {
        if (mMap != null) {
            mMap.clear();
            searchCircle = mMap.addCircle(new CircleOptions().center(latLngCenter).radius(rayon));
            searchCircle.setFillColor(Color.argb(95, 255, 255, 255));
            searchCircle.setStrokeWidth(4);
            searchCircle.setStrokeColor(Color.argb(100, 0, 221, 255));
        }
        all_event.clear();
        all_user.clear();
        getAllUSerandEventPosition();
    }

    private void GetCurrentLocation() {
        double[] d = getlocation();
        LatLng pos = new LatLng(d[0], d[1]);
        FacebookUser.getInstance().setLatitude(pos.latitude);
        FacebookUser.getInstance().setLongitude(pos.longitude);
        latLngCenter = pos;
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pos.latitude, pos.longitude), 13));
        }
        sendPosition();
    }

    public double[] getlocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location location = null;
        for (int i = 0; i < providers.size(); i++) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.no_permission_granted), Toast.LENGTH_LONG).show();
                break;
            } else
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

    public static void sendPosition() {
        Firebase ref = Network.find_user(FacebookUser.getInstance().getUid());
        Map<String, Object> pos = new HashMap<String, Object>();
        pos.put("latitude", String.valueOf(FacebookUser.getInstance().getLatitude()));
        pos.put("longitude", String.valueOf(FacebookUser.getInstance().getLongitude()));
        latLngCenter = new LatLng(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude());
        geoFire.setLocation(FacebookUser.getInstance().getUid(), new GeoLocation(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude()));
        ref.updateChildren(pos, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
            }
        });
    }

    public void update_circle() {
        LatLng pos = new LatLng(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude());
        searchCircle = mMap.addCircle(new CircleOptions().center(pos).radius(rayon));
        searchCircle.setFillColor(Color.argb(95, 255, 255, 255));
        searchCircle.setStrokeWidth(4);
        searchCircle.setStrokeColor(Color.argb(100, 0, 221, 255));
    }

    private void getAllUSerandEventPosition() {
        /* C'est pas dégueu tout ça peut-être ?! */
        GetCurrentLocation();
        GeoLocation geoLocation = new GeoLocation(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude());
        GeoQuery geoQuery = geoFire.queryAtLocation(geoLocation, rayon / 1000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //people
                if (!key.startsWith("Event :")) {
                    Firebase ref = Network.find_user(key);
                    final String fKey = key;
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            boolean userExist = false;
                            if (snapshot != null) {
                                User u = snapshot.getValue(User.class);
                                if (u == null)
                                    return;
                                final String uid = FacebookUser.getInstance().getUid();
                                if (u.getLatitude() != null && u.getLongitude() != null && u.getUid().compareTo(uid) != 0) {
                                    Marker marker = null;
                                    if (gender != Gender.WOMEN && u.getGender().compareTo("male") == 0) {
                                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(
                                                u.getLatitude(), u.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.hmarker)).snippet(String.valueOf(all_user.size())));
                                    } else if (gender != Gender.MEN && u.getGender().compareTo("male") != 0) {
                                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(
                                                u.getLatitude(), u.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.fmarker)).snippet(String.valueOf(all_user.size())));
                                    }
                                    if (fKey != null && marker != null && markers.get(fKey) == null)
                                        markers.put(fKey, marker);
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
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                }
                //events
                else {
                    Firebase ref = Network.find_event(key);
                    final String fKey = key;
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Event event = dataSnapshot.getValue(Event.class);
                            //todo change marker for game here
                            if(checkEventVisibility(event)) {
                                Log.e("mapsActy", "onDataChange: show a marker");
                        //    if (event != null && event.visibility != null && event.getName().equals("Party hard")) {
                                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(
                                        event.getLatitude(), event.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.paryt_marker)).snippet(String.valueOf("event " + all_event.size())));
                                all_event.add(event);
                                if (fKey != null && marker != null && markers.get(fKey) == null)
                                    markers.put(fKey, marker);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {
                markers.remove(key);
                for (int i = 0; i < all_user.size(); i++) {
                    if (all_user.get(i).getUid().compareTo(key) == 0)
                        all_user.remove(i);
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                final Marker marker = markers.get(key);
                String k = key;
                if (marker != null) {
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
                            if (t < 1) {
                                handler.postDelayed(this, 16);
                            }
                        }
                    });
                } else {
                    latLngCenter = new LatLng(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude());
                    updateMap();
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

    public void init_infos_window() {
        if (mMap != null)
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (!marker.getSnippet().startsWith("event ")) { //User
                        final User user = all_user.get(Integer.parseInt(marker.getSnippet()));
                        final Dialog dialog = new Dialog(MapsActivity.this);
                        dialog.setContentView(R.layout.user_pop_up);

                        int age = user.convertBirthdayToAge();
                        dialog.setTitle(user.getName() + "  " + age + " ans");
                        ImageView image = (ImageView) dialog.findViewById(R.id.user_img2);

                        Picasso.with(MapsActivity.this).load(user.getPic1()).fit().centerCrop().into(image);
                        TextView accept = (TextView) dialog.findViewById(R.id.interessé);
                        TextView cancel = (TextView) dialog.findViewById(R.id.pasinteressé);
                        accept.setOnClickListener(new View.OnClickListener() {
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

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                    else
                    {
                        String token = "event ";
                        String realid = marker.getSnippet().replace(token, "");
                        int id = Integer.parseInt(realid);
                        Log.e("MARKER ID", "MARKER ID : " + id);
                        Event e = null;
                        if (id < all_event.size()) {
                            e = all_event.get(id);

                            //todo change the layout for games

                            Intent intent = new Intent(MapsActivity.this, EventFicheActivity.class);
                            Bundle b = new Bundle();
                            b.putSerializable("Event", e);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    }
                }
            });

        GoogleMap.InfoWindowAdapter adapt = new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                ImageView img = (ImageView) v.findViewById(R.id.user_image);
                TextView name = (TextView) v.findViewById(R.id.user_name);
                TextView age = (TextView) v.findViewById(R.id.user_age);
                TextView distance = (TextView) v.findViewById(R.id.distance);

                if (!arg0.getSnippet().startsWith("event ")) //UTILISATEUR
                {
                    int id = Integer.parseInt(arg0.getSnippet());
                    Log.e("MARKER ID", "MARKER ID : " + id);
                    User u = null;
                    if (id < all_user.size())
                        u = all_user.get(id);
                    if (u != null) {
                        double dist = CalculateDistance.getDistance(new LatLng(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude()), new LatLng(u.getLatitude(), u.getLongitude()));
                        distance.setText(String.format("%.1f", dist) + " km");
                        name.setText(u.getName());
                        age.setText(String.valueOf(u.convertBirthdayToAge()) + " ans");
                        Picasso.with(getApplication())
                                .load(u.getPic1())
                                .placeholder(R.drawable.defaultuser)
                                .transform(new RoundedPicasso())
                                .into(img, new InfoWindowRefresher(arg0));
                    }
                    return v;
                } else // Event
                {
                    String token = "event ";
                    String realid = arg0.getSnippet().replace(token, "");
                    int id = Integer.parseInt(realid);
                    Log.e("MARKER ID", "MARKER ID : " + id);
                    Event e = null;
                    if (id < all_event.size())
                        e = all_event.get(id);
                    if (e != null) {
                        Double dist = (CalculateDistance.getDistance(new LatLng(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude()), new LatLng(e.getLatitude(), e.getLongitude())));
                        distance.setText(String.format("%.1f", dist) + " km");
                        img.setBackgroundResource(R.drawable.paryt_marker);
                        name.setText(e.getName());
                        String date = e.date;
                        if(date.length()>5)
                        date = date.substring(0,5);
                        age.setText(date);
                    }
                }
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        };
        if (mMap != null)
            mMap.setInfoWindowAdapter(adapt);
    }

    //todo check all possibilities
    private boolean checkEventVisibility(Event event) {
        if (event == null || event.visibility == null)
            return false;

        try {
            Date now = new Date();
            Date eventFinish = null;
            eventFinish = MyGame.getInstance().getDateFormat().parse(event.getEndDate());
            if (eventFinish == null || eventFinish.before(now))
                return false;
        } catch (ParseException e1) {
            e1.getStackTrace();
        }

        if (event.getOwnerid().equals(FacebookUser.getInstance().getUid()))
            return true;

        ArrayList<String> visibilityList;
        if (event.getVisibility().contains(";")) {
            String visible[] = event.getVisibility().split(";");
            visibilityList = new ArrayList<>(Arrays.asList(visible));
        }
        else {
            visibilityList = new ArrayList<>();
            visibilityList.add(event.getVisibility());
        }

       if (visibilityList.get(0).equals("friend") && FacebookUser.getInstance().haveThisFriend(event.getOwnerid())) {
           if (visibilityList.size() == 1)
               return true;
           if (visibilityList.size() > 1 && visibilityList.get(1).equals(FacebookUser.getInstance().getGender()))
               return true;
           return false;
       }

        if (visibilityList.get(0).equals("friend_selection") && event.getInvited() != null && event.getInvited().contains(FacebookUser.getInstance().getUid()))
            return true;

        if (visibilityList.get(0).equals("all")) {
            if (visibilityList.size() == 1 || visibilityList.get(1).equals("all"))
                return true;
            if (visibilityList.size() > 1 && visibilityList.get(1).equals(FacebookUser.getInstance().getGender()))
                return true;
            return false;
        }

        return false;
    }

    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;

        }

        @Override
        public void onError() {
        }

        @Override
        public void onSuccess() {
            if (markerToRefresh != null && markerToRefresh.isInfoWindowShown()) {
                markerToRefresh.hideInfoWindow();
                markerToRefresh.showInfoWindow();
            }
        }
    }

    @Override
    public void onBackPressed() {
        backtwice += 1;
        if (backtwice >= 2) {
            backtwice = 0;
            super.onBackPressed();
            return;
        } else
                Toast.makeText(this, "Appuie à nouveau pour quitter Meetme", Toast.LENGTH_LONG).show();

    }

}

