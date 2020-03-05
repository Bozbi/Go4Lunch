package com.sbizzera.go4lunch;

import android.app.Application;

import timber.log.Timber;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        //Timber instance
        Timber.plant(new Timber.DebugTree());

    }
}

