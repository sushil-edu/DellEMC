package com.kestone.dellpartnersummit;

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.overrideFont(getApplicationContext(), "fonts/arial.ttf", "fonts/arial.ttf");

    }
}