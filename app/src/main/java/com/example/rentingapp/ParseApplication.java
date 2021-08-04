package com.example.rentingapp;

import android.app.Application;

import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.Location;
import com.example.rentingapp.Models.Rent;
import com.example.rentingapp.Models.SavedItem;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * This class is in charge of configuring Parse in the app.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        // Register parse models
        ParseObject.registerSubclass(Rent.class);
        ParseObject.registerSubclass(Item.class);
        ParseObject.registerSubclass(Location.class);
        ParseObject.registerSubclass(SavedItem.class);

        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .enableLocalDataStore()
                .build());

        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);


    }
}
