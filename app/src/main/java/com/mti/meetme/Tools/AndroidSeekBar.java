package com.mti.meetme.Tools;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mti.meetme.R;

/**
 * Created by Alex on 28/04/2016.
 */
public class AndroidSeekBar extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        final TextView seekBarValue = (TextView) findViewById(R.id.barValue);

        Log.e("AndSeekBar", "onCreate");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                seekBarValue.setText(String.valueOf(progress));
                Log.e("AndSeekBar", "onProgressChanged");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
    }
}
