package com.lsfv.myliteraturesharing;

import android.app.Application;

import fr.xebia.android.freezer.Freezer;

/**
 * Created by Sony on 01-05-2018.
 */

public class SharingApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Freezer.onCreate(this);
    }
}
