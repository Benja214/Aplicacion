package com.example.aplication.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aplication.R;
import com.example.aplication.utils.CircleTransform;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aplication.databinding.NavbarBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class Navbar extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavbarBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = NavbarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_profile, R.id.nav_users, R.id.nav_jobs, R.id.nav_applications, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            updateNavHeaderText(
                                    document.getString("imageUrl"),
                                    document.getString("nombre") + " " + document.getString("apellido"),
                                    document.getString("rol")
                            );
                        }
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateNavHeaderText(String imageUrl, String usuario, String rol) {
        ImageView ivProfileImage = findViewById(R.id.imageView);
        TextView tvUsuarioHeader = findViewById(R.id.usuarioHeader);
        TextView tvRolHeader = findViewById(R.id.rolHeader);
        if (ivProfileImage != null) {
            Picasso.get()
                    .load(imageUrl)
                    .transform(new CircleTransform())
                    .into(ivProfileImage);
        }
        if (tvUsuarioHeader != null) {
            tvUsuarioHeader.setText(usuario);
        }
        if (tvRolHeader != null) {
            tvRolHeader.setText(rol);
        }
        if (Objects.equals(rol, "Administrador")) {
            binding.navView.getMenu().findItem(R.id.nav_users).setVisible(true);
            binding.navView.getMenu().findItem(R.id.nav_applications).setVisible(false);
            binding.navView.setCheckedItem(R.id.nav_profile);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmación")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .create()
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}