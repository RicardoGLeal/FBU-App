package com.example.rentingapp.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.rentingapp.Adapters.RentsAdapter;
import com.example.rentingapp.Models.Rent;
import com.example.rentingapp.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.rentingapp.Controllers.ActionsController.queryRents;

public class OwnRentedItemsFragment extends Fragment {
    private static final String TAG = "OwnRentedItemsFragment";
    private RecyclerView rvRents;
    protected RentsAdapter adapter;
    protected List<Rent> allRents;
    protected ProgressBar progressBar;
    protected SwipeRefreshLayout swipeRefreshLayout;
    private Context context;

    public OwnRentedItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_own_rented_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.pb);
        rvRents = view.findViewById(R.id.rvRents);
        allRents = new ArrayList<>();
        swipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        context = getContext();

        //OnClickListener implemented when the user pulls to refresh.
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                allRents.clear();
                prequeryRents();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvRents.setLayoutManager(new LinearLayoutManager(context));
        adapter = createAdapter();
        rvRents.setAdapter(adapter);
        prequeryRents();
    }

    /**
     * Creates a new RentsAdapter with a true value in ownRentedItems.
     * @return rents adapter
     */
    protected RentsAdapter createAdapter() {
        return new RentsAdapter(getContext(), allRents, true);
    }

    /**
     * Calls the queryRents function that is in the ActionsController class, passing a True value in
     * ownRentedItems, this means it will load the user's own rents.
     */
    protected void prequeryRents() {
        queryRents(TAG, allRents, adapter, progressBar, true);
    }
}