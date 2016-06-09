package com.mti.meetme.Tools;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Alex on 09/06/2016.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    private final List fragments;

    //On fournit à l'adapter la liste des fragments à afficher
    public MyPagerAdapter(FragmentManager fm, List fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return (Fragment) this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
