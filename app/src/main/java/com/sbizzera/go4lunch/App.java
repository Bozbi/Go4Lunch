package com.sbizzera.go4lunch;

import android.app.Application;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.jakewharton.threetenabp.AndroidThreeTen;

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
    Get instances of Frangments not recreate them
    Commons
    AppLogs
    Key Stockage
    Valeurs par défauts
    Permission Handler dans la ListRestaurantActivity
    DeviceLocator
    BitMapDescriptor?
    Mapp Handler
    */


    private static Application sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        //Initializing ABP
        AndroidThreeTen.init(this);

    }

    public static Application getApplication() {
        return sApplication;
    }

}

