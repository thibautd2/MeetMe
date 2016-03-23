package com.mti.meetme.Tools;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.mti.meetme.MapsActivity;
import com.mti.meetme.Model.User;
import com.mti.meetme.ProfileActivity;
import com.mti.meetme.R;
import com.squareup.picasso.Picasso;

/**
 * Created by thiba_000 on 22/03/2016.
 */
public class ImageCaroussel extends Fragment {

    TextView t1,t2,t3,t4,t5;

    public void setCount(int count) {
        this.count = count;
    }

    int count;
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    int position;
    User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.image_caroussel_item, container, false);
        ImageView img = (ImageView) view.findViewById(R.id.item_image);


        t1 = (TextView) view.findViewById(R.id.r1);
        t2 = (TextView) view.findViewById(R.id.r2);
        t3 = (TextView) view.findViewById(R.id.r3);
        t4 = (TextView) view.findViewById(R.id.r4);
        t5 = (TextView) view.findViewById(R.id.r5);

        switch (count)
        {
            case 0:
                t1.setVisibility(View.VISIBLE);
                break;
            case 1:
                t2.setVisibility(View.VISIBLE);
                break;
            case 2:
                t3.setVisibility(View.VISIBLE);
                break;
            case 3:
                t4.setVisibility(View.VISIBLE);
                break;
            case 4:
                t5.setVisibility(View.VISIBLE);
                break;

        }
        Log.e("currentBitem", String.valueOf(ProfileActivity.pager.getCurrentItem()));
        Log.e("position", String.valueOf(position));
        switch (position)
        {
            case 0:
                Picasso.with(getActivity()).load(user.getPic1()).fit().centerCrop().into(img);
                t1.setAlpha(1.f);
                break;
            case 1:
                Picasso.with(getActivity()).load(user.getPic2()).fit().centerCrop().into(img);
                t2.setAlpha(1.f);
                break;
            case 2:
                Picasso.with(getActivity()).load(user.getPic3()).fit().centerCrop().into(img);
                t3.setAlpha(1.f);
                break;
            case 3:
                Picasso.with(getActivity()).load(user.getPic4()).fit().centerCrop().into(img);
                t4.setAlpha(1.f);
                break;
        }
            return view;
        }

}

