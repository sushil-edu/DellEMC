package com.kestone.dellpartnersummit.widget;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Vikrant on 1/15/2017.
 */

public class MyTypeface
{
    public static Typeface getBoldFont(Context c)
    {
        return Typeface.createFromAsset(c.getAssets(),"fonts/arial_bold.ttf");
    }

    public static Typeface getNormalFont(Context c)
    {
        return Typeface.createFromAsset(c.getAssets(),"fonts/arial.ttf");
    }

}
