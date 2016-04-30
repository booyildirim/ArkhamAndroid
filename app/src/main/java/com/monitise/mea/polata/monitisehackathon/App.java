package com.monitise.mea.polata.monitisehackathon;

import android.app.Application;

import com.estimote.sdk.EstimoteSDK;

import timber.log.Timber;

/**
 * Created by polata on 21/04/2016.
 */
public class App extends Application {

    private static final String APP_ID = "blank-test-app-kj4";
    private static final String APP_TOKEN = "6db773d26d17775b94b05638103fa735";
    @Override
    public void onCreate() {
        super.onCreate();

        EstimoteSDK.initialize(getApplicationContext(), APP_ID, APP_TOKEN);
        EstimoteSDK.enableDebugLogging(true);

        Timber.plant(new Timber.DebugTree());
    }
}
