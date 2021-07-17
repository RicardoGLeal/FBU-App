package com.example.rentingapp.Fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rentingapp.Adapters.RentsAdapter;
import com.example.rentingapp.R;

import static com.example.rentingapp.Controllers.ActionsController.queryRents;


public class ForeignRentedItemsFragment extends OwnRentedItemsFragment {

    private static final String TAG = "ForeignRentedItemsFragment";

    public ForeignRentedItemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ForeignRentedItemsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForeignRentedItemsFragment newInstance(String param1, String param2) {
        ForeignRentedItemsFragment fragment = new ForeignRentedItemsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_foreign_rented_items, container, false);
    }

    /**
     * Creates a new RentsAdapter with a false value in ownRentedItems.
     * @return rents adapter
     */
    @Override
    protected RentsAdapter createAdapter() {
        return new RentsAdapter(getContext(), allRents, false);
    }

    /**
     * Calls the queryRents function that is in the ActionsController class, passing a False value in
     * ownRentedItems, this means it will load the user's rents from items of other people.
     */
    @Override
    protected void prequeryRents() {
        queryRents(TAG, allRents, adapter, false);
    }
}