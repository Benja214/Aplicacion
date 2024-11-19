package com.example.aplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText etEmailLogin, etClaveLogin;
    private Button btnLogin, btnRegistro, btnRecover;
    private ProgressBar progressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        etEmailLogin = findViewById(R.id.etEmailLogin);
        etClaveLogin = findViewById(R.id.etClaveLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);
        btnRecover = findViewById(R.id.btnRecover);
        progressBar = findViewById(R.id.progressBar);

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, Navbar.class));
            finish();
        }

        etEmailLogin.setText(auth.getCurrentUser().getEmail());

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
                                Toast.makeText(MainActivity.this, "¡Sesión iniciada con éxito!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, Navbar.class));
                                finish();
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
}
