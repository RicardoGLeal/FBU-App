package com.example.rentingapp.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentingapp.Adapters.ItemsAdapter;
import com.example.rentingapp.LoginActivity;
import com.example.rentingapp.Models.Item;
import com.example.rentingapp.QuickSort;
import com.example.rentingapp.R;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.rentingapp.Controllers.ActionsController.getDistanceInKm;
import static com.example.rentingapp.Controllers.ActionsController.setColorSchemeResources;

public class FeedFragment extends Fragment {
    public static final String TAG = "FeedFragment";
    private RecyclerView rvItems;
    protected ItemsAdapter adapter;
    protected List<Item> allItems;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    private Toolbar toolbar;
    private ImageView ivExpandToolbar;
    private RelativeLayout filtersLayout;
    private TextView tvSelectCategory;
    ArrayList<Integer> catList = new ArrayList<>();
    boolean[] selectedCategory;
    String[] categoriesArray = {"All", "Electronics", "Furniture", "Clothing", "Vehicles", "Sports", "Books", "Toys"};
    List<String> listSelectedCategories = new ArrayList<>();
    private Context context;
    private boolean sortedByDistance = false;
    private boolean sortedByPrice = false;
    int colorPrimaryDark, colorWhite;
    ParseQuery<Item> query;

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
        progressBar = view.findViewById(R.id.pb);

        //ClickListener to expand toolbar
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
        tvSelectCategory = view.findViewById(R.id.tvSelectCategory);
        selectedCategory = new boolean[categoriesArray.length];
        tvSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureCategories();
            }
        });

        rvItems = view.findViewById(R.id.rvItems);
        allItems = new ArrayList<>();
        context = getContext();
        AppCompatActivity activity = (AppCompatActivity) context;
        toolbar.setTitle("");
        activity.setSupportActionBar(toolbar);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.categoriesFeed));
        colorPrimaryDark = getResources().getColor(R.color.colorPrimaryDark);
        colorWhite = getResources().getColor(R.color.white);
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
     * Creates a new AlertDialog with all the categories the user can select. It implements the function
     * setMultiChoiceItems, so the user can select multiple categories.
     */
    private void configureCategories() {
        //Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Categories");
        //set dialog non cancelable

        builder.setMultiChoiceItems(categoriesArray, selectedCategory, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked){
                    //When checkbox selected
                    //Add position in categories list
                    catList.add(which);
                    //Sort category list
                    Collections.sort(catList);
                }
                else {
                    //When checkbox unselected
                    //Remove position from categories list
                    catList.remove(catList.indexOf(which));
                }
            }
        });
        //If the user clicks in the confirm button..
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Initialize string builder
                StringBuilder stringBuilder = new StringBuilder();
                listSelectedCategories = new ArrayList<>();
                for (int i=0; i<catList.size(); i++) {
                    //Concat array value
                    stringBuilder.append(categoriesArray[catList.get(i)]);
                    listSelectedCategories.add(categoriesArray[catList.get(i)]);
                    //Check condition
                    if(i != catList.size()-1) {
                        //When j value not equal to day list size -1
                        //Add comma
                        stringBuilder.append(", ");
                    }
                }
                //Set text on text view
                tvSelectCategory.setText(stringBuilder.toString());
                adapter.clear();
                allItems.clear();
                queryItems();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss dialog
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i<selectedCategory.length; i++) {
                    //Remove all selection
                    selectedCategory[i] = false;
                    //Clear category list
                    catList.clear();
                    //Clear text view value
                    tvSelectCategory.setText("");
                }
            }
        });
        //Show dialog
        builder.show();
    }

    /**
     * Configures the query that will obtain the items, regarding the filters applied.
     */
    private void setupQueryItems() {
        // Specify which class to query
        query = ParseQuery.getQuery(Item.class);
        //include the user of the post
        query.include(Item.KEY_OWNER);
        //Limiting the number of posts getting back.
        query.setLimit(20);
        //Restrict if there are selected categories
        if(!listSelectedCategories.isEmpty() && !listSelectedCategories.contains("All"))
            query.whereContainedIn(Item.KEY_CATEGORY, listSelectedCategories);

        //the items created most recently will come first and the oldest ones will come last.
        query.addDescendingOrder(Item.KEY_CREATED_AT);
    }

    /**
     * Gets the items of all the users.
     */
    private void queryItems() {
        //allItems.clear();
        setupQueryItems();
        // Retrieve all the posts
        progressBar.setVisibility(ProgressBar.VISIBLE);
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> items, ParseException e) {
                if (e != null) {
                   // Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Item item: items) {
                   // Log.i(TAG, "Items: " + item.getDescription());
                    int distance = getDistanceInKm(item, ParseUser.getCurrentUser());
                    item.setDistance(distance);
                }
                allItems.addAll(items);
                adapter.setAllItems(items);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(ProgressBar.GONE);
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
        String title = item.getTitle().toString();

        switch(title) {
            case "SortByLocation": //Sort Items by Distance
            {
                if(!sortedByDistance && !sortedByPrice) {
                    sortItems("distance", item);
                }
                else if(sortedByDistance) {
                    adapter.clear();
                    allItems.clear();
                    queryItems();
                    item.getIcon().setTint(colorPrimaryDark);
                    sortedByDistance = false;
                }
                else
                    clearSortFiltersAndSortBy("distance", item);
                break;
            }
            case "SortByPrice": //Sort Items by Price
            {
                if (!sortedByPrice && !sortedByDistance) {
                    sortItems("price", item);
                }
                else if(sortedByPrice) {
                    adapter.clear();
                    allItems.clear();
                    queryItems();
                    item.getIcon().setTint(colorPrimaryDark);
                    sortedByPrice = false;
                }
                else
                    clearSortFiltersAndSortBy("price", item);
                break;
            }

            case "Logout": //Logout
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sorts the items in the feed by the specified property.
     * @param property
     * @param item
     */
    private void sortItems(String property, MenuItem item) {
        QuickSort ob = new QuickSort(property);
        ob.sort(allItems, 0, allItems.size()-1);
        adapter.notifyDataSetChanged();
        item.getIcon().setTint(colorWhite);
        if(property.equals("distance"))
            sortedByDistance = true;
        else if(property.equals("price"))
            sortedByPrice = true;
        Toast.makeText(getContext(), "Items sorted by "+property, Toast.LENGTH_SHORT).show();
    }

    /**
     * Clears the sorts filters already applied and calls the sortItems function, passing the new sort
     * wanted.
     * @param parameter
     * @param item
     */
    private void clearSortFiltersAndSortBy(String parameter, MenuItem item) {
        toolbar.getMenu().getItem(2).getIcon().setTint(colorPrimaryDark);
        toolbar.getMenu().getItem(3).getIcon().setTint(colorPrimaryDark);
        adapter.clear();
        allItems.clear();
        setupQueryItems();
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> items, ParseException e) {
                if (e != null) {
                    // Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Item item : items) {
                    int distance = getDistanceInKm(item, ParseUser.getCurrentUser());
                    item.setDistance(distance);
                }
                allItems.addAll(items);
                adapter.setAllItems(items);
                adapter.notifyDataSetChanged();

                sortedByDistance = false;
                sortedByPrice = false;
                sortItems(parameter, item);
            }
        });
    }
}