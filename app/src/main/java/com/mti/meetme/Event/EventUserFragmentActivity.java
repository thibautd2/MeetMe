package com.mti.meetme.Event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;
import com.mti.meetme.FriendsListActivity;
import com.mti.meetme.Interface.ContextDrawerAdapter;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.R;
import com.mti.meetme.Tools.Network.DialogNotConnected;
import com.mti.meetme.UserListActivity;


/**
 * Created by thiba_000 on 13/06/2016.
 */

public class EventUserFragmentActivity extends AppCompatActivity implements ContextDrawerAdapter {
    private FragmentTabHost tabHost;
    private DialogNotConnected dialogNotConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_user_activity);
        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        tabHost.addTab(tabHost.newTabSpec("First Tab").setIndicator("Evénement"),
                EventListActivity.class, null);
        tabHost.addTab(tabHost.newTabSpec("Second Tab").setIndicator("Inconnus"),
                UserListActivity.class, null);
        tabHost.addTab(tabHost.newTabSpec("Third Tab").setIndicator("Amis"),
                FriendsListActivity.class, null);

        if (getIntent().getSerializableExtra("tab") != null)
            tabHost.setCurrentTab((int)getIntent().getSerializableExtra("tab"));

        dialogNotConnected = new DialogNotConnected(this);
        dialogNotConnected.interuptNoConection();
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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //todo becarfull, should use the actual page !
    @Override
    public void menuDrawerSeekBarListener(SeekBar seekBar, TextView textView, String btnName) {
      //  ContextDrawerAdapter page = (ContextDrawerAdapter) getSupportFragmentManager().findFragmentById(getTaskId());
        ContextDrawerAdapter page = (ContextDrawerAdapter) getSupportFragmentManager().findFragmentByTag("Second Tab");
        page.menuDrawerSeekBarListener(seekBar, textView, btnName);
    }

    @Override
    public void menuDrawerMultyChoiceListener(CheckBox checkBox, String btnName, boolean ischecked) {
        ContextDrawerAdapter page = (ContextDrawerAdapter) getSupportFragmentManager().findFragmentByTag("Second Tab");
        page.menuDrawerMultyChoiceListener(checkBox, btnName, ischecked);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dialogNotConnected.stopInteruptNoConection();
    }

    @Override
    public void onResume(){
        super.onResume();
        dialogNotConnected.retartInteruptNoConection();
    }
}