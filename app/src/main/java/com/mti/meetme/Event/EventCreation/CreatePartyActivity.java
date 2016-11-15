package com.mti.meetme.Event.EventCreation;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.R;
import com.mti.meetme.Tools.GooglePlacesAutocompleteAdapter;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.MyGame;
import com.mti.meetme.controller.TodayDesire;
import com.mti.meetme.notifications.NotificationSender;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.mti.meetme.Tools.GooglePlacesAutocompleteAdapter.getLocationFromAddress;

/**
 * Created by thiba_000 on 04/06/2016.
 */

public class CreatePartyActivity extends Fragment implements AdapterView.OnItemClickListener {

    public User user;
    public String typeEvent;
    public static GooglePlacesAutocompleteAdapter adapter;
    DatePickerDialog dial;
    private int year;
    private int month;
    private int day;

    private String currentDesire;
    private LinearLayout friendSelectLayout;

    private ArrayList<String> friendsPictures;
    private ArrayList<String> friendsNames;
    private ArrayList<String> friendsUids;

    private HashMap<String, RadioButton> radioIds;

    private EditText date;
    private EditText name;
    private EditText desc;
    private EditText adresse;
    private RadioButton friend;
    private RadioButton all;
    private RadioButton selection;
    private Button Create;
    private TextView type;
    private AutoCompleteTextView autoCompView;
    private  ImageView img;


    private void initCreatAction()
    {
        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().length() == 0 || desc.getText().toString().length() == 0 || adresse.getText().toString().length() == 0 ||
                        date.getText().toString().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Tu dois remplir toutes les informations", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    String visibility = "friends";

                    if (all.isChecked())
                        visibility = "all";
                    else if (selection.isChecked())
                        visibility = "friend_selection";

                    SimpleDateFormat dateFormat = MyGame.getInstance().getDateFormat();
                    Date dateNow = new Date();
                    long oneHour = 3600 * 1000;
                    Date endDate = new Date(dateNow.getTime() + 12 * oneHour);
                    Date beginDate = null;
                    try {
                        String thedate = date.getText().toString();
                        if (thedate.length() > MyGame.getInstance().getDateFormat().toPattern().length())
                            throw new Exception ();

                        beginDate = MyGame.getInstance().getDateFormat().parse(thedate);
                    }
                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Le format de la date est incorrect", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Address address = getLocationFromAddress(adresse.getText().toString());

                    if(address == null)
                    {
                        Toast.makeText(getApplicationContext(), "Merci de sélectionner une adresse proposée", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Event event = new Event(name.getText().toString(), desc.getText().toString(), adresse.getText().toString(),
                                user.getUid(), visibility, typeEvent, dateFormat.format(beginDate).toString(), dateFormat.format(endDate).toString(), FacebookUser.getInstance().getLatitude(),
                                FacebookUser.getInstance().getLongitude(), FacebookUser.getInstance().getName(), typeEvent);

                        event.setLatitude(address.getLatitude());
                        event.setLongitude(address.getLongitude());
                        if (visibility.compareTo("friends") == 0)
                            event.setInvited(user.getMeetMeFriends());
                        else if (visibility.compareTo("friend_selection") == 0)
                        {
                            String invited = "";

                            for(Map.Entry<String, RadioButton> e : radioIds.entrySet()) {
                                String uid = e.getKey();
                                RadioButton radio = e.getValue();

                                if (radio.isChecked())
                                    invited += uid + ";";
                            }

                            event.setInvited(invited);
                        }

                        sendNotificationsToGuests(event.getInvited(), FacebookUser.getInstance().getName(), event.getName());

                        Firebase ref = Network.create_event("Event :" + name.getText().toString() + user.getUid());
                        ref.setValue(event);
                        GeoFire geoFire = new GeoFire(Network.geofire);
                        geoFire.setLocation("Event :" + name.getText().toString() + user.getUid(), new GeoLocation(event.getLatitude(), event.getLongitude()));
                        Toast.makeText(getApplicationContext(), "Evènement créé !", Toast.LENGTH_LONG).show();
                        //todo uncomment this
                        Intent intent = new Intent(getContext(), MapsActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();
        typeEvent = this.getArguments().getString("type");

        if (typeEvent.equals("party")) {
            currentDesire = "Let's go to party !";
            img.setBackgroundResource(R.drawable.soiree2fine);        }
        else {
            currentDesire = "Let's practice !";
            img.setBackgroundResource(R.drawable.finesport);
        }

        type.setText(currentDesire.toString());

        user = FacebookUser.getInstance();

        adapter = new GooglePlacesAutocompleteAdapter(getApplicationContext(), R.layout.adresse_list_item);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        adapter = new GooglePlacesAutocompleteAdapter(getContext(), R.layout.adresse_list_item);
        autoCompView.setAdapter(adapter);
        autoCompView.setOnItemClickListener(this);
        dial = new DatePickerDialog(getContext(), datePickerListener, year, month,day);

        date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dial.show();
                return true;
            }
        });

        checkboxSelection();

        //Selection d'amis
        friendsNames = new ArrayList<>();
        friendsPictures = new ArrayList<>();
        friendsUids = new ArrayList<>();

        radioIds = new HashMap<>();

        if (!FacebookUser.getInstance().getMeetMeFriends().isEmpty())
            fill(FacebookUser.getInstance().getMeetMeFriends().split(";"));

        initCreatAction();
    }

    private void bindViews()
    {
        name = (EditText) getView().findViewById(R.id.event_name);
        desc = (EditText) getView().findViewById(R.id.event_description);
        adresse = (EditText) getView().findViewById(R.id.event_adresse);
        date = (EditText) getView().findViewById(R.id.event_date);
        friend = (RadioButton) getView().findViewById(R.id.event_friends);
        all = (RadioButton) getView().findViewById(R.id.event_all);
        selection = (RadioButton) getView().findViewById(R.id.event_friends_selection);
        Create = (Button) getView().findViewById(R.id.event_create);
        type = (TextView) getView().findViewById(R.id.event_type);
        friendSelectLayout = (LinearLayout) getView().findViewById(R.id.friendsSelectionLayout);
        autoCompView = (AutoCompleteTextView) getView().findViewById(R.id.event_adresse);
        img = (ImageView) getView().findViewById(R.id.event_header);
    }

    private void checkboxSelection()
    {
        CompoundButton.OnCheckedChangeListener change = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    all.setChecked(false);
                    friend.setChecked(false);

                    if (selection.isChecked())
                    {
                        for(Map.Entry<String, RadioButton> e : radioIds.entrySet()) {
                            e.getValue().setChecked(false);
                        }

                        friendSelectLayout.setVisibility(View.GONE);
                        selection.setChecked(false);
                    }

                    buttonView.setChecked(true);

                    if (selection.isChecked())
                        friendSelectLayout.setVisibility(View.VISIBLE);
                }
            }
        };

        friend.setOnCheckedChangeListener(change);
        all.setOnCheckedChangeListener(change);
        selection.setOnCheckedChangeListener(change);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_event, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Firebase.setAndroidContext(getApplicationContext());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String str = (String) parent.getItemAtPosition(position);
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            LocalDate eventDate = new LocalDate (year, month+1, day);
            LocalDate now = new LocalDate();
            if(eventDate.isBefore(now))
                Toast.makeText(getApplicationContext(), "Merci de choisir une date valide", Toast.LENGTH_LONG).show();
            else
                date.setText(new StringBuilder().append(day)
                        .append("/").append(month).append("/").append(year)
                        .append(" 00:00:00"));
            }
    };

    private void sendNotificationsToGuests(String invited, final String eventOwner, final String eventTitle)
    {
        if (invited == null || invited == "")
            return;
        String[] guestIds = invited.split(";");
        for (int i = 0; i < guestIds.length; i++)
        {
            Firebase ref = new Firebase("https://intense-fire-5226.firebaseio.com/users/" + guestIds[i] + "/fcmID" );
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    String fcmId = dataSnapshot.getValue().toString();

                    new NotificationSender().execute(fcmId, "Nouvelle invitation reçue !", eventOwner + " vous invite à l'évènement " + eventTitle);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    private void fill(final String[] friendsIds)
    {
        for (int i = 0; i < friendsIds.length; i++)
        {
            Firebase ref = new Firebase("https://intense-fire-5226.firebaseio.com/users/" + friendsIds[i] );
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    User friend = dataSnapshot.getValue(User.class);

                    friendsNames.add(friend.getName());
                    friendsPictures.add(friend.getPic1());
                    friendsUids.add(friend.getUid());

                    if (friendsNames.size() == friendsIds.length)
                    {
                        for (int i = 0; i < friendsNames.size(); i++)
                        {
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(10, 10, 10, 10);

                            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(100, 100);


                            LinearLayout layout = new LinearLayout(getView().getContext());
                            layout.setLayoutParams(lp);
                            layout.setOrientation(LinearLayout.HORIZONTAL);

                            lp.gravity = Gravity.CENTER_VERTICAL;

                            final RadioButton radio = new RadioButton(getView().getContext());
                            radio.setGravity(Gravity.CENTER_VERTICAL);
                            radioIds.put(friendsUids.get(i), radio);

                            ImageView profilePic = new ImageView(getView().getContext());
                            Picasso.with(getView().getContext()).load(friendsPictures.get(i)).transform(new RoundedPicasso()).into(profilePic);
                            profilePic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (radio.isChecked())
                                        radio.setChecked(false);
                                    else
                                        radio.setChecked(true);
                                }
                            });

                            TextView name = new TextView(getView().getContext());
                            name.setText(friendsNames.get(i));
                            name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (radio.isChecked())
                                        radio.setChecked(false);
                                    else
                                        radio.setChecked(false);
                                }
                            });

                            layout.addView(radio, lp);
                            layout.addView(profilePic, lp2);
                            layout.addView(name, lp);


                            friendSelectLayout.addView(layout, lp);
                        }
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }
}
