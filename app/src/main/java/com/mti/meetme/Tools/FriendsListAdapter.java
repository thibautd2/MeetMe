package com.mti.meetme.Tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.ChatActivity;
import com.mti.meetme.Model.User;
import com.mti.meetme.ProfileActivity;
import com.mti.meetme.R;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.Profil.ProfilsAdapter;
import com.mti.meetme.controller.FacebookUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 12/05/2016.
 */
public class FriendsListAdapter extends  RecyclerView.Adapter<FriendsListAdapter.ViewHolder>
{
    protected ArrayList<User> users;
    protected Activity acti;
    protected View v;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;

    public FriendsListAdapter(ArrayList<User> users, Activity acti)
    {
        this.acti = acti;
        this.users = users;
        v = null;
        simpleItemTouchCallback = getNewItemTocuh();
    }

    public void update(ArrayList<User> users) {
        this.users = users;
    }

    protected ItemTouchHelper.SimpleCallback getNewItemTocuh() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Log.e("friendlistadapter", "onSwiped");
//                Toast.makeText(getApplicationContext(), users.get(viewHolder.getAdapterPosition()).getName() + " deleted from your friendlist", Toast.LENGTH_SHORT);

                Log.e("friendlistadapter", "size users: " + users.size());
                removeFriend(FacebookUser.getInstance(), users.get(viewHolder.getAdapterPosition()));
                removeFriend(users.get(viewHolder.getAdapterPosition()), FacebookUser.getInstance());

                remove(viewHolder.getAdapterPosition());
            }

            private void removeFriend(User user_a, User user_b) {
                ArrayList<String> list = user_a.receiveMeetMeFriendsTab();
                String str = "";

                if (list != null) {
                    for (String s : list)
                        if (!s.equals(user_b.getUid()))
                            str += s + ";";

                    user_a.setMeetMeFriends(str);

                    Firebase ref = Network.find_user(user_a.getUid());

                    Map<String, Object> desc = new HashMap<>();
                    desc.put("meetMeFriends", str);

                    ref.updateChildren(desc, null);
                }
            }
        };
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

    @Override
    public int getItemCount() {
        if(users!=null)
            return users.size();
        else
            return  0;
    }

    public void remove(int position) {
        if(users.size()>position) {
            users.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(0, users.size());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView user_image;
        public TextView user_name;
        public TextView user_age;
        public TextView user_envie;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public ImageButton getImageButton(int id) {
        return (ImageButton) v.findViewById(id);
    }
    public ItemTouchHelper.SimpleCallback getSimpleItemTouchCallback() {
        return simpleItemTouchCallback;
    }
}