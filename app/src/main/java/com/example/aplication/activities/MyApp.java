package com.example.aplication.activities;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("proyectoa-b8822")
                .setApplicationId("1:732444971670:android:a4aee43a9e7e93b0b8e8b7")
                .setApiKey("AIzaSyBx5xk9bjSVQXk8pg1NHtJrEWkHQBZnhu8")
                .setStorageBucket("proyectoa-b8822.firebasestorage.app")
                .build();

        FirebaseApp.initializeApp(this, options, "proyectoStorage");
    }
}