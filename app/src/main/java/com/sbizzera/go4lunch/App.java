package com.sbizzera.go4lunch;

import android.app.Application;

import timber.log.Timber;

public class App extends Application {

    //TODO Questions:
    /*
    ARCHITECTURE:
    Générale: Travailler le schéma avant de travailler le contenu.
    Permission Handler
    ViewModel Factory
    Fragments
    Auth
    Basic Fragments
    Commons
    AppLogs
    Key Stockage
    Valeurs par défauts
    Permission Handler dans la List Activity
    DeviceLocator
    BitMapDescriptor?
    Mapp Handler
    */



    @Override
    public void onCreate() {
        super.onCreate();

        //Timber instance
        Timber.plant(new Timber.DebugTree());

    }
}

