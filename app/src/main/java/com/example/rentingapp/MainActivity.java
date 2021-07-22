package com.example.rentingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.rentingapp.Fragments.FeedFragment;
import com.example.rentingapp.Fragments.RentsFragment;
import com.example.rentingapp.Fragments.ProfileFragment;
import com.example.rentingapp.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity{
    public static final String TAG = "MainActivity";

    //responsible for changing the fragment shown in the frame layout.
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        //Listener for the button navigation view
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new FeedFragment();
                        break;
                    case R.id.action_search:
                        fragment = new SearchFragment();
                        break;
                    case R.id.action_rents:
                        fragment = new RentsFragment();
                        break;
                    case R.id.action_profile:
                    default:
                        fragment = new ProfileFragment(ParseUser.getCurrentUser());
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        //set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    /**
     * Overrided the onBackPressed to always go to the default fragment.
     */
    @Override
    public void onBackPressed() {
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}