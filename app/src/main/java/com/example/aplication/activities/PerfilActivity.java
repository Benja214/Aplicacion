package com.example.aplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private EditText etNombre, etApellido, etRut, etPhone, etEmail;
    private Button btnActualizar, btnBorrar;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        etNombre = findViewById(R.id.etNombres);
        etApellido = findViewById(R.id.etApellidos);
        etRut = findViewById(R.id.etRut);
        etPhone = findViewById(R.id.etTelefonos);
        etEmail = findViewById(R.id.etEmail);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnBorrar = findViewById(R.id.btnBorrar);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        loadUserData();


        btnActualizar.setOnClickListener(v -> updateUserProfile());

        btnBorrar.setOnClickListener(v -> deleteUserProfile());
    }

    private void loadUserData() {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            etNombre.setText(document.getString("nombre"));
                            etApellido.setText(document.getString("apellido"));
                            etRut.setText(document.getString("rut"));
                            etPhone.setText(document.getString("telefono"));
                            etEmail.setText(document.getString("email"));
                        } else {
                            Toast.makeText(PerfilActivity.this, "El perfil no existe", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PerfilActivity.this, "Error al cargar perfil", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserProfile() {
        String nombre = etNombre.getText().toString();
        String apellido = etApellido.getText().toString();
        String rut = etRut.getText().toString();
        String telefono = etPhone.getText().toString();
        String email = etEmail.getText().toString();

        if (!nombre.isEmpty() && !apellido.isEmpty() && !rut.isEmpty() && !telefono.isEmpty() && !email.isEmpty()) {
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("nombre", nombre);
            userProfile.put("apellido", apellido);
            userProfile.put("rut", rut);
            userProfile.put("telefono", telefono);
            userProfile.put("email", email);

            db.collection("users").document(userId).set(userProfile)
                    .addOnSuccessListener(aVoid -> Toast.makeText(PerfilActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(PerfilActivity.this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteUserProfile() {
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PerfilActivity.this, "Perfil eliminado", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(PerfilActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(PerfilActivity.this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
