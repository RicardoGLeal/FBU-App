package com.example.rentingapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import static com.example.rentingapp.Controllers.ActionsController.setColorSchemeResources;

public class FeedFragment extends Fragment {
    public static final String TAG = "FeedFragment";
    private RecyclerView rvItems;
    protected ItemsAdapter adapter;
    protected List<Item> allItems;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private ImageView ivExpandToolbar;
    private RelativeLayout filtersLayout;
    private Spinner spinnerCategories;
    private Context context;
    private Boolean filteredByDistance = false;

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
        toolbar = view.findViewById(R.id.toolbar);
        ivExpandToolbar = view.findViewById(R.id.ivExpandToolbar);
        filtersLayout = view.findViewById(R.id.filtersLayout);
        ivExpandToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filtersLayout.getVisibility()==View.GONE) {
                    TransitionManager.beginDelayedTransition(toolbar, new AutoTransition());
                    filtersLayout.setVisibility(View.VISIBLE);
                    ivExpandToolbar.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                }
                else {
                    TransitionManager.beginDelayedTransition(toolbar, new AutoTransition());
                    filtersLayout.setVisibility(View.GONE);
                    ivExpandToolbar.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
            }
        });

        rvItems = view.findViewById(R.id.rvItems);
        allItems = new ArrayList<>();
        spinnerCategories = view.findViewById(R.id.spinnerCategories);
        context = getContext();
        AppCompatActivity activity = (AppCompatActivity) context;
        toolbar.setTitle("");
        activity.setSupportActionBar(toolbar);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.categoriesFeed));
        spinnerCategories.setAdapter(categoriesAdapter);
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(context, spinnerCategories.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        setColorSchemeResources(swipeRefreshLayout);
        adapter = new ItemsAdapter(getContext(), allItems);
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(context));
        queryItems();
    }

    /**
     * Gets the latest 20 items.
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
                if(!filteredByDistance) {
                    QuickSort ob = new QuickSort();
                    ob.sort(allItems, 0, allItems.size()-1);
                    adapter.notifyDataSetChanged();
                    filteredByDistance = true;
                    Toast.makeText(getContext(), "Items sorted by distance!", Toast.LENGTH_SHORT).show();
                }
                else {
                    adapter.clear();
                    allItems.clear();
                    queryItems();
                    filteredByDistance = false;
                }
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