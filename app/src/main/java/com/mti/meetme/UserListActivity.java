package com.mti.meetme;

import android.content.Intent;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.mti.meetme.Interface.ContextDrawerAdapter;
import com.mti.meetme.Model.SortUserList;
import com.mti.meetme.Model.User;
import com.mti.meetme.Tools.DrawerListAdapter;
import com.mti.meetme.Tools.Map.CalculateDistance;
import com.mti.meetme.Tools.MenuSlideItem;
import com.mti.meetme.Tools.Network.Network;
import com.mti.meetme.Tools.Profil.ProfilsAdapter;
import com.mti.meetme.controller.FacebookUser;
import com.mti.meetme.controller.UserList;

import java.util.ArrayList;

/**
 * Created by thiba_000 on 12/04/2016.
 */

public class UserListActivity extends FragmentActivity implements ContextDrawerAdapter {

    LinearLayoutManager mLinearLayoutManager;
    RecyclerView mRecyclerView;
    ProfilsAdapter adapter;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;
    ArrayList<User> users;

    private ListView mDrawerList;
    private RelativeLayout mDrawerPane;
    private DrawerLayout mDrawerLayout;
    private ArrayList<MenuSlideItem> MenuSlideItems = new ArrayList<MenuSlideItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profils_list);

        simpleItemTouchCallback = getNewItemTocuh();

        users = new ArrayList<>();
        updateUsers();
        bindViews();
        init_menu();
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
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mDrawerPane))
                    mDrawerLayout.closeDrawer(mDrawerPane);
                else
                    mDrawerLayout.openDrawer(mDrawerPane);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bindViews()
    {

        //mLinearLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_item);
    }

    public void populate()
    {
        adapter = new ProfilsAdapter(users, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(gaggeredGridLayoutManager);
        mRecyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        adapter.notifyDataSetChanged();
    }

    public void init_menu() {
        //todo uncomment this
/*        final android.app.ActionBar ab = this.getActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_drawer); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(true);
        */MenuSlideItems = new ArrayList<>();
        MenuSlideItems.add(new MenuSlideItem("Distance", " km", R.drawable.radar, new MenuSlideItem.MySeekBar(0, 10, SortUserList.getInstance().distanceToSearch / 1000)));
        MenuSlideItems.add(new MenuSlideItem("Genre", R.drawable.gender, new MenuSlideItem.MyCheckBox("Men", SortUserList.getInstance().displayMen),
                new MenuSlideItem.MyCheckBox("Women", SortUserList.getInstance().displayWomen), null, null));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter drawerAdapter = new DrawerListAdapter(this, MenuSlideItems);
        mDrawerList.setAdapter(drawerAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });
    }

    public void updateUsers()
    {
        Firebase ref = Network.getAlluser;
         ref.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot snapshot) {
              users.clear();
              for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                  User u = postSnapshot.getValue(User.class);
                  if(u != null && u.getUid() != null && u.getUid().compareTo(FacebookUser.getInstance().getUid())!=0 && SortUserList.getInstance().user_correspond(u))
                      users.add(u);
              }

              UserList.getInstance().sortListUser(users);
              adapter.notifyDataSetChanged();
          }
          @Override
          public void onCancelled(FirebaseError firebaseError) {
          }
      });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(UserListActivity.this, MapsActivity.class));
    }

    private double getDistToMe(User u1) {
        LatLng latLng = new LatLng(u1.getLatitude(), u1.getLongitude());

        User u2 = FacebookUser.getInstance();
        LatLng latLng2 = new LatLng(u2.getLatitude(), u2.getLongitude());

        return CalculateDistance.getDistance(latLng, latLng2);
    }

    private void selectItemFromDrawer(int position) {
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    @Override
    public void menuDrawerSeekBarListener(SeekBar seekBar, TextView textView, String btnName) {
        if (btnName.equals("Distance")) {
            SortUserList.getInstance().distanceToSearch = 1000 * seekBar.getProgress();

            updateUsers();
        }

        //updateMap();
    }

    @Override
    public void menuDrawerMultyChoiceListener(CheckBox checkBox, String btnName, boolean ischecked) {
        if (btnName.equals("Genre")) {
            if (!ischecked && (!SortUserList.getInstance().displayWomen || !SortUserList.getInstance().displayMen)) {
                checkBox.setChecked(true);
                return;
            }

            if (checkBox.getText().equals("Men"))
                SortUserList.getInstance().displayMen = ischecked;
            else if (checkBox.getText().equals("Women"))
                SortUserList.getInstance().displayWomen = ischecked;

            updateUsers();
        }

        adapter.notifyDataSetChanged();
    }
}
