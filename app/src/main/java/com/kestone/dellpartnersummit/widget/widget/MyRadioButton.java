package com.kestone.dellpartnersummit.widget.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;


public class MyRadioButton extends RadioButton {
    public MyRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyRadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(MyTypeface.getNormalFont(getContext()));
        }
    }

}
