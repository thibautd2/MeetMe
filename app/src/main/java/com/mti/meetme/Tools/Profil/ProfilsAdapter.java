package com.mti.meetme.Tools.Profil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import com.google.android.gms.maps.model.LatLng;
import com.mti.meetme.ChatActivity;
import com.mti.meetme.Model.User;
import com.mti.meetme.ProfileActivity;
import com.mti.meetme.R;
import com.mti.meetme.Tools.Map.CalculateDistance;
import com.mti.meetme.controller.FacebookUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by thiba_000 on 12/04/2016.
 */
public class ProfilsAdapter extends RecyclerView.Adapter<ProfilsAdapter.ViewHolder>
{

    protected ArrayList<User> users;
    protected Activity acti;
    protected View v;
    protected int originalLayoutHeight;
    protected int originalImageHeight;

    public ProfilsAdapter(ArrayList<User> users, Activity acti)
    {
        this.acti = acti;
        this.users = users;
        v = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = null;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_cardview, parent, false);
        vh = new ViewHolder(v);
        vh.user_age = (TextView) v.findViewById(R.id.list_user_age);
        vh.user_envie = (TextView) v.findViewById(R.id.user_envie_list);
        vh.user_image = (ImageView) v.findViewById(R.id.user_img_list);
        vh.user_name = (TextView) v.findViewById(R.id.user_name_list);
        vh.user_dist = (TextView) v.findViewById(R.id.card_distance);
        vh.relativeLayout = (RelativeLayout) v.findViewById(R.id.list_user_relative);
        originalLayoutHeight = vh.relativeLayout.getLayoutParams().height;
        originalImageHeight = vh.user_image.getLayoutParams().height;
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User u = users.get(position);
            if (u != null) {

                holder.user_age.setText("" + u.convertBirthdayToAge() + " ans");
                holder.user_name.setText(u.getName());
                holder.user_envie.setText(u.getEnvie());
                double dist =  CalculateDistance.getDistance(new LatLng(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude()), new LatLng(u.getLatitude(), u.getLongitude()));

                holder.user_dist.setText(String.format("%.1f",dist )+ " km");

                if (u.getGender().compareTo("male")==0) {
                    holder.user_image.getLayoutParams().height = originalImageHeight -70;
                    holder.relativeLayout.getLayoutParams().height = originalLayoutHeight -70;
                }
                Picasso.with(acti).load(u.getPic1()).fit().centerCrop().into(holder.user_image);
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

    @Override
    public int getItemCount() {
        if(users != null)
            return users.size();
        else
            return  0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView user_image;
        public TextView user_name;
        public TextView user_age;
        public TextView user_dist;
        public TextView user_envie;
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