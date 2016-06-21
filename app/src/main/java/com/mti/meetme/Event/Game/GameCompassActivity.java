package com.mti.meetme.Event.Game;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mti.meetme.MapsActivity;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.R;
import com.mti.meetme.Tools.FacebookHandler;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.MyGame;
import com.mti.meetme.controller.UserList;
import com.squareup.picasso.Picasso;

/**
 * Created by Alex on 19/06/2016.
 */

public class GameCompassActivity extends AppCompatActivity implements SensorEventListener {
    private ImageView image;
    private TextView stateTxt;
    private ImageView stateImg;
    private TextView title;
    private ImageView pictureOwner;
    private TextView desc;

    private float currentDegree = 0f;

    private SensorManager mSensorManager;

    TextView tvHeading;
    User gameOwner;
    Event game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_compass);

        this.gameOwner = MyGame.getInstance().getOwner();
        this.game = MyGame.getInstance().getGame();

        bindViews();
        populate();
    }

    public void bindViews() {
        image = (ImageView) findViewById(R.id.imageViewCompass);
        tvHeading = (TextView) findViewById(R.id.textViewTitle);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        stateTxt = (TextView) findViewById(R.id.textViewState);
        stateImg = (ImageView) findViewById(R.id.imageViewState);
        title = (TextView) findViewById(R.id.textViewTitle);
        pictureOwner = (ImageView) findViewById(R.id.imageViewOwner);
        desc = (TextView) findViewById(R.id.textViewDesc);
    }

    public void populate() {
        title.setText(game.getName());
        Picasso.with(getApplicationContext()).load(gameOwner.getPic1()).fit().centerCrop().transform(new RoundedPicasso()).into(pictureOwner);
        desc.setText(game.getDescription());
    }

    @Override
    protected void onResume() {
        super.onResume();

        populate();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        degree += Math.cos((gameOwner.getLongitude() - FacebookUser.getInstance().getLongitude()) / UserList.getInstance().getDistToMe(gameOwner));

        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
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
                MyGame.getInstance().finishTheGame();
                Toast.makeText(this, "Partie abandonnée", Toast.LENGTH_LONG);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
