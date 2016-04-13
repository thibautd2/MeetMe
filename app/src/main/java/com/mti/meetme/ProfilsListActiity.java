package com.mti.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.Network;
import com.mti.meetme.Tools.ProfilsAdapter;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.UserList;

import java.util.ArrayList;

/**
 * Created by thiba_000 on 12/04/2016.
 */
public class ProfilsListActiity extends ActionBarActivity{

    LinearLayoutManager mLinearLayoutManager;
    RecyclerView mRecyclerView;
    ArrayList<User> users;
    ProfilsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profils_list);
        users = new ArrayList<>();
        bindViews();
        populate();
        getall_user();
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
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            case R.id.menu_settings:
                displaySettings(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void displaySettings(boolean visible) {
        MenuView.ItemView item = (MenuView.ItemView) findViewById(R.id.menu_settings);
        if (visible)
            item.getItemData().setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        else
            item.getItemData().setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    public void bindViews()
    {
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.list_item);
    }

    public void populate()
    {
        adapter = new ProfilsAdapter(UserList.getInstance(), this);
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
                  if(u.getUid().compareTo(FacebookUser.getInstance().getUid())!=0)
                  users.add(u);
              }
              adapter.notifyDataSetChanged();
          }
          @Override
          public void onCancelled(FirebaseError firebaseError) {
          }
      });
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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
