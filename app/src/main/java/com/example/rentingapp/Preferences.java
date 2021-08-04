package com.example.rentingapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

/**
 This class is in charge of collecting and applying the settings that the user wants, such as the subject, distance units, and type of currency.
 */

public class Preferences extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        loadSettings();
    }

    /**
     * This function loads the settings chosen by the user.
     */
    private void loadSettings() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean check_night = sp.getBoolean("NIGHT", false);
        if (check_night) {
            getListView().setBackgroundColor(Color.parseColor("#222222"));
        }
        else {
            getListView().setBackgroundColor(Color.parseColor("#ffffff"));
        }

        CheckBoxPreference checkNightInstant = (CheckBoxPreference)findPreference("NIGHT");
        checkNightInstant.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean nightMode = (boolean)newValue;
                if(nightMode)
                {
                    getListView().setBackgroundColor(Color.parseColor("#222222"));
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else
                {
                    getListView().setBackgroundColor(Color.parseColor("#ffffff"));
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            }
        });
    }
}
