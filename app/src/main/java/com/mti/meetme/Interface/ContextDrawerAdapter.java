package com.mti.meetme.Interface;

import android.support.annotation.NonNull;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Alex on 28/04/2016.
 */
public interface ContextDrawerAdapter {
    Object getSystemService(@NonNull String name);
    void menuDrawerSimpleButtonListener(int idBtn);
    void menuDrawerSeekBarListener(SeekBar seekBar, TextView textView, String btnName);
    void menuDrawerMultyChoiceListener(CheckBox checkBox, String btnName, boolean ischecked);
}
