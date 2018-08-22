package com.kestone.dellpartnersummit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;


public class MyEditText extends EditText {
    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if(!isInEditMode()){
            setTypeface(MyTypeface.getNormalFont(getContext()));
            setTextSize(16.0f);
        }
    }

}
