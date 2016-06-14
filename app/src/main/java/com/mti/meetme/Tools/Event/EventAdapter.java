package com.mti.meetme.Tools.Event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.mti.meetme.Model.Event;
import com.mti.meetme.R;
import com.mti.meetme.Tools.Map.CalculateDistance;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.TodayDesire;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by thiba_000 on 13/06/2016.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>
{

    protected ArrayList<Event> events;
    protected Activity acti;
    protected View v;
    protected int originalLayoutHeight;
    protected int originalImageHeight;

    public EventAdapter(ArrayList<Event> events, Activity acti)
    {
        this.acti = acti;
        this.events = events;
        v = null;
    }

    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EventAdapter.ViewHolder vh = null;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        vh = new EventAdapter.ViewHolder(v);
        vh.event_adresse = (TextView) v.findViewById(R.id.event_adresse);
        vh.event_dist = (TextView) v.findViewById(R.id.event_card_distance);
        vh.event_image = (ImageView) v.findViewById(R.id.event_img_list);
        vh.event_name = (TextView) v.findViewById(R.id.event_name_list);
        vh.event_heure = (TextView) v.findViewById(R.id.event_date_list);
        vh.relativeLayout = (RelativeLayout) v.findViewById(R.id.list_event_relative);
        vh.event_username = (TextView) v.findViewById(R.id.event_user);
        originalLayoutHeight = vh.relativeLayout.getLayoutParams().height;
        originalImageHeight = vh.event_image.getLayoutParams().height;
        return vh;
    }

    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder holder, int position) {
        final Event e = events.get(position);
        if (e != null) {


            holder.event_name.setText(e.getName());
            holder.event_adresse.setText(e.getAdresse());
            double dist =  CalculateDistance.getDistance(new LatLng(FacebookUser.getInstance().getLatitude(), FacebookUser.getInstance().getLongitude()), new LatLng(e.getLatitude(), e.getLongitude()));
            holder.event_heure.setText(e.getDate());
            holder.event_dist.setText(String.format("%.1f",dist )+ " km");
            holder.event_username.setText(e.getUsername());

            String currentDesire = e.getCategorie();
            if(currentDesire.compareTo(TodayDesire.Desire.play.toString())==0)
                Picasso.with(acti).load(R.drawable.finegames).fit().centerCrop().into(holder.event_image);
            if(currentDesire.compareTo(TodayDesire.Desire.party.toString())==0)
                Picasso.with(acti).load(R.drawable.soiree2fine).fit().centerCrop().into(holder.event_image);
            if(currentDesire.compareTo(TodayDesire.Desire.Drink.toString())==0)
                Picasso.with(acti).load(R.drawable.drinkfine).fit().centerCrop().into(holder.event_image);
            if(currentDesire.compareTo(TodayDesire.Desire.Date.toString())==0)
                Picasso.with(acti).load(R.drawable.rencontrefine).fit().centerCrop().into(holder.event_image);
            if(currentDesire.compareTo(TodayDesire.Desire.Sport.toString())==0)
                Picasso.with(acti).load(R.drawable.finegames).fit().centerCrop().into(holder.event_image);
            if(currentDesire.compareTo(TodayDesire.Desire.Everything.toString())==0)
                Picasso.with(acti).load(R.drawable.allfine).fit().centerCrop().into(holder.event_image);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(events != null)
            return events.size();
        else
            return  0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView event_image;
        public TextView event_name;
        public TextView event_adresse;
        public TextView event_dist;
        public TextView event_heure;
        public TextView event_username;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void remove(int position) {
        if(events.size()>position) {
            events.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(0,events.size());
        }
    }

    public ImageButton getImageButton(int id) {
        return (ImageButton) v.findViewById(id);
    }
}
