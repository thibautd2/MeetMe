package com.mti.meetme.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by W_Corentin on 26/02/2016.
 */
public class CoquetteTextView extends TextView
{
        public CoquetteTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        public CoquetteTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public CoquetteTextView(Context context) {
            super(context);
            init();
        }

        public void init() {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Coquette.ttf");
            setTypeface(tf ,1);
        }

}
