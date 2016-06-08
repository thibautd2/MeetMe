package com.mti.meetme;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.StringDef;
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
    //https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=API_KEY
    private static final String PLACES_API_BASE_GEOCODE = "https://maps.googleapis.com/maps/api/geocode";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDcQEKTAlU8QCI-_W3RLPonTzJcLJMsrSk";
    private static double lon;
    private static double lat;
    public static boolean valCoord;
    public static  PlacesAutoCompleteAdapter adapter;

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
       adapter = new PlacesAutoCompleteAdapter(this, R.layout.adresse_list_item);
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
                    Firebase ref = Network.create_event("Event :"+name.getText().toString() + u.getUid());

                    ref.setValue(event);
                    GeoFire geoFire = new GeoFire(Network.geofire);
                    geoFire.setLocation("Event :"+name.getText().toString() + u.getUid(), new GeoLocation(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude()));
                    Toast.makeText(getApplicationContext(), "Evénement Créé !", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EventActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
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
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }


    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        //ArrayList<String> resultListId = null;
        adapter.notifyDataSetChanged();
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            //sb.append("types=geocode");
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
            resultList = new ArrayList<String>(predsJsonArray.length());
            //resultListId = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                //resultListId.add(predsJsonArray.getJSONObject(i).getString("place_id"));
            }
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        adapter.notifyDataSetChanged();
        return resultList;
    }



    @Override
    public void onResume()
    {
        super.onResume();
    }
    private int[] getCoord(String input) {
        ArrayList<String> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {

            StringBuilder sb = new StringBuilder(PLACES_API_BASE_GEOCODE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            //sb.append("types=geocode");
            sb.append("&components=country:fr");
            sb.append("&address=" + URLEncoder.encode(input, "utf8"));

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
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            //Log.w("RECUP", jsonResults.toString());
            lon = ((JSONArray)jsonObj.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            lat = ((JSONArray)jsonObj.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");

            //double[] coord = new double[] {lon, lat};
            Log.w("COORD", " coordonnee = lon/lat = " + lon + "/" + lat);
            valCoord = true;
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        adapter.notifyDataSetChanged();
        return null;

    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(EventActivity.this, MapsActivity.class));
    }
}
