package com.example.rentingapp.Controllers;

import android.text.format.DateUtils;

import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ActionsController {
    public static String getRelativeTimeAgo(String rawDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static int getDistanceInKm(Item item, ParseUser user) {
        LatLng from = User.getLatLng(user);
        LatLng to = User.getLatLng(item.getOwner());
        int distance = (int) SphericalUtil.computeDistanceBetween(from, to);
        return distance/1000;
    }
}
