package com.mti.meetme.Tools.Profil;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by thiba_000 on 12/04/2016.
 */
public class ProfilsAdapter extends RecyclerView.Adapter<ProfilsAdapter.ViewHolder>
{

    private ArrayList<User> users;
    private Activity acti;
    private boolean friendList;
    View v;

    public ProfilsAdapter(ArrayList<User> users, Activity acti, boolean friendList)
    {
        this.acti = acti;
        this.users = users;
        this.friendList = friendList;
        v = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = null;
       // final int pos = getItemCount();

        if (friendList) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item, parent, false);
            vh = new ViewHolder(v);


        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_item, parent, false);
            vh = new ViewHolder(v);
            vh.user_age = (TextView)v.findViewById(R.id.list_user_age);
        }
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

                if (!friendList) {
                    holder.user_age.setText("" + u.convertBirthdayToAge() + " ans");

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
            }

        if (friendList) {
            getImageButton(R.id.msgBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("UserListActy", "msgButton clicked");
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

    @Override
    public int getItemCount() {
        if(users!=null)
            return users.size();
        else
            return  0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView user_image;
        TextView user_name;
        TextView user_age;
        RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void remove(int position) {
        if(users.size()>position) {
            users.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(0, users.size());
        }
    }

    public ImageButton getImageButton(int id) {
        return (ImageButton) v.findViewById(id);
    }

}