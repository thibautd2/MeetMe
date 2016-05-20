package com.mti.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.Profil.ProfilsAdapter;
import com.mti.meetme.controller.FacebookUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thiba_000 on 12/04/2016.
 */

public class UserListActivity extends AppCompatActivity {

    LinearLayoutManager mLinearLayoutManager;
    RecyclerView mRecyclerView;
    ArrayList<User> users;
    ProfilsAdapter adapter;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profils_list);

        simpleItemTouchCallback = getNewItemTocuh();

        users = new ArrayList<>();
        getall_user();
        bindViews();
        populate();
    }

    protected ItemTouchHelper.SimpleCallback getNewItemTocuh() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                adapter.remove(viewHolder.getAdapterPosition());
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_maps:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_friends:
                Intent intent2 = new Intent(getApplicationContext(), FriendsListActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bindViews()
    {
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.list_item);
    }

    public void populate()
    {
        adapter = new ProfilsAdapter(users, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        adapter.notifyDataSetChanged();
    }

    public void getall_user()
    {
        Firebase ref = Network.getAlluser;
         ref.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot snapshot) {
              users.clear();
              for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                  User u = postSnapshot.getValue(User.class);
                  if(u != null && u.getUid() != null && u.getUid().compareTo(FacebookUser.getInstance().getUid())!=0)
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
