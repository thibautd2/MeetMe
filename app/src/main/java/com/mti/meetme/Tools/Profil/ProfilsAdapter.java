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

    protected ArrayList<User> users;
    protected  Activity acti;
    protected View v;

    public ProfilsAdapter(ArrayList<User> users, Activity acti)
    {
        this.acti = acti;
        this.users = users;
        v = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = null;
        // final int pos = getItemCount();

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_item, parent, false);
        vh = new ViewHolder(v);
        vh.user_age = (TextView) v.findViewById(R.id.list_user_age);

        vh.user_image = (ImageView) v.findViewById(R.id.user_img_list);
        vh.user_name = (TextView) v.findViewById(R.id.user_name_list);
        vh.relativeLayout = (RelativeLayout) v.findViewById(R.id.list_user_relative);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User u = users.get(position);

            if (u != null) {
                Picasso.with(acti).load(u.getPic1()).fit().centerCrop().into(holder.user_image);
                holder.user_name.setText(u.getName());
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
        public ImageView user_image;
        public TextView user_name;
        public TextView user_age;
        public RelativeLayout relativeLayout;
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