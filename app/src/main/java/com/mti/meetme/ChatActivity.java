package com.mti.meetme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.mti.meetme.Model.User;
import com.mti.meetme.controller.FacebookUser;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

public class ChatActivity extends AppCompatActivity {

    public User targetUser;
    public User currentUser;

    public Pubnub pubnub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        targetUser = getIntent().getParcelableExtra("User");
        currentUser = FacebookUser.getInstance();

        pubnub = new Pubnub(getResources().getString(R.string.PublishKey), getResources().getString(R.string.SubscribeKey));

        try {
            pubnub.subscribe("demo_tutorial", new Callback() {
                public void successCallback(String channel, Object message) {
                    Log.i("PUBNUB TEST", message.toString());
                }

                public void errorCallback(String channel, PubnubError error) {
                    Log.i("PUBNUB ERROR", error.toString());
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

}
