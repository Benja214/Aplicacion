package com.example.aplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.aplication.activities.MainActivity;
import com.example.aplication.activities.Navbar;
import com.example.aplication.models.User;
import com.example.aplication.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference userDocRef;

    private ImageView ivProfileImage;
    private EditText etNombre, etApellido, etTelefono,etEmail;
    private Button btnActualizar, btnBorrar;

    private String imageUrl;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ivProfileImage = binding.ivProfileImage;
        etNombre = binding.etNombre;
        etApellido = binding.etApellido;
        etTelefono = binding.etTelefono;
        etEmail = binding.etEmail;
        btnActualizar = binding.btnActualizar;
        btnBorrar = binding.btnEliminar;

        String userId = auth.getCurrentUser().getUid();
        userDocRef = db.collection("users").document(userId);

        cargarDatos();

        btnActualizar.setOnClickListener(v -> actualizarPerfil());
        btnBorrar.setOnClickListener(v -> eliminarPerfil());

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
                                Picasso.get().load(document.getString("imageUrl")).into(ivProfileImage);
                                imageUrl = document.getString("imageUrl");
                            }
                            binding.etNombre.setText(document.getString("nombre"));
                            binding.etApellido.setText(document.getString("apellido"));
                            binding.etTelefono.setText(document.getString("telefono"));
                            binding.etEmail.setText(document.getString("email"));
                            ((Navbar) getActivity()).updateNavHeaderText(
                                    document.getString("nombre") + " " + document.getString("apellido"),
                                    document.getString("email"),
                                    document.getString("rol")
                            );
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
        String nombre = etNombre.getText().toString();
        String apellido = etApellido.getText().toString();
        String telefono = etTelefono.getText().toString();
        String email = etEmail.getText().toString();

        userDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String rol = documentSnapshot.getString("rol");

                        User user = new User(nombre, apellido, telefono, email, rol, imageUrl);
                        userDocRef.set(user).addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void eliminarPerfil() {
        userDocRef.delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Perfil eliminado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error al eliminar perfil", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
