package com.example.rentingapp.Controllers;

import android.text.format.DateUtils;
import android.util.Log;

import com.example.rentingapp.Adapters.RentsAdapter;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.Rent;
import com.example.rentingapp.Models.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ActionsController {
    /**
     * Calculates how much time has passed since a certain date and time so far
     * @param rawDate The date in a string format
     * @return a string which says how long ago the event passed.
     */
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

    /**
     * Calculates the distance in kilometers between a user and the owner's of an item.
     * @param item
     * @param user
     * @return
     */
    public static int getDistanceInKm(Item item, ParseUser user) {
        LatLng from = User.getLatLng(user);
        LatLng to = User.getLatLng(item.getOwner());
        int distance = (int) SphericalUtil.computeDistanceBetween(from, to);
        return distance/1000;
    }

    /**
     * Gets the rents of a user, can be ownRents or foreignRents, it depends of the value of the boolean variable.
     * @param TAG TAG identifier of the fragment.
     * @param allRents list of all the rents retrieved.
     * @param adapter rentsAdapter
     * @param ownRentedItems Is it a rent from your own item, or from someone else's item?
     */
    public static void queryRents(String TAG, List<Rent> allRents, RentsAdapter adapter, boolean ownRentedItems) {
        //Specify which class to query
        ParseQuery<Rent> query = ParseQuery.getQuery(Rent.class);
        //include the user of the post
        query.include(Rent.KEY_ITEM);
        query.include(Rent.KEY_OWNER);
        query.include(Rent.KEY_TENANT);

        if (ownRentedItems)
            query.whereEqualTo(Rent.KEY_OWNER, ParseUser.getCurrentUser());
        else
            query.whereEqualTo(Rent.KEY_TENANT, ParseUser.getCurrentUser());
        //Limiting the number of posts getting back.
        query.setLimit(20);
        //the items created most recently will come first and the oldest ones will come last.
        query.addDescendingOrder(Rent.KEY_CREATED_AT);
        // Retrieve all the posts
        query.findInBackground(new FindCallback<Rent>() {
            @Override
            public void done(List<Rent> rents, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Rent rent: rents) {
                    Log.i(TAG, "Rent retrieved");
                }
                allRents.addAll(rents);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
