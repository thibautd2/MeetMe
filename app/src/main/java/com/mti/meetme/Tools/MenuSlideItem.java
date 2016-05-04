package com.mti.meetme.Tools;


/**
 * Created by Alex on 22/04/2016.
 */
public class MenuSlideItem {
    public enum Type {
        SIMPLE_BUTTON,
        SEEKBAR,
        CHECK_BUTTON;
    }

    public static class MySeekBar {
        int start;
        int end;
        int defaut;

        public MySeekBar(int start, int end, int defaut) {
            this.start = start;
            this.end = end;
            this.defaut = defaut;
        }
    }

    public static class MyCheckBox {
        String title;
        boolean checked;

        public MyCheckBox(String title, boolean checked) {
            this.title = title;
            this.checked = checked;
        }
    }

    Type type;
    String mTitle;
    String mSubtitle;
    int mIcon;
    MySeekBar mySeekBar;
    MyCheckBox checkboxTitle[];

    public MenuSlideItem(String title, int icon, String subtitle) {
        mTitle = title;
        mSubtitle = subtitle;
        mIcon = icon;
        mySeekBar = null;
        checkboxTitle = null;
        type = Type.SIMPLE_BUTTON;
    }

    public MenuSlideItem(String title, String mSubtitle, int icon, MenuSlideItem.MySeekBar mySeekBar) {
        mTitle = title;
        mIcon = icon;
        this.mySeekBar = mySeekBar;
        this.mSubtitle = mSubtitle;
        checkboxTitle = null;
        type = Type.SEEKBAR;
    }

    public MenuSlideItem(String title, int icon, MyCheckBox check1, MyCheckBox check2, MyCheckBox check3, MyCheckBox check4) {
        mTitle = title;
        mSubtitle = null;
        mIcon = icon;
        this.mySeekBar = null;
        type = Type.CHECK_BUTTON;
        checkboxTitle = new MyCheckBox[4];
        checkboxTitle[0] = check1;
        checkboxTitle[1] = check2;
        checkboxTitle[2] = check3;
        checkboxTitle[3] = check4;
    }


    public Type getType() {
        return type;
    }

}

