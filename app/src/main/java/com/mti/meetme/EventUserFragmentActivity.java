package com.mti.meetme;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

/**
 * Created by thiba_000 on 13/06/2016.
 */

public class EventUserFragmentActivity extends TabActivity {
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_user_activity);
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("third Tab");
        tab1.setIndicator("Evénement");
        tab1.setContent(new Intent(this,EventListActivity.class));
        tab2.setIndicator("Inconnus");
        tab2.setContent(new Intent(this,UserListActivity.class));
        tab3.setIndicator("Amis");
        tab3.setContent(new Intent(this,FriendsListActivity.class));
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
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
            case R.id.menu_friends:
                Intent intent2 = new Intent(getApplicationContext(), EventUserFragmentActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}