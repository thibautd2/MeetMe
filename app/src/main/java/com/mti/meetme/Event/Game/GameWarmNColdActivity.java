package com.mti.meetme.Event.Game;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.ProfileActivity;
import com.mti.meetme.R;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.MyGame;
import com.mti.meetme.controller.UserList;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.Collection;
import java.util.Vector;

/**
 * Created by Alex on 19/06/2016.
 */

public class GameWarmNColdActivity extends AppCompatActivity {
    private TextView stateTxt;
    private ImageView stateImg;
    private TextView title;
    private ImageView pictureOwner;
    private TextView desc;


    int timeToWait;
    double distance;
    User owner;
    Event event;
    Color color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.owner = MyGame.getInstance().getOwner();
        this.event = MyGame.getInstance().getGame();

        color = new Color();
        distance = UserList.getInstance().getDistToMe(owner);
        setContentView(R.layout.activity_game_warmncold);

        timeToWait = 30;

        bindViews();
        populate();
        setListener();
        updateState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        populate();
    }

    public void bindViews() {
        stateTxt = (TextView) findViewById(R.id.textViewState);
        stateImg = (ImageView) findViewById(R.id.imageViewState);
        title = (TextView) findViewById(R.id.textViewTitle);
        pictureOwner = (ImageView) findViewById(R.id.imageViewOwner);
        desc = (TextView) findViewById(R.id.textViewDesc);
    }

    public void populate() {
        title.setText(event.getName());
        Picasso.with(getApplicationContext()).load(owner.getPic1()).fit().centerCrop().transform(new RoundedPicasso()).into(pictureOwner);
        desc.setText(event.getDescription());
    }

    private void setListener() {
        Firebase refMyLongitude = Network.find_user_properties(FacebookUser.getInstance().getUid(), "longitude");
        Firebase refMyLatitude =  Network.find_user_properties(FacebookUser.getInstance().getUid(), "latitude");
        Firebase refOwnerLongitude = Network.find_user_properties(owner.getUid(), "longitude");
        Firebase refOwnerLatitude =  Network.find_user_properties(owner.getUid(), "latitude");

        refMyLongitude.addValueEventListener(setValueEventListener());
        refMyLatitude.addValueEventListener(setValueEventListener());
        refOwnerLongitude.addValueEventListener(setValueEventListener());
        refOwnerLatitude.addValueEventListener(setValueEventListener());
    }

    public ValueEventListener setValueEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                distance = UserList.getInstance().getDistToMe(owner) * 1000;
                updateState();
                stateTxt.setText(getTextState());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
    }

    private void updateState(){
        //red 255 green 0 -> 150
        //blue 255 green 255 -> 105

        if (distance <= 200) //red
        {
            int green = (int)(distance / 2);
            stateImg.setBackgroundColor(Color.rgb(255, green, 0));
        }
        else //blue
        {
            int green;
            if (distance > 1000)
                green = 255;
            else
                green = 255 - (int)((1000 - distance) / 1000 * 255);

            stateImg.setBackgroundColor(Color.rgb(0, green, 255));
        }
    }

    private String getTextState() {
        if (distance < 50)
            return "Bouillant";
        if (distance < 100)
            return "Brulant";
        if (distance < 200)
            return "Chaud";
        if (distance < 300)
            return "Tiede";
        if (distance < 500)
            return "Froid";

        return "Glacial";
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        switch (item.getItemId()) {
            case R.id.menu_maps:
                startActivity(intent);
                return true;
            case R.id.menu_end:
                FacebookUser.getInstance().removeParticipation(MyGame.getInstance().getGame().receiveEventId());
                MyGame.getInstance().finishTheGame();
                Toast.makeText(this, "Partie abandonnée", Toast.LENGTH_LONG);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
