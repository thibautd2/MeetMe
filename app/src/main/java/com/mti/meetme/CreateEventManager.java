package com.mti.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.mti.meetme.R;
import com.mti.meetme.Tools.MyPagerAdapter;

import java.util.List;
import java.util.Vector;

/**
 * Created by Alex on 09/06/2016.
 */

public class CreateEventManager extends FragmentActivity {
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.create_event_manager);

        // Création de la liste de Fragments que fera défiler le PagerAdapter
        List fragments = new Vector();

        // Ajout des Fragments dans la liste
        fragments.add(Fragment.instantiate(this,EventActivity.class.getName()));
        fragments.add(Fragment.instantiate(this,EventActivity.class.getName()));
        fragments.add(Fragment.instantiate(this,EventActivity.class.getName()));

        // Création de l'adapter qui s'occupera de l'affichage de la liste de
        // Fragments
        this.mPagerAdapter = new MyPagerAdapter(super.getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager) super.findViewById(R.id.event_viewpager);
        // Affectation de l'adapter au ViewPager
        pager.setAdapter(this.mPagerAdapter);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(CreateEventManager.this, MapsActivity.class));
    }
}
