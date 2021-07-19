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
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private ParseUser user;
    private ImageView ivProfileImage;
    private TextView tvPersonName, tvPersonDescription;;
    private RecyclerView rvItems;
    protected List<Item> allItems;
    private ItemsProfileAdapter adapter;

    public ProfileFragment(ParseUser user) {
        this.user = user;
    }
    /**
     * Create a new instance of this fragment using the provided parameters.
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment(ParseUser.getCurrentUser());
        return fragment;
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
        rvItems = view.findViewById(R.id.rvItems);
        //Asign values
        loadCircleImage(getContext(), user.getParseFile(User.KEY_PROFILE_PICTURE), ivProfileImage);
        tvPersonName.setText(user.getString(User.KEY_NAME));
        tvPersonDescription.setText(user.getString(User.KEY_DESCRIPTION));
        allItems = new ArrayList<>();
        adapter = new ItemsProfileAdapter(getContext(), allItems);
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new GridLayoutManager(getContext(), 3));
        queryItems();
    }

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