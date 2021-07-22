package com.example.rentingapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rentingapp.Adapters.SectionsPagerAdapter;
import com.example.rentingapp.CreateItemFragment;
import com.example.rentingapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RentsFragment#} factory method to
 * create an instance of this fragment.
 */
public class RentsFragment extends Fragment {
    FloatingActionButton fabComposeItem;
    ViewPager viewPager;
    TabLayout tabs;
    View myFragment;


    public RentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_rents, container, false);
        viewPager = myFragment.findViewById(R.id.view_pager);
        tabs = myFragment.findViewById(R.id.tabs);
        return myFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * Creates an adapter and adds the fragments to control.
     * @param viewPager
     */
    private void setUpViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new OwnRentedItemsFragment(),"Own");
        adapter.addFragment(new ForeignRentedItemsFragment(),"Foreign");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabComposeItem = view.findViewById(R.id.fabComposeItem);
        fabComposeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getContext(), CreateItemActivity.class);
                //startActivity(intent);
                CreateItemFragment createItemFragment = new CreateItemFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flContainer, createItemFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }
}