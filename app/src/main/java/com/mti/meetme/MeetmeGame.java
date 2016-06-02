package com.mti.meetme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.mti.meetme.Tools.Network.Network;

/**
 * Created by Alex on 02/06/2016.
 */

public class MeetmeGame extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetmegame);

        bindViews();
        populateViews();
    }

    private void bindViews()
    {
        TextView tv1 = (TextView) findViewById(R.id.textViewCG1);
        TextView tv2 = (TextView) findViewById(R.id.textViewCG2);
        TextView tv3 = (TextView) findViewById(R.id.textViewCG3);
        Button btnSubmit = (Button) findViewById(R.id.buttonCGsend);
        //loginButton = (LoginButton) findViewById(R.id.login_button);
    }

    private void populateViews()
    {
    }

}
