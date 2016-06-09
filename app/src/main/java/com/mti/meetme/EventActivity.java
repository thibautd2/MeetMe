package com.mti.meetme;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.TodayDesire;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import android.content.Context;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * Created by thiba_000 on 04/06/2016.
 */

public class EventActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = "ErreurApiGoogle";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDcQEKTAlU8QCI-_W3RLPonTzJcLJMsrSk";
    private static double lon;
    private static double lat;
    public static boolean valCoord;
    public static  GooglePlacesAutocompleteAdapter adapter;
    public boolean adressevalid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_event);

        ImageView img = (ImageView) findViewById(R.id.event_header);

        final User u  = FacebookUser.getInstance();
        String currentDesire = u.getEnvie();
        if(currentDesire.compareTo(TodayDesire.Desire.play.toString())==0)
            img.setBackgroundResource(R.drawable.finegames);
        if(currentDesire.compareTo(TodayDesire.Desire.party.toString())==0)
            img.setBackgroundResource(R.drawable.soiree2fine);
        if(currentDesire.compareTo(TodayDesire.Desire.Drink.toString())==0)
            img.setBackgroundResource(R.drawable.drinkfine);
        if(currentDesire.compareTo(TodayDesire.Desire.Date.toString())==0)
            img.setBackgroundResource(R.drawable.rencontrefine);
        if(currentDesire.compareTo(TodayDesire.Desire.Sport.toString())==0)
            img.setBackgroundResource(R.drawable.finesport);
        if(currentDesire.compareTo(TodayDesire.Desire.Everything.toString())==0)
            img.setBackgroundResource(R.drawable.allfine);

        Button Create = (Button) findViewById(R.id.event_create);
        final EditText name = (EditText) findViewById(R.id.event_name);
        final EditText desc = (EditText) findViewById(R.id.event_description);
        final EditText adresse = (EditText) findViewById(R.id.event_adresse);
        final EditText date = (EditText) findViewById(R.id.event_date);
        final RadioButton friend = (RadioButton) findViewById(R.id.event_friends);
        final RadioButton all = (RadioButton) findViewById(R.id.event_all);
        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.event_adresse);
        adapter = new GooglePlacesAutocompleteAdapter(this, R.layout.adresse_list_item);
        autoCompView.setAdapter(adapter);
        autoCompView.setOnItemClickListener(this);

        CompoundButton.OnCheckedChangeListener change = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    all.setChecked(false);
                    friend.setChecked(false);
                    buttonView.setChecked(true);

                }
            }
        };

        friend.setOnCheckedChangeListener(change);
        all.setOnCheckedChangeListener(change);

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

                    Event event = new Event(name.getText().toString(), desc.getText().toString(), adresse.getText().toString(),
                            u.getUid(), visibility, u.getEnvie(), date.getText().toString(), FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude());
                    adressevalid = true;
                    getLocationFromAddress(event);
                    if(!adressevalid)
                    {
                        Toast.makeText(getApplicationContext(), "Merci de sélectionner une adresse proposée", Toast.LENGTH_LONG).show();
                    }
                    else {
                        if (visibility.compareTo("friends") == 0)
                            event.setInvited(u.getMeetMeFriends());
                        Firebase ref = Network.create_event("Event :" + name.getText().toString() + u.getUid());
                        ref.setValue(event);
                        GeoFire geoFire = new GeoFire(Network.geofire);
                        geoFire.setLocation("Event :" + name.getText().toString() + u.getUid(), new GeoLocation(event.getLatitude(), event.getLongitude()));
                        Toast.makeText(getApplicationContext(), "Evénement Créé !", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EventActivity.this, MapsActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:fr");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String str = (String) parent.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
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

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());
                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    public String getLocationFromAddress(Event ev){
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        try {
            address = coder.getFromLocationName(ev.getAdresse(),5);
            if (address==null || address.size() == 0 ) {
                adressevalid = false;
                return null;
            }

            Address location=address.get(0);
            ev.setLatitude(location.getLatitude());
            ev.setLongitude(location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(EventActivity.this, MapsActivity.class));
    }
}
