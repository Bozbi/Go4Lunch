package com.sbizzera.go4lunch;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {

    private static Application sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        //Initializing ABP
        AndroidThreeTen.init(this);

    }

    //Returning Application for later user of Context.
    public static Application getApplication() {
        return sApplication;
    }

}

