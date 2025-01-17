package com.example.aplication.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.aplication.R;
import com.example.aplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ImageView ivLogo;
    private EditText etEmailLogin, etClaveLogin;
    private Button btnLogin, btnRegistro, btnRecover;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private SharedPreferences sharedPreferences;
    private boolean isPasswordVisible;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                } else {
                }
            });

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askNotificationPermission();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ivLogo = findViewById(R.id.ivLogo);
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etClaveLogin = findViewById(R.id.etClaveLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);
        btnRecover = findViewById(R.id.btnRecover);
        progressBar = findViewById(R.id.progressBar);

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, Navbar.class));
            finish();
        } else {
            sharedPreferences = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
            boolean darkModeEnabled = sharedPreferences.getBoolean("darkModeEnabled", false);
            if (darkModeEnabled) {
                ivLogo.setColorFilter( getResources().getColor(R.color.white) );
            }
        }

        isPasswordVisible = false;

        etClaveLogin.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etClaveLogin.getRight() - (etClaveLogin.getCompoundDrawables()[2].getBounds().width() * 2))) {
                    if (isPasswordVisible) {
                        etClaveLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        etClaveLogin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye, 0);
                    } else {
                        etClaveLogin.setInputType(InputType.TYPE_CLASS_TEXT);
                        etClaveLogin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.eye_off, 0);
                    }
                    isPasswordVisible = !isPasswordVisible;

                    etClaveLogin.setSelection(etClaveLogin.getText().length());
                    return true;
                }
            }
            return false;
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.GONE);

                String email = etEmailLogin.getText().toString().trim();
                String password = etClaveLogin.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    return;
                }

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (auth.getCurrentUser().isEmailVerified()) {
                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnCompleteListener(tokenTask -> {
                                                if (tokenTask.isSuccessful()) {
                                                    String token = tokenTask.getResult();

                                                    String userId = auth.getCurrentUser().getUid();
                                                    db.collection("users").document(userId).get()
                                                            .addOnCompleteListener(userTask -> {
                                                                if (userTask.isSuccessful()) {
                                                                    DocumentSnapshot document = userTask.getResult();
                                                                    if (document.exists()) {
                                                                        User user = document.toObject(User.class);
                                                                        user.setFcmToken(token);
                                                                        db.collection("users").document(userId)
                                                                                .set(user)
                                                                                .addOnCompleteListener(updateTask -> {
                                                                                    if (updateTask.isSuccessful()) {
                                                                                        Toast.makeText(MainActivity.this, "¡Sesión iniciada con éxito!", Toast.LENGTH_SHORT).show();
                                                                                        startActivity(new Intent(MainActivity.this, Navbar.class));
                                                                                        finish();
                                                                                    } else {
                                                                                        Toast.makeText(MainActivity.this, "Error al registrar el token: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                    }
                                                                } else {
                                                                    Toast.makeText(MainActivity.this, "Error al registrar dispositivo: " + userTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(MainActivity.this, "Error al obtener el token: " + tokenTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    auth.getCurrentUser().sendEmailVerification();
                                    auth.signOut();
                                    Toast.makeText(MainActivity.this, "Verifica tu correo para iniciar sesión.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    btnLogin.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Login fallido. Verifique sus credenciales.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                btnLogin.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Registro.class);
                startActivity(intent);
            }
        });

        btnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmailLogin.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, ingresa un correo", Toast.LENGTH_SHORT).show();
                } else {
                    sendPasswordResetEmail(email);
                }
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Correo de restablecimiento enviado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al enviar el correo: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
