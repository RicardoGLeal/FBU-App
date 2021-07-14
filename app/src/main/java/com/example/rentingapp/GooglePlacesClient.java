package com.example.rentingapp;

import android.content.Context;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

public class GooglePlacesClient {
    public static PlacesClient placesClient;

    public static void Initialize(Context context) {
        //Initialize the SDK
        Places.initialize(context, BuildConfig.GOOGLE_MAPS_API_KEY);

        //Create a new PLaces client instance
        placesClient = Places.createClient(context);
    }


}
