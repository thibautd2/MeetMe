package com.mti.meetme.Event.Game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.mti.meetme.ChatActivity;
import com.mti.meetme.Model.Event;
import com.mti.meetme.Model.User;
import com.mti.meetme.ProfileActivity;
import com.mti.meetme.R;
import com.mti.meetme.Tools.FriendsListAdapter;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.RoundedPicasso;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.UserList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.StreamHandler;

/**
 * Created by Alex on 17/06/2016.
 */

public class ParticipantsListAdapter extends  RecyclerView.Adapter<ParticipantsListAdapter.ViewHolder>{
    protected ArrayList<User> users;
    protected Activity acti;
    protected View v;
    protected Event event;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;

    public ParticipantsListAdapter(ArrayList<User> users, Event event, Activity acti)
    {
        this.acti = acti;
        this.users = users;
        this.event = event;
        v = null;
        simpleItemTouchCallback = getNewItemTocuh();
    }

    protected ItemTouchHelper.SimpleCallback getNewItemTocuh() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                removeParticipant(users.get(viewHolder.getAdapterPosition()));
                remove(viewHolder.getAdapterPosition());
            }

            private void removeParticipant(User user) {
                ArrayList<String> list = event.receiveParticipants();
                String str = "";

                if (list != null) {
                    for (String s : list)
                        if (!s.equals(user.getUid()))
                            str += s + ";";

                    //event.setParticipants(str);
                    Firebase ref = Network.find_event(event.receiveEventId());
                    Map<String, Object> desc = new HashMap<>();
                    desc.put("participants", str);
                    ref.updateChildren(desc, null);
                }
            }
        };
    }

    public void remove(int position) {
        if(users.size()>position) {
            users.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(0, users.size());
        }
    }

    public ItemTouchHelper.SimpleCallback getSimpleItemTouchCallback() {
        return simpleItemTouchCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = null;

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gameparticipants_list_item, parent, false);
        vh = new ViewHolder(v);

        vh.user_image = (ImageView)v.findViewById(R.id.user_img_list);
        vh.user_name = (TextView)v.findViewById(R.id.user_name_list);
        vh.relativeLayout = (RelativeLayout) v.findViewById(R.id.list_user_relative);
        vh.user_distance = (TextView)v.findViewById(R.id.distance);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User u = users.get(position);

        if (u != null) {
            Picasso.with(acti).load(u.getPic1()).fit().centerCrop().transform(new RoundedPicasso()).into(holder.user_image);
            holder.user_name.setText(u.getName());
            holder.user_distance.setText(String.valueOf(Math.round(UserList.getInstance().getDistToMe(u))) + " m");

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

        v.findViewById(R.id.msgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(acti.getApplicationContext(), ChatActivity.class);

                Bundle bundle = new Bundle();
                bundle.putParcelable("User", u);
                chatIntent.putExtras(bundle);

                acti.startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(users!=null)
            return users.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView user_image;
        public TextView user_name;
        public TextView user_age;
        public TextView user_distance;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
