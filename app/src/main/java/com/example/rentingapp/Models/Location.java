package com.example.rentingapp.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Location")
public class Location extends ParseObject {
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_CITY = "city";
    public static final String KEY_ZIP = "ZIP";

    public void setCountry(String country) {
        put(KEY_COUNTRY, country);
    }

    public void setCity(String city) {
        put(KEY_CITY, city);
    }

    public void setZIP(int ZIP) {
        put(KEY_ZIP, ZIP);
    }
}
