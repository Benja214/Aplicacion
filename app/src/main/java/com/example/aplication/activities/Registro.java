package com.example.aplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private EditText etNombre, etApellido, etTelefono, etEmailRegistro, etClaveRegistro;
    private Spinner spnRol;
    private Button btnRegistrar, btnIniciaSesion;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Instancia de Firestore

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etTelefono = findViewById(R.id.etTelefono);
        etEmailRegistro = findViewById(R.id.etEmailRegistro);
        etClaveRegistro = findViewById(R.id.etClaveRegistro);
        spnRol = findViewById(R.id.spnRol);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnIniciaSesion = findViewById(R.id.btnIniciaSesion);

        btnIniciaSesion.setOnClickListener(v -> finish());

        spnRol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRole = parent.getItemAtPosition(position).toString();
                if (selectedRole.equals("Empresa")) {
                    rol = "Empresa";
                } else if (selectedRole.equals("Trabajador")) {
                    rol = "Trabajador";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                String apellido = etApellido.getText().toString();
                String telefono = etTelefono.getText().toString();
                String email = etEmailRegistro.getText().toString();
                String password = etClaveRegistro.getText().toString();

                if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Registro.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                String userId = auth.getCurrentUser().getUid();

                                Map<String, Object> contacto = new HashMap<>();
                                contacto.put("nombre", nombre);
                                contacto.put("apellido", apellido);
                                contacto.put("telefono", telefono);
                                contacto.put("email", email);
                                contacto.put("rol", rol);

                                db.collection("contacto").document(userId).set(contacto)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(Registro.this, "Registro exitoso y datos guardados", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(Registro.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                                        });

                            } else {
                                Toast.makeText(Registro.this, "Error en el registro. Inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
