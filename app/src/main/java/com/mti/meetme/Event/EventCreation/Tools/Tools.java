package com.mti.meetme.Event.EventCreation.Tools;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Context;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.RoundedPicasso;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alex on 16/11/2016.
 */

public class Tools {

    //function who allow to choose a friend on a list
    public static void fill(final String[] friendsIds, final HashMap radioIds, final View view, final LinearLayout friendSelectLayout)
    {

        final ArrayList<String> friendsPictures = new ArrayList<>();
        final ArrayList<String> friendsNames = new ArrayList<>();
        final ArrayList<String> friendsUids = new ArrayList<>();

        for (int i = 0; i < friendsIds.length; i++)
        {
            Firebase ref = new Firebase("https://intense-fire-5226.firebaseio.com/users/" + friendsIds[i] );
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    User friend = dataSnapshot.getValue(User.class);

                    friendsNames.add(friend.getName());
                    friendsPictures.add(friend.getPic1());
                    friendsUids.add(friend.getUid());

                    if (friendsNames.size() == friendsIds.length)
                    {
                        for (int i = 0; i < friendsNames.size(); i++)
                        {
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(10, 10, 10, 10);

                            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(100, 100);

                            LinearLayout layout = new LinearLayout(view.getContext());
                            layout.setLayoutParams(lp);
                            layout.setOrientation(LinearLayout.HORIZONTAL);

                            lp.gravity = Gravity.CENTER_VERTICAL;

                            final RadioButton radio = new RadioButton(view.getContext());
                            radio.setGravity(Gravity.CENTER_VERTICAL);
                            radioIds.put(friendsUids.get(i), radio);

                            ImageView profilePic = new ImageView(view.getContext());
                            Picasso.with(view.getContext()).load(friendsPictures.get(i)).transform(new RoundedPicasso()).into(profilePic);
                            profilePic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (radio.isChecked())
                                        radio.setChecked(false);
                                    else
                                        radio.setChecked(true);
                                }
                            });

                            TextView name = new TextView(view.getContext());
                            name.setText(friendsNames.get(i));
                            name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (radio.isChecked())
                                        radio.setChecked(false);
                                    else
                                        radio.setChecked(false);
                                }
                            });

                            layout.addView(radio, lp);
                            layout.addView(profilePic, lp2);
                            layout.addView(name, lp);


                            friendSelectLayout.addView(layout, lp);
                        }
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }
}
