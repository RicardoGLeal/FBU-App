package com.example.rentingapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rentingapp.Adapters.ItemsProfileAdapter;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.Models.User;
import com.example.rentingapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.rentingapp.Controllers.ImagesController.loadCircleImage;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private ParseUser user;
    private ImageView ivProfileImage;
    private TextView tvPersonName, tvPersonDescription, tvLocation;
    private RecyclerView rvItems;
    protected List<Item> allItems;
    private ItemsProfileAdapter adapter;

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
        //Assign values
        loadCircleImage(getContext(), user.getParseFile(User.KEY_PROFILE_PICTURE), ivProfileImage);
        tvPersonName.setText(user.getString(User.KEY_NAME));
        tvPersonDescription.setText(user.getString(User.KEY_DESCRIPTION));
        tvLocation.setText(user.getString(User.KEY_GENERAL_LOCATION));
        allItems = new ArrayList<>();
        adapter = new ItemsProfileAdapter(getContext(), allItems);
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new GridLayoutManager(getContext(), 3));
        queryItems();
    }

    /**
     * Query for getting the items of an specified user.
     */
    private void queryItems() {
        // Specify which class to query
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        //include the user of the post
        query.include(Item.KEY_OWNER);
        //Limiting the number of posts getting back.
        query.setLimit(20);
        //the items created most recently will come first and the oldest ones will come last.
        query.addDescendingOrder(Item.KEY_CREATED_AT);
        query.whereEqualTo(Item.KEY_OWNER, user);

        // Retrieve all the posts
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> items, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Item item: items) {
                    Log.i(TAG, "Items: " + item.getDescription());
                }
                allItems.addAll(items);
                adapter.notifyDataSetChanged();
            }
        });
    }
}