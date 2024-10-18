package com.example.aplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Perfil extends AppCompatActivity {

    private TextView tvNombre;
    private TextView tvApellido;
    private TextView tvRut;
    private TextView tvPhone;
    private TextView tvEmail;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvNombre = findViewById(R.id.tvNombre);
        tvApellido = findViewById(R.id.tvApellido);
        tvRut = findViewById(R.id.tvRut);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);

        // Obtener el ID del usuario actual
        String userId = auth.getCurrentUser().getUid();

        // Recuperar datos del usuario desde Firestore
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Mostrar los datos en la interfaz
                            tvNombre.setText(document.getString("nombre"));
                            tvApellido.setText(document.getString("apellido"));
                            tvRut.setText(document.getString("rut"));
                            tvPhone.setText(document.getString("telefono")); // Asegúrate que el campo sea "telefono"
                            tvEmail.setText(document.getString("email"));
                        } else {
                            Log.d("Perfil", "No se encontró el documento");
                            Toast.makeText(Perfil.this, "No se encontró el documento", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Perfil", "Error al recuperar los datos", task.getException());
                        Toast.makeText(Perfil.this, "Error al recuperar los datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
