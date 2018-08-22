package com.kestone.dellpartnersummit.widget.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


public class MyBoldTextView extends TextView
{
    public MyBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyBoldTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(MyTypeface.getBoldFont(getContext()));
        }
    }

}
