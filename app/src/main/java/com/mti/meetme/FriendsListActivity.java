package com.mti.meetme;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.FriendsListAdapter;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.Profil.ProfilsAdapter;
import com.mti.meetme.controller.FacebookUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thiba_000 on 12/04/2016.
 */
public class FriendsListActivity extends UserListActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_maps:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            case R.id.menu_friends:
                Intent intent2 = new Intent(getApplicationContext(), UserListActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void populate()
    {
        adapter = new FriendsListAdapter(users, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        adapter.notifyDataSetChanged();
    }



    @Override
    protected ItemTouchHelper.SimpleCallback getNewItemTocuh() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Toast.makeText(getApplicationContext(), users.get(viewHolder.getAdapterPosition()).getName() + " deleted from your friendlist", Toast.LENGTH_SHORT);
                removeFriend(FacebookUser.getInstance(), users.get(viewHolder.getAdapterPosition()));
                removeFriend(users.get(viewHolder.getAdapterPosition()), FacebookUser.getInstance());

                adapter.remove(viewHolder.getAdapterPosition());
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
    public void getall_user()
    {
        Firebase ref = Network.getAlluser;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User u = postSnapshot.getValue(User.class);
                    if(u != null && u.getUid() != null && u.getUid().compareTo(FacebookUser.getInstance().getUid())!=0 && FacebookUser.getInstance().haveThisFriend(u.getUid()))
                        users.add(u);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
}
