package com.mti.meetme.Tools.Profil;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.mti.meetme.Model.User;

/**
 * Created by thiba_000 on 23/03/2016.
 */
public class CarousselPager  extends FragmentStatePagerAdapter {

        User user;

        public User getUser() {
        return user;
    }

        public void setUser(User user) {
        this.user = user;
    }


        public CarousselPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
           ImageCaroussel ic = new ImageCaroussel();
           ic.setUser(user);
            ic.setPosition(position);
            ic.setCount(getCount());
            return ic;
        }

        @Override public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

        }

        @Override
        public int getCount() {
            int nb_result = 0;
            if(user.getPic1()!= null)
                nb_result++;
            if(user.getPic2()!= null)
                nb_result++;
            if(user.getPic3()!= null)
                nb_result++;
            if(user.getPic4()!= null)
                nb_result++;
            if(user.getPic5()!= null)
                nb_result++;
            return nb_result;
        }
    }
