package com.example.rentingapp.Controllers;

import android.text.format.DateUtils;
import android.util.Log;
import android.widget.EditText;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rentingapp.Adapters.RentsAdapter;
import com.example.rentingapp.Fragments.RentItemDialogFragment;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.Rent;
import com.example.rentingapp.Models.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.textfield.TextInputLayout;
import com.google.maps.android.SphericalUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    /**
     * Configure the refreshing colors of the swipeRefreshLayout
     * @param swipeRefreshLayout swipeRefreshLayout
     */
    public static void setColorSchemeResources(SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * This functions manages so see if the EditText is empty or not.
     * @param textInputLayout
     * @param editText
     * @return
     */
    public static boolean validateField(TextInputLayout textInputLayout, EditText editText) {
        if(editText.getText().toString().isEmpty()) {
            textInputLayout.setError("Field can't be empty");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    /**
     * Gets the general address of a place
     * @param place Google place location
     * @return
     */
    public static String getGeneralLocation(Place place) {
        String generalLocation = "";
        List<AddressComponent> addressComponents = place.getAddressComponents().asList();
        for (int i=0; i<addressComponents.size(); i++) {
            if(addressComponents.get(i).getTypes().contains("locality") ||
                    addressComponents.get(i).getTypes().contains("administrative_area_level_2") ||
                    addressComponents.get(i).getTypes().contains("administrative_area_level_1"))
                if (generalLocation == "")
                    generalLocation = addressComponents.get(i).getShortName();
                else
                    generalLocation = generalLocation + ", "+ addressComponents.get(i).getShortName();
            if (addressComponents.get(i).getTypes().contains("country"))
            {
                generalLocation = generalLocation +", "+ addressComponents.get(i).getName();
                break;
            }
        }
        return generalLocation;
    }

    /**
     * Assigns the required values to a user, either to create a new one or edit an existing one
     * @param user
     * @param name user's name
     * @param username user's username
     * @param password user's password
     * @param email user's email
     * @param photoFile user's profile photoFile
     * @param photo user's profile photo
     * @param description user's description
     * @param placeId user's placeId
     * @param placeName user's placeName
     * @param placeAddress user's placeAddress
     * @param placeLat user's place Latitude
     * @param placeLng user's place Longitude
     * @param generalLocation user's general location
     */
    public static void setUserValues(ParseUser user, String name, String username, String password,
                                     String email, File photoFile, ParseFile photo, String description,
                                     String placeId, String placeName, String placeAddress, Double placeLat,
                                     Double placeLng, String generalLocation) {
        user.put(User.KEY_NAME, name);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        if (photoFile != null)
            user.put(User.KEY_PROFILE_PICTURE, photo);
        user.put(User.KEY_DESCRIPTION, description);
        user.put(User.KEY_PLACE_ID, placeId);
        user.put(User.KEY_PLACE_NAME, placeName);
        user.put(User.KEY_PLACE_ADDRESS, placeAddress);
        user.put(User.KEY_LAT, placeLat);
        user.put(User.KEY_LNG, placeLng);
        user.put(User.KEY_GENERAL_LOCATION, generalLocation);
    }


    /*
      Limit selectable Date range
    */
    public static CalendarConstraints.Builder limitRanges(List<RentItemDialogFragment.DateInterval> datesIntervals) {
        //Create array of validators for the dates
        ArrayList<CalendarConstraints.DateValidator> validators = new ArrayList<>();

        for (int i = 0; i < datesIntervals.size(); i++) {
            //Create instances of calendars
            Calendar calendarStart = Calendar.getInstance();
            Calendar calendarEnd = Calendar.getInstance();
            //Set the time of the initial and end dates.
            calendarStart.setTime(datesIntervals.get(i).getInitialDate());
            calendarEnd.setTime(datesIntervals.get(i).getEndDate());
            calendarStart.add(Calendar.DATE, -2);

            //Converts the dates to millis.
            long minDate = calendarStart.getTimeInMillis();
            long maxDate = calendarEnd.getTimeInMillis();
            validators.add(DateValidatorPointBackward.before(minDate));
            validators.add(DateValidatorPointForward.from(maxDate));
        }
        //Creates the constraintsBuilder
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();

        constraintsBuilderRange.setValidator(CompositeDateValidator.anyOf(validators));

        return constraintsBuilderRange;
    }
}
