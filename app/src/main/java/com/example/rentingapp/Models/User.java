package com.example.rentingapp.Models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class User {
    public static final String KEY_NAME = "name";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PLACE_ID = "placeId";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_PLACE_NAME = "placeName";
    public static final String KEY_PLACE_ADDRESS = "placeAddress";
    public static final String KEY_GENERAL_LOCATION = "generalLocation";


    public static LatLng getLatLng(ParseUser user) {
        double latitude, longitude;
        latitude = user.getDouble(User.KEY_LAT);
        longitude = user.getDouble(User.KEY_LNG);
        return new LatLng(latitude, longitude);
    }
}
