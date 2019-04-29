package com.saltechdigital.dliver;

import com.marcinorlowski.fonty.Fonty;

import androidx.multidex.MultiDexApplication;

public class Application extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fonty.context(this)
                .normalTypeface("Exo-Regular.ttf")
                .italicTypeface("Aramis-Italic.ttf")
                .boldTypeface("Capture_it.ttf")
                .build();
    }
}
