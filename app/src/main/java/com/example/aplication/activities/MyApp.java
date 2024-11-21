package com.example.aplication.activities;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        checkDarkMode();

        FirebaseApp.initializeApp(this);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("loginperfilimg")
                .setApplicationId("1:1030729774309:android:f989dcbcc3c39bb40e500c")
                .setApiKey("AIzaSyAgfeveBQYjthGmRU8S6QacpH4LHDOxaUg")
                .setStorageBucket("loginperfilimg.appspot.com")
                .build();

        FirebaseApp.initializeApp(this, options, "proyectoStorage");
    }

    private void checkDarkMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean darkModeEnabled = sharedPreferences.getBoolean("darkModeEnabled", false);

        if (darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}