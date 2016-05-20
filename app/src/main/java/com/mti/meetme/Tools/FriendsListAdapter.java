package com.mti.meetme.Tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mti.meetme.ChatActivity;
import com.mti.meetme.Model.User;
import com.mti.meetme.ProfileActivity;
import com.mti.meetme.R;
import com.mti.meetme.Tools.Profil.ProfilsAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Alex on 12/05/2016.
 */
public class FriendsListAdapter extends ProfilsAdapter
{
    public FriendsListAdapter(ArrayList<User> users, Activity acti) {
        super(users, acti);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = null;

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item, parent, false);
        vh = new ViewHolder(v);

        vh.user_image = (ImageView)v.findViewById(R.id.user_img_list);
        vh.user_name = (TextView)v.findViewById(R.id.user_name_list);
        vh.relativeLayout = (RelativeLayout) v.findViewById(R.id.list_user_relative);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User u = users.get(position);

            if (u != null) {
                Picasso.with(acti).load(u.getPic1()).fit().centerCrop().into(holder.user_image);
                holder.user_name.setText(u.getName());
                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(acti, ProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("User", u);
                        intent.putExtras(b);
                        acti.startActivity(intent);
                    }
                });
            }

            getImageButton(R.id.msgBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("UFriendsListActy", "msgButton clicked");
                        Intent chatIntent = new Intent(acti.getApplicationContext(), ChatActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putParcelable("User", u);
                        chatIntent.putExtras(bundle);

                        acti.startActivity(chatIntent);
                    }
                });

            getImageButton(R.id.findBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("UserListActy", "msgButton clicked");
                }
            });
    }
}