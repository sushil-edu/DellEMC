package com.kestone.dellpartnersummit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {
    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        typFase();
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        typFase();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        typFase();
    }
    public void typFase(){
        if(!isInEditMode()){
            setTypeface(MyTypeface.getNormalFont(getContext()));
        }
    }
}
