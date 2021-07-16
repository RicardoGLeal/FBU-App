package com.example.rentingapp.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rentingapp.Adapters.ItemsAdapter;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class FeedFragment extends Fragment {
    public static final String TAG = "FeedFragment";
    private RecyclerView rvItems;
    protected ItemsAdapter adapter;
    protected List<Item> allItems;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private Context context;

    // Required empty public constructor
    public FeedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        rvItems = view.findViewById(R.id.rvItems);
        allItems = new ArrayList<>();
        toolbar = view.findViewById(R.id.toolbar);
        context = getContext();
        AppCompatActivity activity = (AppCompatActivity) context;
        toolbar.setTitle("");
        activity.setSupportActionBar(toolbar);

        swipeRefreshLayout = view.findViewById(R.id.swipeContainer);

        //OnClickListener implemented when the user pulls to refresh.
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                allItems.clear();
                queryItems();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        adapter = new ItemsAdapter(getContext(), allItems);
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(context));
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
        // Retrieve all the posts
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> items, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Item post: items) {
                    Log.i(TAG, "Items: " + post.getDescription());
                }
                allItems.addAll(items);
                adapter.notifyDataSetChanged();
            }
        });
    }
}