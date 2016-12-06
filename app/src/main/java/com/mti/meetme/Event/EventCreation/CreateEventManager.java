package com.mti.meetme.Event.EventCreation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.R;
import com.mti.meetme.Tools.MyPagerAdapter;

import java.util.List;
import java.util.Vector;

/**
 * Created by Alex on 09/06/2016.
 */

public class CreateEventManager extends AppCompatActivity {
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.create_event_manager);

        // Création de la liste de Fragments que fera défiler le PagerAdapter
        List fragments = new Vector();

        Bundle bundSport = new Bundle();
        Bundle bundparty = new Bundle();

        bundparty.putString("type", "party");
        bundSport.putString("type", "sport");

        // Ajout des Fragments dans la liste
        fragments.add(Fragment.instantiate(this,CreatePartyActivity.class.getName(), bundSport));
        fragments.add(Fragment.instantiate(this,CreatePartyActivity.class.getName(), bundparty));
        fragments.add(Fragment.instantiate(this,CreateGameActivity.class.getName()));

        // Création de l'adapter qui s'occupera de l'affichage de la liste de
        // Fragments
        this.mPagerAdapter = new MyPagerAdapter(super.getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager) super.findViewById(R.id.event_viewpager);
        // Affectation de l'adapter au ViewPager
        pager.setAdapter(this.mPagerAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();

        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_maps:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(CreateEventManager.this, MapsActivity.class));
    }
}
