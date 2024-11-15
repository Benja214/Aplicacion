package com.example.aplication.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aplication.R;
import com.example.aplication.models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Registro extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView ivProfileImage;
    private TextView tvChangeImage;
    private EditText etNombre, etApellido, etTelefono, etEmailRegistro, etClaveRegistro;
    private Spinner spnRol;
    private Button btnRegistrar, btnIniciaSesion;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseApp storageApp;
    private StorageReference storageReference;

    private Uri imageUri;
    private String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvChangeImage = findViewById(R.id.tvChangeImage);
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etTelefono = findViewById(R.id.etTelefono);
        etEmailRegistro = findViewById(R.id.etEmailRegistro);
        etClaveRegistro = findViewById(R.id.etClaveRegistro);
        spnRol = findViewById(R.id.spnRol);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnIniciaSesion = findViewById(R.id.btnIniciaSesion);

        storageApp = FirebaseApp.getInstance("proyectoStorage");
        storageReference = FirebaseStorage.getInstance(storageApp).getReference("tilines");

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

        tvChangeImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                String apellido = etApellido.getText().toString();
                String telefono = etTelefono.getText().toString();
                String email = etEmailRegistro.getText().toString();
                String password = etClaveRegistro.getText().toString();

                if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || email.isEmpty() || password.isEmpty() || imageUri == null) {
                    Toast.makeText(Registro.this, "Por favor, complete todos los campos y seleccione una imagen", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    uploadImageToStorage(user.getUid(), nombre, apellido, telefono, email, rol);
                                }
                            } else {
                                Toast.makeText(Registro.this, "El correo indicado ya se encuentra registrado", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void uploadImageToStorage(String userId, String name, String lastName, String phone, String email, String rol) {
        StorageReference fileReference = storageReference.child(userId + "/" + UUID.randomUUID().toString());
        fileReference.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveUserToDatabase(userId, name, lastName, phone, email, rol, imageUrl);
                });
            } else {
                Toast.makeText(this, "Error al subir la imagen: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToDatabase(String userId, String name, String lastName, String phone, String email, String rol, String imageUrl) {
        User user = new User(name, lastName, phone, email, rol, imageUrl);
        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registro exitoso y datos guardados", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivProfileImage.setImageURI(imageUri);

            Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show();
        }
    }
}
