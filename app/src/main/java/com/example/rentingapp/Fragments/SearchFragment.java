package com.example.rentingapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rentingapp.Models.Rent;
import com.example.rentingapp.Models.User;
import com.example.rentingapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * This fragment is responsible for showing a Google Maps with markers in all the locations where
 * there are users. By clicking on a bookmark, the profile of the corresponding user opens.
 */
public class SearchFragment extends Fragment {
    public static final String TAG = "SearchFragment";
    SupportMapFragment supportMapFragment;
    GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialize view
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        //Initialize map fragment
        supportMapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.google_map);

        //Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //When map is loaded
                map = googleMap;
                queryUsers();

                //When the user clicks on a marker, it goes to the profile of the selected user.
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @SuppressLint("PotentialBehaviorOverride")
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
                        query.whereEqualTo(User.KEY_NAME, marker.getTitle());
                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> users, ParseException e) {
                                if (e == null)
                                {
                                    Fragment fragment = new ProfileFragment(users.get(0));
                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
                                }
                            }
                        });
                        return false;
                    }
                });
            }
        });
        //Return view
        return view;
    }

    /**
     * Gets all the users of the app and calls setMapLocation to create markers in the map.
     */
    private void queryUsers() {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        //the items created most recently will come first and the oldest ones will come last.
        query.addDescendingOrder(Rent.KEY_CREATED_AT);
        // Retrieve all the posts
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (ParseUser user: users) {
                    Log.i(TAG, "Rent retrieved");
                    setMapLocation(user);

                }
                //Moves the camera to the current user location.
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(User.getLatLng(ParseUser.getCurrentUser()), 10));
            }
        });
    }

    /**
     * Creates a new marker for every user.
     * @param user ParseUser
     */
    private void setMapLocation(ParseUser user) {
        //Create a new instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();
        //set the position
        markerOptions.position(User.getLatLng(user));
        //set title
        markerOptions.title(user.get(User.KEY_NAME).toString());
        map.addMarker(markerOptions);
        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}