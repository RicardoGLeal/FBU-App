package com.example.rentingapp.Models;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
@ParseClassName("Location")
public class Location extends ParseObject {
    public static final String KEY_PLACE_ID = "placeId";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_PLACE_NAME = "placeName";
    public static final String KEY_PLACE_ADDRESS = "placeAddress";
    public static final String KEY_GENERAL_LOCATION = "generalLocation";

    public void setPlaceId(String placeId) {
        put(KEY_PLACE_ID, placeId);
    }

    public void setLat(Double lat) {
        put(KEY_LAT, lat);
    }

    public void setLng(Double lng){
        put(KEY_LNG, lng);
    }

    public void setPlaceName(String placeName) {
        put(KEY_PLACE_NAME, placeName);
    }

    public void setPlaceAddress(String placeAddress) {
        put(KEY_PLACE_ADDRESS, placeAddress);
    }

    public void setGeneralLocation(String generalLocation) {
        put(KEY_GENERAL_LOCATION, generalLocation);
    }
}
