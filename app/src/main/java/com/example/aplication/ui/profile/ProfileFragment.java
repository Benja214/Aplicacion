package com.example.aplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.aplication.activities.MainActivity;
import com.example.aplication.activities.Navbar;
import com.example.aplication.models.Usuario;
import com.example.aplication.databinding.FragmentProfileBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference userDocRef;

    private EditText etNombre, etApellido, etTelefono,etEmail;
    private Button btnActualizar, btnBorrar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etNombre = binding.etNombre;
        etApellido = binding.etApellido;
        etTelefono = binding.etTelefono;
        etEmail = binding.etEmail;
        btnActualizar = binding.btnActualizar;
        btnBorrar = binding.btnEliminar;

        String userId = auth.getCurrentUser().getUid();
        userDocRef = db.collection("contacto").document(userId);

        cargarDatos();

        btnActualizar.setOnClickListener(v -> actualizarPerfil());
        btnBorrar.setOnClickListener(v -> eliminarPerfil());

        return root;
    }

    private void cargarDatos() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("contacto").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            binding.etNombre.setText(document.getString("nombre"));
                            binding.etApellido.setText(document.getString("apellido"));
                            binding.etTelefono.setText(document.getString("telefono"));
                            binding.etEmail.setText(document.getString("email"));
                            ((Navbar) getActivity()).updateNavHeaderText(
                                    document.getString("nombre") + " " + document.getString("apellido"),
                                    document.getString("email")
                            );
                        } else {
                            Log.d("Perfil", "No se encontró el documento");
                            Toast.makeText(getContext(), "No se encontró el documento", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Perfil", "Error al recuperar los datos", task.getException());
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

                        Usuario usuario = new Usuario(nombre, apellido, telefono, email, rol);
                        userDocRef.set(usuario).addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Log.d("Firestore", "El documento no existe");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener los datos", e);
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
