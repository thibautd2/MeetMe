package com.mti.meetme.Event.EventCreation;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.mti.meetme.Event.EventCreation.Tools.Tools;
import com.mti.meetme.LoginActivity;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.R;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.MyGame;
import com.mti.meetme.controller.TodayDesire;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by thiba_000 on 04/06/2016.
 */

public class CreateGameActivity extends Fragment implements AdapterView.OnItemClickListener {

    private CheckBox men;
    private CheckBox women;
    private Button create;
    private TextView type;
    private EditText name;
    private EditText desc;
    private RadioButton friend;
    private RadioButton all;
    private RadioButton compass;
    private RadioButton warmNcold;
    private RadioButton selection;
    private LinearLayout friendSelectLayout;

    private HashMap<String, RadioButton> radioIds;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageView img = (ImageView) getView().findViewById(R.id.event_header);

        img.setBackgroundResource(R.drawable.finegames);

        SimpleDateFormat dateFormat = MyGame.getInstance().getDateFormat();
        final Date date = new Date();
        System.out.println(dateFormat.format(date));

        setItems();
        setGenreCheckBox();

        checkboxSelection();

        radioIds = new HashMap<>();

        if (!FacebookUser.getInstance().getMeetMeFriends().isEmpty())
                Tools.fill(FacebookUser.getInstance().getMeetMeFriends().split(";"), radioIds, this.getView(), friendSelectLayout);

        setOnClickCreation();
    }

    private void sendEventToFirebase() {
        User u = FacebookUser.getInstance();

        if (MyGame.getInstance().getGame() != null) {
            u.removeParticipation(MyGame.getInstance().getGame().receiveEventId());
            MyGame.getInstance().finishTheGame();
        }

        String visibility = "friends";
        if (all.isChecked())
            visibility = "all";

        else if (selection.isChecked())
            visibility = "friend_selection";

        if (!men.isChecked())
            visibility += ";women;";
        else if (!women.isChecked())
            visibility += ";men;";
        else
            visibility += ";all;";

        String gameType = "compass";
        if (warmNcold.isChecked())
            gameType = "warmNcold";

        SimpleDateFormat dateFormat = MyGame.getInstance().getDateFormat();
        Date date = new Date();
        long oneHour = 3600 * 1000;
        Date endDate = new Date(date.getTime() + oneHour);

        Event event = new Event(name.getText().toString(), desc.getText().toString(), "not so far",
                u.getUid(), visibility, TodayDesire.Desire.play.toString(), dateFormat.format(date).toString(), dateFormat.format(endDate).toString(), FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude(), FacebookUser.getInstance().getName(), gameType);

        if (visibility.compareTo("friends") == 0)
            event.setInvited(u.getMeetMeFriends());
        else if (visibility.compareTo("friend_selection") == 0)
        {
            String invited = "";

            for(Map.Entry<String, RadioButton> e : radioIds.entrySet()) {
                String uid = e.getKey();
                RadioButton radio = e.getValue();

                if (radio.isChecked())
                    invited += uid + ";";
            }

            event.setInvited(invited);
        }

        Firebase ref = Network.create_event("Event :" + name.getText().toString() + u.getUid());
        ref.setValue(event);
        GeoFire geoFire = new GeoFire(Network.geofire);
        geoFire.setLocation("Event :" + name.getText().toString() + u.getUid(), new GeoLocation(event.getLatitude(), event.getLongitude()));
        Toast.makeText(getApplicationContext(), "Evénement de jeux Créé !", Toast.LENGTH_LONG).show();

        name.setText("");
        desc.setText("");

        MyGame.getInstance().setGame(event);
        u.addParticipateTo(event.receiveEventId());

        Intent intent = new Intent(getContext(), MapsActivity.class);
        startActivity(intent);
    }

    private void setOnClickCreation() {

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //todo check all field checked
                if (name.getText().toString().length() == 0 || desc.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Tu dois remplir toutes les informations", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (MyGame.getInstance().getGame() != null) {
                    warningDialogNewEvent();
                }
                else {
                    sendEventToFirebase();
                }
            }
        });
    }

    private void checkboxSelection()
    {
        final RadioButton friend = (RadioButton) getView().findViewById(R.id.event_friends2);
        final RadioButton all = (RadioButton) getView().findViewById(R.id.event_all2);
        final RadioButton selection = (RadioButton) getView().findViewById(R.id.event_friends_selection2);

        CompoundButton.OnCheckedChangeListener change = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    all.setChecked(false);
                    friend.setChecked(false);

                    if (selection.isChecked())
                    {
                        for(Map.Entry<String, RadioButton> e : radioIds.entrySet()) {
                            e.getValue().setChecked(false);
                        }

                        friendSelectLayout.setVisibility(View.GONE);
                        selection.setChecked(false);
                    }

                    buttonView.setChecked(true);

                    if (selection.isChecked())
                        friendSelectLayout.setVisibility(View.VISIBLE);
                }
            }
        };

        friend.setOnCheckedChangeListener(change);
        all.setOnCheckedChangeListener(change);
        selection.setOnCheckedChangeListener(change);
    }

    private void warningDialogNewEvent() {
        Dialog d = new AlertDialog.Builder(getContext())
                .setTitle("Attention")
                .setMessage("Créer une nouvelle partie mettera fin à la premiere")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendEventToFirebase();
                    }
                })
                .create();
        d.show();
    }

    private void setItems()
    {
        String currentDesire = "Let's play a game !";

        create = (Button) getView().findViewById(R.id.event_create);
        type = (TextView) getView().findViewById(R.id.event_type);
        type.setText(currentDesire.toString());
        name = (EditText) getView().findViewById(R.id.event_name);
        desc = (EditText) getView().findViewById(R.id.event_description);
        friend = (RadioButton) getView().findViewById(R.id.event_friends2);
        all = (RadioButton) getView().findViewById(R.id.event_all2);
        compass = (RadioButton) getView().findViewById(R.id.game_type_compass);
        warmNcold = (RadioButton) getView().findViewById(R.id.game_type_temp);
        selection = (RadioButton) getView().findViewById(R.id.event_friends_selection2);
        friendSelectLayout = (LinearLayout) getView().findViewById(R.id.friendsSelectionLayout2);


        CompoundButton.OnCheckedChangeListener change = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    all.setChecked(false);
                    friend.setChecked(false);
                    buttonView.setChecked(true);
                }
            }
        };

        friend.setOnCheckedChangeListener(change);
        all.setOnCheckedChangeListener(change);
        all.setChecked(true);

        CompoundButton.OnCheckedChangeListener changeType = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    compass.setChecked(false);
                    warmNcold.setChecked(false);
                    buttonView.setChecked(true);
                }
            }
        };


        compass.setOnCheckedChangeListener(changeType);
        warmNcold.setOnCheckedChangeListener(changeType);
        compass.setChecked(true);
    }

    private void setGenreCheckBox() {
        men = (CheckBox) getView().findViewById(R.id.sex_male);
        women = (CheckBox) getView().findViewById(R.id.sex_female);
        men.setChecked(true);
        women.setChecked(true);

        men.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && !women.isChecked())
                    men.setChecked(true);
            }
        });

        women.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && !men.isChecked())
                    women.setChecked(true);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_create_game, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Firebase.setAndroidContext(getApplicationContext());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String str = (String) parent.getItemAtPosition(position);
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }
}
