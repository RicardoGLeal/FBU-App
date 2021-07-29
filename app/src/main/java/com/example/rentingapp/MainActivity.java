package com.example.rentingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.rentingapp.Fragments.CreateItemFragment;
import com.example.rentingapp.Fragments.FeedFragment;
import com.example.rentingapp.Fragments.RentsFragment;
import com.example.rentingapp.Fragments.ProfileFragment;
import com.example.rentingapp.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import static com.example.rentingapp.Controllers.SendPushNotification.configurePushNotifications;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    //responsible for changing the fragment shown in the frame layout.
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    FloatingActionButton fabComposeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configurePushNotifications();

        fabComposeItem = findViewById(R.id.fabComposeItem);
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

        fabComposeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateItemFragment createItemFragment = new CreateItemFragment();
                FragmentTransaction transaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flContainer, createItemFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    /**
     * Overrided the onBackPressed to always go to the default fragment.
     */
    @Override
    public void onBackPressed() {
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}