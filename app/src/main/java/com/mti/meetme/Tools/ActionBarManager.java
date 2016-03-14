package com.mti.meetme.Tools;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.mti.meetme.MapsActivity;
import com.mti.meetme.R;

/**
 * Created by thiba_000 on 02/03/2016.
 */
public class ActionBarManager {
    private Activity acti = null;
    private ActionBar actionBar;

     public void setMapActionBar(Activity a) {     //   fonctionne cest map

         acti = a;
         actionBar.setDisplayShowTitleEnabled(false);
         actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
         actionBar.setCustomView(R.layout.actionbar_custom_layout);
         Button map = (Button) acti.findViewById(R.id.map);
         Button profil = (Button) acti.findViewById(R.id.profil);
         map.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(acti.getApplication(), MapsActivity.class);
                 acti.startActivity(intent);
             }
         });

     }
}
