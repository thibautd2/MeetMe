package com.mti.meetme.Event.EventCreation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.R;
import com.mti.meetme.Tools.MyPagerAdapter;
import com.mti.meetme.Tools.Network.DialogNotConnected;

import java.util.List;
import java.util.Vector;

/**
 * Created by Alex on 09/06/2016.
 */

public class CreateEventManager extends AppCompatActivity {
    private PagerAdapter mPagerAdapter;
    private DialogNotConnected dialogNotConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
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

        dialogNotConnected = new DialogNotConnected(this);
        dialogNotConnected.interuptNoConection();
        TextView txt  = (TextView) findViewById(R.id.Title);
        txt.setText("Evènement");

    }

    @Override
    protected void onPause() {
        dialogNotConnected.stopInteruptNoConection();
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        dialogNotConnected.retartInteruptNoConection();

        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(this);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        ActionBar.LayoutParams lp1 = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View customNav = LayoutInflater.from(this).inflate(R.layout.actionbar_list_elements, null); // layout which contains your button.

        actionBar.setCustomView(customNav, lp1);
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
