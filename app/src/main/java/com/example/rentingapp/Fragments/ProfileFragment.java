package com.example.rentingapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.rentingapp.Adapters.ItemsProfileAdapter;
import com.example.rentingapp.FullSizeImageActivity;
import com.example.rentingapp.MainActivity;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.SavedItem;
import com.example.rentingapp.Models.User;
import com.example.rentingapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.rentingapp.Controllers.ImagesController.loadCircleImage;
import static com.example.rentingapp.Controllers.ImagesController.openImage;

/**
 * This class is in charge of displaying the profile of a user.
 */
public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private ParseUser user;
    private ImageView ivProfileImage;
    private TextView tvPersonName, tvPersonDescription, tvLocation;
    private RecyclerView rvItems, rvWishList;
    protected List<Item> allItems, savedItems;
    private ItemsProfileAdapter adapter, adapterWishList;
    private FloatingActionButton fabEditProfile;
    private LottieAnimationView lottieLoadingItems, lottieLoadingWishList;

    public ProfileFragment(ParseUser user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Get references
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvPersonName = view.findViewById(R.id.tvPersonName);
        tvPersonDescription = view.findViewById(R.id.tvPersonDescription);
        tvLocation = view.findViewById(R.id.tvLocation);
        rvItems = view.findViewById(R.id.rvItems);
        rvWishList = view.findViewById(R.id.rvWishList);
        fabEditProfile = view.findViewById(R.id.fabEditProfile);
        lottieLoadingItems = view.findViewById(R.id.lottieLoadingItems);
        lottieLoadingWishList = view.findViewById(R.id.lottieLoadingWishList);
        //Assign values
        loadCircleImage(getContext(), user.getParseFile(User.KEY_PROFILE_PICTURE), ivProfileImage);
        tvPersonName.setText(user.getString(User.KEY_NAME));
        tvPersonDescription.setText(user.getString(User.KEY_DESCRIPTION));
        tvLocation.setText(user.getString(User.KEY_GENERAL_LOCATION));
        allItems = new ArrayList<>();
        savedItems = new ArrayList<>();
        adapter = new ItemsProfileAdapter(getContext(), allItems);
        adapterWishList = new ItemsProfileAdapter(getContext(), savedItems);
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new GridLayoutManager(getContext(), 3));

        rvWishList.setAdapter(adapterWishList);
        rvWishList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        queryOwnItems();
        querySavedItems();

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String photoUrl = user.getParseFile(User.KEY_PROFILE_PICTURE).getUrl();
                openImage(photoUrl, getContext(), ivProfileImage);
            }
        });

        if(user.equals(ParseUser.getCurrentUser()))
            fabEditProfile.setVisibility(FloatingActionButton.VISIBLE);
        else
            fabEditProfile.setVisibility(FloatingActionButton.GONE);

        fabEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) getContext();
                FragmentManager fm = activity.getSupportFragmentManager();
                EditProfileDialogFragment editProfileDialogFragment = EditProfileDialogFragment.newInstance(user);
                editProfileDialogFragment.show(fm,"fragment_rent_item");
            }
        });
    }

    /**
     * Query for getting the items of an specified user.
     */
    private void queryOwnItems() {
        // Specify which class to query
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        //include the user of the post
        query.include(Item.KEY_OWNER);
        //the items created most recently will come first and the oldest ones will come last.
        query.addDescendingOrder(Item.KEY_CREATED_AT);
        query.whereEqualTo(Item.KEY_OWNER, user);
        lottieLoadingItems.setVisibility(LottieAnimationView.VISIBLE);
        // Retrieve all the posts
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> items, ParseException e) {
                if (e != null) {
                    return;
                }
                allItems.addAll(items);
                adapter.notifyDataSetChanged();
                adapterWishList.notifyDataSetChanged();
                lottieLoadingItems.setVisibility(LottieAnimationView.GONE);
            }
        });
    }


    /**
     * Query for getting the items saved by the user.
     */
    private void querySavedItems() {
        // Specify which class to query
        ParseQuery<SavedItem> query = ParseQuery.getQuery(SavedItem.class);
        //include the user of the post
        query.include(SavedItem.KEY_USER);
        query.include(SavedItem.KEY_ITEM);
        //the items created most recently will come first and the oldest ones will come last.
        query.addDescendingOrder(Item.KEY_CREATED_AT);
        query.whereEqualTo(SavedItem.KEY_USER, user);
        lottieLoadingWishList.setVisibility(LottieAnimationView.VISIBLE);
        // Retrieve all the posts
        query.findInBackground(new FindCallback<SavedItem>() {
            @Override
            public void done(List<SavedItem> savedItemsList, ParseException e) {
                if (e != null) {
                    return;
                }
                for (SavedItem savedItem: savedItemsList) {
                    savedItems.add(savedItem.getItem());
                }
                adapter.notifyDataSetChanged();
                adapterWishList.notifyDataSetChanged();
                lottieLoadingWishList.setVisibility(LottieAnimationView.GONE);
            }
        });
    }
}