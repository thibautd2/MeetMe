package com.mti.meetme;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.TodayDesire;

import org.joda.time.LocalDate;
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
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by thiba_000 on 04/06/2016.
 */

public class CreateGameActivity extends Fragment implements AdapterView.OnItemClickListener {

    public boolean adressevalid = true;
    DatePickerDialog dial;
    private int year;
    private int month;
    private int day;

    private CheckBox men;
    private CheckBox women;
    private Button create;
    private TextView type;
    private EditText name;
    private EditText desc;
    private RadioButton friend;
    private RadioButton all;
    private RadioButton compass;
    private RadioButton warmNcold;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageView img = (ImageView) getView().findViewById(R.id.event_header);

        img.setBackgroundResource(R.drawable.finegames);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date();
        System.out.println(dateFormat.format(date));

        setItems();
        setGenreCheckBox();
        sendEventToFirebase();
    }

    private void sendEventToFirebase() {
        final User u = FacebookUser.getInstance();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //todo check all field checked
                if (name.getText().toString().length() == 0 || desc.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Tu dois remplir toutes les informations", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    String visibility = "friends";
                    if (all.isChecked())
                        visibility = "all";

                    if (!men.isChecked())
                        visibility += ";women";
                    else if (!women.isChecked())
                        visibility += ";men";
                    else
                        visibility += ";all";

                    String gameType = "compass";
                    if (warmNcold.isChecked())
                        gameType = "warmNcold";

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    Event event = new Event(name.getText().toString(), desc.getText().toString(), "not so far",
                            u.getUid(), visibility, "game", dateFormat.format(date).toString(), FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude(), FacebookUser.getInstance().getName(), gameType);

                    adressevalid = true;

                    if (visibility.compareTo("friends") == 0)
                        event.setInvited(u.getMeetMeFriends());
                    
                    Firebase ref = Network.create_event("Event :" + name.getText().toString() + u.getUid());
                    ref.setValue(event);
                    GeoFire geoFire = new GeoFire(Network.geofire);
                    geoFire.setLocation("Event :" + name.getText().toString() + u.getUid(), new GeoLocation(event.getLatitude(), event.getLongitude()));
                    Toast.makeText(getApplicationContext(), "Evénement de jeux Créé !", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getContext(), MapsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setItems()
    {
        String currentDesire = "Let's play a game !";

        create = (Button) getView().findViewById(R.id.event_create);
        type = (TextView) getView().findViewById(R.id.event_type);
        type.setText(currentDesire.toString());
        name = (EditText) getView().findViewById(R.id.event_name);
        desc = (EditText) getView().findViewById(R.id.event_description);
        friend = (RadioButton) getView().findViewById(R.id.event_friends);
        all = (RadioButton) getView().findViewById(R.id.event_all);

        CompoundButton.OnCheckedChangeListener change = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    all.setChecked(true);
                    friend.setChecked(false);
                    buttonView.setChecked(true);
                }
            }
        };

        friend.setOnCheckedChangeListener(change);
        all.setOnCheckedChangeListener(change);
        all.setChecked(true);

        compass = (RadioButton) getView().findViewById(R.id.game_type_compass);
        warmNcold = (RadioButton) getView().findViewById(R.id.game_type_temp);
        compass.setChecked(true);
    }

    private void setGenreCheckBox() {
        men = (CheckBox) getView().findViewById(R.id.sex_male);
        women = (CheckBox) getView().findViewById(R.id.sex_female);
        men.setChecked(true);
        women.setChecked(true);

        men.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && !women.isChecked())
                    men.setChecked(true);
            }
        });

        women.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && !men.isChecked())
                    women.setChecked(true);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_create_game, container, false);
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

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return (String) resultList.get(index);
        }
    }
}
