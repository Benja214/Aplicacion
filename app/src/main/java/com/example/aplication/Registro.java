package com.example.aplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Registro extends AppCompatActivity {

    private EditText etNombre, etApellido, etRUT, etTelefono, etEmailRegistro, etClaveRegistro;
    private Button btnRegistrar, btnIniciar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        auth = FirebaseAuth.getInstance();

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etRUT = findViewById(R.id.etRut);
        etTelefono = findViewById(R.id.etTelefono);
        etEmailRegistro = findViewById(R.id.etEmailRegistro);
        etClaveRegistro = findViewById(R.id.etClaveRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnIniciar = findViewById(R.id.btnIniciar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                String apellido = etApellido.getText().toString();
                String rut = etRUT.getText().toString();
                String telefono = etTelefono.getText().toString();
                String email = etEmailRegistro.getText().toString();
                String password = etClaveRegistro.getText().toString();

                if (nombre.isEmpty() || apellido.isEmpty() || rut.isEmpty() || telefono.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Registro.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(Registro.this, "Error en el registro. Inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
