package com.kestone.dellpartnersummit.widget.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;


public class MyButton extends Button {
    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if(!isInEditMode()){ setTypeface(MyTypeface.getNormalFont(getContext()));
        }
    }

}
