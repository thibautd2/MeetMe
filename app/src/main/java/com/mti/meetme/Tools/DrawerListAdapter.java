package com.mti.meetme.Tools;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mti.meetme.Interface.ContextDrawerAdapter;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.R;

import java.util.ArrayList;

/**
 * Created by Alex on 22/04/2016.
 */
public class DrawerListAdapter extends BaseAdapter {
    ContextDrawerAdapter mContext;
    ArrayList<MenuSlideItem> mNavItems;

    public DrawerListAdapter(ContextDrawerAdapter context, ArrayList<MenuSlideItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (mNavItems.get(position).getType() == MenuSlideItem.Type.SIMPLE_BUTTON) {
                view = inflater.inflate(R.layout.drawer_item_title, null);
                TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
                subtitleView.setText(mNavItems.get(position).mSubtitle);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.menuDrawerSimpleButtonListener(mNavItems.get(position).mIcon);
                    }
                });
            }
            else if (mNavItems.get(position).getType() == MenuSlideItem.Type.SEEKBAR) {
                view = inflater.inflate(R.layout.drawer_item_seekbar, null);

                SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
                final TextView seekBarValue = (TextView) view.findViewById(R.id.barValue);

                seekBar.setProgress(mNavItems.get(position).mySeekBar.defaut);
                seekBar.setMax(mNavItems.get(position).mySeekBar.end);
               // seekBar.set(mNavItems.get(position).mySikBar.defaut);
                seekBarValue.setText(String.valueOf(mNavItems.get(position).mySeekBar.defaut) + mNavItems.get(position).mSubtitle);

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        seekBarValue.setText(String.valueOf(progress) + mNavItems.get(position).mSubtitle);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mContext.menuDrawerSeekBarListener(seekBar,seekBarValue, mNavItems.get(position).mTitle);
                    }
                });

//                seekBarValue.setText(seekBar.getProgress());
            }
            else { //checkox
                view = inflater.inflate(R.layout.drawer_item_multychoice, null);

                ArrayList<CheckBox> checkboxs = new ArrayList<>();
                checkboxs.add((CheckBox) view.findViewById(R.id.checkBox));
                checkboxs.add((CheckBox) view.findViewById(R.id.checkBox2));
                checkboxs.add((CheckBox) view.findViewById(R.id.checkBox3));
                checkboxs.add((CheckBox) view.findViewById(R.id.checkBox4));

                for (int i = 0; i < 4; i++)
                    setCheckBoxListener(checkboxs.get(i), position, i);
            }
        } else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        iconView.setImageResource(mNavItems.get(position).mIcon);
        titleView.setText(mNavItems.get(position).mTitle);

        return view;
    }

    private void setCheckBoxListener(final CheckBox checkbox, final int position, int nb) {
        if (mNavItems.get(position).checkboxTitle[nb] == null) {
            checkbox.setVisibility(View.INVISIBLE);
            return;
        }

        checkbox.setText(mNavItems.get(position).checkboxTitle[nb].title);
        checkbox.setChecked(mNavItems.get(position).checkboxTitle[nb].checked);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                     if (isChecked) {
                                                         mContext.menuDrawerMultyChoiceListener(checkbox, mNavItems.get(position).mTitle, true);
                                                       //  Log.e("DrawerAdapter", "checkbox check");
                                                     } else {
                                                      //   Log.e("DrawerAdapter", "checkbox UN- check");
                                                         mContext.menuDrawerMultyChoiceListener(checkbox, mNavItems.get(position).mTitle, false);
                                                     }
                                                 }
                                             }
        );
    }
}
