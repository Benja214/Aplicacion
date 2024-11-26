package com.example.aplication.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.aplication.R;
import com.example.aplication.activities.MainActivity;
import com.example.aplication.models.Application;
import com.example.aplication.models.Job;
import com.example.aplication.models.User;
import com.example.aplication.databinding.FragmentProfileBinding;
import com.example.aplication.utils.CircleTransform;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseApp storageApp;
    private StorageReference storageReference;
    private DocumentReference userDocRef;
    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView tvChangeImage;
    private ImageView ivProfileImage;
    private EditText etNombre, etApellido, etTelefono,etEmail;
    private Button btnActualizar, btnBorrar;

    private String imageUrl;
    private Uri imageUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ivProfileImage = binding.ivProfileImage;
        tvChangeImage = binding.tvChangeImage;
        etNombre = binding.etNombre;
        etApellido = binding.etApellido;
        etTelefono = binding.etTelefono;
        etEmail = binding.etEmail;
        btnActualizar = binding.btnActualizar;
        btnBorrar = binding.btnEliminar;

        String userId = auth.getCurrentUser().getUid();
        userDocRef = db.collection("users").document(userId);

        cargarDatos();

        tvChangeImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnActualizar.setOnClickListener(v -> actualizarPerfil());

        btnBorrar.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_nav_profile_to_delete_account);
        });

        return root;
    }

    private void cargarDatos() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.getString("imageUrl") != null) {
                                imageUrl = document.getString("imageUrl");
                                Picasso.get()
                                        .load(imageUrl)
                                        .transform(new CircleTransform())
                                        .into(ivProfileImage);
                            }
                            binding.etNombre.setText(document.getString("nombre"));
                            binding.etApellido.setText(document.getString("apellido"));
                            binding.etTelefono.setText(document.getString("telefono"));
                            binding.etEmail.setText(document.getString("email"));
                        } else {
                            Toast.makeText(getContext(), "El usuario indicado se encuentra bloqueado", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            requireActivity().finish();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al recuperar los datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void actualizarPerfil() {
        if (imageUri != null) {
            uploadImageToStorage(auth.getCurrentUser().getUid());
        } else {
            saveUserToDatabase();
        }
    }

    private void uploadImageToStorage(String userId) {
        storageApp = FirebaseApp.getInstance("proyectoStorage");
        storageReference = FirebaseStorage.getInstance(storageApp).getReference("GrupoFuenzalida");

        StorageReference fileReference = storageReference.child(userId + "/" + UUID.randomUUID().toString());
        fileReference.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrl = uri.toString();
                    saveUserToDatabase();
                });
            } else {
                Toast.makeText(getContext(), "Error al subir la imagen: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToDatabase() {
        String nombre = etNombre.getText().toString();
        String apellido = etApellido.getText().toString();
        String telefono = etTelefono.getText().toString();
        String email = etEmail.getText().toString();

        userDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String rol = documentSnapshot.getString("rol");
                        String fcmToken = documentSnapshot.getString("fcmToken");

                        User user = new User(nombre, apellido, telefono, email, rol, imageUrl, fcmToken);
                        userDocRef.set(user).addOnSuccessListener(aVoid -> {
                            if (Objects.equals(rol, "Empresa")) {
                                db.collection("jobs")
                                        .whereEqualTo("companyEmail", email)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Job job = document.toObject(Job.class);
                                                    job.setCompanyImage(imageUrl);
                                                    document.getReference().set(job)
                                                            .addOnCompleteListener(jobTask -> {});
                                                }
                                            } else {
                                                Toast.makeText(getContext(), "Error al modificar los trabajos", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                db.collection("applications")
                                        .whereEqualTo("companyEmail", email)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Application application = document.toObject(Application.class);
                                                    application.setCompanyImage(imageUrl);
                                                    document.getReference().set(application)
                                                            .addOnCompleteListener(jobTask -> {});
                                                }
                                            } else {
                                                Toast.makeText(getContext(), "Error al modificar las postulaciones", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else if (Objects.equals(rol, "Trabajador")) {
                                db.collection("applications")
                                        .whereEqualTo("workerEmail", email)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Application application = document.toObject(Application.class);
                                                    application.setWorkerImage(imageUrl);
                                                    document.getReference().set(application)
                                                            .addOnCompleteListener(jobTask -> {});
                                                }
                                            } else {
                                                Toast.makeText(getContext(), "Error al modificar las postulaciones", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get()
                    .load(imageUri)
                    .transform(new CircleTransform())
                    .into(ivProfileImage);

            Toast.makeText(getContext(), "Imagen seleccionada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
