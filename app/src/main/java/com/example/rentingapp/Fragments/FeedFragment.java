package com.example.rentingapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rentingapp.Adapters.ItemsAdapter;
import com.example.rentingapp.LoginActivity;
import com.example.rentingapp.MainActivity;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.QuickSort;
import com.example.rentingapp.R;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.rentingapp.Controllers.ActionsController.getDistanceInKm;


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
        setHasOptionsMenu(true);
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
                for (Item item: items) {
                    Log.i(TAG, "Items: " + item.getDescription());
                    int distance = getDistanceInKm(item, ParseUser.getCurrentUser());
                    item.setDistance(distance);
                }
                allItems.addAll(items);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Creates the menu for the toolbar in the FeedFragment.
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    /**
     * This function is call when the user clicks on a item that is inside of the Menu.
     * @param item The item pressed in the menu.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_sortByLocation:
                QuickSort ob = new QuickSort();
                ob.sort(allItems, 0, allItems.size()-1);
                adapter.notifyDataSetChanged();
                break;

            case R.id.logout_btn:
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        AppCompatActivity activity = (AppCompatActivity) context;
                        activity.finish();
                    }
                });
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}